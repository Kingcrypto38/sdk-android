package com.example.afterpay

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.createGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.fragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.afterpay.android.Afterpay
import com.example.afterpay.checkout.CheckoutFragment
import com.example.afterpay.receipt.ReceiptFragment
import com.example.afterpay.shopping.ShoppingFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        val navController = findNavController(R.id.nav_host_fragment).apply {
            graph = createGraph(nav_graph.id, nav_graph.dest.shopping) {
                fragment<ShoppingFragment>(nav_graph.dest.shopping) {
                    label = getString(R.string.title_shopping)
                    action(nav_graph.action.to_checkout) {
                        destinationId = nav_graph.dest.checkout
                    }
                }
                fragment<CheckoutFragment>(nav_graph.dest.checkout) {
                    label = getString(R.string.title_checkout)
                    argument(nav_graph.args.total_cost) {
                        type = NavType.ParcelableType(BigDecimal::class.java)
                    }
                    action(nav_graph.action.to_receipt) {
                        destinationId = nav_graph.dest.receipt
                    }
                }
                fragment<ReceiptFragment>(nav_graph.dest.receipt) {
                    label = getString(R.string.title_receipt)
                    argument(nav_graph.args.checkout_token) {
                        type = NavType.StringType
                    }
                    action(nav_graph.action.back_to_shopping) {
                        destinationId = nav_graph.dest.shopping
                        navOptions {
                            popUpTo(nav_graph.dest.shopping) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }

        findViewById<Toolbar>(R.id.main_toolbar).apply {
            setupWithNavController(navController, AppBarConfiguration(navController.graph))
            setNavigationOnClickListener {
                onBackPressed()
            }
        }

        fetchAfterpayConfiguration()
    }

    private fun fetchAfterpayConfiguration() {
        lifecycleScope.launchWhenStarted {
            try {
                val configuration = withContext(Dispatchers.IO) {
                    Dependencies.merchantApi.configuration()
                }
                Afterpay.setConfiguration(
                    minimumAmount = configuration.minimumAmount?.amount,
                    maximumAmount = configuration.maximumAmount.amount,
                    currencyCode = configuration.maximumAmount.currency
                )
            } catch (_: Exception) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.configuration_error_message,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.configuration_error_action_retry) {
                        fetchAfterpayConfiguration()
                    }
                    .show()
            }
        }
    }
}

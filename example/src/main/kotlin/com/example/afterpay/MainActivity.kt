package com.example.afterpay

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavType
import androidx.navigation.createGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.fragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.afterpay.checkout.CheckoutFragment
import com.example.afterpay.checkout.SuccessFragment
import com.example.afterpay.shopping.ShoppingFragment
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
                    action(nav_graph.action.to_success) {
                        destinationId = nav_graph.dest.success
                    }
                }
                fragment<SuccessFragment>(nav_graph.dest.success) {
                    label = getString(R.string.title_checkout_success)
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
    }
}

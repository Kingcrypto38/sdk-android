package com.example.afterpay.checkout

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afterpay.android.Afterpay
import com.afterpay.android.view.AfterpayPaymentButton
import com.example.afterpay.NavGraph
import com.example.afterpay.R
import com.example.afterpay.checkout.CheckoutViewModel.Command
import com.example.afterpay.getDependencies
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal

class CheckoutFragment : Fragment() {
    private companion object {
        const val CHECKOUT_WITH_AFTERPAY = 1234
    }

    private val viewModel by viewModels<CheckoutViewModel> {
        CheckoutViewModel.factory(
            totalCost = requireNotNull(arguments?.get(NavGraph.args.total_cost) as? BigDecimal),
            merchantApi = getDependencies().merchantApi,
            preferences = getDependencies().sharedPreferences,
        )
    }

    // when launching the checkout with V2, the token must be generated
    // with 'popupOriginUrl' set to 'https://static.afterpay.com' under the
    // top level 'merchant' object
    private val checkoutHandler = CheckoutHandler(
        onDidCommenceCheckout = { viewModel.loadCheckoutToken() },
        onShippingAddressDidChange = { viewModel.selectAddress(it) },
        onShippingOptionDidChange = { viewModel.selectShippingOption(it) },
    )

    private val cashAppHandler = CashAppHandler(
        onDidCommenceCheckout = { viewModel.loadCheckoutToken() },
        onDidReceiveResponse = {
            Log.d("mylogger", "Pass the result to cash app ($it)")
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Afterpay.setCheckoutV2Handler(checkoutHandler)
        Afterpay.setCashAppHandler(cashAppHandler)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailField = view.findViewById<EditText>(R.id.cart_editText_emailAddress)
        emailField.addTextChangedListener { text ->
            viewModel.enterEmailAddress(email = text.toString())
        }

        val checkoutButton = view.findViewById<AfterpayPaymentButton>(R.id.cart_button_checkout)
        checkoutButton.setOnClickListener { viewModel.showAfterpayCheckout() }

        val totalCost = view.findViewById<TextView>(R.id.cart_totalCost)

        val expressRow = view.findViewById<View>(R.id.cart_expressRow)
        val expressCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_expressCheckBox)
        expressRow.setOnClickListener { expressCheckBox.toggle() }
        expressCheckBox.setOnCheckedChangeListener { _, checked -> viewModel.checkExpress(checked) }

        val versionRow = view.findViewById<View>(R.id.cart_v1Row)
        val versionCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_v1CheckBox)
        versionRow.setOnClickListener { versionCheckBox.toggle() }
        versionCheckBox.setOnCheckedChangeListener { _, checked -> viewModel.checkVersion(checked) }

        val buyNowRow = view.findViewById<View>(R.id.cart_buyNowRow)
        val buyNowCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_buyNowCheckBox)
        buyNowRow.setOnClickListener { buyNowCheckBox.toggle() }
        buyNowCheckBox.setOnCheckedChangeListener { _, checked -> viewModel.checkBuyNow(checked) }

        val pickupRow = view.findViewById<View>(R.id.cart_pickupRow)
        val pickupCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_pickupCheckBox)
        pickupRow.setOnClickListener { pickupCheckBox.toggle() }
        pickupCheckBox.setOnCheckedChangeListener { _, checked -> viewModel.checkPickup(checked) }

        val shippingOptionsRequiredRow = view.findViewById<View>(R.id.cart_shippingOptionsRequiredRow)
        val shippingOptionsRequiredCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_shippingOptionsRequiredCheckBox)
        shippingOptionsRequiredRow.setOnClickListener { pickupCheckBox.toggle() }
        shippingOptionsRequiredCheckBox.setOnCheckedChangeListener { _, checked ->
            viewModel.checkShippingOptionsRequired(checked)
        }

        val cashAppRow = view.findViewById<View>(R.id.cart_cashAppRow)
        val cashAppCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_cashAppCheckBox)
        cashAppRow.setOnClickListener { cashAppCheckBox.toggle() }
        cashAppCheckBox.setOnCheckedChangeListener { _, checked -> viewModel.checkCashApp(checked) }

        lifecycleScope.launchWhenCreated {
            viewModel.state().collectLatest { state ->
                if (emailField.text.toString() != state.emailAddress) {
                    emailField.setText(state.emailAddress)
                }

                totalCost.text = state.totalCost
                versionCheckBox.isChecked = state.useV1
                expressCheckBox.isChecked = state.express
                buyNowCheckBox.isChecked = state.buyNow
                pickupCheckBox.isChecked = state.pickup
                shippingOptionsRequiredCheckBox.isChecked = state.shippingOptionsRequired
                checkoutButton.isEnabled = state.enableCheckoutButton
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.commands().collectLatest { command ->
                when (command) {
                    is Command.ShowAfterpayCheckoutV2 -> {
                        val intent = Afterpay.createCheckoutV2Intent(requireContext(), command.options)
                        getCheckoutResult.launch(intent)
                    }
                    is Command.ShowAfterpayCheckoutV1 -> {
                        val intent = Afterpay.createCheckoutIntent(requireContext(), command.checkoutUrl)
                        getCheckoutResult.launch(intent)
                    }
                    is Command.LaunchCashAppPay ->
                        Afterpay.retrieveCashAppData()
                    is Command.ProvideCheckoutTokenResult ->
                        checkoutHandler.provideTokenResult(command.tokenResult)
                    is Command.ProvideCashAppTokenResult ->
                        cashAppHandler.provideTokenResult(command.tokenResult)
                    is Command.ProvideShippingOptionsResult ->
                        checkoutHandler.provideShippingOptionsResult(command.shippingOptionsResult)
                    is Command.ProvideShippingOptionUpdateResult ->
                        checkoutHandler.provideShippingOptionUpdateResult(
                            command.shippingOptionUpdateResult,
                        )
                }
            }
        }
    }

    private val getCheckoutResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    val intent = checkNotNull(result.data) {
                        "Intent should always be populated by the SDK"
                    }
                    val token = checkNotNull(Afterpay.parseCheckoutSuccessResponse(intent)) {
                        "A token is always associated with a successful Afterpay transaction"
                    }
                    findNavController().navigate(
                        NavGraph.action.to_receipt,
                        bundleOf(NavGraph.args.checkout_token to token),
                    )
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    val intent = requireNotNull(result.data) {
                        "Intent should always be populated by the SDK"
                    }
                    val status =
                        checkNotNull(Afterpay.parseCheckoutCancellationResponse(intent)) {
                            "A cancelled Afterpay transaction always contains a status"
                        }
                    Snackbar.make(requireView(), "Cancelled: $status", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}

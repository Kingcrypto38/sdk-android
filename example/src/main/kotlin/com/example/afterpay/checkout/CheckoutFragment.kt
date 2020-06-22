package com.example.afterpay.checkout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.afterpay.android.Afterpay
import com.example.afterpay.R
import com.example.afterpay.checkout.CheckoutViewModel.Command
import kotlinx.coroutines.flow.collectLatest

class CheckoutFragment : Fragment() {
    private companion object {
        const val CHECKOUT_WITH_AFTERPAY = 1234
    }

    private val viewModel by activityViewModels<CheckoutViewModel> { CheckoutViewModel.factory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailField = view.findViewById<EditText>(R.id.cart_editText_emailAddress)
        emailField.addTextChangedListener { text ->
            viewModel.enterEmailAddress(email = text.toString())
        }

        val checkoutButton = view.findViewById<Button>(R.id.cart_button_checkout)
        checkoutButton.setOnClickListener {
            viewModel.checkoutWithAfterpay()
        }

        val progressBar = view.findViewById<ProgressBar>(R.id.cart_progressBar)

        lifecycleScope.launchWhenCreated {
            viewModel.state().collectLatest { state ->
                checkoutButton.isEnabled = state.enableCheckoutButton
                progressBar.visibility = if (state.showProgressBar) View.VISIBLE else View.INVISIBLE
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.commands().collectLatest { event ->
                when (event) {
                    is Command.StartAfterpayCheckout -> {
                        val intent = Afterpay.createCheckoutIntent(requireContext(), event.url)
                        startActivityForResult(intent, CHECKOUT_WITH_AFTERPAY)
                    }
                    is Command.DisplayError -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode to resultCode) {
            CHECKOUT_WITH_AFTERPAY to AppCompatActivity.RESULT_OK -> {
                val intent = requireNotNull(data) { "Intent should always be populated by the SDK" }
                val token = Afterpay.parseCheckoutResponse(intent) ?: error("Should have a token")
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.fragment_container, SuccessFragment(token), null)
                    addToBackStack(null)
                }
            }
            CHECKOUT_WITH_AFTERPAY to AppCompatActivity.RESULT_CANCELED -> {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

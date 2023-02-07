package com.afterpay.android.cashapp

import com.afterpay.android.Afterpay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


sealed class CashAppValidationResponse {
    data class Success(val response : AfterpayCashAppValidationResponse) : CashAppValidationResponse()
    data class Failure(val error: Throwable) : CashAppValidationResponse()
}

class AfterpayCashAppCheckout(cashHandler: AfterpayCashAppHandler?) {
    private var handler: AfterpayCashAppHandler? = cashHandler ?: Afterpay.cashAppHandler

    init {
        require(Afterpay.cashAppHandler != null) { "CashApp handler must be set and not null before attempting to retrieve data" }
    }

    fun commenceCheckout() {
        handler?.didCommenceCheckout { result ->
            runBlocking {
                result.onSuccess { token ->
                    signPayment(token)
                        .onSuccess { response ->
                            val jwtBody = AfterpayCashAppJwt.decode(response.jwtToken)
                            jwtBody?.let {
                                val cashApp = AfterpayCashApp(
                                    amount = jwtBody.amount.amount.toDouble(),
                                    redirectUri = jwtBody.redirectUrl,
                                    merchantId = jwtBody.externalMerchantId,
                                    brandId = response.externalBrandId,
                                    jwt = response.jwtToken,
                                )

                                handler!!.didReceiveCashAppData(cashApp)
                            }
                        }
                        .onFailure {

                        }
                }
            }
        }
    }

    private fun signPayment(token: String): Result<AfterpayCashAppSigningResponse> {
        return runBlocking {
            Afterpay.environment?.cashAppPaymentSigningUrl?.let { url ->
                val payload = """{ "token": "$token" }"""

                val response = withContext(Dispatchers.Unconfined) {
                    AfterpayCashAppApi.cashRequest<AfterpayCashAppSigningResponse, String>(
                        url = url,
                        method = AfterpayCashAppApi.CashHttpVerb.POST,
                        body = payload
                    )
                }

                response
            } ?: Result.failure(Exception("Environment not set"))
        }
    }

    companion object {
        fun validatePayment(
            jwt: String,
            customerId: String,
            grantId: String,
            complete: (validationResponse: CashAppValidationResponse) -> Unit
        ) {
            return runBlocking {
                Afterpay.environment?.cashAppPaymentValidationUrl?.let { url ->
                    val payload = """
                        {
                            "jwt": "$jwt",
                            "externalCustomerId": "$customerId",
                            "externalGrantId": "$grantId"
                        }
                    """.trimIndent()

                    val response = withContext(Dispatchers.Unconfined) {
                        AfterpayCashAppApi.cashRequest<AfterpayCashAppValidationResponse, String>(
                            url = url,
                            method = AfterpayCashAppApi.CashHttpVerb.POST,
                            body = payload
                        )
                    }

                    response
                        .onSuccess {
                            when (it.status) {
                                "SUCCESS" -> complete(CashAppValidationResponse.Success(it))
                                else -> complete(CashAppValidationResponse.Failure(Exception("status is ${it.status}")))
                            }
                        }
                        .onFailure {
                            complete(CashAppValidationResponse.Failure(Exception(it.message)))
                        }

                    Unit
                }
            } ?: complete(CashAppValidationResponse.Failure(Exception("environment not set")))
        }
    }
}

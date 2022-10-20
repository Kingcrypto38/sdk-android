package com.afterpay.android.internal

import com.afterpay.android.Afterpay
import com.afterpay.android.internal.Locales.EN_AU
import com.afterpay.android.internal.Locales.EN_CA
import com.afterpay.android.internal.Locales.EN_GB
import com.afterpay.android.internal.Locales.EN_NZ
import com.afterpay.android.internal.Locales.EN_US
import com.afterpay.android.internal.Locales.ES_ES
import com.afterpay.android.internal.Locales.FR_CA
import com.afterpay.android.internal.Locales.FR_FR
import com.afterpay.android.internal.Locales.IT_IT

private val localeLanguages = mapOf(
    EN_AU to AfterpayString.EN,
    EN_GB to AfterpayString.EN,
    EN_NZ to AfterpayString.EN,
    EN_US to AfterpayString.EN,
    EN_CA to AfterpayString.EN,
    FR_CA to AfterpayString.FR_CA,
    FR_FR to AfterpayString.FR,
    IT_IT to AfterpayString.IT,
    ES_ES to AfterpayString.ES
)

internal enum class AfterpayString(
    val breakdownLimit: String,
    val breakdownLimitDescription: String,

    val introOrTitle: String,
    val introOr: String,
    val introInTitle: String,
    val introIn: String,
    val introPayTitle: String,
    val introPay: String,
    val introPayInTitle: String,
    val introPayIn: String,
    val introMakeTitle: String,
    val introMake: String,

    val noConfigurationDescription: String,
    val noConfiguration: String,

    val loadErrorTitle: String,
    val loadErrorRetry: String,
    val loadErrorCancel: String,
    val loadErrorMessage: String,

    val paymentButtonContentDescription: String,

    val priceBreakdownAvailable: String,
    val priceBreakdownAvailableDescription: String,
    val priceBreakdownWith: String,
    val priceBreakdownInterestFree: String,
    val priceBreakdownLinkLearnMore: String,
    val priceBreakdownLinkMoreInfo: String
) {
    EN(
        breakdownLimit = "available for orders between %1\$s – %2\$s",
        breakdownLimitDescription = "%1\$s available for orders between %2\$s – %3\$s",
        introOrTitle = "Or",
        introOr = "or",
        introInTitle = "In",
        introIn = "in",
        introPayTitle = "Pay",
        introPay = "pay",
        introPayInTitle = "Pay in",
        introPayIn = "pay in",
        introMakeTitle = "Make",
        introMake = "make",
        noConfigurationDescription = "Or pay with %1\$s",
        noConfiguration = "or pay with",
        loadErrorTitle = "Error",
        loadErrorRetry = "Retry",
        loadErrorCancel = "Cancel",
        loadErrorMessage = "Failed to load %1\$s checkout",
        paymentButtonContentDescription = "Pay now with %1\$s",
        priceBreakdownAvailable = "%1\$s %2\$s %3\$spayments of %4\$s %5\$s",
        priceBreakdownAvailableDescription = "%1\$s %2\$s %3\$spayments of %4\$s %5\$s%6\$s",
        priceBreakdownWith = "with ",
        priceBreakdownInterestFree = "interest-free ",
        priceBreakdownLinkLearnMore = "Learn More",
        priceBreakdownLinkMoreInfo = "More Info"
    ),
    FR_CA(
        breakdownLimit = "disponible pour les montants entre %1\$s – %2\$s",
        breakdownLimitDescription = "%1\$s disponible pour les montants entre %2\$s – %3\$s",
        introOrTitle = "Ou",
        introOr = "ou",
        introInTitle = "En",
        introIn = "en",
        introPayTitle = "Payez",
        introPay = "payez",
        introPayInTitle = "Payez en",
        introPayIn = "payez en",
        introMakeTitle = "Effectuez",
        introMake = "effectuez",
        noConfigurationDescription = "Ou payer avec %1\$s",
        noConfiguration = "ou payer avec",
        loadErrorTitle = "Erreur",
        loadErrorRetry = "Retenter",
        loadErrorCancel = "Annuler",
        loadErrorMessage = "Échec du chargement de la caisse %1\$s",
        paymentButtonContentDescription = "Payez maintenant avec %1\$s",
        priceBreakdownAvailable = "%1\$s %2\$s paiements %3\$sde %4\$s %5\$s",
        priceBreakdownAvailableDescription = "%1\$s %2\$s paiements %3\$sde %4\$s %5\$s%6\$s",
        priceBreakdownWith = "avec ",
        priceBreakdownInterestFree = "sans intérêts ",
        priceBreakdownLinkLearnMore = "En savoir plus",
        priceBreakdownLinkMoreInfo = "Plus d'infos"
    ),
    FR(
        breakdownLimit = "disponible pour les montants entre %1\$s – %2\$s",
        breakdownLimitDescription = "%1\$s disponible pour les montants entre %2\$s – %3\$s",
        introOrTitle = "Ou",
        introOr = "ou",
        introInTitle = "En",
        introIn = "en",
        introPayTitle = "Payez",
        introPay = "payez",
        introPayInTitle = "Payez en",
        introPayIn = "payez en",
        introMakeTitle = "Effectuez",
        introMake = "effectuez",
        noConfigurationDescription = "Ou payer avec %1\$s",
        noConfiguration = "ou payer avec",
        loadErrorTitle = "Erreur",
        loadErrorRetry = "Réessayer",
        loadErrorCancel = "Annuler",
        loadErrorMessage = "Échec du chargement de la page de paiement 1%\$s",
        paymentButtonContentDescription = "Payez maintenant avec %1\$s",
        priceBreakdownAvailable = "%1\$s %2\$s paiements %3\$sde %4\$s %5\$s",
        priceBreakdownAvailableDescription = "%1\$s %2\$s paiements %3\$sde %4\$s %5\$s%6\$s",
        priceBreakdownWith = "avec ",
        priceBreakdownInterestFree = "sans frais ",
        priceBreakdownLinkLearnMore = "En savoir plus",
        priceBreakdownLinkMoreInfo = "Plus d'infos"
    ),
    IT(
        breakdownLimit = "disponibile per importi fra %1\$s – %2\$s",
        breakdownLimitDescription = "%1\$s disponibile per importi fra %2\$s – %3\$s",
        introOrTitle = "O",
        introOr = "o",
        introInTitle = "In",
        introIn = "in",
        introPayTitle = "Paga",
        introPay = "paga",
        introPayInTitle = "Paga in",
        introPayIn = "paga in",
        introMakeTitle = "Scegli",
        introMake = "scegli",
        noConfigurationDescription = "O paga con %1\$s",
        noConfiguration = "o paga con",
        loadErrorTitle = "Errore",
        loadErrorRetry = "Riprovare",
        loadErrorCancel = "Annulla",
        loadErrorMessage = "Impossibile caricare il cassa di %1\$s",
        paymentButtonContentDescription = "Paga ora con %1\$s",
        priceBreakdownAvailable = "%1\$s %2\$s rate %3\$sda %4\$s %5\$s",
        priceBreakdownAvailableDescription = "%1\$s %2\$s rate %3\$sda %4\$s %5\$s%6\$s",
        priceBreakdownWith = "con ",
        priceBreakdownInterestFree = "senza interessi ",
        priceBreakdownLinkLearnMore = "Scopri di piú",
        priceBreakdownLinkMoreInfo = "Maggiori info"
    ),
    ES(
        breakdownLimit = "disponible para importes entre %1\$s – %2\$s",
        breakdownLimitDescription = "%1\$s disponible para importes entre %2\$s – %3\$s",
        introOrTitle = "O",
        introOr = "o",
        introInTitle = "En",
        introIn = "en",
        introPayTitle = "Paga",
        introPay = "paga",
        introPayInTitle = "Paga en",
        introPayIn = "paga en",
        introMakeTitle = "Haga",
        introMake = "haga",
        noConfigurationDescription = "O pagar con %1\$s",
        noConfiguration = "o pagar con",
        loadErrorTitle = "Error",
        loadErrorRetry = "Volver a intentar",
        loadErrorCancel = "Cancelar",
        loadErrorMessage = "Imposible cargar la página de pago de %1\$s",
        paymentButtonContentDescription = "Pagar ahora con %1\$s",
        priceBreakdownAvailable = "%1\$s %2\$s pagos %3\$sde %4\$s %5\$s",
        priceBreakdownAvailableDescription = "%1\$s %2\$s pagos %3\$sde %4\$s %5\$s%6\$s",
        priceBreakdownWith = "con ",
        priceBreakdownInterestFree = "sin coste ",
        priceBreakdownLinkLearnMore = "Saber más",
        priceBreakdownLinkMoreInfo = "Más infos"
    );

    companion object {
        fun forLocale(): AfterpayString {
            return localeLanguages[Afterpay.language] ?: EN
        }
    }
}

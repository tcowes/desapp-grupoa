package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import java.math.BigDecimal

interface CryptoService {
    fun getCryptoQuote(cryptoActiveName: CryptoCurrencyEnum): Float?
    fun showCryptoAssetQuotes(): Map<String, Float?>
    fun getCryptoCurrencyValueUSDTtoARS() : BigDecimal?
    fun showCryptoAssetQuotesLast24Hours(cryptoCurrency: CryptoCurrencyEnum): List<String>

}
package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Transaction
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsTransaction.ExchangeRateException
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.TransactionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.UserRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.TransactionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.dataResponse.CoinGeckoResponse
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.CryptoAssetDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.dataResponse.ExchangeRateResponse
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.VolumeOperatedDTO
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class TransactionServiceImpl : TransactionService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    private val restTemplate: RestTemplate = RestTemplate()

    override fun getTransactionById(transactionId: Long): Transaction {
        return transactionRepository.findById(transactionId)
            .orElseThrow { EntityNotFoundException("Transaction not found with id: $transactionId") }
    }

    override fun cancelTransaction(userId: Long, transactionId: Long) {
        val user = userRepository.findById(userId).get()
        val transaction = getTransactionById(transactionId)
        transaction.cancelTransaction(user)
        userRepository.save(user)
        transactionRepository.save(transaction)
    }

    override fun getVolumeOperated(userId: Long, startDate: LocalDateTime, endDate: LocalDateTime): VolumeOperatedDTO {
        val transactions = transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate)

        val totalUSD = transactions.sumOf { BigDecimal(it.amount).multiply(getCurrentCryptoPriceInUSD(it.cryptocurrency.name)) }
        val totalARS = totalUSD.multiply(getCurrentUSDtoARSExchangeRate())

        val assets = transactions.groupBy { it.cryptocurrency.name }
            .map { (cryptocurrency, trans) ->
                val totalAmount = trans.sumOf { it.amount }
                val currentPrice = getCurrentCryptoPriceInUSD(cryptocurrency)
                val currentPriceARS = currentPrice.multiply(getCurrentUSDtoARSExchangeRate())
                CryptoAssetDTO(cryptocurrency, totalAmount, currentPrice, currentPriceARS)
            }

        return VolumeOperatedDTO(
            timestamp = LocalDateTime.now(),
            totalUSD = totalUSD,
            totalARS = totalARS,
            assets = assets
        )

    }

    private fun getCurrentUSDtoARSExchangeRate(): BigDecimal {
        val apiUrl = "https://dolarapi.com/v1/dolares/oficial"
        return try {
            val response = restTemplate.getForObject(apiUrl, ExchangeRateResponse::class.java)
            response?.venta ?: throw ExchangeRateException("Could not get USD to ARS exchange rate")
        } catch (e: RestClientException) {
            throw ExchangeRateException("Error getting USD to ARS exchange rate: ${e.message}")
        }
    }

    private fun getCurrentCryptoPriceInUSD(cryptoAsset: String): BigDecimal {
        val apiUrl = UriComponentsBuilder.fromHttpUrl("https://api.coingecko.com/api/v3/coins")
            .pathSegment(cryptoAsset)
            .build()
            .toUriString()

        return try {
            val restTemplate = RestTemplate()
            val response = restTemplate.getForObject(apiUrl, CoinGeckoResponse::class.java)
            response?.market_data?.current_price?.get("usd")
                ?: throw ExchangeRateException("Could not get price of $cryptoAsset cryptocurrency in USD")
        } catch (e: RestClientException) {
            throw ExchangeRateException("Error when obtaining the price of the crypto asset $cryptoAsset in USD: ${e.message}")
        }


    }

    override fun deleteAll() {
        transactionRepository.deleteAll()
    }

}
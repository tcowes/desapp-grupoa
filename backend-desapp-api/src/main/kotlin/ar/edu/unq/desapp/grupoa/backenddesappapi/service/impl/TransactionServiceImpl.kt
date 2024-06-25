package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Transaction
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.TransactionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.UserRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.CryptoService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.TransactionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.CryptoAssetDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.VolumeOperatedDTO
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import org.slf4j.LoggerFactory

@Service
@Transactional
class TransactionServiceImpl : TransactionService {

    private val logger = LoggerFactory.getLogger(TransactionService::class.java)

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var cryptoService: CryptoService


    override fun getTransactionById(transactionId: Long): Transaction {
        return transactionRepository.findById(transactionId)
            .orElseThrow { EntityNotFoundException("Transaction not found with id: $transactionId") }
    }

    override fun cancelTransaction(userId: Long, transactionId: Long) {
        logger.info("Initiating transaction cancellation for user with ID: $userId and the transaction with ID: $transactionId")
        val user = userRepository.findById(userId).get()
        val transaction = getTransactionById(transactionId)
        transaction.cancelTransaction(user)
        userRepository.save(user)
        transactionRepository.save(transaction)
        logger.info("Transaction canceled successfully for user with ID: $userId and the transaction with ID: $transactionId")
    }

    override fun getVolumeOperated(userId: Long, startDate: LocalDateTime, endDate: LocalDateTime): VolumeOperatedDTO {
        logger.info("Starting calculation of the traded volume for the user with ID: $userId, from $startDate until $endDate")

        val transactions = transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate)
        logger.debug("Number of transactions found for the user with ID $userId from $startDate y $endDate: ${transactions.size}")

        val totalUSD = transactions.sumOf {
            val cryptoPriceUSD = cryptoService.getCryptoQuote(it.cryptocurrency)
            if (cryptoPriceUSD != null) {
                val cryptoUSD = BigDecimal(cryptoPriceUSD.toDouble())
                BigDecimal(it.amount.toString()) * cryptoUSD
            } else {
                BigDecimal.ZERO
            }
        }

        val conversionRate = cryptoService.getCryptoCurrencyValueUSDTtoARS() ?: BigDecimal.ZERO
        val totalARS = totalUSD * conversionRate

        val assets = transactions.groupBy { it.cryptocurrency }
            .map { (cryptocurrency, trans) ->
                val totalAmount = trans.sumOf { it.amount }
                val currentPriceUSD: BigDecimal? = cryptoService.getCryptoQuote(cryptocurrency)?.let {
                    BigDecimal.valueOf(it.toDouble())
                }
                val currentPriceARS: BigDecimal = currentPriceUSD?.times(conversionRate) ?: BigDecimal.ZERO
                CryptoAssetDTO(cryptocurrency.name, totalAmount, currentPriceUSD ?: BigDecimal.ZERO, currentPriceARS)
            }

        logger.info("Calculation of the completed traded volume for the user with ID: $userId")
        return VolumeOperatedDTO(
            timestamp = LocalDateTime.now(),
            totalUSD = totalUSD,
            totalARS = totalARS,
            assets = assets
        )

    }

    override fun deleteAll() {
        transactionRepository.deleteAll()
    }

}
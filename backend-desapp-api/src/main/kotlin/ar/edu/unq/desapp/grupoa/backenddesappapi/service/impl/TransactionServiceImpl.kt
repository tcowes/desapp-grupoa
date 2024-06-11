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

@Service
@Transactional
class TransactionServiceImpl : TransactionService {
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
        val user = userRepository.findById(userId).get()
        val transaction = getTransactionById(transactionId)
        transaction.cancelTransaction(user)
        userRepository.save(user)
        transactionRepository.save(transaction)
    }

    override fun getVolumeOperated(userId: Long, startDate: LocalDateTime, endDate: LocalDateTime): VolumeOperatedDTO {
        val transactions = transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate)

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
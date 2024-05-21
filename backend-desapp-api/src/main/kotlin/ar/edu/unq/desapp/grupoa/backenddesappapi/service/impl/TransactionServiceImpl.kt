package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.TransactionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.UserRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TransactionServiceImpl : TransactionService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    override fun cancelTransaction(userId: Long, transactionId: Long) {
        val user = userRepository.findById(userId).get()
        val transaction = transactionRepository.findById(transactionId).get()
        transaction.cancelTransaction(user)
        userRepository.save(user)
        transactionRepository.save(transaction)
    }

    override fun deleteAll() {
        transactionRepository.deleteAll()
    }

}
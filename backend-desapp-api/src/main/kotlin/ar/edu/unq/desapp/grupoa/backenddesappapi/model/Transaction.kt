package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import java.util.*

class Transaction (val seller: User, var buyer: User, var cryptocurrency: Cryptocurrency, var amount: Double, var createdAt: Date
){
    var id : Long? = null
    var status: TransactionStatus = TransactionStatus.PENDING
}
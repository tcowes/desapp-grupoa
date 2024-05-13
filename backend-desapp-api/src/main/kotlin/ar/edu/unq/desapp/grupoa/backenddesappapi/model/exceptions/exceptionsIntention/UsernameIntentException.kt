package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention

class UsernameIntentException(userId: Long) :
    Exception("Error: user with id $userId is not registered to make a BUY/SELL Intent")

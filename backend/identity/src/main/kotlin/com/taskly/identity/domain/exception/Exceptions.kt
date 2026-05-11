package com.taskly.identity.domain.exception

class EmailAlreadyUsedException(email: String) :
    RuntimeException("Email '$email' is already registered")

class InvalidCredentialsException :
    RuntimeException("Invalid email or password")

class UserNotFoundException(id: String) :
    RuntimeException("User not found: $id")

class InvalidTokenException(reason: String = "Token is invalid or expired") :
    RuntimeException(reason)

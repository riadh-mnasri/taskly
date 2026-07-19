package com.taskly.identity.application.service

import com.taskly.identity.domain.exception.UserNotFoundException
import com.taskly.identity.domain.port.inbound.CurrentUserView
import com.taskly.identity.domain.port.inbound.GetCurrentUserUseCase
import com.taskly.identity.domain.port.outbound.UserRepository
import com.taskly.sharedkernel.domain.model.UserId

class GetCurrentUserService(
    private val userRepository: UserRepository
) : GetCurrentUserUseCase {

    override fun getCurrentUser(userId: UserId): CurrentUserView {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.toString())

        return CurrentUserView(email = user.email.value)
    }
}

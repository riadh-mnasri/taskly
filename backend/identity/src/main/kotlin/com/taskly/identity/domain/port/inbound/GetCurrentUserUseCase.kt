package com.taskly.identity.domain.port.inbound

import com.taskly.sharedkernel.domain.model.UserId

interface GetCurrentUserUseCase {
    fun getCurrentUser(userId: UserId): CurrentUserView
}

data class CurrentUserView(val email: String)

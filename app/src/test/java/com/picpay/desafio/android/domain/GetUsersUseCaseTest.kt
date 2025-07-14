package com.picpay.desafio.android.domain

import com.picpay.desafio.android.domain.repository.UsersRepository
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetUsersUseCaseTest {

    private val userRepository: UsersRepository = mockk()
    private val getUsersUseCase = GetUsersUseCase(userRepository)

    @Test
    fun `use case's invoke invoke should return flow of users when repository returns users`() = runBlocking {
        // Given
        val expectedUsers = listOf(
            User(img = "http://example.com/user1.png", name = "User One", id = 1, username = "userone"),
            User(img = "http://example.com/user2.png", name = "User Two", id = 2, username = "usertwo")
        )
        every { userRepository.getUsers() } returns flowOf(expectedUsers)

        // When
        val resultFlow = getUsersUseCase.invoke()
        val actualUsers = resultFlow.first()

        // Then
        assertEquals(expectedUsers, actualUsers)
        verify(exactly = 1) { userRepository.getUsers() }
    }

    @Test
    fun `use case's invoke should return empty flow when repository returns empty list`() = runBlocking {
        // Given
        val expectedUsers = emptyList<User>()
        every { userRepository.getUsers() } returns flowOf(expectedUsers)

        // When
        val resultFlow = getUsersUseCase.invoke()
        val actualUsers = resultFlow.first()

        // Then
        assertTrue(actualUsers.isEmpty())
        verify(exactly = 1) { userRepository.getUsers() }
    }

    @Test(expected = RuntimeException::class)
    fun `use case's invoke should throw exception when repository throws exception`() = runBlocking {
        // Given
        val errorMessage = "Network error"
        every { userRepository.getUsers() } throws RuntimeException(errorMessage)

        // When
        getUsersUseCase().first()

        // Then
        verify(exactly = 1) { userRepository.getUsers() }
    }
}

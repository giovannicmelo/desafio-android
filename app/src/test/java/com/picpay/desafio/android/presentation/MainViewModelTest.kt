package com.picpay.desafio.android.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import com.picpay.desafio.android.presentation.action.MainViewAction
import com.picpay.desafio.android.presentation.state.MainViewState
import com.picpay.desafio.android.presentation.viewmodel.MainViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val getUsersUseCase: GetUsersUseCase = mockk()
    private val stateObserver: Observer<MainViewState> = mockk(relaxed = true)
    private val errorObserver: Observer<MainViewAction> = mockk(relaxed = true)
    private val viewModel = MainViewModel(getUsersUseCase, testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel.state.observeForever(stateObserver)
        viewModel.action.observeForever(errorObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
        viewModel.state.removeObserver(stateObserver)
        viewModel.action.removeObserver(errorObserver)
    }

    @Test
    fun `getUsers should update state with users when use case returns success`() = testDispatcher.runBlockingTest {
        // Given
        val users = listOf(User("img", "name", 1, "username"))
        every { getUsersUseCase() } returns flowOf(users)

        // When
        viewModel.getUsers()

        // Then
        verify { stateObserver.onChanged(MainViewState(isLoading = true)) }
        verify { stateObserver.onChanged(MainViewState(users = users, isLoading = false)) }
    }

    @Test
    fun `getUsers should update error when use case returns error`() = testDispatcher.runBlockingTest {
        // Given
        val errorMessage = "Error fetching users"
        every { getUsersUseCase() } returns flow { throw RuntimeException(errorMessage) }

        // When
        viewModel.getUsers()

        // Then
        verify { stateObserver.onChanged(MainViewState(isLoading = true)) }
        verify { errorObserver.onChanged(MainViewAction.ShowErrorMessage(errorMessage)) }
        verify { stateObserver.onChanged(MainViewState(isLoading = false)) }
    }
}

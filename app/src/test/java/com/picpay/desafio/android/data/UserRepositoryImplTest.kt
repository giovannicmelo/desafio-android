package com.picpay.desafio.android.data

import com.google.gson.Gson
import com.picpay.desafio.android.data.api.PicPayService
import com.picpay.desafio.android.data.datasource.UsersDataSource
import com.picpay.desafio.android.data.extension.toDomain
import com.picpay.desafio.android.data.model.UserResponse
import com.picpay.desafio.android.data.repository.UsersRepositoryImpl
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.repository.UsersRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class UsersRepositoryImplTest {

    private val mockWebServer = MockWebServer()
    private val gson = Gson()
    private lateinit var localDataSource: UsersDataSource.Local
    private lateinit var remoteDataSource: UsersDataSource.Remote
    private lateinit var picPayService: PicPayService
    private lateinit var repository: UsersRepository

    @Before
    fun setUp() {
        mockWebServer.start()

        picPayService = createRetrofit(gson)
            .create(PicPayService::class.java)

        remoteDataSource = object : UsersDataSource.Remote {
            override fun getUsers() = flow {
                emit(picPayService.getUsers().map { it.toDomain() })
            }
        }
        localDataSource = mockk(relaxed = true)
        repository = UsersRepositoryImpl(remoteDataSource, localDataSource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getUsers when cache is empty should fetch from remote and save to cache and return remote users`() = runBlocking {
        // Given
        val remoteUserList = listOf(
            UserResponse(img = "img1.png", name = "User 1", id = 1, username = "user1"),
            UserResponse(img = "img2.png", name = "User 2", id = 2, username = "user2")
        )
        val expectedDomainUsers = remoteUserList.map { it.toDomain() }

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(gson.toJson(remoteUserList))
        mockWebServer.enqueue(mockResponse)

        every { localDataSource.getCachedUsers() } returns emptyList()
        justRun { localDataSource.saveUsersInCache(any()) }

        // When
        val result = repository.getUsers().first()

        // Then
        assertEquals(expectedDomainUsers, result)
        assertEquals(1, mockWebServer.requestCount)
        verify(exactly = 1) { localDataSource.getCachedUsers() }
        verify(exactly = 1) { localDataSource.saveUsersInCache(expectedDomainUsers) }
    }

    @Test
    fun `getUsers when cache is not empty should return cached users and not call remote`() = runBlocking {
        // Given
        val cachedUsers = listOf(
            User(img = "img_cache.png", name = "Cached User", id = 10, username = "cacheduser")
        )
        every { localDataSource.getCachedUsers() } returns cachedUsers

        // When
        val result = repository.getUsers().first()

        // Then
        assertEquals(cachedUsers, result)
        assertEquals(0, mockWebServer.requestCount)
        verify (exactly = 1) { localDataSource.getCachedUsers() }
        verify(exactly = 0) { localDataSource.saveUsersInCache(any()) }
    }

    @Test
    fun `getUsers when cache is empty and remote call fails should throw exception and not save to cache`() = runBlocking {
        // Given
        val mockResponse = MockResponse().setResponseCode(500).setBody("Server Error")
        mockWebServer.enqueue(mockResponse)
        var caughtException: Throwable? = null

        every { localDataSource.getCachedUsers() } returns emptyList()

        // When
        try {
            repository.getUsers().toList()
        } catch (e: Exception) {
            caughtException = e
        }

        // Then
        assertTrue(caughtException is retrofit2.HttpException)
        assertEquals(1, mockWebServer.requestCount)
        verify(exactly = 1) { localDataSource.getCachedUsers() }
        verify(exactly = 0) { localDataSource.saveUsersInCache(any()) }
    }

    private fun createRetrofit(gson: Gson): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}

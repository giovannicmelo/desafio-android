package com.picpay.desafio.android.presentation.activity

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.picpay.desafio.android.R
import com.picpay.desafio.android.presentation.adapter.UserListAdapter
import com.picpay.desafio.android.presentation.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    private val adapter: UserListAdapter by lazy { UserListAdapter() }
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        configureRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUsers()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.user_list_progress_bar)
    }

    private fun configureRecyclerView() {
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        viewModel.state.observe(this, Observer { state ->
            progressBar?.isVisible = state.isLoading
            adapter.users = state.users
        })

        viewModel.error.observe(this, Observer { error ->
            val message = getString(R.string.error)

            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)
                .show()

            Log.e(this@MainActivity::class.java.simpleName, error)
        })
    }
}

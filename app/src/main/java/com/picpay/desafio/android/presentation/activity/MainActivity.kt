package com.picpay.desafio.android.presentation.activity

import android.util.Log
import android.view.View
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: UserListAdapter

    private val viewModel: MainViewModel by viewModel()

    override fun onResume() {
        super.onResume()

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.user_list_progress_bar)

        adapter = UserListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        progressBar.visibility = View.VISIBLE

        viewModel.state.observe(this, Observer { state ->
            progressBar.isVisible = state.isLoading
            adapter.users = state.users
        })

        viewModel.error.observe(this, Observer { error ->
            val message = getString(R.string.error)

            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE

            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)
                .show()

            Log.e(this@MainActivity::class.java.simpleName, error)
        })
    }
}

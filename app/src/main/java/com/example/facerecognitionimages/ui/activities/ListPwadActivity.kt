package com.example.facerecognitionimages.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.facerecognitionimages.R
import com.example.facerecognitionimages.data.services.PwadService
import com.example.facerecognitionimages.databinding.ActivityListPwadBinding
import com.example.facerecognitionimages.ui.adapters.PwadAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ListPwadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListPwadBinding
    private lateinit var pwadAdapter: PwadAdapter
    private val pwadService: PwadService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListPwadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerView()
        fetchData()

    }

    private fun initRecyclerView(){
        binding.pwadRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pwadRecyclerView.setHasFixedSize(false)

        pwadAdapter = PwadAdapter(mutableListOf()) { pwad ->
            val intent = Intent(this@ListPwadActivity, PwadDetailsActivity::class.java)
            intent.putExtra("pwad", pwad.id)
            startActivity(intent)
        }

        binding.pwadRecyclerView.adapter = pwadAdapter
    }

    private fun fetchData(){
        pwadAdapter.pwadList.clear()
        CoroutineScope(Dispatchers.Main).launch {
            val pwads = pwadService.listPwads()
//            Toast.makeText(this@ListPwadActivity, pwads.joinToString { x -> x.carefulToken }, Toast.LENGTH_LONG).show()
            pwadAdapter.pwadList.addAll(pwads)
            pwadAdapter.notifyDataSetChanged()


        }
    }
}
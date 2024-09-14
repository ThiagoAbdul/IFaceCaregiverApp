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
import com.example.facerecognitionimages.databinding.ActivityListknownPersonsBinding
import com.example.facerecognitionimages.ui.adapters.KnownPersonAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ListKnownPersonsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListknownPersonsBinding
    private lateinit var adapter: KnownPersonAdapter
    private lateinit var pwadId: String
    private val pwadService: PwadService by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListknownPersonsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pwadId = intent.extras?.getString("pwad")!!

        initRecyclerView()

        fetchData()
    }

    private fun initRecyclerView(){
        adapter = KnownPersonAdapter { knownPerson ->
            val intent = Intent(this@ListKnownPersonsActivity, KnownPersonsDetailsActivity::class.java)

            intent.putExtra("knownPerson", knownPerson.id)
            startActivity(intent)
        }

        binding.knownPersonRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.knownPersonRecyclerView.setHasFixedSize(false)
        binding.knownPersonRecyclerView.adapter = adapter
    }

    private fun fetchData(){
        CoroutineScope(Dispatchers.Main).launch {
            val knownPersonsList = pwadService.listKnwonPersonByPwadId(pwadId)
            adapter.knownPeopleList.addAll(knownPersonsList)
            adapter.notifyDataSetChanged()
        }
    }
}
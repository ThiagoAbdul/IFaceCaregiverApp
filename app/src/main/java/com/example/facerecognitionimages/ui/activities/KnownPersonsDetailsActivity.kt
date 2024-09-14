package com.example.facerecognitionimages.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.facerecognitionimages.R
import com.example.facerecognitionimages.RegisterActivity
import com.example.facerecognitionimages.data.models.KnownPersonResponse
import com.example.facerecognitionimages.data.services.KnownPersonService
import com.example.facerecognitionimages.data.services.PwadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class KnownPersonsDetailsActivity : AppCompatActivity() {

    private var knownPerson: KnownPersonResponse? = null
    private val knownPersonService: KnownPersonService by inject()
    private lateinit var tvKnownPersonName: TextView
    private lateinit var btnAddImage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_knwon_persons_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvKnownPersonName = findViewById(R.id.tv_kwnown_person_details_name)
        btnAddImage = findViewById(R.id.btnKnownPersonDetailsAddImage)

        val knownPersonId: String = intent.extras?.getString("knownPerson")!!

        btnAddImage.setOnClickListener {
            val intent = Intent(this@KnownPersonsDetailsActivity, RegisterActivity::class.java)
            intent.putExtra("knownPerson", knownPersonId)
            startActivity(intent)
        }

        fetchKnownPerson(knownPersonId)

    }

    private fun fetchKnownPerson(knownPersonId: String){
        CoroutineScope(Dispatchers.Main).launch {
            knownPersonService.getKnownPersonById(knownPersonId).also { knownPerson ->
                this@KnownPersonsDetailsActivity.knownPerson = knownPerson
                if(knownPerson != null){
                    tvKnownPersonName.text = knownPerson.person.firstName
                }

            }

        }
    }
}
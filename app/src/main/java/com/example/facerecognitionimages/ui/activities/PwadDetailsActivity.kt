package com.example.facerecognitionimages.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.facerecognitionimages.R
import com.example.facerecognitionimages.data.models.PwadResponse
import com.example.facerecognitionimages.data.services.PwadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PwadDetailsActivity : AppCompatActivity() {

    private lateinit var pwad: PwadResponse
    private lateinit var tvPwadName: TextView
    private lateinit var btnViewKnownPersons: Button
    private val pwadService: PwadService by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pwad_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvPwadName = findViewById(R.id.tv_pwad_details_name)
        btnViewKnownPersons = findViewById(R.id.btn_pwad_known_persons)

        val pwadId: String = intent.extras?.getString("pwad")!!

        btnViewKnownPersons.setOnClickListener {
            val intent = Intent(this, ListKnownPersonsActivity::class.java)
            intent.putExtra("pwad", pwadId)
            startActivity(intent)
        }

        fetchPwad(pwadId)
    }

    private fun fetchPwad(id: String){
        CoroutineScope(Dispatchers.Main).launch {
            val _pwad: PwadResponse? = pwadService.getPwadById(id)
            if(_pwad != null){
                pwad = _pwad
                tvPwadName.text = pwad.person.firstName
            }
        }
    }
}
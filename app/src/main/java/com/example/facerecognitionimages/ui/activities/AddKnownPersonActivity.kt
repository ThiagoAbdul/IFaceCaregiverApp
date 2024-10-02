package com.example.facerecognitionimages.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.facerecognitionimages.R
import com.example.facerecognitionimages.data.models.AddKnownPersonRequest
import com.example.facerecognitionimages.data.services.PwadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddKnownPersonActivity : AppCompatActivity() {

    private val pwadService: PwadService by inject()
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etDescription: EditText
    private lateinit var pwadId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_known_person)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pwadId = intent.extras?.getString("pwad")!!

        etFirstName = findViewById(R.id.et_add_known_person_first_name)
        etLastName = findViewById(R.id.et_add_known_person_last_name)
        etDescription = findViewById(R.id.et_add_known_person_description)

        findViewById<Button>(R.id.btn_save_known_person).setOnClickListener {

            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val description = etDescription.text.toString()

            val request = AddKnownPersonRequest(firstName, lastName, description)

            CoroutineScope(Dispatchers.Main).launch {
                val addKnownPerson = pwadService.addKnownPerson(pwadId, request)
                goToListKnownPersons()

            }

        }


    }

    fun goToListKnownPersons(){
        val intent = Intent(this@AddKnownPersonActivity, ListKnownPersonsActivity::class.java)
        intent.putExtra("pwad", pwadId)
        startActivity(intent)
    }
}
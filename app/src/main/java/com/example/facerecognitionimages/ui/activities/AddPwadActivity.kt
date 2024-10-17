package com.example.facerecognitionimages.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.facerecognitionimages.MainActivity
import com.example.facerecognitionimages.R
import com.example.facerecognitionimages.data.models.PwadResponse
import com.example.facerecognitionimages.data.models.RegisterPwadRequest
import com.example.facerecognitionimages.data.services.PwadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddPwadActivity : AppCompatActivity() {

    lateinit var etFirstName: EditText
    lateinit var etLastName: EditText
    lateinit var btnSave: Button
    private val pwadService: PwadService by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()

        setContentView(R.layout.activity_add_pwad)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etFirstName = findViewById(R.id.et_pwad_first_name)
        etLastName = findViewById(R.id.et_pwad_last_name)
        btnSave = findViewById(R.id.btn_save)

        btnSave.setOnClickListener {

            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val request = RegisterPwadRequest(firstName, lastName)

            CoroutineScope(Dispatchers.Main).launch {
                val addPwad = pwadService.addPwad(request)
                showResult(addPwad)
                delay(750)

                startActivity(
                    Intent(this@AddPwadActivity, MainActivity::class.java)
                )
            }


        }




    }

    fun showResult(response: PwadResponse){
        Toast.makeText(this, response.carefulToken, Toast.LENGTH_LONG).show()
    }
}


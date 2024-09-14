package com.example.facerecognitionimages

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.facerecognitionimages.ui.activities.AddPwadActivity
import com.example.facerecognitionimages.ui.activities.ListPwadActivity

class MainActivity : AppCompatActivity() {
    lateinit var registerBtn: Button
    lateinit var recognizeBtn: Button
    lateinit var addPwadBtn: Button
    lateinit var listPwadBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerBtn = findViewById(R.id.buttonregister)
        recognizeBtn = findViewById(R.id.buttonrecognize)
        addPwadBtn = findViewById(R.id.buttonaddpwad)
        listPwadBtn = findViewById(R.id.buttonlistpwads)


        registerBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    RegisterActivity::class.java
                )
            )
        }

        recognizeBtn.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, RecognitionActivity::class.java)
            )
        }

        addPwadBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddPwadActivity::class.java))
        }

        listPwadBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, ListPwadActivity::class.java))
        }
    }
}
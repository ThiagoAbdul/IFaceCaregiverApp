package com.example.facerecognitionimages.ui.activities

import android.R.attr.label
import android.R.attr.text
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.facerecognitionimages.R
import com.example.facerecognitionimages.data.models.PwadLocationResponse
import com.example.facerecognitionimages.data.models.PwadResponse
import com.example.facerecognitionimages.data.services.LocationService
import com.example.facerecognitionimages.data.services.PwadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class PwadDetailsActivity : AppCompatActivity() {

    private var pwad: PwadResponse? = null
    private lateinit var tvPwadName: TextView
    private lateinit var btnViewKnownPersons: Button
    private val pwadService: PwadService by inject()
    private val locationService: LocationService by inject()


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
        val carefulToken = intent.extras?.getString("carefulToken")!!

        findViewById<Button>(R.id.btn_copy_careful_token).setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Careful token", carefulToken)
            clipboard.setPrimaryClip(clip)
        }

        btnViewKnownPersons.setOnClickListener {
            val intent = Intent(this, ListKnownPersonsActivity::class.java)
            intent.putExtra("pwad", pwadId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_add_known_person).setOnClickListener {
            val intent = Intent(this@PwadDetailsActivity, AddKnownPersonActivity::class.java)
            intent.putExtra("pwad", pwadId)
            startActivity(intent)
        }

        fetchPwad(pwadId)

        findViewById<Button>(R.id.btn_refresh_location).setOnClickListener {
            loadLocation()
        }
    }

    private fun fetchPwad(id: String){
        CoroutineScope(Dispatchers.Main).launch {
            val _pwad: PwadResponse? = pwadService.getPwadById(id)
            if(_pwad != null){
                pwad = _pwad
                tvPwadName.text = pwad!!.person.firstName
                loadLocation()
            }
        }
    }

    private fun loadLocation(){
        if(pwad == null)
            return

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val lastLocation = locationService.getLastLocation(pwad!!.id)
                if(lastLocation != null)
                    setViews(lastLocation)
            }
        }
        catch (ex: Exception){
            Log.e("ERRO", ex.message.toString())
        }

    }

    private fun setViews(location: PwadLocationResponse){
        findViewById<TextView>(R.id.tv_country).text = location.country
        findViewById<TextView>(R.id.tv_state).text = location.state
        findViewById<TextView>(R.id.tv_city).text = location.city
        findViewById<TextView>(R.id.tv_suburb).text = location.suburb
        findViewById<TextView>(R.id.tv_road).text = location.road
        findViewById<TextView>(R.id.tv_number).text = location.houseNumber
        findViewById<TextView>(R.id.tv_postcode).text = location.postcode



    }
}
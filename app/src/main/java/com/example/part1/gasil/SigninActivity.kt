package com.example.part1.gasil

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.part1.gasil.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject

class SigninActivity: ComponentActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.completeButton.setOnClickListener {
            //requestSignin(binding.idValue.text.toString(), binding.nameValue.text.toString(), binding.pwValue.text.toString())
            auth.createUserWithEmailAndPassword(binding.idValue.text.toString(), binding.pwValue.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        //updateUI(user)
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        //updateUI(null)
                    }
                }
        }
        binding.cancelButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }




    fun requestSignin(ID: String?, NAME: String?, PW: String?) {
        val url = "http://121.128.32.143:3000/process/adduser"

        // JSON형식으로 데이터 통신을 진행
        val testjson = JSONObject()
        try {
            // 데이터를 json형식으로 바꿔 넣어줌
            testjson.put("id", ID)
            testjson.put("name", NAME)
            testjson.put("password", PW)
            val jsonString = testjson.toString() //완성된 json 포맷

            // 전송
            val requestQueue = Volley.newRequestQueue(this@SigninActivity)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, testjson,
                { response ->
                    // 데이터 전달을 끝내고 그 응답을 받을 차례
                    try {
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            ) //서버로 데이터 전달 및 응답 받기에 실패한 경우 아래 코드가 실행됨
            { error -> error.printStackTrace() }
            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            requestQueue.add(jsonObjectRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        /*getDataUiUpdate()*/
    }
}

package com.example.part1.gasil

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.part1.gasil.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject


class LoginActivity: ComponentActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.loginButton.setOnClickListener {
            auth.signInWithEmailAndPassword(binding.idValue.text.toString(), binding.pwValue.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        //updateUI(user)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        //updateUI(null)
                    }
                }
        }

        binding.signinButton.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {

        }
    }

    fun reqLogin(ID: String?, PW: String?) {
        val url = "http://211.104.4.132:3000"

        //JSON형식으로 데이터 통신을 진행
        val testjson = JSONObject()
        try {
            testjson.put("id", ID)
            testjson.put("password", PW)
            val jsonString = testjson.toString() //완성된 json 포맷

            //json request 전송
            val requestQueue = Volley.newRequestQueue(this@LoginActivity)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, testjson,
                { response ->

                    //데이터 전달을 끝내고 이제 그 응답을 받을 차례
                    try {
                        //받은 json형식의 응답을 받아
                        val jsonObject = JSONObject(response.toString())

                        //key값에 따라 value값을 쪼개 받아옴
                        val resultId = jsonObject.getString("approve_id")
                        val resultPassword = jsonObject.getString("approve_pw")
                        if ((resultId == "OK") and (resultPassword == "OK")) {
                            Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            easyToast("로그인 실패")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            ) //서버로 데이터 전달 및 응답 받기에 실패한 경우 아래 코드가 실행됩니다.
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

    fun easyToast(str: String?) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        /*getDataUiUpdate()*/
    }
}

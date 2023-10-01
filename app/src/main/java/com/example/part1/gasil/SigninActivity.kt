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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject

class SigninActivity: ComponentActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.completeButton.setOnClickListener {
            if (binding.nameValue.text.toString() == "")
                Toast.makeText(this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            else if (binding.idValue.text.toString() == "")
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            else if (binding.pwValue.text.toString() == "")
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            else {
                auth.createUserWithEmailAndPassword(
                    binding.idValue.text.toString(),
                    binding.pwValue.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            //updateUI(user)

                            // firestore에 저장
                            db = FirebaseFirestore.getInstance()
                            val data = hashMapOf(
                                "name" to binding.nameValue.text.toString(),
                                "email" to binding.idValue.text.toString(),
                                "pw" to binding.pwValue.text.toString()
                            )
                            db.collection("Users").document("${binding.idValue.text.toString()}")
                                .set(data)

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
        }

        binding.cancelButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onResume() {
        super.onResume()
        /*getDataUiUpdate()*/
    }
}

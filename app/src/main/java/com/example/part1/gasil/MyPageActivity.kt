package com.example.part1.gasil

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.part1.gasil.databinding.ActivityMyPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class MyPageActivity: ComponentActivity() {
    private lateinit var binding: ActivityMyPageBinding
    private lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        var userName: String? = null

        /*val user = auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl
            val emailVerified = it.isEmailVerified
            val uid = it.uid
        }*/

        var userEmail = auth.currentUser?.email.toString()

        db.collection("Users").document(userEmail).get().addOnSuccessListener {
            userName = it.getString("name")
        }
        binding.userNameTextView.text = userName

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.addGroupButton.setOnClickListener {
            val intent = Intent(this, AddGroupActivity::class.java)
            startActivity(intent)
        }

        binding.settingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        //title = ""

        val mypage = arrayOf("프로필 변경", "모임 지출 내역 통계", "내 지출 내역 통계")

        val list = binding.settingListView

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, mypage)
        list.adapter = adapter

        list.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                Toast.makeText(
                    applicationContext,
                    mypage[position],
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    override fun onResume() {
        super.onResume()
        /*getDataUiUpdate()*/
    }
}
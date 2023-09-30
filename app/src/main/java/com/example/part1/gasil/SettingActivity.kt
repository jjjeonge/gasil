package com.example.part1.gasil

import android.R
import android.content.Intent
import androidx.activity.ComponentActivity
import com.example.part1.gasil.databinding.ActivitySettingBinding
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class SettingActivity: ComponentActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val user = Firebase.auth.currentUser

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.addGroupButton.setOnClickListener {
            val intent = Intent(this, AddGroupActivity::class.java)
            startActivity(intent)
        }

        binding.myPageButton.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        val setting = arrayOf("회원 정보", "비밀번호 변경", "로그아웃")

        val list = binding.settingListView

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, setting)
        list.adapter = adapter

        list.onItemClickListener =
             OnItemClickListener { parent, view, position, id ->
                if (position == 2) auth.signOut()
                 val intent = Intent(this, LoginActivity::class.java)
                 startActivity(intent)
                Toast.makeText(
                    applicationContext,
                    setting[position],
                    Toast.LENGTH_SHORT
                ).show()



            }
    }

    override fun onResume() {
        super.onResume()
        /*getDataUiUpdate()*/
    }
}
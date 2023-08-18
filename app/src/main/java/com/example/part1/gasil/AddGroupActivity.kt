package com.example.part1.gasil

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.part1.gasil.databinding.ActivityAddGroupBinding

class AddGroupActivity: ComponentActivity() {
    private lateinit var binding: ActivityAddGroupBinding

    override fun onCreate(savedInstantState: Bundle?) {
        super.onCreate(savedInstantState)
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.myPageButton.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        binding.settingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        /*getDataUiUpdate()*/
    }
}
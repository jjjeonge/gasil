package com.example.part1.gasil

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.part1.gasil.databinding.ActivityMyPageBinding


class MyPageActivity: ComponentActivity() {
    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
package com.example.part1.gasil

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.part1.gasil.databinding.ActivityMainBinding
import com.example.part1.gasil.ui.theme.GasilTheme

class MainActivity: ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var accountAdapter: AccountInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.selectGroupSpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.accounts,
            android.R.layout.simple_list_item_1
        )*/

        binding.addGroupButton.setOnClickListener {
            val intent = Intent(this, AddGroupActivity::class.java)
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

        binding.addListButton.setOnClickListener {
            val intent = Intent(this, AddListActivity::class.java)
            startActivity(intent)
        }

        val dummyList = mutableListOf(
            AccountInfo("2023/08/17", "유수정", "23,000", "출금", "0"),
            AccountInfo("2023/08/17", "이규란", "32,000", "출금", "0"),
            AccountInfo("2023/08/17", "유수정", "13,000", "입금", "0"),
            AccountInfo("2023/08/17", "이규란", "21,000", "입금", "0")
        )

        accountAdapter = AccountInfoAdapter(dummyList)
        binding.accountInfoRecyclerView.apply {
            adapter = accountAdapter
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }

    }

    /*private fun showAlertDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("종료하시겠습니까?")
            setPositiveButton("네") { _, _ ->

            }
            setNegativeButton("아니요", null)
        }.show()
    }*/

    override fun onResume() {
        super.onResume()
        /*getDataUiUpdate()*/
    }
    /*private fun getDataUiUpdate() {
        with(getSharedPreferences(USER_INFORMATION, Context.MODE_PRIVATE)) {
            binding..text = getString(STATE, null)
            binding.birthdateValueTextView.text = getString(BIRTHDATE, null)
            binding.bloodTypeValueTextView.text = getString(BLOOD_TYPE, null)

            binding.warningTextView.isVisible = warning.isNullOrEmpty().not()
            binding.warningValueTextView.isVisible = warning.isNullOrEmpty().not()
            if(warning.isNullOrEmpty()) {
                binding.warningTextView.isVisible = false
                binding.warningValueTextView.isVisible = false
            }
        }
    }*/
}


package com.example.part1.gasil

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.part1.gasil.databinding.ActivityMainBinding

class MainActivity: ComponentActivity(), AccountInfoAdapter.AccountInfoClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var accountAdapter: AccountInfoAdapter
    private var selectedInfo:AccountInfo? = null
    private val updateAddListResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val isUpdated = result.data?.getBooleanExtra("isUpdated", false) ?: false

        if(result.resultCode == RESULT_OK && isUpdated) {
            updateAddList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        binding.selectGroupSpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.accounts,
            android.R.layout.simple_list_item_1
        )

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
            updateAddListResult.launch(intent)
        }

        binding.deleteButton.setOnClickListener {
            delete()
        }

    }

    private fun initRecyclerView() {
        accountAdapter = AccountInfoAdapter(mutableListOf(), this)
        binding.accountInfoRecyclerView.apply {
            adapter = accountAdapter
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            val dividerItemDecoration = DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }

        Thread {
            val list = AppDataBase.getInstance(this)?.accountInfoDao()?.getAll() ?: emptyList()
            accountAdapter.list.addAll(list)
            runOnUiThread{ accountAdapter.notifyDataSetChanged() }
        }.start()
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

    private fun updateAddList() {
        Thread {
            AppDataBase.getInstance(this)?.accountInfoDao()?.getLatestInfo()?.let { info ->
                accountAdapter.list.add(0, info)
                runOnUiThread {
                    accountAdapter.notifyDataSetChanged()
                }
            }
        }.start()
    }

    private fun delete() {
        if(selectedInfo == null) return

        Thread {
            selectedInfo?.let { info ->
                AppDataBase.getInstance(this)?.accountInfoDao()?.delete(info)
                runOnUiThread {
                    accountAdapter.list.remove(info)
                    accountAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "삭제가 완료됐습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun onClick(info: AccountInfo) {
        selectedInfo = info
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


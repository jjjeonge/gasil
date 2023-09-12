package com.example.part1.gasil

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import androidx.activity.ComponentActivity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import com.example.part1.gasil.databinding.ActivityAddListBinding
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import java.text.DecimalFormat


class AddListActivity: ComponentActivity() {
    private lateinit var binding: ActivityAddListBinding
    private val decimalFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dateLayer.setOnClickListener {
            val listner = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                binding.dateValueTextView.text = "$year/${month.inc()}/$dayOfMonth"
            }
            DatePickerDialog(
                this,
                listner,
                2023,
                9,
                1
            ).show()
        }

        binding.completeButton.setOnClickListener {
            complete()
            finish()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun saveData() {
        with(getSharedPreferences(USER_INFORMATION, Context.MODE_PRIVATE).edit()) {
            putString(STATE, binding.statementEditText.text.toString())
            putString(DATE, binding.dateValueTextView.text.toString())
            putString(TYPE, getType())
            apply()
        }

        Toast.makeText(this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getType(): String{
        val type = if(binding.typePlus.isChecked) "입금" else "출금"
        return "$type"
    }

    private fun complete() {
        val date = binding.dateValueTextView.text.toString()
        val user = "f"
        val statement = binding.statementEditText.text.toString()
        val money = decimalFormat.format(binding.moneyEditText.toString().toBigDecimal()).toString()
        val type = (if(binding.typePlus.isChecked) "입금" else "출금").toString()
        val sumValue = (AppDataBase.getInstance(this)?.accountInfoDao()?.sum()).toString()
        val info = AccountInfo(date, user, statement, money, type, sumValue)

        Thread {
            AppDataBase.getInstance(this)?.accountInfoDao()?.insert(info)
            runOnUiThread {
                Toast.makeText(this, "저장을 완료했습니다", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent().putExtra("isUpdated", true)
            setResult(RESULT_OK, intent)
            finish()
        }.start()
    }

}
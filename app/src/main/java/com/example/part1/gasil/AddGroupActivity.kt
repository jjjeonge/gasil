package com.example.part1.gasil

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.part1.gasil.databinding.ActivityAddGroupBinding
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.firebase.firestore.FirebaseFirestore


class AddGroupActivity: ComponentActivity() {
    private lateinit var binding: ActivityAddGroupBinding
    private lateinit var db: FirebaseFirestore

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

        binding.completeButton.setOnClickListener {
            complete()
            finish()
        }

//        binding.colorButton.setOnClickListener {
//            ColorPickerDialog
//                .Builder(this)
//                .setTitle("Pick Theme")
//                .setColorShape(ColorShape.SQAURE)
//                /*.setColorListener{ color, colorHex ->
//                    binding.groupColor.text = "$colorHex"
//                }*/
//                .show()
//
//        }

        val clickbtn: ImageButton = findViewById(R.id.colorButton)
        clickbtn.setOnClickListener {
            MaterialColorPickerDialog
                .Builder(this)        					// Pass Activity Instance
                .setTitle("Pick Theme")           		// Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)   	// Default ColorShape.CIRCLE // Default ColorSwatch._500
                .setColorListener { color, colorHex ->
                    binding.groupColor.text = colorHex
                }
                // Handle Color Selection }
                .show()
        }



    }

    override fun onResume() {
        super.onResume()
        /*getDataUiUpdate()*/
    }

    private fun complete() {
        val groupName = binding.groupName.text.toString()
        val groupColor = binding.groupColor.text.toString()
        val id = (0..10000).random()
        val newGroup = Group(groupName, groupColor, id)
        val data = hashMapOf("color" to groupColor)

        val email = intent.getStringExtra("userEmail")
        db = FirebaseFirestore.getInstance()


        db.collection("Users/${email}/Groups").document("$groupName").set(data)
        val intent = Intent().putExtra("isUpdated", true)
        setResult(RESULT_OK, intent)
        finish()


//        Thread {
//            AppDataBase.getInstance(this)?.groupDao()?.insert(newGroup)
//            runOnUiThread {
//                Toast.makeText(this, "저장을 완료했습니다", Toast.LENGTH_SHORT).show()
//            }
//
//            val intent = Intent().putExtra("isUpdated", true)
//            setResult(RESULT_OK, intent)
//            finish()
//        }.start()
    }
}
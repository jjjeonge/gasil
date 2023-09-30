package com.example.part1.gasil

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.example.part1.gasil.databinding.ActivityDetailBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.File
import java.io.IOException

class DetailActivity: ComponentActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentGroup = intent.getStringExtra("currentGroup")
        val docId = intent.getStringExtra("docId")

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference()
        val pathRef = storageRef.child("${currentGroup}/${docId}")

        try {
            pathRef.downloadUrl.addOnSuccessListener { uri->
                Glide.with(this).load(uri).into(binding.detailImageView)
            }.addOnFailureListener {
                Log.d("error", "오류")
                Log.d("currentGroup", "$currentGroup")
                Log.d("docId", "$docId")
            }
        } catch (e: IOException) {
            Log.d("error", "오류1")
        }

        binding.closeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showTextRecog(bitmap: Bitmap) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            recognizer.process(image)
                .addOnSuccessListener {
                    binding.detailTextView.text = it.text
                }
                .addOnFailureListener {
                    binding.detailTextView.text = it.message
                }
        } catch (e: IOException) {
            Log.d("error", "오류2")
        }
    }

}
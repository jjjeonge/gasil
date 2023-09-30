package com.example.part1.gasil

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import androidx.activity.ComponentActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.TextView
import com.example.part1.gasil.databinding.ActivityAddListBinding
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.part1.ocr_test2.findFloat
import com.example.part1.ocr_test2.firstLine
import com.example.part1.ocr_test2.getDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.IOException
import java.text.DecimalFormat
import java.util.Collections
import java.util.TreeSet
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.telecom.Call
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class AddListActivity: ComponentActivity() {
    private lateinit var binding: ActivityAddListBinding
    private val decimalFormat = DecimalFormat("#,###")
    private lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99
    var IS_CHANGED: Boolean = false

    val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
        .setLanguageHints(listOf("ko", "en"))
        .build()
    private val textDeviceDetector = FirebaseVision.getInstance()
        .getCloudTextRecognizer(options)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference()
        val pathRef = storageRef.child("group1/cdHfVgmjWomW1opeavIP.jfif")

        val camera = binding.cameraButton
        camera.setOnClickListener {
            OpenCamera()
        }

        val picture = binding.galleryButton
        picture.setOnClickListener {
            GetAlbum()
        }


        binding.dateLayer.setOnClickListener {
            val listner = DatePickerDialog.OnDateSetListener { _, year, tempMonth, tempDayOfMonth ->
                val month: String
                val dayOfMonth: String
                if (tempMonth < 10) {
                    month = "0${tempMonth}"
                } else month = "$tempMonth"
                if (tempDayOfMonth < 10) {
                    dayOfMonth = "0${tempDayOfMonth}"
                } else dayOfMonth = "$tempDayOfMonth"
                binding.dateValueTextView.text = "$year${month}$dayOfMonth"
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

    private fun getType(): String {
        val type = if (binding.typePlus.isChecked) "입금" else "출금"
        return "$type"
    }

    private fun complete() {
        //
        val user = Firebase.auth.currentUser
        user?.let {
            val email = it.email
        }
        val currentGroup = intent.getStringExtra("currentGroup")
        val date = binding.dateValueTextView.text.toString().toInt()
        val statement = binding.statementEditText.text.toString()
        //val money = decimalFormat.format(binding.moneyEditText.toString().toBigDecimal()).toString()
        val money = binding.moneyEditText.text.toString().toInt()
        val type = getType()
        val color = intent.getStringExtra("currentGroupColor")
        println(color)
        //val tempInfo = arrayListOf(date, name, statement, money, type, color)
        val tempInfo = hashMapOf(
            "date" to date,
            "statement" to statement,
            "money" to money,
            "type" to type,
            "color" to color,
        )
        //
        Thread {
            db.collection("$currentGroup").add(tempInfo)
                .addOnSuccessListener { docRef ->
                    val docId = docRef.id
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.getReference()
                    val pathRef = storageRef.child("${currentGroup}/${docId}")
                    val img = binding.avatars.drawable.toBitmap()
                    db.collection("$currentGroup").document("$docId")
                        .update("docId", "$docId")
                    db.collection("Users").document("${user?.email}")
                        .get()
                        .addOnSuccessListener {
                            val name = it.get("name").toString()
                            println("이름: $name")
                            db.collection("$currentGroup").document("$docId")
                                .update("user", "$name")
                        }

                    val baos = ByteArrayOutputStream()
                    img.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    var uploadTask = pathRef.putBytes(data)
                    uploadTask.addOnFailureListener {
                        println("업로드 실패")
                    }.addOnSuccessListener { taskSnapshot ->
                        println("업로드 성공")
                    }

                }.addOnFailureListener {
                    Log.d("저장 에러", "저장 에러")
                }
            runOnUiThread {
                Toast.makeText(this, "저장을 완료했습니다", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent().putExtra("isUpdated", true)
            setResult(RESULT_OK, intent)
            finish()
        }.start()

    }

    private fun showTextRecog(bitmap: Bitmap) {
        var text = ""
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            //val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//            val recognizer =
//                TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
//            recognizer.process(image)
            textDeviceDetector.processImage(FirebaseVisionImage.fromBitmap(bitmap))
                .addOnSuccessListener {
                    for (block in it.textBlocks) text += block.text + "\n"
                    val receipts = getReceipts(text)
                    binding.moneyEditText.setText(receipts.total, TextView.BufferType.EDITABLE)
                    binding.statementEditText.setText(
                        receipts.statement,
                        TextView.BufferType.EDITABLE
                    )
                    binding.dateValueTextView.setText(
                        receipts.date,
                        TextView.BufferType.EDITABLE
                    )
                }
        } catch (e: IOException) {
            Log.d("error", "오류2")
        }
    }

    private fun getReceipts(text: String): Receipts {
        val originalResult = text.findFloat()
        if (originalResult.isEmpty()) return Receipts()
        else {
            val receipts = Receipts()
            val totalF = Collections.max(originalResult)
            //val secondLargestF = findSecondLargestFloat(originalResult)
            receipts.total = totalF.toString()
            //receipts.vat = if (secondLargestF == 0.0f) "0" else "%.2f".format(totalF - secondLargestF)
            receipts.date = text.getDate()
            receipts.statement = text.firstLine()
            return receipts
        }
    }

    private fun findSecondLargestFloat(input: ArrayList<Float>?): Float {
        if (input == null || input.isEmpty() || input.size == 1) return 0.0f
        else {
            try {
                val tempSet = HashSet(input)
                val sortedSet = TreeSet(tempSet)
                return sortedSet.elementAt(sortedSet.size - 2)
            } catch (e: Exception) {
                return 0.0f
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }
            }

            STORAGE_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun checkPermission(permissions: Array<out String>, type: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }
        return true
    }

    fun OpenCamera() {
        if (checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itt, CAMERA_CODE)
        }

    }

    val permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE)

        // 권한을 허용하도록 요청
//    private val requestMultiplePermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
//            results.forEach {
//                if (!it.value) {
//                    Toast.makeText(applicationContext, "권한 허용 필요", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//            }
//        }


fun saveFile(fileName:String, mimeType:String, bitmap: Bitmap):Uri?{

        var CV = ContentValues()

        // MediaStore 에 파일명, mimeType 을 지정
        CV.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        // 안정성 검사
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        // MediaStore 에 파일을 저장
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)
        if(uri != null){
            var scriptor = contentResolver.openFileDescriptor(uri, "w")

            val fos = FileOutputStream(scriptor?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                CV.clear()
                // IS_PENDING 을 초기화
                CV.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, CV, null, null)
            }
        }
        return uri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView = findViewById<ImageView>(R.id.avatars)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                CAMERA_CODE -> {
                    if(data?.extras?.get("data") != null){
                        val img = data?.extras?.get("data") as Bitmap
                        val uri = saveFile(RandomFileName(), "image/jpeg", img)
                        imageView.setImageURI(uri)
                        val bitmap = imageView.drawable.toBitmap()
                        showTextRecog(bitmap)
                    }
                }
                STORAGE_CODE -> {
                    val uri = data?.data
                    imageView.setImageURI(uri)
                    val bitmap = imageView.drawable.toBitmap()
                    showTextRecog(bitmap)
                }
            }
        }
    }

    fun RandomFileName() : String{
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
        return fileName
    }

    fun GetAlbum(){
        if(checkPermission(STORAGE, STORAGE_CODE)){
            val itt = Intent(Intent.ACTION_PICK)
            itt.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(itt, STORAGE_CODE)
        }
    }



}
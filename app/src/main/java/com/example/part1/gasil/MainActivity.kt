package com.example.part1.gasil

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.part1.gasil.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.selects.select
import java.lang.reflect.Array
import java.text.DecimalFormat


class MainActivity: ComponentActivity()/*, AccountInfoAdapter.AccountInfoClickListener*/ {
    private lateinit var binding: ActivityMainBinding
    private val decimalFormat = DecimalFormat("#,###")
    private lateinit var accountInfoRecyclerView: RecyclerView
    private lateinit var accountInfoArrayList: ArrayList<AccountInfo>
    private lateinit var accountInfoAdapter: AccountInfoAdapter
    private lateinit var selectedGroup: String
    private lateinit var currentGroupColor: String
    private var selectedInfo:AccountInfo? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private val updateAddListResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val isUpdated = result.data?.getBooleanExtra("isUpdated", false) ?: false

        if(result.resultCode == RESULT_OK && isUpdated) {
            EvantChangeListener()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
          //initRecyclerView()

        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl
            val emailVerified = it.isEmailVerified
            val uid = it.uid
        }

        /*binding.loginButton.setOnClickListener {
            binding.loginButton.isClickable = false
            binding.loginButton.isVisible = false
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }*/


        var groupList = arrayListOf("select group")
        binding.selectGroupSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            groupList
        )

        db = FirebaseFirestore.getInstance()
        db.collection("Users/${user!!.email}/Groups").get()
            .addOnSuccessListener {
                groupList.clear()
                for (doc in it) {
                    if (groupList == null) {
                        groupList = arrayListOf("${doc.id}")
                    } else {
                        groupList += doc.id
                    }
                }
                binding.selectGroupSpinner.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    groupList
                )
            }.addOnFailureListener {
                println("그룹 불러오기 오류")
            }


//        binding.selectGroupSpinner.adapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.accounts,
//            android.R.layout.simple_list_item_1
//        )

        db = FirebaseFirestore.getInstance()

        selectedGroup = binding.selectGroupSpinner.selectedItem.toString()
        //currentGroupColor = db.collection("Users/${user.email}/Groups")

        binding.selectGroupSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedGroup = groupList[p2]
                println("이벤트체인지리스너 실행직전")
                EvantChangeListener()
                println("이벤트체인지리스너 실행완료")
                db.collection("Users/${user!!.email}/Groups")
                    .document("$selectedGroup")
                    .get()
                    .addOnSuccessListener {
                        currentGroupColor = it.get("color").toString()
                        println("색2: $currentGroupColor")
                    }.addOnFailureListener {
                        println(it)
                    }
            }

        }


        if (auth.currentUser != null) {
            db.collection("Users/${user!!.email}/Groups")
                .document("$selectedGroup")
                .get()
                .addOnSuccessListener {
                    currentGroupColor = it.get("color").toString()
                    println("색1: $currentGroupColor")
                }

        }




        binding.addGroupButton.setOnClickListener {
            val intent = Intent(this, AddGroupActivity::class.java)
                .putExtra("userEmail", user!!.email)
            startActivity(intent)
            /*Intent(this, AddGroupActivity::class.java)
                .putExtra("userEmail", user!!.email)
                .let {
                    updateAddGroupResult.launch(it)
                }*/
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
            println(currentGroupColor)
            //val intent =
            Intent(this, AddListActivity::class.java)
                .putExtra("currentGroup", "$selectedGroup")
                .putExtra("currentGroupColor", "$currentGroupColor")
                .let {
                    updateAddListResult.launch(it)
                }
            //startActivity(intent)
        }

//        binding.deleteButton.setOnClickListener {
//            delete()
//            //EvantChangeListener()
//        }

        binding.showDetailButton.setOnClickListener {
            val docId = selectedInfo?.docId
            println(docId)
            val intent = Intent(this, DetailActivity::class.java)
                .putExtra("currentGroup", selectedGroup)
                .putExtra("docId", docId)
            startActivity(intent)
        }


        accountInfoRecyclerView = findViewById(R.id.accountInfoRecyclerView)
        accountInfoRecyclerView.layoutManager = LinearLayoutManager(this)
        accountInfoRecyclerView.setHasFixedSize(true)
        accountInfoRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

        accountInfoArrayList = arrayListOf()
        accountInfoAdapter = AccountInfoAdapter(accountInfoArrayList)
        accountInfoRecyclerView.adapter = accountInfoAdapter

        db = FirebaseFirestore.getInstance()

        db.collection("$selectedGroup").orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty) {
                    for (data in it.documents) {
                        val accountInfo: AccountInfo? = data.toObject(AccountInfo::class.java)
                        if (accountInfo != null) {
                            accountInfoArrayList.add(accountInfo)

                        }
                    }
                    accountInfoRecyclerView.adapter = AccountInfoAdapter(accountInfoArrayList)
                }
            }

        /*accountInfoAdapter.setOnItemClickListener(object : AccountInfoAdapter.OnItemClickListener{
            override fun onItemClick(info: AccountInfo) {
                Intent(this@MainActivity, DetailActivity::class.java).apply {
                    putExtra("docId", info.docId)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }

        })*/

        accountInfoRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                val position = rv.getChildAdapterPosition(child!!)
                selectedInfo = accountInfoArrayList[position]
                Log.d("터치이벤트", "${selectedInfo!!.docId}")


                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }
        })


    }


//    private fun EvantChangeListener() {
//
//        db = FirebaseFirestore.getInstance()
//        db.collection("$selectedGroup").orderBy("date", Query.Direction.DESCENDING)
//            .addSnapshotListener(object: EventListener<QuerySnapshot> {
//                override fun onEvent(
//                    value: QuerySnapshot?,
//                    error: FirebaseFirestoreException?
//                ) {
//                    if (error != null) {
//                        Log.e("Firestore error", error.message.toString())
//                        return
//                    }
//
//                    for (dc: DocumentChange in value?.documentChanges!!) {
//                        if (dc.type == DocumentChange.Type.ADDED) {
//                            accountInfoArrayList.add(dc.document.toObject(AccountInfo::class.java))
//                        } else if (dc.type == DocumentChange.Type.REMOVED) {
//                            accountInfoArrayList.remove(dc.document.toObject(AccountInfo::class.java))
//                        }
//                    }
//                }
//            })
//    }

    private fun EvantChangeListener() {
        println("이벤트체인지리스너 실행직후")
        accountInfoRecyclerView = findViewById(R.id.accountInfoRecyclerView)
        accountInfoRecyclerView.layoutManager = LinearLayoutManager(this)
        accountInfoRecyclerView.setHasFixedSize(true)
        accountInfoRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

        accountInfoArrayList = arrayListOf()
        accountInfoAdapter = AccountInfoAdapter(accountInfoArrayList)
        accountInfoRecyclerView.adapter = accountInfoAdapter

        db = FirebaseFirestore.getInstance()
        db.collection("$selectedGroup").orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty) {
                    for (data in it.documents) {
                        val accountInfo: AccountInfo? = data.toObject(AccountInfo::class.java)
                        if (accountInfo != null) {
                            accountInfoArrayList.add(accountInfo)
                        }
                    }
                    accountInfoRecyclerView.adapter = AccountInfoAdapter(accountInfoArrayList)
                }
            }
        accountInfoAdapter.notifyDataSetChanged()
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

        binding.loginButton.isClickable = false
        binding.loginButton.isVisible = false

        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.loginButton.isClickable = false
            binding.loginButton.isVisible = false
            //initRecyclerView()
            EvantChangeListener()

        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.loginButton.isClickable = false
            binding.loginButton.isVisible = false
            //initRecyclerView()

        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        /*db = FirebaseFirestore.getInstance()
        db.collection("$selectedGroup").orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty) {
                    for (data in it.documents) {
                        val accountInfo: AccountInfo? = data.toObject(AccountInfo::class.java)
                        if (accountInfo != null) {
                            accountInfoArrayList.add(accountInfo)
                        }
                    }
                    accountInfoRecyclerView.adapter = AccountInfoAdapter(accountInfoArrayList)
                }
            }*/
    }

    private fun delete() {
        if(selectedInfo == null) return
//        val date = selectedInfo!!.date
//        val user = selectedInfo!!.user
//        val statement = selectedInfo!!.statement
//        val type = selectedInfo!!.type
//        val money = selectedInfo!!.money

        Thread {
            selectedInfo?.let { info ->
                db.collection("$selectedGroup")
                    .whereEqualTo("date", info.date)
                    .whereEqualTo("user", info.user)
                    .whereEqualTo("statement", info.statement)
                    .whereEqualTo("money", info.money)
                    .get()
                    .addOnSuccessListener {
                        for (document in it) {
                            db.collection("$selectedGroup").document(document.id).delete()
                        }
                    }
                //AppDataBase.getInstance(this)?.accountInfoDao()?.delete(info)
                runOnUiThread {
                    accountInfoAdapter.list.remove(info)
                    accountInfoAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "삭제가 완료됐습니다.", Toast.LENGTH_SHORT).show()
                }
                selectedInfo = null
            }
        }.start()
    }

    /*override fun onClick(info: AccountInfo) {
        selectedInfo = info
        Log.d("selected", "info가 선택되었습니다.")
        Toast.makeText(this, "선택됨", Toast.LENGTH_SHORT).show()
    }*/
}


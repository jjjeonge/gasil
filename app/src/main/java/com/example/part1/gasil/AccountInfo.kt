package com.example.part1.gasil

data class AccountInfo (
    val date : String,
    val user : String,
    val money : String,
    val type : String, //입금 or 지출
    val sumValue : String,
)
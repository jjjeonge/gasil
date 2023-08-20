package com.example.part1.gasil

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accountInfo")
data class AccountInfo (
    val date : String, //날짜
    val user : String, //팀원
    val statement : String, //거래내용
    val money : String, //입출금 금액
    val type : String, //입금 or 지출
    val sumValue : String, //잔액
    @PrimaryKey(autoGenerate= true)val id: Int = 0,
)
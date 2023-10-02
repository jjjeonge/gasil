package com.example.part1.gasil

import androidx.room.Entity
import androidx.room.PrimaryKey

data class AccountInfo (
    var color : String? = "", //사용자 색상
    var date : Int? = 0, //날짜
    var docId : String? = "", //문서 id
    var money : Int? = 0, //입출금 금액
    var statement : String? = "", //거래내용
    var type : String? = "", //입금 or 지출
    var user : String? = "", //팀원
)




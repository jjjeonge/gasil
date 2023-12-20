package com.example.part1.ocr_test2

import java.util.*

//fun String.findFloat(): ArrayList<Float> {
//    //get digits from result
//    if (this.isEmpty()) return ArrayList<Float>()
//    val originalResult = ArrayList<Float>()
//    val matchedResults = Regex(pattern = "[+-]?([0-9]*[.])?[0-9]+").findAll(this)
//    for (txt in matchedResults) {
//        if (txt.value.isFloatAndWhole()) originalResult.add(txt.value.toFloat())
//    }
//    return originalResult
//}

fun String.findFloat(): ArrayList<Int> {
    if (this.isEmpty()) return ArrayList<Int>()
    val originalResult = ArrayList<Int>()
    val matchedResults = Regex(pattern = "([0-9]+[,|.])+[0-9]{3}").findAll(this)
    println(matchedResults)
    for (txt in matchedResults) {
        var tempText = txt.value.split(",", ".")
        var tempNum = ""
        for (num in tempText) {
            tempNum = tempNum.plus(num)
        }
        originalResult.add(tempNum.toInt())

    }
    return originalResult
}

fun String.firstLine(): String {
    if (this.isEmpty()) return ""
    var statement: String = ""
    val list = this.split("\n", " ", ":", "[", "]", "\"", ",")
    println(list)
    for (i in 0 until list.size) {
        if (!(list[i].contains("영수증") || list[i] == "코드" || list[i].contains("신용") ||
                    list[i].contains("카드") || list[i].contains("주문") ||
                    list[i].contains("전표") || list[i].contains("온라인") ||
                    list[i].contains("결제") || list[i].contains("가맹정") ||
                    list[i].contains("매출") || list[i].contains("사업자") ||
                    list[i].contains("거래") || list[i].contains("(주)") ||
                    list[i].contains("발행") || list[i].contains("매장") ||
                    list[i].contains("단말") || list[i].contains("KICC") ||
                    list[i].contains("진동벨") || list[i] == "" ||
                    list[i] =="KOCES" || list[i].contains("가게") ||
                    (list[i] =="Easy" && list[i+1] == "Check") || (list[i] =="Check" && list[i-1] == "Easy") ||
                    list[i].contains("번호") ||
                    (list[i].contains("번") && list[i+1].contains("호")) ||
                    (list[i].contains("호") && list[i-1].contains("번")) ||
                    list[i].contains("주소") ||
                    (list[i].contains("주") && list[i+1].contains("소")) ||
                    (list[i].contains("소") && list[i-1].contains("주")) ||
                    list[i].contains("대표") ||
                    (list[i].contains("대") && list[i+1].contains("표")) ||
                    (list[i].contains("표") && list[i-1].contains("대")) ||
                    list[i].contains("전화") ||
                    (list[i].contains("전") && list[i+1].contains("화")) ||
                    (list[i].contains("화") && list[i-1].contains("전")) ||
                    list[i].contains("상호") ||
                    (list[i].contains("상") && list[i+1].contains("호")) ||
                    (list[i].contains("호") && list[i-1].contains("상")) ||
                    list[i].contains("[0-9]".toRegex()))) {
            statement = list[i]
            break
        }
    }
    println(statement)
    return statement
}

fun String.getDate(): String {
    if (this.isEmpty()) return ""
    var date: String = ""
    val list = this.split("\n", " ", ":", "[", "]")
    for (element in list) {
        if (element.contains("[2][0][0-9]{2}-[0-1][0-9]-[0-3][0-9]\$".toRegex()) ||
            element.contains("[2][0][0-9]{2}/[0-1][0-9]/[0-3][0-9]\$".toRegex()) ||
            element.contains("[2][0][0-9]{2}[,|.][0-1][0-9][,|.][0-3][0-9]\$".toRegex())) {
            var tempDate = element.split("-", "/", ".")
            for (num in tempDate) {
                date = date.plus(num)
            }
            break
        }
    }
    return date
}

//private fun String.isFloatAndWhole() = this.matches("\\d*\\.\\d*".toRegex())

private fun String.isFloat(): Boolean {
    try {
        val d = java.lang.Double.valueOf(this)
        return d != d.toInt().toDouble()
    } catch (e: Exception) {
        return false
    }
}
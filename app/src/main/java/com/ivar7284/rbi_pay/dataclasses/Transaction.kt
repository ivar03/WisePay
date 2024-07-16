package com.ivar7284.rbi_pay.dataclasses

data class Transaction(
    val id: Int,
    val TransactionID: String,
    val sender_phnno: Long,
    val receiver_phno: Long,
    val CustomerID: String,
    val CustomerDOB: String,
    val CustGender: String,
    val CustLocation: String,
    val CustAccountBalance: Double,
    val TransactionDate: String,
    val TransactionTime: Long,
    val TransactionAmount: Double
)

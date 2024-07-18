package com.ivar7284.rbi_pay.dataclasses

data class Transaction(
    val id: Int,
    val transaction_id: String,
    val sender_phnno: String,
    val receiver_phno: String,
    val customer_id: String,
    val customer_dob: String?,
    val customer_gender: String?,
    val customer_location: String,
    val customer_account_balance: Double,
    val transaction_date: String,
    val transaction_time: Long,
    val transaction_amount: Double
)

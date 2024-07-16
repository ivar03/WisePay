package com.ivar7284.rbi_pay.dataclasses

data class SMSMessage(val sender: String, val message: String, var prediction: Int = 0)

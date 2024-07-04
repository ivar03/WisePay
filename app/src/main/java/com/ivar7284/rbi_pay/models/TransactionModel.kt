package com.ivar7284.rbi_pay.models

class TransactionModel {
    lateinit var txn_date : String
    lateinit var txn_med : String
    lateinit var txn_dealer : String
    lateinit var txn_amt : String
    lateinit var txn_amt_cd : String
    var img_txn_way : Int = 0
}
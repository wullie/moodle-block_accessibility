package com.movingchecklist.model

import java.util.UUID

data class Column(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var colorHex: String = "#5E6C84",
    val cards: MutableList<Card> = mutableListOf()
)

package com.movingchecklist.model

import java.util.UUID

data class Card(
    val id: String = UUID.randomUUID().toString(),
    var text: String,
    var priority: Priority = Priority.MEDIUM
)

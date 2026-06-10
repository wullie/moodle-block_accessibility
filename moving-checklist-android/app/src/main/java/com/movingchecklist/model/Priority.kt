package com.movingchecklist.model

enum class Priority(
    val label: String,
    val bgColorHex: String,
    val textColorHex: String,
    val accentColorHex: String
) {
    URGENT("Urgent", "#FFEBE6", "#BF2600", "#FF5630"),
    HIGH("High",    "#FFF0B3", "#974F0C", "#FFAB00"),
    MEDIUM("Medium","#E3FCEF", "#006644", "#36B37E"),
    LOW("Low",      "#DEEBFF", "#0747A6", "#0052CC")
}

package com.movingchecklist.data

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.movingchecklist.model.Card
import com.movingchecklist.model.Column
import com.movingchecklist.model.Priority

class DataManager(context: Context) {

    private val prefs = context.getSharedPreferences("moving_checklist", Context.MODE_PRIVATE)
    private val gson = GsonBuilder().create()

    fun loadColumns(): MutableList<Column> {
        val json = prefs.getString(KEY_COLUMNS, null) ?: return defaultColumns()
        return try {
            val type = object : TypeToken<MutableList<Column>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            defaultColumns()
        }
    }

    fun saveColumns(columns: List<Column>) {
        prefs.edit().putString(KEY_COLUMNS, gson.toJson(columns)).apply()
    }

    private fun defaultColumns(): MutableList<Column> = mutableListOf(
        Column(title = "To Do", colorHex = "#5E6C84", cards = mutableListOf(
            Card(text = "Notify current landlord / give notice",              priority = Priority.URGENT),
            Card(text = "Hire a removal company or rent a van",               priority = Priority.HIGH),
            Card(text = "Book professional cleaners for old property",        priority = Priority.HIGH),
            Card(text = "Redirect Royal Mail post to new address",            priority = Priority.HIGH),
            Card(text = "Update address with bank & financial institutions",  priority = Priority.HIGH),
            Card(text = "Notify HMRC / DVLA of new address",                 priority = Priority.MEDIUM),
            Card(text = "Transfer or cancel broadband & TV package",          priority = Priority.MEDIUM),
            Card(text = "Sort gas, electricity & water for new property",     priority = Priority.HIGH),
            Card(text = "Update electoral roll registration",                 priority = Priority.MEDIUM),
            Card(text = "Arrange contents & building insurance",              priority = Priority.HIGH),
            Card(text = "Measure new rooms for furniture",                    priority = Priority.MEDIUM),
            Card(text = "Collect packing boxes and supplies",                 priority = Priority.MEDIUM),
            Card(text = "Take meter readings at old property",                priority = Priority.URGENT),
            Card(text = "Return old property keys to landlord/agent",         priority = Priority.HIGH),
            Card(text = "Update GP, dentist and NHS records",                 priority = Priority.MEDIUM),
            Card(text = "Transfer children's school places if needed",        priority = Priority.URGENT),
            Card(text = "Cancel or move any regular deliveries",              priority = Priority.LOW),
            Card(text = "Label boxes by room",                                priority = Priority.LOW)
        )),
        Column(title = "In Progress", colorHex = "#0052CC", cards = mutableListOf(
            Card(text = "Declutter and sell / donate unwanted items",         priority = Priority.MEDIUM),
            Card(text = "Pack non-essential items",                           priority = Priority.MEDIUM),
            Card(text = "Research the new area (schools, shops, transport)",  priority = Priority.LOW)
        )),
        Column(title = "Done", colorHex = "#36B37E", cards = mutableListOf()),
        Column(title = "Blocked / Waiting", colorHex = "#FF5630", cards = mutableListOf(
            Card(text = "Solicitor / conveyancer confirmation of completion date", priority = Priority.URGENT)
        ))
    )

    companion object {
        private const val KEY_COLUMNS = "columns"
    }
}

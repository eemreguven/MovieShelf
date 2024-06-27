package com.mrguven.movieshelf.utils

import androidx.core.net.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class FormatDate {
    companion object {
        fun formatDate(inputText: String): String {
            return if (inputText.isNotEmpty()) {
                try {
                    val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val date = inputFormatter.parse(inputText)
                    outputFormatter.format(date!!)
                } catch (e: ParseException) {
                    "No Date"
                }
            } else {
                "No Date"
            }
        }
    }
}

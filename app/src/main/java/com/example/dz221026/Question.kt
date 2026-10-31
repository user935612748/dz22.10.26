package com.example.dz221026

enum class QuestionType {
    SPINNER,
    RADIO,
    CHECKBOX,
}

data class Question(
    val id: Int,
    val questionText: String,
    val type: QuestionType,
    val options: List<String>,
    val correctAnswer: String,
)

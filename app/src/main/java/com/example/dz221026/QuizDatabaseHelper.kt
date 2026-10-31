package com.example.dz221026

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class QuizDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "quiz.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_QUESTIONS = "questions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TEXT = "question_text"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_OPTIONS = "options"
        private const val COLUMN_CORRECT = "correct_answer"
        const val DELIMITER = "|||"
    }

    override fun onCreate(db: SQLiteDatabase) {
        recreateTableAndInsertQuestions(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        recreateTableAndInsertQuestions(db)
    }

    private fun recreateTableAndInsertQuestions(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")

        val createTableQuery = """
            CREATE TABLE $TABLE_QUESTIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TEXT TEXT NOT NULL,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_OPTIONS TEXT NOT NULL,
                $COLUMN_CORRECT TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
        insertInitialQuestions(db)
    }

    private fun insertInitialQuestions(db: SQLiteDatabase) {
        val questions = listOf(
            Question(
                id = 1,
                questionText = "В строчке кода Kotlin представлена общая форма (синтаксис) объявления переменной, предназначенной только для чтения:\n\nval VarName : Type\n\nВыберите из выпадающего списка правильный ответ:",
                type = QuestionType.SPINNER,
                options = listOf(
                    "с частичной инициализацией",
                    "без начальной инициализации",
                    "с инициализацией по мере объявления переменной",
                    "с начальной инициализацией",
                ),
                correctAnswer = "без начальной инициализации",
            ),
            Question(
                id = 2,
                questionText = "Какой вариант кода на Kotlin соответствует императивной реализации цикла?\n\nВариант 1:\nprivate fun cosFixpoint(): Double {\n    var x = 1.0\n    while (true) {\n        val y = Math.cos(x)\n        if (x == y) return y\n        x = y\n    }\n}\n\nВариант 2:\ntailrec fun cosFixpoint(x: Double = 1.0): Double {\n    val r = Math.cos(x)\n    return if (x == r) x else cosFixpoint(r)\n}\n\nВыберите один правильный ответ:",
                type = QuestionType.RADIO,
                options = listOf(
                    "оба варианта",
                    "Вариант 1",
                    "Вариант 2",
                    "ни один вариант",
                ),
                correctAnswer = "Вариант 1",
            ),
            Question(
                id = 3,
                questionText = "___ подразумевает разбиение функции многих переменных в последовательность вызовов нескольких функций одного аргумента. Ниже представлен пример его использования в Kotlin:\n\nimport org.funktionale.currying.*\nval sum2ints = { x: Int, y: Int -> x + y }\nval curried: (Int) -> (Int) -> Int = sum2ints.curried()\nassertEquals(curried(2)(4), 6)\nval add5 = curried(5)\nassertEquals(add5(7), 12)\n\nВыберите из выпадающего списка правильный ответ:",
                type = QuestionType.SPINNER,
                options = listOf(
                    "Каррирование",
                    "Курирование",
                ),
                correctAnswer = "Каррирование",
            ),
            Question(
                id = 4,
                questionText = "Укажите все верные утверждения о языке Kotlin.\n\nВыберите один или несколько вариантов:",
                type = QuestionType.CHECKBOX,
                options = listOf(
                    "более громоздкий программный код, по сравнению с языками-предшественниками",
                    "поддерживается повышение функциональности любых классов без необходимости описания новых классов",
                    "компилируется в байткод JVM или в JScript посредством LLVM",
                    "Kotlin можно интегрировать с Maven, Gradle и другими системами сборки",
                ),
                correctAnswer = "поддерживается повышение функциональности любых классов без необходимости описания новых классов" +
                        DELIMITER + "компилируется в байткод JVM или в JScript посредством LLVM" +
                        DELIMITER + "Kotlin можно интегрировать с Maven, Gradle и другими системами сборки",
            ),
            Question(
                id = 5,
                questionText = "Как называется подход к описанию функций, который позволяет зафиксировать некоторые параметры функции и получить функцию с меньшим количеством аргументов (Kotlin)?\n\nВыберите из выпадающего списка правильный ответ:",
                type = QuestionType.SPINNER,
                options = listOf(
                    "частичное применение",
                    "полное применение",
                ),
                correctAnswer = "частичное применение",
            ),
        )

        for ((_, questionText, type, options, correctAnswer) in questions) {
            val values = ContentValues().apply {
                put(COLUMN_TEXT, questionText)
                put(COLUMN_TYPE, type.name)
                put(COLUMN_OPTIONS, options.joinToString(DELIMITER))
                put(COLUMN_CORRECT, correctAnswer)
            }
            db.insert(TABLE_QUESTIONS, null, values)
        }
    }

    fun getAllQuestions(): List<Question> {
        val db = writableDatabase

        recreateTableAndInsertQuestions(db)

        val list = mutableListOf<Question>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_QUESTIONS ORDER BY $COLUMN_ID ASC", null)

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(COLUMN_ID)
            val textIndex = c.getColumnIndexOrThrow(COLUMN_TEXT)
            val typeIndex = c.getColumnIndexOrThrow(COLUMN_TYPE)
            val optionsIndex = c.getColumnIndexOrThrow(COLUMN_OPTIONS)
            val correctIndex = c.getColumnIndexOrThrow(COLUMN_CORRECT)

            while (c.moveToNext()) {
                val id = c.getInt(idIndex)
                val text = c.getString(textIndex)
                val typeStr = c.getString(typeIndex)
                val optionsStr = c.getString(optionsIndex)
                val correct = c.getString(correctIndex)

                val type = try {
                    QuestionType.valueOf(typeStr)
                } catch (_: IllegalArgumentException) {
                    QuestionType.RADIO
                }

                val options = if (optionsStr.isEmpty()) {
                    emptyList()
                } else {
                    optionsStr.split(DELIMITER)
                }

                list.add(
                    Question(
                        id = id,
                        questionText = text,
                        type = type,
                        options = options,
                        correctAnswer = correct,
                    ),
                )
            }
        }
        return list
    }
}

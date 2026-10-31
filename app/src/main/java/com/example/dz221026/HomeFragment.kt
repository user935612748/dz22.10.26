package com.example.dz221026

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dz221026.databinding.FragmentHomeBinding
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: QuizDatabaseHelper
    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var userScore = 0
    private var username = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = arguments?.getString("username") ?: getString(R.string.login)
        binding.welcomeText.text = getString(R.string.welcome_user, username)

        dbHelper = QuizDatabaseHelper(requireContext())
        questions = dbHelper.getAllQuestions()

        if (questions.isNotEmpty()) {
            currentIndex = 0
            userScore = 0
            displayQuestion(questions[currentIndex])
        }

        binding.nextButton.setOnClickListener {
            if (questions.isEmpty()) return@setOnClickListener

            val currentQuestion = questions[currentIndex]
            val isAnswerGiven = checkAndEvaluateAnswer(currentQuestion)

            if (!isAnswerGiven) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_select_answer),
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }

            if (currentIndex < (questions.size - 1)) {
                currentIndex++
                displayQuestion(questions[currentIndex])
            } else {
                val bundle = Bundle().apply {
                    putString("username", username)
                    putInt("score", userScore)
                    putInt("total", questions.size)
                }
                findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
            }
        }

        binding.logoutButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }

    private fun displayQuestion(question: Question) {
        binding.progressText.text =
            getString(R.string.question_progress, currentIndex + 1, questions.size)
        binding.questionText.text = question.questionText

        if (currentIndex == (questions.size - 1)) {
            binding.nextButton.text = getString(R.string.btn_finish_quiz)
        } else {
            binding.nextButton.text = getString(R.string.btn_next_question)
        }

        val container = binding.answerContainer
        container.removeAllViews()

        when (question.type) {
            QuestionType.SPINNER -> {
                val spinner = Spinner(requireContext()).apply {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        question.options,
                    )
                    this.adapter = adapter
                }
                container.addView(spinner)
            }

            QuestionType.RADIO -> {
                val radioGroup = RadioGroup(requireContext())
                for (option in question.options) {
                    val rb = MaterialRadioButton(requireContext()).apply {
                        text = option
                        id = View.generateViewId()
                    }
                    radioGroup.addView(rb)
                }
                container.addView(radioGroup)
            }

            QuestionType.CHECKBOX -> {
                for (option in question.options) {
                    val cb = MaterialCheckBox(requireContext()).apply {
                        text = option
                        id = View.generateViewId()
                    }
                    container.addView(cb)
                }
            }
        }
    }

    private fun checkAndEvaluateAnswer(question: Question): Boolean {
        val container = binding.answerContainer

        when (question.type) {
            QuestionType.SPINNER -> {
                val spinner = container.getChildAt(0) as? Spinner ?: return false
                val selectedOption = spinner.selectedItem?.toString().orEmpty()
                if (selectedOption.isEmpty()) return false

                if (selectedOption == question.correctAnswer) {
                    userScore++
                }
                return true
            }

            QuestionType.RADIO -> {
                val radioGroup = container.getChildAt(0) as? RadioGroup ?: return false
                val selectedId = radioGroup.checkedRadioButtonId
                if (selectedId == -1) return false

                val rb = radioGroup.findViewById<RadioButton>(selectedId)
                val selectedOption = rb?.text?.toString().orEmpty()

                if (selectedOption == question.correctAnswer) {
                    userScore++
                }
                return true
            }

            QuestionType.CHECKBOX -> {
                val selectedOptions = mutableListOf<String>()
                for (i in 0 until container.childCount) {
                    val cb = container.getChildAt(i) as? CheckBox
                    if (cb != null && cb.isChecked) {
                        selectedOptions.add(cb.text.toString())
                    }
                }

                if (selectedOptions.isEmpty()) return false

                val userFormatted = selectedOptions.joinToString(QuizDatabaseHelper.DELIMITER)
                if (userFormatted == question.correctAnswer) {
                    userScore++
                }
                return true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

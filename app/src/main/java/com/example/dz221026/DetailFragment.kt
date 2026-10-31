package com.example.dz221026

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dz221026.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString("username") ?: getString(R.string.login)
        val score = arguments?.getInt("score", 0) ?: 0
        val total = arguments?.getInt("total", 5) ?: 5

        binding.userText.text = getString(R.string.user_prefix, username)
        binding.scoreText.text = getString(R.string.result_score, score, total)

        binding.feedbackText.text = when {
            score == total -> "Отличный результат!"
            score >= 3 -> "Тест пройден!"
            else -> "Попробуйте ещё раз, чтобы улучшить результат!"
        }

        binding.restartButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("username", username)
            }
            findNavController().navigate(R.id.action_detailFragment_to_homeFragment, bundle)
        }

        binding.logoutButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

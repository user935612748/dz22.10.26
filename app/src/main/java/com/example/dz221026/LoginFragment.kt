package com.example.dz221026

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dz221026.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val login = binding.loginEdit.text?.toString()?.trim().orEmpty()
            val password = binding.passwordEdit.text?.toString()?.trim().orEmpty()

            var isValid = true

            if (login.isEmpty()) {
                binding.loginLayout.error = getString(R.string.error_empty_field)
                isValid = false
            } else {
                binding.loginLayout.error = null
            }

            if (password.isEmpty()) {
                binding.passwordLayout.error = getString(R.string.error_empty_field)
                isValid = false
            } else {
                binding.passwordLayout.error = null
            }

            if (isValid) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.welcome_message, login),
                    Toast.LENGTH_SHORT,
                ).show()

                val bundle = Bundle().apply {
                    putString("username", login)
                }
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

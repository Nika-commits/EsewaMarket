package com.example.xml_app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.xml_app.activities.MainActivity
import com.example.xml_app.databinding.FragmentLoginBinding
import com.example.xml_app.viewModel.LoginViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLoginButton()
        observeFormState()
        showResultToast()
    }

    fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            viewModel.login(email, password)
        }
    }

    fun observeFormState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.formState.collect { state ->
                    binding.tilEmail.error = state.emailError
                    binding.tilPassword.error = state.passwordError

                    binding.btnLogin.isEnabled = !state.isLoading

                    binding.loading.isVisible = state.isLoading

                    if (state.isLoading) {
                        binding.btnLogin.text = ""
                    } else {
                        binding.btnLogin.text = "LOGIN"
                    }

                }
            }
        }
    }

    fun showResultToast() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { result ->
                    if (result == true) {
                        Toast.makeText(
                            requireContext(),
                            "Signed In Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        Intent(requireContext(), MainActivity::class.java).also {
                            startActivity(it)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to Sign in", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
package com.example.xml_app.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.xml_app.R
import com.example.xml_app.activities.MainActivity
import com.example.xml_app.databinding.FragmentRegisterBinding
import com.example.xml_app.viewModel.RegisterViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch

class Register : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var credentialManager: CredentialManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        credentialManager = CredentialManager.create(requireContext())

        setupRegisterButton()
        observeFormState()
        showResultToast()
        setupGoogleButton()
    }

    private fun setupRegisterButton() {
        binding.btnSignup.setOnClickListener {
            val fullname = binding.etFullName.text?.toString().orEmpty()
            val username = binding.etUsername.text?.toString().orEmpty()
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()

            viewModel.register(username, email, password)

        }
    }

    private fun setupGoogleButton() {
        binding.btnGoogle.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        context = requireContext(),
                        request = createGoogleSignInRequest()
                    )

                    viewModel.registerWithGoogle(result.credential)
                } catch (e: GetCredentialException) {
                    Log.e("Register", "${e.message}")
                }
            }

        }
    }

    private fun createGoogleSignInRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.google_oauth2_0_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }


    private fun observeFormState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.formState.collect { state ->
                    binding.tilFullName.error = state.fullNameError
                    binding.tilUsername.error = state.usernameError
                    binding.tilEmail.error = state.emailError
                    binding.tilPassword.error = state.passwordError

                    binding.btnSignup.isEnabled = !state.isLoading


                    if (state.isLoading) {
                        binding.btnSignup.visibility = View.GONE
                        binding.btnSignup.isEnabled = false
                        binding.loading.visibility = View.VISIBLE
                    } else {
                        binding.loading.visibility = View.GONE
                        binding.btnSignup.isEnabled = true
                    }
                }
            }
        }
    }

    private fun showResultToast() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { result ->
                    when (result) {
                        true -> {
                            Toast.makeText(
                                requireContext(),
                                "Created User Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            Intent(requireContext(), MainActivity::class.java).also {
                                startActivity(it)
                            }
                        }

                        false -> {
                            Toast.makeText(requireContext(), "Failed to Signup", Toast.LENGTH_SHORT)
                                .show()
                        }

                        null -> {}
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
package com.example.xml_app.activities

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.xml_app.databinding.ActivityAuthBinding
import com.example.xml_app.fragments.LoginFragment
import com.example.xml_app.fragments.Register

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    companion object {
        const val DESTINATION = "destination"
        const val LOGIN = "login"
        const val REGISTER = "register"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            openInitialFragment()
        }
    }

    private fun openInitialFragment() {
        val destination = intent.getStringExtra(DESTINATION)

        val fragment = when (destination) {
            REGISTER -> Register()
            LOGIN -> LoginFragment()
            else -> LoginFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.fvAuthContainer.id, fragment)
            .commit()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
package com.example.vkmobile.ui.password

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.core.view.children
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vkmobile.R
import com.example.vkmobile.databinding.FragmentPasswordBinding

class PasswordFragment : Fragment() {

    companion object {
        private const val SHARED_PREF = "SHARED_PREF"
        private const val PASSWORD = "PASSWORD"
        private const val LOGIN = "LOGIN"
    }

    private val binding by viewBinding(FragmentPasswordBinding::bind)

    private var writtenNumbers = 0
    private var enteredPassword = ""

    private var passwordButtons: MutableList<Button> = mutableListOf()

    private var isSettling = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        if (getLoginFromSharedPref()) {
            binding.textView.text = getString(R.string.enter_password)
            isSettling = false
        } else {
            isSettling = true
        }

        passwordButtons.addAll(listOf(
            binding.btn0,
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5,
            binding.btn6,
            binding.btn7,
            binding.btn8,
            binding.btn9
        ))

        for (v in passwordButtons) {
            v.setOnClickListener {
                passwordButtonListener(it)
            }
        }

        binding.btnDelete.setOnClickListener {
            if (writtenNumbers > 0) {
                writtenNumbers--
                enteredPassword = enteredPassword.dropLast(1)
                updateRadioButtons(writtenNumbers)
                binding.tvError.visibility = View.INVISIBLE
                binding.tvSuccess.visibility = View.INVISIBLE
            }
        }
    }

    private fun passwordButtonListener(view: View) {
        if (writtenNumbers < 4) {
            writtenNumbers++
            updateRadioButtons(writtenNumbers)
            enteredPassword += (view as Button).text

            if (writtenNumbers == 4) {
                if (isSettling) {
                    isSettling = false
                    savePasswordToSharedPref(enteredPassword)
                    setLoginToSharedPref()
                    enteredPassword = ""
                    writtenNumbers = 0
                    updateRadioButtons(writtenNumbers)
                    binding.textView.text = getString(R.string.enter_password)
                } else {
                    updatePasswordEnterResult(enteredPassword == getPasswordFromSharedPref())
                }
            }
        }
    }

    private fun updatePasswordEnterResult(isCorrect: Boolean) {
        binding.tvError.visibility = if (isCorrect) View.INVISIBLE else View.VISIBLE
        binding.tvSuccess.visibility = if (isCorrect) View.VISIBLE else View.INVISIBLE
    }

    private fun updateRadioButtons(count: Int) {
        var ind = 0
        for (btn in binding.radioGroup.children) {
            (btn as RadioButton).isChecked = ind++ < count
        }
    }

    fun setLoginToSharedPref() {
        requireContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit()
            .putBoolean(LOGIN, true)
            .apply()
    }

    fun getLoginFromSharedPref(): Boolean {
        return requireContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
            .getBoolean(LOGIN, false)
    }

    fun savePasswordToSharedPref(password: String) {
        requireContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit()
            .putString(PASSWORD, password)
            .apply()
    }

    fun getPasswordFromSharedPref(): String {
        return requireContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
            .getString(PASSWORD, "").toString()
    }
}
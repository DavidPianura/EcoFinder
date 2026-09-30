package br.edu.ufabc.ecofinder

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ufabc.ecofinder.databinding.LoginActivityBinding

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
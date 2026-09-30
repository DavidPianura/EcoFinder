package br.edu.ufabc.ecofinder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.edu.ufabc.ecofinder.databinding.SignUpScreenFragmentBinding

class SignUpFragment : Fragment() {
    private lateinit var binding: SignUpScreenFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUpScreenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}
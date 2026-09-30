package br.edu.ufabc.ecofinder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.edu.ufabc.ecofinder.databinding.FavoriteEcopontosFragmentBinding

class FavoriteEcopontosFragment: Fragment() {
    private lateinit var binding: FavoriteEcopontosFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FavoriteEcopontosFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}
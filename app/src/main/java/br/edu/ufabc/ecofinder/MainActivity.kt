package br.edu.ufabc.ecofinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import br.edu.ufabc.ecofinder.databinding.ActivityMainBinding
import br.edu.ufabc.ecofinder.view.EcopontosFragment
import br.edu.ufabc.ecofinder.view.FavoriteEcopontosFragment
import br.edu.ufabc.ecofinder.view.ProfileFragment
import br.edu.ufabc.ecofinder.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        replaceFragment(EcopontosFragment())
//        binding.mainBottomNavigationView.isVisible = true
//
//        binding.mainBottomNavigationView.setOnItemSelectedListener {
//            when(it.itemId) {
//                R.id.ecopontos -> replaceFragment(EcopontosFragment())
//                R.id.favorites -> replaceFragment(FavoriteEcopontosFragment())
//                R.id.profile -> replaceFragment(ProfileFragment())
//
//                else -> {}
//
//            }
//            true
//        }

    }

//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.commit {
//            replace(R.id.MainFragmentContainerView, fragment)
//            addToBackStack(null)
//        }
//    }
}


package br.edu.ufabc.ecofinder.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import br.edu.ufabc.ecofinder.databinding.EcopontoItemFragmentBinding
import br.edu.ufabc.ecofinder.model.Ecoponto
import br.edu.ufabc.ecofinder.model.EcopontoAux
import br.edu.ufabc.ecofinder.viewmodel.FirebaseViewModel
import br.edu.ufabc.ecofinder.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class EcopontoItemFragment : Fragment() {
    private lateinit var binding: EcopontoItemFragmentBinding
    private val args: EcopontoItemFragmentArgs by navArgs()
    private val viewModel: MainViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()
    private var id: Long = -1
    private var favorite = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EcopontoItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindEvents()

        try {
            args.ecopontoId.takeIf { it >= 0 }?.also { ecopontoId ->
                id = ecopontoId
                viewModel.isLoading.value = true
                binding.root.visibility = View.INVISIBLE
                val ecoponto = viewModel.getEcopontoById(ecopontoId)

                val ecopontoAUX : EcopontoAux

                if(ecoponto?.cidade == "SA") {
                    Log.d("ECOPONTOITEM", "PEGOU DE SA")
                    ecopontoAUX = viewModel.getEcopontosAuxSA()

                } else {
                    Log.d("ECOPONTOITEM", "PEGOU DE SBC")
                    ecopontoAUX = viewModel.getEcopontosAuxSBC()

                }

                if (ecoponto != null) {
                    favorite = ecoponto.favorite
                    if (ecoponto.favorite) {
                        binding.ImageViewButtonFavorites.setColorFilter(Color.argb(255, 107, 142, 35))
                    } else {
                        binding.ImageViewButtonFavorites.setColorFilter(Color.argb(255, 169, 169, 169))
                    }
                }

                binding.TextViewEcopontoItemTitle.text = ecoponto?.name
                binding.TextViewEcopontoAddress.text = ecoponto?.address
                binding.TextViewEcopontoHorariodeFuncionamentoTime.text = ecoponto?.horarioFunc

                binding.TextViewEcopontosINFO.text = ecopontoAUX.description

                binding.root.visibility = View.VISIBLE
                viewModel.isLoading.value = false
            }
        } catch (e: Exception) {
            Log.e("ECOPONTOITEM", "Failed to create item detail view", e)
            Snackbar.make(view.rootView, "No valid id was provided",
                Snackbar.LENGTH_LONG).show()
            binding.root.visibility = View.INVISIBLE
        }
    }



    private fun abrirMapa() {
        val ecoponto = viewModel.getEcopontoById(id)

        val ecopontoLatitude = ecoponto?.coordinates?.first ?: 0.0
        val ecopontoLongitude = ecoponto?.coordinates?.second ?: 0.0
        val endereco = ecoponto?.address ?: ""

        val uri = Uri.parse("geo:$ecopontoLatitude,$ecopontoLongitude?q=$ecopontoLatitude,$ecopontoLongitude($endereco)")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    private fun addToFavorites() {
        firebaseViewModel.addToFavorites(id).observe(viewLifecycleOwner) { result ->
            when (result.status) {
                is FirebaseViewModel.Status.Success ->{
                    binding.ImageViewButtonFavorites.setColorFilter(Color.argb(255, 107, 142, 35))
                    // test()
                }
                else -> {
                    Log.d("FAVORITE_LIST", "nao adicionou")
                }
            }
        }
    }

    private fun removeFromFavorites() {
        firebaseViewModel.removeFromFavorites(id).observe(viewLifecycleOwner) { result ->
            when (result.status) {
                is FirebaseViewModel.Status.Success -> {
                    binding.ImageViewButtonFavorites.setColorFilter(Color.argb(255, 169, 169, 169))
                }
                else -> {
                    Log.d("FAVORITE_LIST", "nao removeu")
                }
            }
        }
    }

    private fun bindEvents() {

        // ImageView FavoriteStar
        binding.ImageViewButtonFavorites.setOnClickListener {
            if (!favorite)
                addToFavorites()
            else
                removeFromFavorites()
        }

        binding.TextViewEcopontoAddress.setOnClickListener {
            try {
                abrirMapa()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Não foi possível abrir um aplicativo", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

}
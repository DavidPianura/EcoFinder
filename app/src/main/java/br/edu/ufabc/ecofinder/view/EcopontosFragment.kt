package br.edu.ufabc.ecofinder.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.fragment.findNavController
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import br.edu.ufabc.ecofinder.databinding.EcopontosScreenFragmentBinding
import br.edu.ufabc.ecofinder.model.Ecoponto
import br.edu.ufabc.ecofinder.viewmodel.FirebaseViewModel
import br.edu.ufabc.ecofinder.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar


class EcopontosFragment : Fragment(), LifecycleObserver {
    private lateinit var binding: EcopontosScreenFragmentBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var favoriteEcopontosId: List<Long>
    private var favoriteEcopontosObjects = emptyList<Ecoponto>()
    private val REQUEST_LOCATION_PERMISSION = 1
    private var userLatitude = 0.0
    private var userLongitude = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EcopontosScreenFragmentBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return binding.root
    }

    private fun refresh(forceUpdate: Boolean = true) {
        binding.swipeRefreshLayout.isRefreshing = true

        if (forceUpdate) {
            viewModel.allEcopontos().removeObservers(viewLifecycleOwner)
            Log.d("FAVORITE_LIST", "RESETANDO O REFRESH")
        }

        viewModel.allEcopontos().observe(viewLifecycleOwner) { result ->
            when (result.status) {

                is MainViewModel.Status.Success -> {
                    Log.d("ECOPONTOSFRAGMENT", "DISPARANDO REFRESH")
                    val ecopontosList = result.result ?: emptyList()

                    if (favoriteEcopontosId.isNotEmpty()) {

                        ecopontosList.forEach { ecoponto ->
                            ecoponto.favorite = ecoponto.id in favoriteEcopontosId
                        }
                        favoriteEcopontosObjects = ecopontosList.filter { it.favorite }
                    }
                    updateRecyclerView(ecopontosList)
                }
                is MainViewModel.Status.Error -> {}
            }
        binding.swipeRefreshLayout.isRefreshing = false
        }
    }

//    private fun checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_LOCATION_PERMISSION
//            )
//        } else {
//            getLocation()
//        }
//    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return false
        } else {
            getLocation {
                dolocation()
            }
            return true
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation {
                        getLocation {
                            dolocation()
                        }
                    }
                } else {
                    Snackbar.make(binding.root, "Permissão de localização negada", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getLocation(callback: () -> Unit) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLatitude = location.latitude
                    userLongitude = location.longitude

//                    Snackbar.make(
//                        binding.root,
//                        "Latitude: $userLatitude, Longitude: $userLatitude",
//                        Snackbar.LENGTH_SHORT
//                    ).show()
                    callback()
                } else {
                    Snackbar.make(binding.root, "Localização não disponível", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun updateRecyclerView(ecopontosList: List<Ecoponto>) {
        binding.RecyclerViewEcopontosList.apply {
            adapter = EcopontoAdapter(ecopontosList, findNavController())
        }
    }

    private fun getFav(forceUpdate: Boolean = false, callback: () -> Unit) {

        if (forceUpdate) {
            Log.d("FAVORITE_LIST", "FORCANDO UPDATE")
            firebaseViewModel.getFavorites().removeObservers(viewLifecycleOwner)
        }

        firebaseViewModel.getFavorites().observe(viewLifecycleOwner) { result ->
            when (result.status) {
                is FirebaseViewModel.Status.Success -> {
                    favoriteEcopontosId = result.result ?: emptyList()
                    Log.d("FAVORITE_LIST", "Chamando a função de dentro do fragment tem-se os ecopontos: ${result.result ?: emptyList()}")
                    callback()
                }
                is FirebaseViewModel.Status.Error -> {
                    Log.d("FAVORITE_LIST", "ERRO! Chamando a função de dentro do fragment")}
            }
        }
    }

    private fun dolocation() {
        val switchFav = binding.switchShowFavorites
        if(switchFav.isChecked)
            updateRecyclerView(viewModel.getEcopontosOrdenados(userLatitude, userLongitude).filter { it.favorite })
        else
            updateRecyclerView(viewModel.getEcopontosOrdenados(userLatitude, userLongitude))
    }

    private fun bindEvents() {

        val switchFav = binding.switchShowFavorites
        val switchLocation = binding.switchUseLocation

        // Switch dos Favoritos
        switchFav.setOnCheckedChangeListener { _, b ->
            if (b) {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    Snackbar.make(binding.root, "Aguarde o carregamento da lista de Ecopontos", Snackbar.LENGTH_SHORT).show()
                    binding.switchShowFavorites.isChecked = false
                    return@setOnCheckedChangeListener
                }
                // Checando se o switch da localização está On
                if (switchLocation.isChecked)
                    updateRecyclerView(viewModel.getEcopontosOrdenados(userLatitude, userLongitude).filter { it.favorite })
                else
                    updateRecyclerView(favoriteEcopontosObjects)

                binding.imageViewFavoritesSwitch.setColorFilter(Color.argb(255, 255, 0, 0))

            } else {
                if (switchLocation.isChecked)
                    updateRecyclerView(viewModel.getEcopontosOrdenados(userLatitude, userLongitude))
                else
                    updateRecyclerView(viewModel.getEcopontosImediata())

                binding.imageViewFavoritesSwitch.setColorFilter(Color.argb(255, 255, 255, 255))

            }
        }

        // Switch da Localização
        switchLocation.setOnCheckedChangeListener { _, b ->
            if (b) {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    Snackbar.make(binding.root, "Aguarde o carregamento da lista de Ecopontos", Snackbar.LENGTH_SHORT).show()
                    binding.switchUseLocation.isChecked = false
                    return@setOnCheckedChangeListener
                }

                if (checkLocationPermission())
                    binding.imageViewLocationIcon.setColorFilter(Color.argb(255, 255, 0, 0))

            } else {
                updateRecyclerView(viewModel.getEcopontosImediata())
                binding.imageViewLocationIcon.setColorFilter(Color.argb(255, 255, 255, 255))
            }
        }
    }



    override fun onStart() {
        super.onStart()
        Log.d("ECOPONTOSFRAGMENT", "DISPARANDO ONSTART")

        bindEvents()
        getFav(forceUpdate = true) {
            refresh()
        }
         binding.switchUseLocation.isChecked = false
         binding.switchShowFavorites.isChecked = false

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.switchUseLocation.isChecked = false
            binding.switchShowFavorites.isChecked = false
            getFav (forceUpdate = true){
                refresh()
            }
        }
    }

}


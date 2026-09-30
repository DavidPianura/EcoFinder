package br.edu.ufabc.ecofinder.model

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.atomic.AtomicBoolean

class RepositoryFirebaseAuth(private val application: Application) {
    private val db: FirebaseFirestore = Firebase.firestore
    private val isConnected = AtomicBoolean(true)

    companion object {
        private const val ecopontoCollection = "ecoponto_favorites_auth"
        private lateinit var ecopontosId : List<Long>
    }


    data class EcopontoFirestore(
        val id: Long?,
        val name: String?,
        val address: String?,
        val horarioFunc: String?,
        val cidade: String?,
        val favorite: Boolean?,
        val coordinates: Pair<Double, Double>?,
        val user: String? = null
    ) {
        fun toEcoponto() = Ecoponto (
            id = id ?: 0,
            name = name ?: "",
            address = address ?: "",
            horarioFunc = horarioFunc ?: "",
            cidade = cidade ?: "",
            favorite = favorite ?: false,
            coordinates = coordinates
                )

        companion object {
            fun fromEcoponto(ecoponto: Ecoponto, user: String) = EcopontoFirestore (
                id = ecoponto.id,
                name = ecoponto.name,
                address = ecoponto.address,
                horarioFunc = ecoponto.horarioFunc,
                cidade = ecoponto.cidade,
                favorite = ecoponto.favorite,
                coordinates = ecoponto.coordinates,
                user = user
            )
        }
    }
    init {
        application.applicationContext.getSystemService(ConnectivityManager::class.java).apply {
            val connected = getNetworkCapabilities(activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false

            isConnected.set(connected)
            registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isConnected.set(true)
                }

                override fun onLost(network: Network) {
                    isConnected.set(false)
                }
            })
        }
    }


    private fun getSource() = if (isConnected.get()) Source.DEFAULT else Source.CACHE

    private fun getCurrentUser() : String = FirebaseAuth.getInstance().currentUser?.uid
        ?: throw Exception("No user is signed in")

    private fun getCollection() = db.collection(ecopontoCollection)

    suspend fun getUserEcopontoIdFavorites() {
        val favoritos : List<Long>
    }

    suspend fun addToFavorites(ecoponto: Ecoponto): Long = EcopontoFirestore (
        id = ecoponto.id,
        name = ecoponto.name,
        address = ecoponto.address,
        horarioFunc = ecoponto.horarioFunc,
        cidade = ecoponto.cidade,
        favorite = ecoponto.favorite,
        coordinates = ecoponto.coordinates,
        user = getCurrentUser()
            ).let {
                getCollection().add(it)
                it.id ?: throw Exception("Failed to add element with valid id")
    }

}
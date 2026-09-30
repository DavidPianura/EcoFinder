package br.edu.ufabc.ecofinder.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import br.edu.ufabc.ecofinder.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {
    // private val repositoryFirebase = RepositoryFactory(application).create()
    private val repository = RepositoryFirebaseAuth2()

    val isLoading = MutableLiveData(false)

    sealed class Status {
        class Error(val e: Exception) : Status()
        object Success : Status()
    }

    data class EcopontoListResult(
        val result: Unit,
        val status: Status
    )

    data class EcopontoAuxListResult(
        val result: List<EcopontoAux>?,
        val status: Status
    )

    data class FavoriteListResult (
        val result: List<Long>?,
        val status: Status
        )

    fun addToFavorites(id: Long) = liveData {
        Log.d("FIREBASEVIEWMODEL", "Entrou na função")
        try {
            emit(EcopontoListResult(repository.addEcopontoToFavorites(id), Status.Success))
        } catch (e: Exception) {
            Status.Error(Exception("Failed to add to list of favorite ecopontos ID", e))
        }
    }

    fun removeFromFavorites(id: Long) = liveData {
        try {
            emit(EcopontoListResult(repository.removeEcopontoFromFavorites(id), Status.Success))
        } catch (e: Exception) {
            Status.Error(Exception("Failed to remove from the list of favorite ecopontos ID", e))
        }
    }

    fun getFavorites() = liveData {
        try {
            emit(FavoriteListResult(repository.fetchFavoriteEcopontoIds(), Status.Success))
        } catch (e: Exception) {
            Status.Error(Exception("Failed to get the list of favorite ecopontos ID", e))
        }
    }

}
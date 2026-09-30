package br.edu.ufabc.ecofinder.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import br.edu.ufabc.ecofinder.model.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // private val repositoryFirebase = RepositoryFactory(application).create()
    private val repository = Repository(application, application.applicationContext)

    val isLoading = MutableLiveData(false)

    sealed class Status {
        class Error(val e: Exception) : Status()
        object Success : Status()
    }

    data class EcopontoListResult(
        val result: List<Ecoponto>?,
        val status: Status
    )

    data class EcopontoAuxListResult(
        val result: List<EcopontoAux>?,
        val status: Status
    )

    fun allEcopontos() = liveData {
        try {
            emit(EcopontoListResult(repository.bindEcopontos(), Status.Success))
        } catch (e: Exception) {
            Status.Error(Exception("Failed to fetch list of ecopontos", e))
        }
    }

//    fun bindEcopontosAux() = liveData {
//        try {
//            emit(EcopontoAuxListResult(repository.bindEcopontosAux(), Status.Success))
//        } catch (e: Exception) {
//            Status.Error(Exception("Failed to fetch list of ecopontos aux", e))
//        }
//    }

    fun getEcopontoById(id: Long) = repository.getEcopontoById(id)

    fun getEcopontosAuxSA() = repository.ecopontosAux[0]

    fun getEcopontosAuxSBC() = repository.ecopontosAux[1]

    fun getEcopontosOrdenados(userLat:Double, userLong: Double) = repository.getEcopontosOrdenados(userLat, userLong)

    fun getEcopontosImediata() = repository.getEcopontos()

}
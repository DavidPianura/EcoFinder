package br.edu.ufabc.ecofinder.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class RepositoryFirestore {
    private val db = Firebase.firestore

    companion object {
        private const val ecopontosCollection = "ecopontos"
        private const val ecopontoIdDoc = "ecopontoId"

        private object EcopontoDoc {
            const val name = "name"
            const val address = "address"
            const val horarioFunc = "horarioFunc"
        }
    }

//    private data class EcopontoFirebase (
//        val name : String?,
//        val address : String?,
//        val horarioFunc : String?
//        ) {
//        fun toTask() = Ecoponto(
//            name = name ?: "",
//            address = address ?: "",
//            horarioFunc = horarioFunc ?: ""
//        )
//    }

//    private fun getCollection() = db.collection(ecopontosCollection)
//    suspend fun getAll(): List<Ecoponto> = getCollection().get().await().toObjects(EcopontoFirebase::class.java).map { it.toTask()}


}
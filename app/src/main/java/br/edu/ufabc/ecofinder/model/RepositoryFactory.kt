package br.edu.ufabc.ecofinder.model

import android.app.Application

class RepositoryFactory(private val application: Application) {

    enum class Type {
        Firestore,
        FirestoreAuth
    }

//    fun create(type: Type = Type.FirestoreAuth) = when (type){
//        Type.Firestore -> RepositoryFirestore(application)
//
//
//    }
}
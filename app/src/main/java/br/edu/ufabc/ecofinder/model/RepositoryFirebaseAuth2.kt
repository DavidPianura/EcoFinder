package br.edu.ufabc.ecofinder.model

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resumeWithException

class RepositoryFirebaseAuth2 {

    private fun getCurrentUser() : String = FirebaseAuth.getInstance().currentUser?.uid
        ?: throw Exception("No user is signed in")

    fun createUserDocument() {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Users").document(getCurrentUser())

        val userData = hashMapOf(
            "favoriteEcopontos" to arrayListOf<Long>()
        )

        userRef.set(userData, SetOptions.merge())
    }


    fun addEcopontoToFavorites(ecopontoId: Long) {
        Log.d("FAVORITE_LIST", "Adicionando o ID $ecopontoId aos favoritos do user ${getCurrentUser()}")
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Users").document(getCurrentUser())

        // Verifique se o documento do usuário existe
        userRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Adicione o ecoponto aos favoritos do usuário
                userRef.update("favoriteEcopontos", FieldValue.arrayUnion(ecopontoId))
                Log.d("FAVORITE_LIST", "O ID $ecopontoId foi adicionado com sucesso no user ${getCurrentUser()}")
            } else {
                // Crie o documento do usuário e adicione o ecoponto aos favoritos
                createUserDocument()
                userRef.update("favoriteEcopontos", FieldValue.arrayUnion(ecopontoId))
                Log.d("FAVORITE_LIST", "PRIMEIRO CRIOU O DOCUMENTO e O ID $ecopontoId foi adicionado com sucesso no user ${getCurrentUser()}")
            }
        }
    }


    fun removeEcopontoFromFavorites(ecopontoId: Long) {
        try {
            Log.d("FAVORITE_LIST", "REMOVENDO DOS FAVORITOS")
            val db = FirebaseFirestore.getInstance()
            Log.d("FAVORITE_LIST", "REMOVENDO DOS FAVORITOS PEGOU O DB")
            val userRef = db.collection("Users").document(getCurrentUser())
            Log.d("FAVORITE_LIST", "REMOVENDO DOS FAVORITOS PEGOU O USERREF")

            userRef.update("favoriteEcopontos", FieldValue.arrayRemove(ecopontoId))
            Log.d("FAVORITE_LIST", "REMOVIDO!")
        } catch (e: Exception) {
            Log.e("FAVORITE_LIST", "Erro ao remover dos favoritos", e)
        }

    }

    suspend fun fetchFavoriteEcopontoIds(): List<Long> {
        Log.d("FAVORITE_LIST", "Entrou na funcao fetchFavoriteEcopontoIds")
        val db = FirebaseFirestore.getInstance()
        Log.d("FAVORITE_LIST", "Deu getInstance")
        val userRef = db.collection("Users").document(getCurrentUser())
        Log.d("FAVORITE_LIST", "Pegou userRef")

        return try {
            val snapshot = userRef.get().await()
            Log.d("FAVORITE_LIST", "Pegou snapshot")
            val favoriteEcopontos = snapshot.get("favoriteEcopontos") as? List<Long> ?: emptyList()
            Log.d("FAVORITE_LIST", "Os ecopontos favoritos do usuario sao: $favoriteEcopontos")
            favoriteEcopontos
        } catch (e: Exception) {
            Log.e("FAVORITE_LIST", "Erro ao buscar ecopontos favoritos", e)
            emptyList()
        }
    }

}
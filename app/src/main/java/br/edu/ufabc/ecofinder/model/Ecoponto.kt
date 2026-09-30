package br.edu.ufabc.ecofinder.model

data class Ecoponto(
    val id: Long,
    val name: String?,
    val address: String?,
    val horarioFunc: String?,
    val cidade: String?,
    var favorite: Boolean,
    val coordinates: Pair<Double, Double>?
)

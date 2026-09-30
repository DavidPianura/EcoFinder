package br.edu.ufabc.ecofinder.model

import android.app.Application
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.io.IOException
import java.util.Locale
import kotlin.math.pow


class Repository(application: Application, private val context: Context) {
    private val urlSA = "https://www.semasa.sp.gov.br/residuos/coleta-domiciliar-2/coleta-seletiva/estacoes-de-coleta/"
    private val urlSBC = "https://www.saobernardo.sp.gov.br/maximizada/-/asset_publisher/5cLluTMVcxDN/content/su-ecopontos-rcd?inheritRedirect=false"
    private val ecopontos = mutableListOf<Ecoponto>()
    // private var ecopontosOrdenados = mutableListOf<Ecoponto>()
    val ecopontosAux = mutableListOf<EcopontoAux>()
    var id: Long = 0


    private fun getAddressCoordinates(addressString: String): Pair<Double, Double>? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address> = geocoder.getFromLocationName(addressString, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                Pair(address.latitude, address.longitude)
            } else {
                Pair(0.0, 0.0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getEcopontosSA(): List<Ecoponto> {
        val ecopontosAux = mutableListOf<String>()
        val horariosAux = mutableListOf<String>()
        val ecopontos = mutableListOf<Ecoponto>()
        ecopontos.clear()

            val document = Jsoup.connect(urlSA).get()

            val horarios = document.select("p:contains(Atendimento de)")

            for (horario in horarios) {
                horariosAux.add(horario.text())
            }

            val tables = document.select("table")


            for ((i, table) in tables.withIndex()) {
                val rows = table.select("tr")
                for (row in rows) {
                    val columns = row.select("td")
                    Log.d("WEBSCRAP", "COLUMNS[0]: ${columns[0].text()}")

                    val elemento = columns[0].text()
                    ecopontosAux.add(elemento)


                    if (ecopontosAux.size == 2) {
                        val coordinates = getAddressCoordinates(ecopontosAux[1])

                        Log.d("WEBSCRAPSAID", "ID DO ECOPONTO (SA): $id")
                        ecopontos.add(Ecoponto(id, ecopontosAux[0].replace("•", ""), ecopontosAux[1], horariosAux[i], "SA", false, coordinates))

                        ecopontosAux.clear()
                        id++
                    }
                }
            }

        return ecopontos
    }

    private fun getEcopontosAuxSA() : EcopontoAux {
        val id: Long = 0
        val description: String
        val notFindText = "?"

        // Doing Description
            val document = Jsoup.connect(urlSA).get()
            val pElement = document.selectFirst("p")?.nextElementSibling()
            var ownText = pElement?.ownText() ?: "Texto nao encontrado"
            val childElements = pElement?.select("span, strong")
            var childTexts = childElements?.joinToString(separator = "") { it.ownText() }

            if (childTexts != null) {
                childTexts = childTexts.replace(",,", ",")
                ownText = ownText.replace("As Estações de Coleta do Semasa são ecopontos", "As Estações de Coleta do Semasa são ecopontos, ${childTexts.trim()}")
            }

            val firstElementUl = document.selectFirst("ul:contains(Também aceitam)")?.text() ?: notFindText
            val secondElementUl = document.selectFirst("ul:contains(Além disso)")?.text() ?: notFindText

            val hojeSao = document.selectFirst("p:contains(Hoje, são)")?.text() ?: notFindText
            val casoVeiculo = document.selectFirst("span:contains(Caso o veículo que)")?.text() ?: notFindText

            description = "$ownText\n\n• $firstElementUl\n• $secondElementUl\n\n$hojeSao\n\n$casoVeiculo"
            // Log.d("WEBSCRAPSA", "Description SA: $description")

        return EcopontoAux(id, "SA", description)
    }


    private fun getEcopontosSBC(): List<Ecoponto> {
        val ecopontos = mutableListOf<Ecoponto>()

        val doc = Jsoup.connect(urlSBC).get()
        val strong = doc.select("strong:contains(HORÁRIO DE FUNCIONAMENTO)").first()
        val p = strong?.parent()
        val horarioFunc = p?.ownText() ?: "Não foi possível encontrar o horário de funcionamento"

        val startElement = doc.selectFirst("p:contains(CONFIRA ABAIXO A RELAÇÃO DOS ECOPONTOS DE SÃO BERNARDO)")
        var currentElement = startElement?.nextElementSibling()

        while (currentElement != null && !currentElement.text().startsWith("HORÁRIO DE FUNCIONAMENTO")) {
            id++

            val name = currentElement.selectFirst("strong")?.text() ?: ""
            val address = currentElement.selectFirst("em")?.text() ?: ""
            val coordinates = getAddressCoordinates(address)
            ecopontos.add(Ecoponto(id, name, address, horarioFunc, "SBC", false, coordinates))
            currentElement = currentElement.nextElementSibling()
        }

        return ecopontos
    }

    private fun getEcopontosAuxSBC(): EcopontoAux {
        val id: Long = 1
        val description: String

        val notFindText = "Texto nao encontrado "

        val document = Jsoup.connect(urlSBC).get()
        Log.d("WEBSCRAPSBC", "conectado com SBC")

        val firstPElement = document.selectFirst("p:contains(A Prefeitura de)")
        val firstPElementText = firstPElement?.ownText()

        Log.d("WEBSCRAPSBC", "DESCRIPTION\n: $firstPElementText")

        val secondPElementText = firstPElement?.nextElementSibling()?.text() ?: notFindText

        // O que pode ser descartado?
        val pElementOquePodeSerDescartado = document.selectFirst("p:contains(O QUE PODE SER)")?.text() ?: notFindText
        var oQuePodeSerDescartadoTopics = ""
        val bulletPointsPodeSerDescartado = pElementOquePodeSerDescartado.split("•").drop(1).map { it.trim() }
        bulletPointsPodeSerDescartado.forEach {
            oQuePodeSerDescartadoTopics += "\n•$it"
        }

        // Log.d("WEBSCRAPSBC", "O que Nao pode ser Descartado:\n$oQuePodeSerDescartadoTopics")

        val pElementOqueNaoPodeSerDescartado = document.selectFirst("p:contains(O QUE NÃO PODE)")?.text() ?: notFindText
        val bulletPointsNaoPodeSerDescartado = pElementOqueNaoPodeSerDescartado.split("•").drop(1).map { it.trim() }
        var oQueNaoPodeSerDescartadoTopics = ""
        bulletPointsNaoPodeSerDescartado.forEach {
            oQueNaoPodeSerDescartadoTopics += "\n•$it"
        }

        description = "$firstPElementText\n\n$secondPElementText\n\n" +
                "O QUE PODE SER DESCARTADO NO ECOPONTO?\n$oQuePodeSerDescartadoTopics\n\n" +
                "O QUE NÃO PODE SER DESCARTADO\n$oQueNaoPodeSerDescartadoTopics"

        Log.d("WEBSCRAPSBC", "DESCRIPTION:\n$description")

        return EcopontoAux(id, "SBC", description)

    }

    suspend fun bindEcopontos(): List<Ecoponto> {
        withContext(Dispatchers.IO) {
            val ecopontosSA = getEcopontosSA()
            val ecopontosSBC = getEcopontosSBC()
            ecopontos.addAll(ecopontosSA)
            ecopontos.addAll(ecopontosSBC)
            bindEcopontosAux()
        }
        return ecopontos.distinct()
    }

    suspend fun bindEcopontosAux(){
        withContext(Dispatchers.IO) {
            ecopontosAux.add(getEcopontosAuxSA())
            ecopontosAux.add(getEcopontosAuxSBC())
        }
    }

    fun getEcopontoById(id: Long) = ecopontos.find {
        it.id == id
    }

    /*
    Ordenando lista utilizando a localização do usuário
        A fórmula de Haversine é uma equação usada para calcular a distância entre dois pontos em uma esfera, como a Terra.
        Ela leva em consideração a latitude e a longitude dos dois pontos para calcular a distância mínima ao longo da superfície da esfera.
        A fórmula é dada por:
            d = 2r * arcsin(sqrt(sin²((lat2-lat1)/2) + cos(lat1) * cos(lat2) * sin²((lon2-lon1)/2)))

        A fórmula de Haversine é amplamente utilizada em sistemas de geolocalização e navegação para calcular a distância entre dois pontos em uma
        superfície esférica, como a Terra.
    */

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // raio da Terra em km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2).pow(2.0) + kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(
            Math.toRadians(lat2)
        ) * kotlin.math.sin(dLon / 2).pow(2.0)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return earthRadius * c
    }

    fun getEcopontosOrdenados(userLatitude : Double, userLongitude : Double): List<Ecoponto> {
        val ecopontosOrdenados = ecopontos.sortedBy { ecoponto ->
            haversineDistance(userLatitude, userLongitude, ecoponto.coordinates?.first ?: 0.0,
                ecoponto.coordinates?.second ?: 0.0
            )
        }
        return ecopontosOrdenados.distinct()
    }

    fun getEcopontos() = ecopontos

}


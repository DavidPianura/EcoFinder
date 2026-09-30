package br.edu.ufabc.ecofinder.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.ecofinder.databinding.EcopontosListItemBinding
import br.edu.ufabc.ecofinder.model.Ecoponto

class EcopontoAdapter(private val ecopontos :List<Ecoponto>, private val navController: NavController) :
    RecyclerView.Adapter<EcopontoAdapter.EcopontoViewHolder>() {

    inner class EcopontoViewHolder(itemBinding: EcopontosListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root){

        val name = itemBinding.textViewEcopontoListItemName
        val address = itemBinding.textViewEcopontoListItemLocation
        val description = itemBinding.textViewEcopontoListItemDescription
        val favIcon = itemBinding.imageViewEcopontoItemFavoriteStar

        init {
            itemBinding.root.setOnClickListener {
                Log.d("ECOPONTOITEM", "ID DO ECOPONTO: ${getItemId(bindingAdapterPosition)}")
                EcopontosFragmentDirections
                    .showEcopontoDetails(getItemId(bindingAdapterPosition)).let {
                        navController.navigate(it)
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EcopontoViewHolder =
        EcopontoViewHolder (
            EcopontosListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
                )


    override fun getItemCount(): Int = ecopontos.size

    override fun onBindViewHolder(holder: EcopontoViewHolder, position: Int) {
        val ecoponto = ecopontos[position]
        holder.name.text = ecoponto.name
        holder.address.text = ecoponto.address
        holder.description.text = ecoponto.horarioFunc
        holder.favIcon.isVisible = ecoponto.favorite
    }

    override fun getItemId(position: Int): Long = ecopontos[position].id





}
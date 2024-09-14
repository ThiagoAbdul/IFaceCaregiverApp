package com.example.facerecognitionimages.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.facerecognitionimages.R
import com.example.facerecognitionimages.data.models.KnownPersonResponse

class KnownPersonAdapter(
    private val onKnownPersonSelected: (KnownPersonResponse) -> Unit ) : RecyclerView.Adapter<KnownPersonAdapter.KnownPersonViewHolder>() {

    val knownPeopleList: MutableList<KnownPersonResponse> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KnownPersonViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_know_person, parent, false)

        return KnownPersonViewHolder(itemView)
    }

    override fun getItemCount() = knownPeopleList.size


    override fun onBindViewHolder(holder: KnownPersonViewHolder, position: Int) {
        val knownPerson: KnownPersonResponse = knownPeopleList[position]
        val knownPersonName: String = knownPerson.person.firstName
            .plus(" ")
            .plus(knownPerson.person.lastName)

        holder.tvKnwonPersonName.text = knownPersonName
        holder.tvKnwonPersonName.setOnClickListener {
            onKnownPersonSelected(knownPerson)
        }
    }

    class KnownPersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvKnwonPersonName: TextView = itemView.findViewById(R.id.tv_item_known_person)
    }
}
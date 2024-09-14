package com.example.facerecognitionimages.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.facerecognitionimages.R
import com.example.facerecognitionimages.data.models.PwadResponse

class PwadAdapter(
    val pwadList: MutableList<PwadResponse>,
    val onPwadSelected: (PwadResponse) -> Unit) : RecyclerView.Adapter<PwadAdapter.PwadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PwadViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pwad, parent, false)

        return PwadViewHolder(itemView)
    }

    override fun getItemCount() = pwadList.size

    override fun onBindViewHolder(holder: PwadViewHolder, position: Int) {

        val pwad = pwadList[position]

        val pwadName: String = pwad.person.firstName
            .plus(" ")
            .plus(pwad.person.lastName)

        holder.pwadName.text = pwadName
        holder.pwadName.setOnClickListener {
            onPwadSelected(pwad)
        }
    }

    class PwadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val pwadName: TextView = itemView.findViewById(R.id.tv_pwad_name)



    }
}

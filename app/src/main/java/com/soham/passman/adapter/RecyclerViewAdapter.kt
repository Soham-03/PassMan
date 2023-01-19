package com.soham.passman.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.soham.passman.R
import com.soham.passman.activity.EditOrDeletePassword
import com.soham.passman.data.Data
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecyclerViewAdapter(private var list: ArrayList<Data>,private var context: Context): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtAccount:TextView = itemView.findViewById(R.id.txtAccountName)
        val txtUserName:TextView = itemView.findViewById(R.id.txtUserName)
        val txtPassword:TextView = itemView.findViewById(R.id.txtPassword)
        val cardViewColor: MaterialCardView = itemView.findViewById(R.id.singleRowColor)
        val txtDate:TextView = itemView.findViewById(R.id.txtDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_cardview,parent,false)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        when(data.color){
            "Green"->{
                holder.cardViewColor.setCardBackgroundColor(Color.GREEN)
            }
            "Yellow"->{
                holder.cardViewColor.setCardBackgroundColor(Color.YELLOW)
            }
            "Red"->{
                holder.cardViewColor.setCardBackgroundColor(Color.RED)
            }
        }
        val time = Calendar.getInstance().time
        val dateInString = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(time)
        if(data.date.dropLast(9) == dateInString){
            holder.txtDate.text = "Today at ${data.date.drop(11).dropLast(3)}"
        }
        else{
            val date = data.date.dropLast(9)
            val onlyDate = date.drop(8)
            val onlyMonth = date.drop(5).dropLast(3)
            val onlyYear = date.drop(2).dropLast(6)
            holder.txtDate.text = onlyDate+"/"+onlyMonth+"/"+onlyYear+" at "+data.date.drop(11).dropLast(3)
        }
        holder.txtAccount.text = data.account
        holder.txtUserName.text = data.userName
        holder.txtPassword.text = data.password
        holder.itemView.setOnClickListener {
            val intent = Intent(context,EditOrDeletePassword::class.java)
            intent.putExtra("id",data.id)
            intent.putExtra("account",data.account)
            intent.putExtra("userName",data.userName)
            intent.putExtra("password",data.password)
            intent.putExtra("color",data.color)
            intent.putExtra("date",data.date)
            startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList: ArrayList<Data>) {
        list = newList
        notifyDataSetChanged()
    }
}
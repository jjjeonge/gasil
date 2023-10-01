package com.example.part1.gasil

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.part1.gasil.databinding.ItemAccountInfoBinding
import com.google.firebase.firestore.DocumentSnapshot
import java.text.DecimalFormat
import kotlin.coroutines.coroutineContext

class AccountInfoAdapter(
    val list: MutableList<AccountInfo>,
    //private var accountInfoClickListener: AccountInfoClickListener? = null,
    ) : RecyclerView.Adapter<AccountInfoAdapter.AccountInfoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountInfoViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemAccountInfoBinding.inflate(inflater, parent, false)
        return AccountInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountInfoViewHolder, position: Int) {
        val info = list[position]
        holder.bind(info)
        //holder.itemView.setOnClickListener { accountInfoClickListener?.onClick(it, position)}
        //holder.itemView.setOnClickListener { accountInfoClickListener?.onClick(info) }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class AccountInfoViewHolder(private val binding: ItemAccountInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        private val decimalFormat = DecimalFormat("#,###")

        @SuppressLint("Range")
        fun bind(info: AccountInfo) {

            binding.apply {
                dateTextView.text = info.date.toString()
                userTextView.text = info.user
                statementTextView.text = info.statement
                moneyTextView.text = decimalFormat.format(info.money.toString().toBigDecimal())
                typeTextView.text = info.type
                userTextView.setTextColor(Color.parseColor("${info.color}"))
                docIdTextView.text = info.docId
                sumTextView.text = info.color

                /*val pos = adapterPosition
                if(pos!= RecyclerView.NO_POSITION)
                {
                    itemView.setOnClickListener {
                        accountInfoClickListener?.onClick(info)
                    }
                }*/
            }

            
        }
    }

    /*interface OnItemClickListener{
        fun onItemClick(info: AccountInfo)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }*/

    /*interface AccountInfoClickListener {
        fun onClick(info: AccountInfo)
    }*/
//
//    fun setItemClickListener(accountInfoClickListener: AccountInfoClickListener) {
//        this.accountInfoClickListener = accountInfoClickListener
//    }
//
//    private val accountInfoClickListener: AccountInfoClickListener? = null
}
package com.example.part1.gasil

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.part1.gasil.databinding.ItemAccountInfoBinding

class AccountInfoAdapter(
    val list: MutableList<AccountInfo>,
    private val accountInfoClickListener: AccountInfoClickListener? = null,
    ) : RecyclerView.Adapter<AccountInfoAdapter.AccountInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountInfoViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemAccountInfoBinding.inflate(inflater, parent, false)
        return AccountInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountInfoViewHolder, position: Int) {
        val info = list[position]
        holder.bind(info)
        holder.itemView.setOnClickListener { accountInfoClickListener?.onClick(info)}
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class AccountInfoViewHolder(private val binding: ItemAccountInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(info: AccountInfo) {
            binding.apply {
                dateTextView.text = info.date
                userTextView.text = info.user
                statementTextView.text = info.statement
                moneyTextView.text = info.money
                typeTextView.text = info.type
                sumValueTextView.text = info.sumValue
            }
            
        }
    }

    interface AccountInfoClickListener{
        fun onClick(info: AccountInfo)
    }
}
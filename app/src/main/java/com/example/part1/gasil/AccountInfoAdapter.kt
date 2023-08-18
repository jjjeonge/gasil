package com.example.part1.gasil

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.part1.gasil.databinding.ItemAccountInfoBinding

class AccountInfoAdapter(private val list: MutableList<AccountInfo>) : RecyclerView.Adapter<AccountInfoAdapter.AccountInfoViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountInfoViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemAccountInfoBinding.inflate(inflater, parent, false)
        return AccountInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountInfoViewHolder, position: Int) {
        holder.binding.apply {
            val info = list[position]
            dateTextView.text = info.date
            userTextView.text = info.user
            moneyTextView.text = info.money
            typeTextView.text = info.type
            sumValueTextView.text = info.sumValue
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class AccountInfoViewHolder(val binding: ItemAccountInfoBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}
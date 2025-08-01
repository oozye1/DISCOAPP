package com.example.discoapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.discoapp.databinding.ItemEffectChipBinding

class EffectChipAdapter(
    private val effects: List<String>,
    private var selectedIndex: Int = 0,
    private val onEffectSelected: (Int) -> Unit
) : RecyclerView.Adapter<EffectChipAdapter.ChipViewHolder>() {

    inner class ChipViewHolder(val binding: ItemEffectChipBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val binding = ItemEffectChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        val effectName = effects[position]
        holder.binding.chipEffect.text = effectName
        holder.binding.chipEffect.isChecked = position == selectedIndex
        holder.binding.chipEffect.setOnClickListener {
            if (selectedIndex != position) {
                val previousIndex = selectedIndex
                selectedIndex = position
                notifyItemChanged(previousIndex)
                notifyItemChanged(selectedIndex)
                onEffectSelected(position)
            }
        }
    }

    override fun getItemCount(): Int = effects.size
}

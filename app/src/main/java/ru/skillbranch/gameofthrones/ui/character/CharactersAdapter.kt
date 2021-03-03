package ru.skillbranch.gameofthrones.ui.houses.house

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_character.view.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem

class CharactersAdapter(private val listener : (CharacterItem) -> Unit )
    : ListAdapter<CharacterItem, CharactersAdapter.CharacterViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val container = LayoutInflater.from(parent.context).inflate(R.layout.item_character,parent,false)
        return CharacterViewHolder(container)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position),listener)
    }

    class CharacterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(character: CharacterItem, listener: (CharacterItem) -> Unit) {
            with(itemView){
                textView_name.text = if(character.name.isBlank()) "Information is unknown" else character.name
                val listOfAliases  = character.titles.plus(character.aliases).filter {it.isNotBlank()}
                textView_aliases.text = if(listOfAliases.isEmpty()) "Information is unknown"
                        else listOfAliases.joinToString(",")
                image_avatar.setImageResource(character.house.icon)
                setOnClickListener {listener (character)}
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CharacterItem>() {
        override fun areItemsTheSame(oldItem: CharacterItem, newItem: CharacterItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CharacterItem, newItem: CharacterItem): Boolean {
            return oldItem.id == newItem.id
        }
    }

}
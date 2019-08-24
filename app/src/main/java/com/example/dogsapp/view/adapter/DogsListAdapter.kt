package com.example.dogsapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.dogsapp.R
import com.example.dogsapp.databinding.ItemDogBinding
import com.example.dogsapp.model.DogBreed
import com.example.dogsapp.view.DogClickListener
import com.example.dogsapp.view.fragment.DogsFragmentDirections
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter(private val dogsList: ArrayList<DogBreed>): RecyclerView.Adapter<DogsListAdapter.DogViewHolder>() {

    fun updateDogsList(newDogsList: List<DogBreed>){
        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater, R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun getItemCount(): Int = dogsList.size

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(dogsList[position])
    }

    class DogViewHolder(val view: ItemDogBinding): RecyclerView.ViewHolder(view.root),
        DogClickListener {
        override fun onDogClicked(v: View) {
            val uuid = v.dogId.text.toString().toInt()
            val action = DogsFragmentDirections.actionDetailFragment()
            action.dogUuid = uuid
            Navigation.findNavController(v).navigate(action)
        }

        fun bind(dog: DogBreed){
            view.dog = dog
            view.listener = this

//            itemView.name.text = dog.dogBreed
//            itemView.lifeSpan.text = dog.lifeSpan
//            itemView.dogImage.loadImage(dog.imageUrl, getProgressDrawable(itemView.dogImage.context))

//            itemView.setOnClickListener {
//                val action = DogsFragmentDirections.actionDetailFragment()
//                action.dogUuid = dog.uuid
//                Navigation.findNavController(it).navigate(action)
//            }

        }
    }
}
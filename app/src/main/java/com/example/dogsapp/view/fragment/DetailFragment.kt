package com.example.dogsapp.view.fragment


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.dogsapp.R
import com.example.dogsapp.databinding.FragmentDetailBinding
import com.example.dogsapp.model.DogPalette
import com.example.dogsapp.viewmodel.DetailViewModel


class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel
    private var dogUuid: Int = 0
    private lateinit var dataBinding: FragmentDetailBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.refresh(dogUuid)

        observeViewModel(view)

    }

    private fun observeViewModel(v: View){
        viewModel.dog.observe(this, Observer {
            it?.let{
                dataBinding.dogDetail = it

                it.imageUrl?.let {
                    setupBackgroundColor(it)
                }
            }
        })
    }

    private fun setupBackgroundColor(url: String){
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object: CustomTarget<Bitmap>(){
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate {
                            val color = it?.darkMutedSwatch?.rgb ?: 0
                            val myPalette = DogPalette(color)
                            dataBinding.palette = myPalette
                        }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.sendSms ->{
                Toast.makeText(context, "You press send sms", Toast.LENGTH_LONG).show()
            }
            R.id.share ->{
                Toast.makeText(context, "You press share", Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}

package com.example.dogsapp.view.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.dogsapp.R
import com.example.dogsapp.view.adapter.DogsListAdapter
import com.example.dogsapp.viewmodel.DogsViewModel
import kotlinx.android.synthetic.main.fragment_dogs.*

class DogsFragment : Fragment() {

    private lateinit var  viewModel: DogsViewModel
    private var dogsListAdapter = DogsListAdapter(arrayListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_dogs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DogsViewModel::class.java)
        viewModel.refresh()

        dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogsListAdapter
        }

        refreshLayout.setOnRefreshListener {
            dogsList.visibility = View.GONE
            listError.visibility = View.GONE
            loadingView.visibility = View.VISIBLE
            viewModel.refreshByPassCache()
            refreshLayout.isRefreshing = false
        }

        observeDogsViewModel()



    }

    private fun observeDogsViewModel(){
        viewModel.dogs.observe(this, Observer {
            it?.let {
                dogsList.visibility = View.VISIBLE
                dogsListAdapter.updateDogsList(it)
            }
        })

        viewModel.loadError.observe(this, Observer {
            it?.let {
                listError.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(this, Observer {
            it?.let {
                loadingView.visibility = if(it) View.VISIBLE else View.GONE
                if(it){
                    listError.visibility = View.GONE
                    dogsList.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dog_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.setting ->{
                view?.let { Navigation.findNavController(it).navigate(DogsFragmentDirections.actionDogsFragmentToSettingFragment()) }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

package com.tja.disko2.features.listfavorite

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.tja.disko2.R
import com.tja.disko2.domain.PlaceO2
import com.tja.disko2.features.aboutplace.AboutPlaceActivity
import com.tja.disko2.features.listplace.AdapterPlaceList
import com.tja.disko2.features.listplace.PlaceViewModel
import com.tja.disko2.features.util.MyViewModelFactory


class ListFavoriteFragment : Fragment() {

    private lateinit var viewModel: PlaceViewModel
    private var adapter: AdapterPlaceList = AdapterPlaceList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_favorite, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(requireActivity(), requireActivity().application)).get(
                PlaceViewModel::class.java
            )

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview)
        adapter = AdapterPlaceList()
        recyclerView.adapter = adapter
        adapter.setOnClickPlace { place_click, itemView -> clickFavorite(place_click, itemView) }

        viewModel.allPlacesFavorite.observe(this, Observer {
            it?.let {
                adapter.setData(it)
            }
        })

    }

    private fun clickFavorite(placeO2: PlaceO2, itemView: View) {
        if (itemView.id == R.id.iv_favorite) {
            viewModel.favorite(placeO2)
        } else if (itemView.id == R.id.container) {
            AboutPlaceActivity.openAboutPlaceActivity(requireActivity(),placeO2)
        }else if (itemView.id == R.id.card_call) {
            viewModel.intentCall(placeO2)
        } else if (itemView.id == R.id.card_wpp) {
            viewModel.intentWpp(placeO2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListFavoriteFragment()
    }
}
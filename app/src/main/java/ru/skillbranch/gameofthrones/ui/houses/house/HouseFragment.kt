package ru.skillbranch.gameofthrones.ui.houses.house

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_house.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.houses.HousesFragmentDirections

class HouseFragment : Fragment() {
    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var houseViewModel: HouseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val houseName = arguments?.getString(HOUSE_NAME) ?: HouseType.STARK.title
        val showBookmarked = arguments?.getBoolean(ONLY_FAVORITE) ?: false

        val vmFactory = HouseViewModelFactory(houseName)
        charactersAdapter = CharactersAdapter {
            val action = HousesFragmentDirections.actionNavHousesToNavCharacter(
                it.id,
                it.house.title,
                it.name,
                it.isBookmarked
            )
            findNavController().navigate(action)
        }
        houseViewModel = ViewModelProviders.of(this, vmFactory).get(HouseViewModel::class.java)
        if (showBookmarked) {
            houseViewModel.getFavoriteCharacters().observe(this, Observer<List<CharacterItem>> {
                charactersAdapter.submitList(it)
            })
        }
        else {
            houseViewModel.getCharacters().observe(this, Observer<List<CharacterItem>> {
                charactersAdapter.submitList(it)
            })
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        with(menu.findItem(R.id.action_search).actionView as SearchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    houseViewModel.handleSearchQuery(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    houseViewModel.handleSearchQuery(newText)
                    return true
                }
            })
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorites -> {

            }
            else -> false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_characters.apply {
            adapter = charactersAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        recycler_characters.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    companion object {
        private const val HOUSE_NAME = "house_name"
        private const val ONLY_FAVORITE = "show_only_favorite"

        fun newInstance(houseName: String, showBookmarked: Boolean): HouseFragment {
            return HouseFragment().apply {
                arguments = bundleOf(
                    HOUSE_NAME to houseName,
                    ONLY_FAVORITE to showBookmarked
                )
            }
        }
    }
}

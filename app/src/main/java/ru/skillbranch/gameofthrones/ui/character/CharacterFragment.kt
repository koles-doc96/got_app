package ru.skillbranch.gameofthrones.ui.character

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_character.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.RootActivity


class CharacterFragment : Fragment() {
    private val args: CharacterFragmentArgs by navArgs()
    private lateinit var characterViewModel: CharacterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        characterViewModel =
            ViewModelProviders.of(this, CharacterViewModelFactory(args.characterId))
                .get(CharacterViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_character, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_add_favorite)
        toggleIcon(item = item, state = args.isBookmarked)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_add_favorite -> {
                val state = args.isBookmarked
                val toast: Toast
                if (state) {
                    toast = Toast.makeText(
                        requireActivity(), "Deleted",
                        Toast.LENGTH_SHORT
                    )
                } else {
                    toast = Toast.makeText(
                        requireActivity(), "Added",
                        Toast.LENGTH_SHORT
                    )
                }
                toast.show()

                characterViewModel.setBookmarked()
                val action = CharacterFragmentDirections.actionNavCharacterSelf(
                    args.characterId,
                    args.house,
                    args.title,
                    !state
                )

                findNavController().navigate(action, navOptions {
                    launchSingleTop = true
                })
            }
            else -> false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_character, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val houseType = HouseType.fromString(args.house)
        val arms = houseType.coastOfArms
        val scrim = houseType.primaryColor
        val scrimDark = houseType.darkColor

        val rootActivity = requireActivity() as RootActivity
        rootActivity.setSupportActionBar(toolbar)
        rootActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = args.title
        }
        iv_arms.setImageResource(arms)
        with(collapsing_layout) {
            setBackgroundResource(scrim)
            setContentScrimResource(scrim)
            setStatusBarScrimResource(scrimDark)
        }

        collapsing_layout.post { collapsing_layout.requestLayout() }

        characterViewModel.getCharacter()
            .observe(viewLifecycleOwner, Observer<CharacterFull> { character ->
                if (character == null) return@Observer

                val iconColor = requireContext().getColor(houseType.accentColor)
                listOf(tv_words_label, tv_born_label, tv_titles_label, tv_aliases_label)
                    .forEach { it.compoundDrawables.first().setTint(iconColor) }

                tv_words.text = character.words
                tv_born.text = character.born
                tv_titles.text = character.titles
                    .filter { it.isNotEmpty() }
                    .joinToString(separator = "\n")
                tv_aliases.text = character.aliases
                    .filter { it.isNotEmpty() }
                    .joinToString(separator = "\n")

                character.father?.let {
                    group_father.visibility = View.VISIBLE
                    btn_father.text = it.name
                    val action =
                        CharacterFragmentDirections.actionNavCharacterSelf(
                            it.id,
                            it.house,
                            it.name,
                            it.is_bookmarked
                        )
                    btn_father.setOnClickListener { findNavController().navigate(action) }
                }
                character.mother?.let {
                    group_mother.visibility = View.VISIBLE
                    btn_mother.text = it.name
                    val action =
                        CharacterFragmentDirections.actionNavCharacterSelf(
                            it.id,
                            it.house,
                            it.name,
                            it.is_bookmarked
                        )
                    btn_mother.setOnClickListener { findNavController().navigate(action) }
                }
                if (character.died.isNotBlank()) {
                    Snackbar.make(
                        coordinator,
                        "Died in ${character.died}",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .show()
                }
            })

    }

    fun toggleIcon(state: Boolean, item: MenuItem) {
        if (state)
            item.setIcon(R.drawable.ic_bookmarked_24)
        else
            item.setIcon(R.drawable.ic_not_bookmarked_24)
    }

}

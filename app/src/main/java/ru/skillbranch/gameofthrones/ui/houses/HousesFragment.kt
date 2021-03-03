package ru.skillbranch.gameofthrones.ui.houses

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.CheckedTextView
import android.widget.SearchView
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_houses.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.ui.RootActivity
import kotlin.math.hypot
import kotlin.math.max

class HousesFragment : Fragment() {
    private val args: HousesFragmentArgs by navArgs()
    private lateinit var colors: Array<Int>
    private lateinit var housesPageAdapter: HousesPageAdapter
    private var showBookmarked: Boolean = false

    @ColorInt
    private var currentColor: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        showBookmarked = args.showOnlyFavorite
        housesPageAdapter = HousesPageAdapter(childFragmentManager, showBookmarked)
        colors = requireContext().run {
            arrayOf(
                getColor(R.color.stark_primary),
                getColor(R.color.lannister_primary),
                getColor(R.color.targaryen_primary),
                getColor(R.color.baratheon_primary),
                getColor(R.color.greyjoy_primary),
                getColor(R.color.martel_primary),
                getColor(R.color.tyrel_primary)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        with(menu.findItem(R.id.action_search)?.actionView as SearchView) {
            queryHint = "Search character"
        }
        with(menu.findItem(R.id.action_favorites)?.actionView as CheckedTextView) {
            isChecked = showBookmarked

            if (isChecked) setCheckMarkDrawable(R.drawable.ic_checked_24)
            else setCheckMarkDrawable(R.drawable.ic_not_checked_24)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorites -> {
                with(item.actionView as CheckedTextView) {
                    showBookmarked = !isChecked

                    val action = HousesFragmentDirections.actionNavHousesSelf(showBookmarked)
                    findNavController().navigate(action, navOptions {
                        launchSingleTop = true
                    })
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_houses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as RootActivity).setSupportActionBar(toolbar)

        if (currentColor != -1)
            appbar.setBackgroundColor(currentColor)
        view_pager.adapter = housesPageAdapter
        with(tabs) {
            setupWithViewPager(view_pager)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {}
                override fun onTabUnselected(p0: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val position = tab?.position ?: 0

                    if ((appbar.background as ColorDrawable).color != colors[position]) {
                        val rect = Rect()
                        val tabView = tab?.view as View
                        tabView.postDelayed(
                            {
                                tabView.getGlobalVisibleRect(rect)
                                animateAppbarReval(position, rect.centerX(), rect.centerY())
                            },
                            100
                        )
                    }
                }
            })
        }
    }

    private fun animateAppbarReval(position: Int, centerX: Int, centerY: Int) {
        val endRadius = max(
            hypot(centerX.toDouble(), centerY.toDouble()),
            hypot(appbar.width.toDouble() - centerX.toDouble(), centerY.toDouble())
        )
        with(reveal_view) {
            visibility = View.VISIBLE
            setBackgroundColor(colors[position])
        }
        ViewAnimationUtils.createCircularReveal(
            reveal_view,
            centerX,
            centerY,
            0f,
            endRadius.toFloat()
        ).apply {
            doOnEnd {
                appbar?.setBackgroundColor(colors[position])
                reveal_view?.visibility = View.INVISIBLE
            }
            start()
        }
        currentColor = colors[position]
    }

}

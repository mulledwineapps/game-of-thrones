package ru.skillbranch.gameofthrones.ui.houses


import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.core.animation.doOnEnd
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_houses.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.ui.RootActivity
import kotlin.math.hypot
import kotlin.math.max

// 11:00 мастер-класса
class HousesFragment : Fragment() {

    private lateinit var colors: Array<Int>
    private lateinit var housesPagerAdapter: HousesPagerAdapter

    @ColorInt
    // цвет TabBarLayout
    private var currentColor: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // позволяет создать для фрагмента дополнительное submenu,
        // которое будет отвечать за поиск персонажа на каждом фрагменте, находящемся во вью пейджере,
        // т.е. создаём этот пункт меню в корневом родительском фрагменте
        setHasOptionsMenu(true) // create search menu

        // именно childFragmentManager, чтобы внутри этого фрагмента
        // все фрагменты внутри вью пэйджера сохраняли своё состояние
        housesPagerAdapter = HousesPagerAdapter(childFragmentManager)
        // requireContext() возвращает именно not nullable контекст
        colors = requireContext().run {
            arrayOf(
                getColor(R.color.stark_primary),
                getColor(R.color.lannister_primary),
                getColor(R.color.targaryen_primary),
                getColor(R.color.baratheon_primary),
                getColor(R.color.greyjoy_primary),
                getColor(R.color.martell_primary),
                getColor(R.color.tyrell_primary)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        with(menu.findItem(R.id.action_search)?.actionView as SearchView) {
            queryHint = "Search character"
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    // 00:14:00
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_houses, container, false)

    // т.к. мы используем в проекте kotlin synthetics, чтобы находить вью их разметки без вызова
    // метода findViewById, все манипуляции со вью элементами мы должны производить в методе onViewCreated,
    // потому что в методе onCreateView вью ещё не созданы и обратиться к ним через синтетик мы не можем
    // 14:02
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as RootActivity).setSupportActionBar(toolbar)

        // если уже производили навигацию, либо вернулись с экрана персонажа, устанавливаем цвет
        if (currentColor != -1) appbar.setBackgroundColor(currentColor)

        view_pager.adapter = housesPagerAdapter
        with(tabs) {
            setupWithViewPager(view_pager)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    val position = tab.position

                    // if colors are different, animate

                    // в 1.1.0-beta02 appbar.background это не ColorDrawable, а MaterialShapeDrawable.. при первом клике
                    val appbarColor = with(appbar.background) {
                        // Log.d("M_HousesFragment", this.javaClass.name)
                        when (this) {
                            is ColorDrawable -> color
                            is MaterialShapeDrawable -> fillColor!!.defaultColor
                            else -> throw IllegalStateException()
                        }
                    }

                    //if ((appbar.background as ColorDrawable).color != colors[position]) {
                    if (appbarColor != colors[position]) {
                        val rect = Rect()
                        val tabView = tab.view as View

                        // длительность стандартной material анимации составляет 300 мс
                        // без реализованной ниже задержки, центр анимации начинается в месте
                        // предыдущего положения выбранной вкладки (tab),
                        // то есть визуально неверно в случае скролла TabLayout
                        tabView.postDelayed(
                            {
                                tabView.getGlobalVisibleRect(rect)
                                animateAppbarReveal(position, rect.centerX(), rect.centerY())
                            },
                            300
                        )
                    }
                }

            })
        }
    }

    private fun animateAppbarReveal(position: Int, centerX: Int, centerY: Int) {
        // 18:20, 20:20
        // 21:20 мастер-класса можно посмотреть линии катетов и гипотенузы
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
            // duration = 5000
            doOnEnd {
                appbar?.setBackgroundColor(colors[position])
                reveal_view?.visibility = View.INVISIBLE

            }
            start()
        }

        // сохраняем цвет для его восстановления при возвращении на экран
        currentColor = colors[position]
    }
}

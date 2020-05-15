package ru.skillbranch.gameofthrones.ui.houses.house


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_house.*

import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.custom.ItemDivider
import ru.skillbranch.gameofthrones.ui.houses.HousesFragmentDirections

// 23:03 мастер-класса
class HouseFragment : Fragment() {

    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var viewModel: HouseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val houseName = arguments?.getString(HOUSE_NAME) ?: HouseType.STARK.title
        val vmFactory = HouseViewModelFactory(houseName)
        charactersAdapter = CharactersAdapter {
            // лямбда-функция, которая отрабатывает нажание на персонажа
            val action = HousesFragmentDirections.actionNavHousesToNavCharacter(
                it.id,
                it.house.title,
                it.name
            )
            // 34:20 мастер-класса - демонстрация перехода к экрану персонажа
            findNavController().navigate(action)
        }
        viewModel = ViewModelProviders.of(this, vmFactory).get(HouseViewModel::class.java)
        viewModel.getCharacters().observe(this, Observer<List<CharacterItem>> {
            charactersAdapter.updateItems(it)
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        // обращается к уже созданному в HousesFragment меню и только переопределяет нужный листенер
        // при этом эта функция гораздо легче, чем onCreateOptionsMenu, поэтому лучше использовать её,
        // если необходимые айтемы меню уже созданы

        // 31:50 мастер класса производится демонстрация поиска
        with(menu.findItem(R.id.action_search).actionView as SearchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                // нажатие search на клавиатуре
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.handleSearchQuery(query)
                    return true
                }

                // изменение запроса
                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.handleSearchQuery(newText)
                    return true
                }
            })

        }

        super.onPrepareOptionsMenu(menu)
    }

    // 25:39
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_house, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(rv_characters_list as RecyclerView) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(ItemDivider())
            adapter = charactersAdapter
        }
    }

    companion object {
        private const val HOUSE_NAME = "house_name"

        @JvmStatic
        fun newInstance(houseName: String): HouseFragment {
            return HouseFragment().apply {
                // аргумент извлекаем в методе onCreate
                arguments = bundleOf(HOUSE_NAME to houseName)
            }
        }
    }

}

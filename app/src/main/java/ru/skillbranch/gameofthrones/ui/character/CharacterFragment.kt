package ru.skillbranch.gameofthrones.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_character.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.RootActivity

// 35:23
// 39:46 - после демонстрации разметки
class CharacterFragment : Fragment() {
    // navArgs() содержит все аргументы, которые мы передали при помощи action в текущий фрагмент
    private val args: CharacterFragmentArgs by navArgs()
    private lateinit var mViewModel: CharacterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this, CharacterViewModelFactory(args.characterId))
            .get(CharacterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_character, container, false)

    // 36:00
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

        // герб
        iv_arms.setImageResource(arms)

        with(collapsing_layout) {
            // здесь важно устанавливать именно ссылки на ресурсы, а не напрямую цвета

            setBackgroundResource(scrim)
            setContentScrimResource(scrim) // цвет в свёрнутом режиме
            setStatusBarScrimResource(scrimDark)
        }

        // bug fix
        // иначе collapsing toolbar layout не может корректно рассчитать высоту title
        // поэтому вызываем метод, который позволяет пересчитать размер этого layout
        // (requestLayout() вывывает заново весь процесс отрисовки и измерения вью)
        // 41:27
        collapsing_layout.post { collapsing_layout.requestLayout() }

        mViewModel.getCharacter().observe(requireActivity(), Observer<CharacterFull> { character ->
            if (character == null) return@Observer

            val iconColor = requireContext().getColor(houseType.accentColor)

            // set decor_icon color
            // compoundDrawables - та иконка слева от лэйбла
            listOf(tv_words_label, tv_born_label, tv_titles_label, tv_aliases_label)
                .forEach { it.compoundDrawables.first().setTint(iconColor) }

            tv_words.text = character.words
            tv_born.text = character.born
            tv_titles.text = character.titles
                .filter { it.isNotEmpty() }
                .joinToString("\n")
            tv_aliases.text = character.aliases
                .filter { it.isNotEmpty() }
                .joinToString("\n")

            // TODO При возвращении с экрана персонажа остаются только отфильтрованные при поиске элементы

            // TODO Не отображается кнопка отца Daena Targaryen (Daena the Defiand)
            // https://www.anapioficeandfire.com/api/characters/270

            character.father?.let {
                group_father.visibility = View.VISIBLE
                btn_father.text = it.name
                val action =
                    CharacterFragmentDirections.actionNavCharacterSelf(it.id, it.house, it.name)
                btn_father.setOnClickListener { findNavController().navigate(action) }
            }

            character.mother?.let {
                group_mother.visibility = View.VISIBLE
                btn_mother.text = it.name
                val action =
                    CharacterFragmentDirections.actionNavCharacterSelf(it.id, it.house, it.name)
                btn_mother.setOnClickListener { findNavController().navigate(action) }
            }

            if (character.died.isNotBlank()) {
                Snackbar.make(
                    coordinator,
                    "Died in : ${character.died}",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .show()
            }
        })
    }
}
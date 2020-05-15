package ru.skillbranch.gameofthrones.data.local.entities

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import ru.skillbranch.gameofthrones.R
import java.lang.IllegalStateException

// 23:20
enum class HouseType(
    val title: String,
    @DrawableRes
    val icon: Int,
    @DrawableRes
    val coastOfArms: Int,
    @ColorRes
    val primaryColor: Int,
    @ColorRes
    val accentColor: Int,
    @ColorRes
    val darkColor: Int
) {
    STARK("Stark", R.drawable.stark_icon, R.drawable.stark, R.color.stark_primary, R.color.stark_accent, R.color.stark_dark),
    LANNISTER("Lannister", R.drawable.lannister_icon, R.drawable.lannister, R.color.lannister_primary, R.color.lannister_accent, R.color.lannister_dark),
    TARGARYEN("Targaryen", R.drawable.targaryen_icon, R.drawable.targaryen, R.color.targaryen_primary, R.color.targaryen_accent, R.color.targaryen_dark),
    BARATHEON("Baratheon", R.drawable.baratheon_icon, R.drawable.baratheon, R.color.baratheon_primary, R.color.baratheon_accent, R.color.baratheon_dark),
    GREYJOY("Greyjoy", R.drawable.greyjoy_icon, R.drawable.greyjoy, R.color.greyjoy_primary, R.color.greyjoy_accent, R.color.greyjoy_dark),
    MARTELL("Martell", R.drawable.martell_icon, R.drawable.martell, R.color.martell_primary, R.color.martell_accent, R.color.martell_dark),
    TYRELL("Tyrell", R.drawable.tyrell_icon, R.drawable.tyrell, R.color.tyrell_primary, R.color.tyrell_accent, R.color.tyrell_dark);

    companion object {
        fun fromString(title: String): HouseType {
            return when (title) {
                "Stark" -> STARK
                "Lannister" -> LANNISTER
                "Targaryen" -> TARGARYEN
                "Baratheon" -> BARATHEON
                "Greyjoy" -> GREYJOY
                "Martell" -> MARTELL
                "Tyrell" -> TYRELL
                else -> throw IllegalStateException("unknown house $title")
            }
        }
    }

}
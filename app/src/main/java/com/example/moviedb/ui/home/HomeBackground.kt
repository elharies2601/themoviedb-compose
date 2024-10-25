package com.example.moviedb.ui.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.kmpalette.palette.graphics.Palette

@Composable
internal fun Palette?.paletteBackgroundColor(): State<Color> {
    val defaultBackground = MaterialTheme.colorScheme.background
    return remember(this) {
        derivedStateOf {
            val rgb = this?.dominantSwatch?.rgb
            if (rgb == null) {
                defaultBackground
            } else {
                Color(rgb)
            }
        }
    }
}

@Composable
internal fun Palette?.paletteTextColor(): State<Color> {
    val defaultTitleTextColor = MaterialTheme.colorScheme.onBackground
    return remember(this) {
        derivedStateOf {
            val rgb = this?.dominantSwatch?.bodyTextColor
            if (rgb == null) {
                defaultTitleTextColor
            } else {
                Color(rgb)
            }
        }
    }
}
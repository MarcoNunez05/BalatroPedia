// Última modificación: 21/11/2026
// Autor: Marco Núñez

package com.example.balatropedia.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

enum class ColorBlindMode(val label: String) {
    NONE("Desactivado"),
    PROTANOPIA("Protanopia (Rojo)"),
    DEUTERANOPIA("Deuteranopia (Verde)"),
    TRITANOPIA("Tritanopia (Azul)"),
    MONOCHROME("Alto Contraste / Monocromo")
}

data class AccessibilityState(
    val fontScale: Float = 1.0f,
    val fontScaleOption: String = "Normal (100%)",
    val colorBlindMode: ColorBlindMode = ColorBlindMode.NONE,
    val transitionToggle: Boolean = false,
    val talkBackOptimized: Boolean = false
)

val LocalAccessibilityState = staticCompositionLocalOf { AccessibilityState() }
class AccessibilityPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("balatropedia_accessibility", Context.MODE_PRIVATE)

    fun load(): AccessibilityState {
        val fontOption = prefs.getString("font_option", "Normal (100%)") ?: "Normal (100%)"
        val defaultScale = scaleFromOption(fontOption)
        val scale = prefs.getFloat("font_scale", defaultScale)

        val modeName = prefs.getString("color_blind_mode", ColorBlindMode.NONE.name) ?: ColorBlindMode.NONE.name
        val mode = runCatching { ColorBlindMode.valueOf(modeName) }.getOrDefault(ColorBlindMode.NONE)
        val reducedAnim = prefs.getBoolean("reduced_animations", false)
        val talkBackOpt = prefs.getBoolean("talkback_optimized", false)

        return AccessibilityState(
            fontScale = scale,
            fontScaleOption = fontOption,
            colorBlindMode = mode,
            transitionToggle = reducedAnim,
            talkBackOptimized = talkBackOpt
        )
    }

    fun save(state: AccessibilityState) {
        prefs.edit()
            .putString("font_option", state.fontScaleOption)
            .putFloat("font_scale", state.fontScale)
            .putString("color_blind_mode", state.colorBlindMode.name)
            .putBoolean("reduced_animations", state.transitionToggle)
            .putBoolean("talkback_optimized", state.talkBackOptimized)
            .apply()
    }

    companion object {
        fun scaleFromOption(option: String): Float {
            return when (option.trim()) {
                "Grande (150%)" -> 1.5f
                "Muy Grande (200%)" -> 2.0f
                else -> 1.0f
            }
        }
    }
}

class AccessibilityViewModel(private val prefs: AccessibilityPrefs) : ViewModel() {
    var state by mutableStateOf(prefs.load())
        private set

    fun updatePreferences(
        fontScaleOption: String,
        colorBlindOption: String,
        transitionOption: String,
        talkBackOption: String
    ) {
        val scale = AccessibilityPrefs.scaleFromOption(fontScaleOption)
        val mode = ColorBlindMode.entries.firstOrNull { it.label == colorBlindOption } ?: ColorBlindMode.NONE
        val transitionTog = transitionOption == "Desactivada"
        val talkBackOpt = talkBackOption == "Optimizado para TalkBack"

        val newState = AccessibilityState(
            fontScale = scale,
            fontScaleOption = fontScaleOption,
            colorBlindMode = mode,
            transitionToggle = transitionTog,
            talkBackOptimized = talkBackOpt
        )
        prefs.save(newState)
        state = newState
    }
}

class AccessibilityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccessibilityViewModel(AccessibilityPrefs(context.applicationContext)) as T
    }
}
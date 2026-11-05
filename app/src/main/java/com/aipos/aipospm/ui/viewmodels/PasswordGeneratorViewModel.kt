package com.aipos.aipospm.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GeneratorUiState(
    val generatedPassword: String = "",
    val length: Int = 16,
    val useUppercase: Boolean = true,
    val useLowercase: Boolean = true,
    val useDigits: Boolean = true,
    val useSymbols: Boolean = true,
    val useHyphen: Boolean = true,
    val useSpaces: Boolean = false,
    val useUnderscore: Boolean = true,
    val useExtendedChars: Boolean = false,
    val useBrackets: Boolean = true,
    val strength: PasswordStrength = PasswordStrength.NONE,
    val history: List<String> = emptyList()
)

class PasswordGeneratorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    init {
        generatePassword()
    }

    fun recordCurrentPasswordInHistory() {
        val current = _uiState.value.generatedPassword
        if (current.isNotEmpty() && !_uiState.value.history.contains(current)) {
            _uiState.value = _uiState.value.copy(
                history = (listOf(current) + _uiState.value.history).take(10)
            )
        }
    }

    fun updateLength(length: Int) {
        _uiState.value = _uiState.value.copy(length = length)
        generatePassword()
    }

    fun toggleUppercase(enabled: Boolean) {
        if (!enabled && !hasOtherCharSets(uppercase = false)) return
        _uiState.value = _uiState.value.copy(useUppercase = enabled)
        generatePassword()
    }

    fun toggleLowercase(enabled: Boolean) {
        if (!enabled && !hasOtherCharSets(lowercase = false)) return
        _uiState.value = _uiState.value.copy(useLowercase = enabled)
        generatePassword()
    }

    fun toggleDigits(enabled: Boolean) {
        if (!enabled && !hasOtherCharSets(digits = false)) return
        _uiState.value = _uiState.value.copy(useDigits = enabled)
        generatePassword()
    }

    fun toggleSymbols(enabled: Boolean) {
        if (!enabled && !hasOtherCharSets(symbols = false)) return
        _uiState.value = _uiState.value.copy(useSymbols = enabled)
        generatePassword()
    }

    fun toggleHyphen(enabled: Boolean) {
        if (!enabled && !hasOtherCharSets(hyphen = false)) return
        _uiState.value = _uiState.value.copy(useHyphen = enabled)
        generatePassword()
    }

    fun toggleSpaces(enabled: Boolean) {
        if (!enabled && !hasOtherCharSets(spaces = false)) return
        _uiState.value = _uiState.value.copy(useSpaces = enabled)
        generatePassword()
    }

    fun toggleUnderscore(enabled: Boolean) {
        if (!enabled && !hasOtherCharSets(underscore = false)) return
        _uiState.value = _uiState.value.copy(useUnderscore = enabled)
        generatePassword()
    }

    fun toggleExtendedChars(enabled: Boolean) {
        if (!enabled && !hasOtherCharSets(extendedChars = false)) return
        _uiState.value = _uiState.value.copy(useExtendedChars = enabled)
        generatePassword()
    }

    fun toggleBrackets(enabled: Boolean) {
        if (!enabled && !hasOtherCharSets(brackets = false)) return
        _uiState.value = _uiState.value.copy(useBrackets = enabled)
        generatePassword()
    }

    fun generatePassword(): String {
        val state = _uiState.value
        val password = generateRandomPassword(
            length = state.length,
            useUppercase = state.useUppercase,
            useLowercase = state.useLowercase,
            useDigits = state.useDigits,
            useSymbols = state.useSymbols,
            useHyphen = state.useHyphen,
            useSpaces = state.useSpaces,
            useUnderscore = state.useUnderscore,
            useExtendedChars = state.useExtendedChars,
            useBrackets = state.useBrackets
        )
        val strength = evaluateStrength(password)
        _uiState.value = state.copy(generatedPassword = password, strength = strength)
        return password
    }

    companion object {
        private val SECURE_RANDOM = java.security.SecureRandom()

        fun generateRandomPassword(
            length: Int = 16,
            useUppercase: Boolean = true,
            useLowercase: Boolean = true,
            useDigits: Boolean = true,
            useSymbols: Boolean = true,
            useHyphen: Boolean = true,
            useSpaces: Boolean = false,
            useUnderscore: Boolean = true,
            useExtendedChars: Boolean = false,
            useBrackets: Boolean = true
        ): String {
            val charPool = buildString {
                if (useUppercase) append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
                if (useLowercase) append("abcdefghijklmnopqrstuvwxyz")
                if (useDigits) append("0123456789")
                if (useSymbols) append("!@#\$%^&*+=|;:',.?/~`\"\\")
                if (useHyphen) append('-')
                if (useSpaces) append(' ')
                if (useUnderscore) append('_')
                if (useExtendedChars) append("ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿ")
                if (useBrackets) append("<>[]{}()")
            }
            if (charPool.isEmpty()) return ""
            return buildString {
                repeat(length) {
                    append(charPool[SECURE_RANDOM.nextInt(charPool.length)])
                }
            }
        }
    }

    private fun evaluateStrength(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength.NONE
        var score = 0
        if (password.length >= 8) score++
        if (password.length >= 12) score++
        if (password.length >= 20) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        return when {
            score <= 2 -> PasswordStrength.WEAK
            score <= 4 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.STRONG
        }
    }

    private fun hasOtherCharSets(
        uppercase: Boolean = _uiState.value.useUppercase,
        lowercase: Boolean = _uiState.value.useLowercase,
        digits: Boolean = _uiState.value.useDigits,
        symbols: Boolean = _uiState.value.useSymbols,
        hyphen: Boolean = _uiState.value.useHyphen,
        spaces: Boolean = _uiState.value.useSpaces,
        underscore: Boolean = _uiState.value.useUnderscore,
        extendedChars: Boolean = _uiState.value.useExtendedChars,
        brackets: Boolean = _uiState.value.useBrackets
    ): Boolean {
        return listOf(uppercase, lowercase, digits, symbols, hyphen, spaces, underscore, extendedChars, brackets).any { it }
    }
}

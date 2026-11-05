package com.aipos.aipospm

import com.aipos.aipospm.ui.viewmodels.PasswordGeneratorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordGeneratorOptionsTest {

    @Test
    fun eachOptionalCharacterSetCanBeSelectedIndependently() {
        assertEquals("-".repeat(32), only(hyphen = true))
        assertEquals("_".repeat(32), only(underscore = true))
        assertEquals(" ".repeat(32), only(spaces = true))
        assertTrue(only(extended = true).all { it in EXTENDED_LETTERS })
        assertTrue(only(brackets = true).all { it in "<>[]{}()" })
    }

    @Test
    fun noSelectedCharacterSetsReturnAnEmptyPassword() {
        assertEquals("", only())
    }

    private fun only(
        hyphen: Boolean = false,
        spaces: Boolean = false,
        underscore: Boolean = false,
        extended: Boolean = false,
        brackets: Boolean = false
    ): String = PasswordGeneratorViewModel.generateRandomPassword(
        length = 32,
        useUppercase = false,
        useLowercase = false,
        useDigits = false,
        useSymbols = false,
        useHyphen = hyphen,
        useSpaces = spaces,
        useUnderscore = underscore,
        useExtendedChars = extended,
        useBrackets = brackets
    )

    private companion object {
        const val EXTENDED_LETTERS = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿ"
    }
}

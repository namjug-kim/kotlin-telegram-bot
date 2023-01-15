package com.github.kotlintelegrambot.entities

import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName as Name

data class KeyboardReplyMarkup(
    val keyboard: List<List<KeyboardButton>>,
    @Name("input_field_placeholder") val inputFieldPlaceholder: String? = null,
    @Name("resize_keyboard") val resizeKeyboard: Boolean = false,
    @Name("one_time_keyboard") val oneTimeKeyboard: Boolean = false,
    @Name("is_persistent") val isPersistent: Boolean = false,
    val selective: Boolean? = null
) : ReplyMarkup {

    constructor(
        vararg keyboard: KeyboardButton,
        inputFieldPlaceholder: String? = null,
        resizeKeyboard: Boolean = false,
        oneTimeKeyboard: Boolean = false,
        isPersistent: Boolean = false,
        selective: Boolean? = null
    ) : this(listOf(keyboard.toList()), inputFieldPlaceholder, resizeKeyboard, oneTimeKeyboard, isPersistent, selective)

    companion object {
        val GSON = Gson()

        fun createSimpleKeyboard(
            keyboard: List<List<String>>,
            inputFieldPlaceholder: String? = null,
            resizeKeyboard: Boolean = true,
            oneTimeKeyboard: Boolean = false,
            isPersistent: Boolean = false,
            selective: Boolean? = null
        ): KeyboardReplyMarkup {
            return KeyboardReplyMarkup(
                keyboard.map { it.map { KeyboardButton(text = it) } },
                inputFieldPlaceholder,
                resizeKeyboard,
                oneTimeKeyboard,
                isPersistent,
                selective
            )
        }
    }

    override fun toString(): String = GSON.toJson(this)
}

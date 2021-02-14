package com.github.kotlintelegrambot.network.apiclient

import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class GetUpdatesIT : ApiClientIT() {

    @Test
    fun `getUpdates returning an update with callback query containing inline keyboard buttons`() {
        givenGetUpdatesResponse(
            """
            {
                "ok": true,
                "result": [
                    {
                        "update_id": 1,
                        "callback_query": {
                            "id": "1",
                            "from": {
                                "id": 1,
                                "is_bot": false,
                                "first_name": "TestName",
                                "username": "testname",
                                "language_code": "de"
                            },
                            "message": {
                                "message_id": 1,
                                "from": {
                                    "id": 1,
                                    "is_bot": true,
                                    "first_name": "testbot",
                                    "username": "testbot"
                                },
                                "chat": {
                                    "id": 1,
                                    "first_name": "TestName",
                                    "username": "testname",
                                    "type": "private"
                                },
                                "date": 1606317592,
                                "text": "Hello, inline buttons!",
                                "reply_markup": {
                                    "inline_keyboard": [
                                        [
                                            {
                                                "text": "Test Inline Button",
                                                "callback_data": "testButton"
                                            }
                                        ],
                                        [
                                            {
                                                "text": "Show alert",
                                                "callback_data": "showAlert"
                                            }
                                        ]
                                    ]
                                }
                            },
                            "chat_instance": "1",
                            "data": "showAlert"
                        }
                    }
                ]
            }
            """.trimIndent()
        )

        val getUpdatesResult = sut.getUpdates().execute()

        val expectedUpdates = listOf(
            Update(
                updateId = 1,
                callbackQuery = CallbackQuery(
                    id = "1",
                    from = User(
                        id = 1,
                        isBot = false,
                        firstName = "TestName",
                        username = "testname",
                        languageCode = "de"
                    ),
                    message = Message(
                        messageId = 1,
                        from = User(
                            id = 1,
                            isBot = true,
                            firstName = "testbot",
                            username = "testbot"
                        ),
                        chat = Chat(
                            id = 1,
                            firstName = "TestName",
                            username = "testname",
                            type = "private"
                        ),
                        date = 1606317592,
                        text = "Hello, inline buttons!",
                        replyMarkup = InlineKeyboardMarkup.create(
                            listOf(
                                InlineKeyboardButton.CallbackData(
                                    text = "Test Inline Button",
                                    callbackData = "testButton"
                                )
                            ),
                            listOf(
                                InlineKeyboardButton.CallbackData(
                                    text = "Show alert",
                                    callbackData = "showAlert"
                                )
                            )
                        )
                    ),
                    chatInstance = "1",
                    data = "showAlert"
                )
            )
        )
        val actualUpdates: List<Update> = getUpdatesResult.body()?.result!!
        assertEquals(expectedUpdates, actualUpdates)
    }

    @Test
    fun `getUpdates with a channel post containing sender chat`() {
        givenGetUpdatesResponse(
            """
                {
                    "ok": true,
                    "result": [
                        {
                            "update_id": 132059007,
                            "channel_post": {
                                "message_id": 18,
                                "sender_chat": {
                                    "id": -1001367429635,
                                    "title": "[Channel] Test Telegram Bot",
                                    "username": "testtelegrambotapi",
                                    "type": "channel"
                                },
                                "chat": {
                                    "id": -1001367429635,
                                    "title": "[Channel] Test Telegram Bot",
                                    "username": "testtelegrambotapi",
                                    "type": "channel"
                                },
                                "date": 1612631280,
                                "text": "Test"
                            }
                        }
                    ]
                }
            """.trimIndent()
        )

        val getUpdatesResult = sut.getUpdates().execute()

        val expectedGetUpdatesResult = listOf(
            Update(
                updateId = 132059007,
                channelPost = Message(
                    messageId = 18,
                    senderChat = Chat(
                        id = -1001367429635,
                        title = "[Channel] Test Telegram Bot",
                        username = "testtelegrambotapi",
                        type = "channel",
                    ),
                    chat = Chat(
                        id = -1001367429635,
                        title = "[Channel] Test Telegram Bot",
                        username = "testtelegrambotapi",
                        type = "channel",
                    ),
                    date = 1612631280,
                    text = "Test"
                )
            )
        )
        assertEquals(expectedGetUpdatesResult, getUpdatesResult.body()?.result)
    }

    @Test
    fun `getUpdates with a message containing a date after 03h14m07s UTC on 19 January 2038`() {
        givenGetUpdatesResponse(
            """
                {
                    "ok": true,
                    "result": [
                        {
                            "update_id": 132059007,
                            "message": {
                                "message_id": 18,
                                "chat": {
                                    "id": -1001367429635,
                                    "title": "[Channel] Test Telegram Bot",
                                    "username": "testtelegrambotapi",
                                    "type": "channel"
                                },
                                "date": 2147483648,
                                "text": "Test"
                            }
                        }
                    ]
                }
            """.trimIndent()
        )

        val getUpdatesResult = sut.getUpdates().execute()

        val expectedGetUpdatesResult = listOf(
            Update(
                updateId = 132059007,
                message = Message(
                    messageId = 18,
                    chat = Chat(
                        id = -1001367429635,
                        title = "[Channel] Test Telegram Bot",
                        username = "testtelegrambotapi",
                        type = "channel",
                    ),
                    date = 2147483648,
                    text = "Test"
                )
            )
        )
        assertEquals(expectedGetUpdatesResult, getUpdatesResult.body()?.result)
    }

    private fun givenGetUpdatesResponse(getUpdatesResponseJson: String) {
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(getUpdatesResponseJson)
        mockWebServer.enqueue(mockedResponse)
    }
}
package com.khoben.ticker.api.finnhub.websocket

import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import java.lang.reflect.Type

@ExperimentalSerializationApi
class KotlinSerializationMessageAdapter<T> constructor(
    private val serializer: KSerializer<T>,
    private val stringFormat: StringFormat
) : MessageAdapter<T> {

    override fun fromMessage(message: Message): T {
        val stringValue = when (message) {
            is Message.Text -> message.value
            is Message.Bytes -> String(message.value)
        }
        return stringFormat.decodeFromString(serializer, stringValue)
    }

    override fun toMessage(data: T): Message {
        return Message.Text(stringFormat.encodeToString(serializer, data))
    }

    class Factory(private val stringFormat: StringFormat) : MessageAdapter.Factory {

        override fun create(type: Type, annotations: Array<Annotation>): MessageAdapter<*> {
            return KotlinSerializationMessageAdapter(serializer(type), stringFormat)
        }
    }
}

@ExperimentalSerializationApi
@JvmName("create")
fun StringFormat.asMessageAdapterFactory(): MessageAdapter.Factory {
    return KotlinSerializationMessageAdapter.Factory(this)
}
package com.khoben.ticker.api.finnhub.websocket

import com.khoben.ticker.api.finnhub.websocket.model.FinnHubSubscribeMessage
import com.khoben.ticker.api.finnhub.websocket.model.FinnHubWebSocketResponse
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.channels.ReceiveChannel

interface FinnHubWSService {
    @Receive
    fun observeEvents(): ReceiveChannel<WebSocketEvent>

    @Receive
    fun observeApi(): ReceiveChannel<FinnHubWebSocketResponse>

    @Send
    fun subscribe(subscribe: FinnHubSubscribeMessage)
}
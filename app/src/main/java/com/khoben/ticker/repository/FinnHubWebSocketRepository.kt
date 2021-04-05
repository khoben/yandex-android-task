package com.khoben.ticker.repository

import com.khoben.ticker.api.finnhub.websocket.FinnHubWebSocket
import com.khoben.ticker.api.finnhub.websocket.model.FinnHubSubscribeMessage
import com.khoben.ticker.model.WebSocketTickerUpdate
import com.tinder.scarlet.websocket.WebSocketEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class FinnHubWebSocketRepository(private val socket: FinnHubWebSocket) : WebSocketRepository {

    @FlowPreview
    override fun startSocket(tickers: List<String>): Flow<WebSocketTickerUpdate> {
        // subscribe on websocket opened
        socket.observeEvents()
            .consumeAsFlow()
            .onEach {
                if (it is WebSocketEvent.OnConnectionOpened) {
                    tickers.forEach { symbol ->
                        socket.subscribe(
                            FinnHubSubscribeMessage(
                                type = "subscribe",
                                symbol = symbol
                            )
                        )
                    }
                }
            }.launchIn(CoroutineScope(Dispatchers.IO))

        return socket.observeApi()
            .consumeAsFlow()
            .filterNot { response -> response.data == null }
            .flatMapMerge { response -> response.data!!.asFlow() }
            .map { tickerUpdate ->
                WebSocketTickerUpdate(ticker = tickerUpdate.symbol, price = tickerUpdate.lastPrice)
            }
    }

    /**
     * Actually for Scarlet lib isn't exist straight forward method to closing socket
     * Its controlled by provided lifecycle
     */
    override fun stopSocket() {
    }
}
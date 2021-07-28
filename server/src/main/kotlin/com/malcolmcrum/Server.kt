package com.malcolmcrum

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


fun main() {
    val id = Random().nextInt()
    val webSocketHandler = WebSocketHandler()
    Spark.webSocket("/websocket", webSocketHandler)
    Spark.staticFiles.location("/web"); // resources folder
    Spark.get("/") { req, res -> res.redirect("/web/index.html") }
    Spark.get("/id") { req, res -> id }
    Spark.init()

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            webSocketHandler.shutDown()
        }
    })
}

@WebSocket(maxTextMessageSize = 10000000, maxBinaryMessageSize = 10000000)
class WebSocketHandler {
    private val sessions: Queue<Session> = ConcurrentLinkedQueue()

    @OnWebSocketConnect
    fun connected(session: Session) {
        println("Session connected")
        sessions.add(session)
    }

    @OnWebSocketClose
    fun closed(session: Session, statusCode: Int, reason: String?) {
        println("Session closed: $statusCode $reason")
        sessions.remove(session)
    }

    @OnWebSocketMessage
    fun message(session: Session, message: String) {
        println("Got: $message") // Print message
        session.remote.sendString(message) // and send it back
    }

    fun shutDown() {
        for (session in sessions) {
            session.remote.sendString("reconnect")
        }
    }
}
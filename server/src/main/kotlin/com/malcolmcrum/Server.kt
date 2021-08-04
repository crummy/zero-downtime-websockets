package com.malcolmcrum

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

val serverId = Random().nextInt()

fun main() {
    val webSocketHandler = WebSocketHandler()
    Spark.webSocket("/websocket", webSocketHandler)
    Spark.staticFiles.location("/web"); // resources folder
    Spark.get("/") { req, res -> res.redirect("/web/index.html") }
    Spark.get("/id") { req, res -> serverId }
    Spark.init()

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            Spark.unmap("/websocket")
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
        session.remote.sendString(toJson("hello"))
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
        session.remote.sendString(toJson(message)) // and send it back
    }

    fun shutDown() {
        for (session in sessions) {
            session.remote.sendString(toJson("reconnect"))
        }
    }

    fun toJson(message: String): String {
        return """{"message": "$message", "server": "$serverId"}"""
    }
}
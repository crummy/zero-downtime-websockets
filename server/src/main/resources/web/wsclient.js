// from https://www.pegaxchange.com/2018/03/23/websocket-client/

var webSocket = null;
var ws_protocol = null;
var ws_hostname = null;
var ws_port = null;
var ws_endpoint = null;
var echo_loop = false;
window.onload = () => {
    document.getElementById("echo_loop").onclick = () => echo_loop = !echo_loop
}

/**
 * Event handler for clicking on button "Connect"
 */
function onConnectClick() {
    var ws_protocol = document.getElementById("protocol").value;
    var ws_hostname = document.getElementById("hostname").value;
    var ws_port = document.getElementById("port").value;
    var ws_endpoint = document.getElementById("endpoint").value;
    openWSConnection(ws_protocol, ws_hostname, ws_port, ws_endpoint);
}

/**
 * Event handler for clicking on button "Disconnect"
 */
function onDisconnectClick() {
    webSocket.close();
}

/**
 * Open a new WebSocket connection using the given parameters
 */
function openWSConnection(protocol, hostname, port, endpoint) {
    var webSocketURL = null;
    webSocketURL = protocol + "://" + hostname + ":" + port + endpoint;
    console.log("openWSConnection::Connecting to: " + webSocketURL);
    try {
        webSocket = new WebSocket(webSocketURL);
        webSocket.onopen = function (openEvent) {
            console.log("WebSocket OPEN", openEvent);
            document.getElementById("btnSend").disabled = false;
            document.getElementById("btnConnect").disabled = true;
            document.getElementById("btnDisconnect").disabled = false;
        };
        webSocket.onclose = function (closeEvent) {
            console.log("WebSocket CLOSE", closeEvent);
            document.getElementById("btnSend").disabled = true;
            document.getElementById("btnConnect").disabled = false;
            document.getElementById("btnDisconnect").disabled = true;
        };
        webSocket.onerror = function (errorEvent) {
            console.log("WebSocket ERROR", errorEvent);
        };
        webSocket.onmessage = function (messageEvent) {
            const wsMsg = JSON.parse(messageEvent.data);
            console.log("WebSocket MESSAGE: ", wsMsg);
            document.getElementById("incomingMsgOutput").value += wsMsg.server + "/" + wsMsg.time + ": " + wsMsg.message + "\n"
            if (wsMsg.message == "reconnect") {
                webSocket.close(4900, "reconnecting")
                openWSConnection(protocol, hostname, port, endpoint)
            }
        };
    } catch (exception) {
        console.error(exception);
    }
}

/**
 * Send a message to the WebSocket server
 */
function onSendClick() {
    if (webSocket.readyState != WebSocket.OPEN) {
        console.error("webSocket is not open: " + webSocket.readyState);
        return;
    }
    const msg = document.getElementById("message").value;
    webSocket.send(msg);
    document.getElementById("incomingMsgOutput").value += "sent: " + msg + "\n";
}

function echoLoop() {
    setTimeout(() => {
        if (echo_loop && webSocket != null && webSocket.readyState == WebSocket.OPEN) webSocket.send("echo");
        echoLoop();
    }, 100)
}
echoLoop();
$(document).ready(function() {
    "use strict";

    var $content = $('#content');
    var $input = $('#input');

    window.WebSocket = window.WebSocket || window.MozWebSocket;
    if(!window.WebSocket) {
        content.html($('<p>', {
            text:'Sorry, but your browser doesn\'t support WebSocket.'
        }));
        return;
    }

    var $startForm = $('#start-form');
    var $startButton = $('#start-button');

    var modalStart = document.getElementById('modal-start');

    $startForm.on('submit', $startForm, function(e) {
        e.preventDefault();
        var name = $('#nameInput').val();
        if(name.length == 0 || name !== undefined) {
            startWebSocket(name);
            return false;
        }
    });
});

function startWebSocket(username) {
    var gameRunning = false;

    var modalStart = document.getElementById("modal-start");
    var $status = $('#status');
    var socketURL = $('body').data("ws-url");

    var players = [];

    var connection = new WebSocket(socketURL);
    connection.onopen = function() {
        console.log("web socket connected with server");
        $status.html("Connected");
        modalStart.style.display = "none";

        var obj = "{\"msgType\": \"playerJoined\", \"obj\": { \"name\":\"" + username + "\"}}";
//        var obj = {"msgType": "Join", "obj": "toto"}
        console.log(obj);
        connection.send(obj);
        gameRunning = true;
    }
    connection.onerror = function(error) {
        $status.html($('<p>', {
          text: 'Sorry, but there\'s some problem with your connection or the server is down.\n'
        }));
        console.log(error);
    }
    connection.onmessage = function(message) {
        console.log("received message from server");
        console.log(message);
        var msgJson = JSON.parse(message.data);
        console.log(msgJson);

        switch(msgJson.msgType) {
            case "clientResponse":
                break;
            case "clientPlayersUpdate":
                updatePlayers(msgJson.obj);
                break;
            case "clientMessageUpdate":
                updateMessage(msgJson.obj);
                break;
        }
    }

    $('#gameStart').click(function() {
        if(!gameRunning) {
            console.log("Starting game...");
        }
    });



    $('#send-msg-button').click(function(e) {
        e.preventDefault();
        var text = $('#msg-input').val();
        console.log(text);
        var chatMsg = "{\"msgType\": \"playerSendMessage\", \"obj\": { \"text\": \"" + text + "\", \"user\": \"" + username
        + "\", \"date\": \"" + Date.now() + "\" }}";
        connection.send(chatMsg);
        return false;
    });

    var arrowKeyCode = {37: 'LEFT', 38: 'UP', 39: 'RIGHT', 40: 'DOWN'}
    $(document).keydown(function(e) {
        if(gameRunning && arrowKeyCode[e.keyCode] !== undefined) {
            console.log("keyPressed : " + arrowKeyCode[e.keyCode]);
            //connection.send(JSON.stringify({"msgType" : "Tick", "obj": arrowKeyCode[e.keyCode]}));
            var obj2 = "{\"msgType\" : \"playerSendMessage\", \"obj\":  { \"text\":\"" + arrowKeyCode[e.keyCode] + "\", \"user\": \""+ username +"\", \"date\": \"" + Date.now() + "\"}}"
            connection.send(obj2);
        }
    });
}

function updatePlayers(players) {
    $('#people-list').empty();
    players.forEach(player => addPlayer(player))
}

function addPlayer(player) {
    console.log("adding " + player.name);
    var li = document.createElement("li");
    var peopleList = document.getElementById("people-list");
    li.appendChild(document.createTextNode(player.name));
    peopleList.appendChild(li);
}

function updateMessage(message) {
    console.log("update message");
    console.log(message);
    var msg = document.createElement("div");
    var msgText = document.createElement("div");
    var msgAuthor = document.createElement("div");
    var msgDate = document.createElement("div");

    msg.classList.add("message-item");
    msgAuthor.classList.add("message-author");
    msgText.classList.add("message-text");
    msgDate.classList.add("message-date");

    msgAuthor.textContent = message.user;
    msgText.textContent = message.text;
    msgDate.textContent = message.date;

    msg.appendChild(msgAuthor);
    msg.appendChild(msgText);
    msg.appendChild(msgDate);

    document.getElementById("message-list").appendChild(msg);
}
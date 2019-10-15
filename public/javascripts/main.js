var name;
var players = [];

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
        name = $('#nameInput').val();
        if(name.length == 0 || name !== undefined) {
            startWebSocket(name);
            return false;
        }
    });
});

function startWebSocket(username) {
    var gameRunning = false;

    var modalStart = document.getElementById("modal-start");
    var modalGameChoice = document.getElementById("modal-game-choice");
    var modalGame = document.getElementById("modal-game");
    var $status = $('#status');
    var socketURL = $('body').data("ws-url");

    var players = [];

    var connection = new WebSocket(socketURL);
    connection.onopen = function() {
        console.log("web socket connected with server");
        $status.html("Connected");
        modalStart.classList.remove("modal");
        modalStart.classList.add("hidden-modal");

        var obj = "{\"msgType\": \"playerJoined\", \"obj\": { \"name\":\"" + username + "\"}}";
//        var obj = {"msgType": "Join", "obj": "toto"}
        console.log(obj);
        connection.send(obj);
        gameRunning = true;
    };
    connection.onerror = function(error) {
        $status.html($('<p>', {
          text: 'Sorry, but there\'s some problem with your connection or the server is down.\n'
        }));
        $('#send-msg-button').prop('disabled', true);
        console.log(error);
    };
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
            case "error_nameAlreayUsed":
                modalStart.classList.remove("hidden-modal");
                modalStart.classList.add("modal");
                $status.html("Connected but username already used");
                $('#start-form div label').html("Please choose another name");
                break;
        }
    };
    connection.onclose = function(event) {
        console.log("WebSocket has been closed");
        startWebSocket(username);
    };

    $('#gameStart').click(function() {
        if(!gameRunning) {
            console.log("Starting game...");
        }
    });

    $('#show-game-choice-button').click(function(e) {
        e.preventDefault();
        console.log("play button clicked");
        displayPlayersChoice();
        modalGameChoice.classList.remove("hidden-modal");
        modalGameChoice.classList.add("modal");

        var startGameButton = $('#start-game-button');
        startGameButton.click(function(){
            var users = document.getElementById("multiselect_to");
            console.log("size to : " + users.length);
            return false;
        });

        var closeGameChoice = $('#close-game-choice-button');
        closeGameChoice.click(function(){
            modalGameChoice.classList.remove("modal");
            modalGameChoice.classList.add("hidden-modal");
            return false;
        });

        return false;
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
            var obj2 = "{\"msgType\" : \"playerMove\", \"obj\":  { \"text\":\"" + arrowKeyCode[e.keyCode] + "\", \"user\": \""+ username +"\", \"date\": \"" + Date.now() + "\"}}"
            connection.send(obj2);
        }
    });
}

function updatePlayers(playersList) {
    $('#people-list').empty();
    players = [];
    playersList.forEach(player => addPlayer(player))
    players = playersList;

    for(var i = 0; i < players.length; i++) {
        console.log(players[i]);
    }

    console.log("new player added: " + players.length);

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

    var date = new Date();
    var hours = date.getHours();

    var minutes = "0" + date.getMinutes();
    var time = hours + ":" + minutes.substr(-2);
    console.log(time);

    msgAuthor.textContent = message.user;
    msgText.textContent = message.text;
    msgDate.textContent = time;

    msg.appendChild(msgAuthor);
    msg.appendChild(msgText);
    msg.appendChild(msgDate);

    if(name === message.user) msg.classList.add("message-self");

    document.getElementById("message-list").appendChild(msg);
    $('#msg-input').val("");
}

function displayPlayersChoice() {
    var usersSelector = document.getElementById("multiselect");
    usersSelector.options.length = 0;
    var playersSelector = document.getElementById("multiselect_to");
    playersSelector.options.length = 0;
    players.forEach(player => {
        console.log(player);
        var option = document.createElement("option");
        option.value = player.name;
        option.text = player.name;
        usersSelector.appendChild(option);
    });

    $('#multiselect').multiselect();
}
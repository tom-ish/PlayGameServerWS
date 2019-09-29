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

    var $startForm = $('form');
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
    }

    $('#gameStart').click(function() {
        if(!gameRunning) {
            console.log("Starting game...");
        }
    });

    var arrowKeyCode = {37: 'LEFT', 38: 'UP', 39: 'RIGHT', 40: 'DOWN'}
    $(document).keydown(function(e) {
        if(gameRunning && arrowKeyCode[e.keyCode] !== undefined) {
            console.log("keyPressed : " + arrowKeyCode[e.keyCode]);
            //connection.send(JSON.stringify({"msgType" : "Tick", "obj": arrowKeyCode[e.keyCode]}));
            var obj2 = "{\"msgType\" : \"Tick\", \"obj\": \""+arrowKeyCode[e.keyCode]+"\"}"
            connection.send(obj2);
        }
    });
}
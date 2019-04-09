'use strict';


var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('#connecting');

var stompClient = null;
var senderUsername = null;


function connect() {
    senderUsername = document.querySelector('#username').innerText.trim();

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

// Connect to WebSocket Server.
connect();

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({senderUsername: senderUsername, type: 'JOIN', date: new Date()})
    )

    connectingElement.setAttribute('style','display: none;');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            senderUsername: senderUsername,
            content: messageInput.value,
            date: new Date(),
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('div');

    if(message.type === 'JOIN') {
        message.content = message.senderUsername + ' joined at ';
    } else if (message.type === 'LEAVE') {
        message.content = message.senderUsername + ' left at ';
    } else {
        var usernameElement = document.createElement('div');
 //       usernameElement.classList.add('font-weight-bold')
        var usernameText = document.createTextNode(message.senderUsername+"\t");
        usernameElement.appendChild(usernameText);
        var dateElement = document.createElement('small');
        var dateText = document.createTextNode(parse(message.date));
        dateElement.appendChild(dateText);

        usernameElement.appendChild(dateElement);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('div');
    textElement.classList.add('col');
    var messageText = document.createTextNode(message.content);

    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


messageForm.addEventListener('submit', sendMessage, true);


function parse(date){
    var messageDate = new Date(date);
    var currentDate = new Date();
    if (messageDate.getFullYear() != currentDate.getFullYear()) {
        return messageDate.getDate()+'.'+messageDate.getMonth()+'.'+messageDate.getFullYear();
    } else if(messageDate.getDate() != currentDate.getDate()) {
        return messageDate.toLocaleTimeString().replace(/:\d+ /, ' ')+' '+messageDate.getDate()+'.'+messageDate.getMonth();
    } else {
        return messageDate.toLocaleTimeString().replace(/:\d+ /, ' ');
    }
}
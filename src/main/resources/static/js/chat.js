'use strict';


var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('#connecting');
var taskId = document.getElementById('data').dataset.taskid;
var senderUsername = document.getElementById('data').dataset.currentuser;

var stompClient = null;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/public/task/' + taskId, onMessageReceived);
    // Tell your username to the server
    stompClient.send("/app/chat.addUser.task." + taskId,
        {},
        JSON.stringify({
            senderUsername: senderUsername,
            type: 'JOIN',
            date: new Date(),
            taskId: taskId
        })
    )
    connectingElement.setAttribute('style', 'display: none;');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            senderUsername: senderUsername,
            content: messageInput.value,
            date: new Date(),
            taskId: taskId,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage.task." + taskId, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    if (message.type === 'CHAT') {
        var messageElement = document.createElement('div');
        if (message.senderUsername == senderUsername) {
            messageElement.classList.add('text-right', 'my-2');
        }
        messageElement.id = 'chatMessage';
        var userDateElement = document.createElement('div');
        var userText = document.createTextNode(message.senderUsername + '\t\t');
        userDateElement.appendChild(userText);
        var dateElement = document.createElement('small');
        var dateText = document.createTextNode(parse(message.date));
        dateElement.appendChild(dateText);
        userDateElement.appendChild(dateElement);
        messageElement.appendChild(userDateElement);
        var textElement = document.createElement('div');
        textElement.classList.add('col');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);
        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;

        $(document).ready(
            function () {
                $('div#chatMessage').hover(
                    function (event) {
                        $(this).addClass('rowHighlight');
                    }
                    ,
                    function (event) {
                        $(this).removeClass('rowHighlight');
                    }
                );
            }
        );
    }
}

connect();
messageForm.addEventListener('submit', sendMessage, true);


function parse(date) {
    var messageDate = new Date(date);
    return messageDate.toLocaleTimeString().replace(/:\d+ /, ' ');
}
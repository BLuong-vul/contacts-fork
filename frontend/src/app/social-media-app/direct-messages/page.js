'use client';
import { useState, useEffect, useRef } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import styles from './direct-messages.module.css';

import { validateTokenWithRedirect } from '../../../components/Functions';



const DirectMessages = () => {
    const [userId, setUserId] = useState('');
    const [currentUsername, setCurrentUsername] = useState('');
    const [inputUsername, setInputUsername] = useState('');
    const [otherId, setOtherId] = useState('');
    const [message, setMessage] = useState(""); //track input
    const [isSendDisabled, setIsSendDisabled] = useState(true);
    const [conversation, setConversation] = useState([]); // holds received messages
    const stompClient = useRef(null); // websocket client ref

    // Fetch User ID and username when the component mounts
    useEffect(() => {
        const fetchUserId = async () => {
            try {
                const token = localStorage.getItem('token');
                validateTokenWithRedirect(token);

                const response = await fetch(`https://four800-webapp.onrender.com/user/info`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch ID: ' + response.statusText);
                }

                const result = await response.json();
                console.log("Current user ID: " + result.userId);
                setUserId(result.userId);
                setCurrentUsername(result.username);
            } catch (error) {
                console.error('Error fetching ID:', error);
                throw error;
            }
        };

        fetchUserId();
    }, []);


    // websocket connection and subscription
    useEffect(() => {
        if (userId && otherId) {
            const socket = new SockJS('https://four800-webapp.onrender.com/ws');
            stompClient.current = Stomp.over(socket);

            stompClient.current.connect({}, () => {
                console.log('Connected to WebSocket');

                const conversationId = userId < otherId ? `${userId}-${otherId}` : `${otherId}-${userId}`;

                // subscribe to topic to receive messages for current user
                stompClient.current.subscribe(`/topic/conversations/${conversationId}`, (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    setConversation((prev) => [...prev, receivedMessage]);
                });
            });

            return () => {
                // clean up websocket connection on component unmount
                if (stompClient.current) {
                    stompClient.current.disconnect(() => {
                        console.log('Disconnected from WebSocket');
                    });
                }
            };
        }
    }, [userId, otherId]);

    // sending message
    const handleSendMessage = () => {
        if (message.trim() && stompClient.current) {
            const newMessage = {
                body: message,
                senderId: userId,
                recipientId: otherId,
                dateSent: new Date(),
            };

            // send message to websocket endpoint
            stompClient.current.send('/app/sendMessage', {}, JSON.stringify(newMessage));
            setMessage(''); // clear input
        }
    };


    // For inputting username
    // Gets ID of corresponding user, assigns it to otherId
    const handleUsernameSubmit = async (event) => {
        event.preventDefault();
        if (inputUsername.trim() !== '') {
            console.log('Starting chat with:', inputUsername);
            try {
                const response = await fetch(`https://four800-webapp.onrender.com/user/id/${inputUsername}`);

                if (!response.ok) {
                    throw new Error(`Network response not ok: ${response.statusText}`);
                }

                const result = await response.json();
                setOtherId(result);
                console.log("Other user ID: " + result);
            } catch (error) {
                console.error('Error:', error);
            }
        }
    };


    
    const handleMessageChange = (e) => {
        setMessage(e.target.value); //update message state
        setIsSendDisabled(e.target.value.trim() === '')
    };
    
    //handling file input change
    const handleFileChange = (event) => {
        const file = event.target.files[0];
    };
    
    const handleAttachClick = () => {
        const fileInput = document.getElementById('fileInput');
        if (fileInput){
            fileInput.click();
        }
    };
    

     return (
        <div className={styles.container}>
            {/* Select user to chat */}
            <aside className={styles.container}>
                <form className={styles.form} onSubmit={handleUsernameSubmit}>
                    <label htmlFor="inputUsername" className={styles.label}>
                        Enter username:
                    </label>
                    <input
                        type="text"
                        id="input-username"
                        name="input-username"
                        className={styles.input}
                        placeholder="Username"
                        value={inputUsername}
                        onChange={(e) => setInputUsername(e.target.value)}
                        required
                    />
                    <button type="submit" className={styles.button}>
                        Start Chat
                    </button>
                </form>
            </aside>
            {/* END Select user to chat */}

            {/* Chat Area */}
            <main className={styles.chatArea}>
                <h2 className={styles.chatHeader}>Conversation with {otherId || 'No one selected'}</h2>
                <div className={styles.conversationArea}>
                    {conversation.map((msg, index) => (
                        <div key={index} className={styles.messageBubble}>
                            <strong>{msg.senderId === userId ? currentUsername : inputUsername}: </strong>
                            {msg.body}
                        </div>
                    ))}
                </div>
                <div className={styles.messageInputBar}>
                    <button className={styles.attachButton} onClick={handleAttachClick}>
                        📎
                    </button>
                    <input
                        type="text"
                        placeholder="Type a message..."
                        className={styles.messageInput}
                        value={message}
                        onChange={handleMessageChange}
                    />
                    <button
                        className={`${styles.sendButton} ${isSendDisabled ? styles.disabledButton : ''}`}
                        disabled={isSendDisabled}
                        onClick={handleSendMessage}
                    >
                        Send
                    </button>
                    <input
                        type="file"
                        id="fileInput"
                        style={{ display: 'none' }}
                        onChange={(e) => console.log(e.target.files)}
                    />
                </div>
            </main>
        </div>
    );
}
export default DirectMessages;

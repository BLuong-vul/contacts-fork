'use client';
import { useState, useEffect, useRef } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import styles from './direct-messages.module.css';
import Tooltip from '@mui/material/Tooltip';

import * as Fetch from '../../../components/Functions';


const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';


const DirectMessages = () => {
    const [userId, setUserId] = useState('');
    const [currentUsername, setCurrentUsername] = useState('');
    const [inputUsername, setInputUsername] = useState('');
    const [otherId, setOtherId] = useState('');
    const [message, setMessage] = useState(""); //track input
    const [isSendDisabled, setIsSendDisabled] = useState(true);
    const [conversation, setConversation] = useState([]); // holds received messages
    const stompClient = useRef(null); // websocket client ref
    const [selectedCategory, setSelectedCategory] = useState('mutual');

    // Placeholder arrays for friends categories
    const mutualFriends = ['Alice', 'Bob', 'Charlie'];
    const followers = ['David', 'Eva'];
    const following = ['Frank', 'Grace'];

    // Fetch User ID and username when the component mounts
    useEffect(() => {
        const fetchUserId = async () => {
            const result = await Fetch.getCurrentUserInfo();
            if (!result) window.location.href = '/login';
            setUserId(result.userId);
            setCurrentUsername(result.username);
        };

        fetchUserId();
    }, []);


    // websocket connection and subscription
    useEffect(() => {
        if (userId && otherId) {
            const socket = new SockJS(`${baseURL}/ws`);
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
                const response = await fetch(`${baseURL}/user/id/${inputUsername}`);

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
    
    const handleCategoryChange = (e) => {
        setSelectedCategory(e.target.value);
    };

     return (
        <div className={styles.container}>
            {/* Select user to chat */}
            <aside className={styles.asideContainer}>
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
                    
                    <Tooltip title="Choose Type" arrow>
                    	<div className={styles.dropdownContainer}>
                        <label htmlFor="friendsDropdown" className={styles.dropDownLabel}>
                            view:
                        </label>
                        <select
                            id="friendsDropdown"
                            className={styles.dropDownMenu}
                            value={selectedCategory}
                            onChange={handleCategoryChange}
                        >
                            <option value="mutual">Mutuals </option>
                            <option value="followers">Followers</option>
                            <option value="following">Following</option>
                        </select>
                    </div>
                    </Tooltip>
                    {
                    /*
                    *
                    * start the display of Mutuals, Friends, Followers
                    *
                    */
                    }
                    <div className={styles.listContainer}>
                        {selectedCategory === 'mutual' && (
                            <ul>
                                {mutualFriends.map((friend, index) => (
                                <li key={index}>{friend}</li>
                                ))}
                            </ul>
                        )}

                        {selectedCategory === 'followers' && (
                            <ul>
                                {followers.map((friend, index) => (
                                <li key={index}>{friend}</li>
                                ))}
                            </ul>
                        )}

                        {selectedCategory === 'following' && (
                            <ul>
                                {following.map((friend, index) => (
                                <li key={index}>{friend}</li>
                                ))}
                            </ul>
                        )}
                    </div>
                </form>
            </aside>
            {/* END Select user to chat */}
            
            
            {/* Chat Area */}
            <main className={styles.chatArea}>
                <h2 className={styles.chatHeader}>Conversation with {otherId || 'No one selected'}</h2>
                <div className={styles.conversationArea}>
                    {conversation.map((msg, index) => (
                        <div 
                            key={index} 
                            className={`${styles.messageBubble} ${
                                msg.senderId === userId ? styles.sentBubble : styles.receivedBubble
                            }`} 
                        >
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

'use client';
import { useState, useEffect, useRef } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import styles from './direct-messages.module.css';
import Tooltip from '@mui/material/Tooltip';
import { FaUser } from "react-icons/fa";

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

    // Friend categories
    const [mutuals, setMutuals] = useState([]);
    const [followers, setFollowers] = useState([]);
    const [following, setFollowing] = useState([]);

    const [selectedUserData, setSelectedUserData] = useState(null);

    // Fetch User ID and username when the component mounts
    // and friend categories
    useEffect(() => {
        const fetchUserId = async () => {
            const result = await Fetch.getCurrentUserInfo();
            if (!result) window.location.href = '/login';
            setUserId(result.userId);
            setCurrentUsername(result.username);
        };

        const fetchFriends = async () => {
            // Following users list
            const followingRes = await Fetch.getFollowingList();
            setFollowing(followingRes);

            // Followers list
            const followersRes = await Fetch.getFollowersList();
            setFollowers(followersRes);

            // Mutuals list
            const mutualsFilter = followingRes.filter(user => followersRes.some(follower => follower.userId===user.userId));
            setMutuals(mutualsFilter);
        }

        fetchUserId();
        fetchFriends();
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

            const allUsers = [...mutuals, ...followers, ...following];
            const userData = allUsers.find(user => user.username === inputUsername);

            if (userData) setSelectedUserData(userData);

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
    
    const handleCategoryChange = (e) => {
        setSelectedCategory(e.target.value);
    };

     return (
        <div className={styles.container}>
            {/* Select user to chat */}
            <aside className={styles.asideContainer}>
                <form className={styles.form} onSubmit={handleUsernameSubmit}>
                    <div className="mt-2 mb-8">
                        <input
                            type="text"
                            id="input-username"
                            name="input-username"
                            className="bg-slate-500 rounded-xl text-sm px-2 py-2 mr-2 placeholder-slate-200"
                            placeholder="Username"
                            value={inputUsername}
                            onChange={(e) => setInputUsername(e.target.value)}
                            required
                        />
                        <button type="submit" className="text-white text-sm p-2 rounded-md bg-blue-500 hover:bg-blue-700 transition duration-100 mr-2">
                            Start Chat
                        </button>
                    </div>
                    
                    <Tooltip title="Choose Type" arrow>
                    	<div className={styles.dropdownContainer}>
                        <label htmlFor="friendsDropdown" className={styles.dropDownLabel}>
                            View:
                        </label>
                        <select
                            id="friendsDropdown"
                            className="p-2 rounded-md text-base bg-slate-900 border border-slate-600"
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
                                {mutuals.map(user => (
                                <li key={user.id} className="mt-4 flex text-xl bg-slate-900 rounded-md p-2 cursor-pointer hover:bg-slate-950 transition duration-100 mr-2" onClick={() => setInputUsername(user.username)}>
                                    <FaUser className="w-8 h-8 rounded-full bg-slate-600 ml-2 mr-4"/>
                                    <a className="mr-4 text-slate-200"> {user.displayName ? user.displayName : user.username} </a>
                                    <a className="text-slate-400"> @{user.username} </a>
                                </li>
                                ))}
                            </ul>
                        )}

                        {selectedCategory === 'followers' && (
                            <ul>
                                {followers.map(user => (
                                <li key={user.id} className="mt-4 flex text-xl bg-slate-900 rounded-md p-2 cursor-pointer hover:bg-slate-950 transition duration-100 mr-2" onClick={() => setInputUsername(user.username)}>
                                    <FaUser className="w-8 h-8 rounded-full bg-slate-600 ml-2 mr-4"/>
                                    <a className="mr-4 text-slate-200"> {user.displayName ? user.displayName : user.username} </a>
                                    <a className="text-slate-400"> @{user.username} </a>
                                </li>
                                ))}
                            </ul>
                        )}

                        {selectedCategory === 'following' && (
                            <ul>
                                {following.map(user => (
                                <li key={user.id} className="mt-4 flex text-xl bg-slate-900 rounded-md p-2 cursor-pointer hover:bg-slate-950 transition duration-100 mr-2" onClick={() => setInputUsername(user.username)}>
                                    <FaUser className="w-8 h-8 rounded-full bg-slate-600 ml-2 mr-4"/>
                                    <a className="mr-4 text-slate-200"> {user.displayName ? user.displayName : user.username} </a>
                                    <a className="text-slate-400"> @{user.username} </a>
                                </li>
                                ))}
                            </ul>
                        )}
                    </div>
                </form>
            </aside>
            {/* END Select user to chat */}
            
            
            {/* Chat Area */}
            <main className={styles.chatArea}>
                <h2 className="text-3xl font-semibold text-slate-200 mb-4">
                    <a> {otherId ? "Chatting with "+(selectedUserData.displayName ? selectedUserData.displayName : selectedUserData.username)
                        : ("Select username to start chat")
                    } </a>
                    <a className="text-slate-400">{otherId ? '@'+selectedUserData.username : ''} </a>
                </h2>
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
                    <input
                        type="text"
                        placeholder="Type a message..."
                        className={styles.messageInput}
                        value={message}
                        onChange={handleMessageChange}
                    />
                    <button
                        className={`px-4 py-2 bg-blue-500 text-white rounded-md border-none transition-colors duration-100 ${isSendDisabled ? 'bg-gray-400 cursor-not-allowed' : 'hover:bg-blue-700 cursor-pointer'}`}
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

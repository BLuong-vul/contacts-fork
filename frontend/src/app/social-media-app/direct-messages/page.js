'use client';
import { useState } from 'react';
import styles from './direct-messages.module.css';

const DirectMessages = () => {
	const [message, setMessage] = useState(""); //track input
	const [isSendDisabled, setIsSendDisabled] = useState(true);
	
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
	
	const handleSendMessage = () => {
		/*handle sending message to friend here*/
	};
	
	return (
		<div className={styles.container}>
			{/*Friends Left Section*/}
			<aside className={styles.container}>
				<div className={styles.placeholder}>
					Select a friend to start chatting.
				</div>
			</aside>

			{/*Chat Area*/}
			<main className={styles.chatArea}>
				<h2 className={styles.chatHeader}>
					No Conversation Selected
				</h2>
				<div className={styles.conversationArea}>
					<p className={styles.chatPlaceholder}>
						Select a Friend to view the conversation.
					</p>
				</div>
				<div className={styles.messageInputBar}>
					<button className={styles.attachButton} onClick={handleAttachClick}>
						📎
					</button>
					<input
						type='text'
						placeholder='Type a message...'
						className={styles.messageInput}
						value={message}
						onChange={(e) => setMessage(e.target.value)} //track input
					/>
					<button
						className={`${styles.sendButton} ${isSendDisabled ? styles.disabledButton : ''}`} //add disable button
						disabled={isSendDisabled} //disable button if no message
						onClick={handleSendMessage}
					>
						Send
					</button>
					<input
                        type="file"
                        id="fileInput"
                        style={{ display: 'none' }} // Hide the file input
                        onChange={(e) => console.log(e.target.files)} // Handle file selection here
                    />
				</div>
			</main>
		</div>
	);
}
export default DirectMessages;
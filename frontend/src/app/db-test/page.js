"use client"; 

import { useState } from 'react';
import styles from '../styles/demo.module.css';

export default function Home() {
	{/* Setting these this way is probably not even necessary*/}
	const [userData, setUserData] = useState({
		userId:'',
		fullName:'',
		username:'',
		password:'',
		email:'',
		phoneNumber:'',
		address:'',
		city:'',
		state:'',
		zipCode:'',
		country:'',
		followerCount:'',
	});
	const [postData, setPostData] = useState({
		postId:'',
		userId:'',
		likeCount:'',
		dislikeCount:'',
		datePosted:'',
		title:'',
		text:''
	});
	const [replyData, setReplyData] = useState({
		replyId:'',
		postId:'',
		datePosted:'',
		text:'',
		userId:'',
		parentReplyId:'',
	});

	const [userId, setUserId] = useState('');
	const [postId, setPostId] = useState('');
	const [replyId, setReplyId] = useState('');

	{/* These functions should probably be reworked so that they can be merged into one */}
	const fetchUserData = async () => {
		try {
			const response = await fetch(`https://four800-webapp.onrender.com/demo-fetch/user/${userId}`);
			if (!response.ok){
				throw new Error('Error fetching user data');
			}
			const userDataResponse = await response.json();
			setUserData(userDataResponse);
		} catch (err) {
			console.log("error");
		}
	};

	const fetchPostData = async () => {
		try {
			const response = await fetch(`https://four800-webapp.onrender.com/demo-fetch/post/${postId}`);
			if (!response.ok){
				throw new Error('Error fetching user data');
			}
			const postDataResponse = await response.json();
			setPostData(postDataResponse);
		} catch (err) {
			console.log("error");
		}
	};

	const fetchReplyData = async () => {
		try {
			const response = await fetch(`https://four800-webapp.onrender.com/demo-fetch/reply/${replyId}`);
			if (!response.ok){
				throw new Error('Error fetching reply data');
			}
			const postReplyResponse = await response.json();
			setReplyData(postReplyResponse);
		} catch (err) {
			console.log("error");
		}
	};

	return (
		<div>
			<h1>Database Fetch Demo</h1>

			{/* Div containing all other elements (so they display in a row) */}
			<div style={{ display: 'flex' }}>

				{/* User data */}
				<div>
					<div style={{ display: 'flex', alignItems: 'center', gap: '10px'}}>
						<button
							onClick={fetchUserData}
							className={styles.fetchButton}
							onMouseOver={(e) => (e.target.style.backgroundColor = 'lightgrey')}
							onMouseOut={(e) => (e.target.style.backgroundColor = 'white')}
						>
							Fetch User Data
						</button>
						<input
							type="text"
							placeholder="Enter User ID"
							value={userId}
							onChange={(e) => setUserId(e.target.value)}
							className={styles.idInput}
						/>
					</div>
					<div className={styles.formContainer}>
						{Object.entries(userData).map(([key, value]) => (
							<div className={styles.formRow} key={key}>
								<label className={styles.label}>{key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1')}:</label>
								<input className={styles.input} type="text" value={value} readOnly />
							</div>
						))}
					</div>
				</div>
			
				<div style={{height: '100px'}}></div>

				{/* Post data */}
				<div>
					<div style={{ display: 'flex', alignItems: 'center', gap: '10px'}}>
						<button
							onClick={fetchPostData}
							className={styles.fetchButton}
							onMouseOver={(e) => (e.target.style.backgroundColor = 'lightgrey')}
							onMouseOut={(e) => (e.target.style.backgroundColor = 'white')}
						>
							Fetch Post Data
						</button>
						<input
							type="text"
							placeholder="Enter Post ID"
							value={postId}
							onChange={(e) => setPostId(e.target.value)}
							className={styles.idInput}
						/>
					</div>
					<div className={styles.formContainer}>
						{Object.entries(postData).map(([key, value]) => (
							<div className={styles.formRow} key={key}>
								<label className={styles.label}>{key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1')}:</label>
								<input className={styles.input} type="text" value={value} readOnly />
							</div>
						))}
					</div>
				</div>

				{/* Reply data */}
				<div>
					<div style={{ display: 'flex', alignItems: 'center', gap: '10px'}}>
						<button
							onClick={fetchReplyData}
							className={styles.fetchButton}
							onMouseOver={(e) => (e.target.style.backgroundColor = 'lightgrey')}
							onMouseOut={(e) => (e.target.style.backgroundColor = 'white')}
						>
							Fetch Reply Data
						</button>
						<input
							type="text"
							placeholder="Enter Reply ID"
							value={replyId}
							onChange={(e) => setReplyId(e.target.value)}
							className={styles.idInput}
						/>
					</div>
					<div className={styles.formContainer}>
						{Object.entries(replyData).map(([key, value]) => (
							<div className={styles.formRow} key={key}>
								<label className={styles.label}>{key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1')}:</label>
								<input className={styles.input} type="text" value={value} readOnly />
							</div>
						))}
					</div>
				</div>

			</div>

		</div>
	);
}

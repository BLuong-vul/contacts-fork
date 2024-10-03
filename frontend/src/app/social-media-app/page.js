'use client';
import React, { useEffect, useState } from 'react';
import homepagestyles from './social-media-homepage.module.css'; // adjust the path as necessary
import styles from '../styles/app.layout.css';
import Image from "next/image";
import Navbar from "../../components/Navbar";
import Link from 'next/link';

import { Post } from './Post.js';


async function fetchPostsBeforeDate(date){
	const response = await fetch(`http://localhost:8080/post/before/${date}`);
	if (!response.ok){
		throw new Error('Network response not ok ' + response.statusText);
	}
	const posts = await response.json();
	console.log(posts);

	const postData = posts.map(post => new Post(post));

	return postData;
}


export default function Projects() {
	{/*Adjustable sidebar function section*/}
	const [sidebarWidth, setSidebarWidth] = useState(250); //initial width of the sidebar
	const minWidth = 5; //minimum width for sidebar
	const maxWidth = 600; //maximum width
	
	const handleMouseDown = (e) => {
		const startX = e.clientX; //get starting X position

		const onMouseMove = (moveEvent) => {
			
			const newWidth = window.innerWidth - moveEvent.clientX;
			const dragSpeedMultiplier = 2.0;
			if (newWidth > minWidth && newWidth < maxWidth) {
				setSidebarWidth(newWidth * dragSpeedMultiplier);
			}
		};
		
		const onMouseUp = () => {
			window.removeEventListener('mousemove', onMouseMove);
			window.removeEventListener('mouseup', onMouseUp);
		};
		window.addEventListener('mousemove', onMouseMove);
		window.addEventListener('mouseup', onMouseUp);
	};

		// Track like, dislike, and comment countd for each post
	const [likes, setLikes] = useState({});
	const [dislikes, setDislikes] = useState({});
	const [comments, setCOmments] = useState({});
	const [userLiked, setUserLiked] = useState({});
	const [userDisliked, setUserDisliked] = useState({});
	
	//create post form state
	const [isCreatingPost, setIsCreatingPost] = useState(false);
	const [postTitle, setPostTitle] = useState('');
	const [postText, setPostText] = useState('');
	const [postImage, setPostImage] = useState(null);
	const [postVideo, setPostVideo] = useState(null);
	const [error, setError] = useState('');
	
	const toggleCreatePost = () => setIsCreatingPost(!isCreatingPost);
	
	const handleCreatePost = () => {
		if (!postText && !postImage && !postVideo) {
			setError('Must enter text, or upload an image or video.')
		}
		else {
			const newPost = {
				id: posts.length + 1,
				text: postText, 
				image: postImage ? URL.createObjectURL(postImage) : '', //URL for image
				video: postVideo ? URL.createObjectURL(postVideo) : ''  //URL for the video
			};
			
			// add new post to the array
			setPosts([...posts, newPost]);
			// reset form once submission is done
			setPostText('');
			setPostImage(null);
			setPostVideo(null);
			setError('');
			setIsCreatingPost(false);
		}
	};


	//temporary posts
	// const tempPostData = [
	// 	{
 //            id: 1,
 //            text: "This is the first post.",
 //            image: "/path/to/image1.jpg", // Image for the post
 //            video: "" // No video in this post
 //        },
 //        {
 //            id: 2,
 //            text: "Here is a second post with a video!",
 //            image: "",
 //            video: "/path/to/video.mp4" // Video for this post
 //        },
 //        {
 //            id: 3,
 //            text: "Third post with another image.",
 //            image: "/path/to/image2.jpg", // Image for the post
 //            video: ""
 //        }
	// ];


	const [posts, setPosts] = useState([]);
	// Fetches posts from database and saves to "posts"
	useEffect(() => {
	    const fetchAndSetPosts = async () => {
	        try {
	            const fetchedPosts = await fetchPostsBeforeDate('2025-01-30');
	            setPosts(fetchedPosts.map(postData => new Post(postData))); 
	        } catch (error) {
	            console.error('Error fetching posts:', error);
	            setError('Failed to load posts.');
	        }
	    };

	    fetchAndSetPosts();
	}, []); 
	


	
	
	return (
		<>
		<header className={styles.header}>
		<Navbar />
		</header>
		<main className={styles.mainContainer}>
			{/* sidebar section start */}
			<div className={homepagestyles.sidebar} style={{ width: sidebarWidth}}>
				<div className={homepagestyles.dragHandle} onMouseDown={handleMouseDown}>
					<div className={homepagestyles.indicator}>&lt;&gt;</div>
				</div>
					
			{ /* sidebar content can go here */}
			<p>Sidebar Content</p>
			</div>
			{/* sidebar section ends */}
			
			{/*display tabs section for navigation*/}
			<div className={homepagestyles.contentContainer} style={{ marginRight: setSidebarWidth}}>
          		<nav className={homepagestyles.navContainer}>
          			<Link href="/social-media-app" className={homepagestyles.linkBox}>
            			Home
          			</Link>
                	<Link href="/social-media-app/friends-list" className={homepagestyles.linkBox}>
            			Friends List
          			</Link>
          		</nav>
				{/*button for creating post*/}
				<div className={homepagestyles.createPostContainer}>
					<button onClick={toggleCreatePost} className={homepagestyles.createPostButton}>
						Create Post
					</button>
				</div>
				
				{/*Create Post Form*/}
				{isCreatingPost && (
					<>
					<div className={homepagestyles.modalOverlay}></div>
					<div className={homepagestyles.createPostModal}>
						<textarea //title upload
							placeholder="Enter title"
							value={postTitle}
							onChange={(e) => setPostTitle(e.target.value)}
						/>
						<textarea //text upload
							placeholder="Enter body"
							value={postText}
							onChange={(e) => setPostText(e.target.value)}
						/>
						<div>
						<label htmlFor="imageUpload" className={homepagestyles.fileLabel}>Upload Image:</label>
							<input //image upload
							type="file"
							accept="image/*"
							onChange={(e) => setPostImage(e.target.files[0])}
							/>
						</div>
						<div>
							<label htmlFor="imageUpload" className={homepagestyles.fileLabel}>Upload Video:</label>
							<input //video upload 
							id="videoUpload"
							type="file"
							accept="video/*"
							onChange={(e) => setPostVideo(e.target.files[0])}
							/>
						</div>
						{error && <p className={homepagestyles.errorText}>{error}</p>}
						
						<div className={homepagestyles.buttonGroup}>
							<button onClick={handleCreatePost}>Post</button>
							<button onClick={() => setIsCreatingPost(false)}>Cancel</button>
						</div>
					</div>
					</>
				)}

			{/*display posts section*/}
			<div className={homepagestyles.postsContainer}>
				{posts.map(post => post.render())}
            </div>
		</div>
		</main></>
	);
}

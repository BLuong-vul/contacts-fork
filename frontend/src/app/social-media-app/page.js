'use client';
import React, { useEffect, useState } from 'react';
import homepagestyles from './social-media-homepage.module.css'; 
import styles from '../styles/app.layout.css';

import Image from "next/image";
import Link from 'next/link';

import Navbar from "../../components/Navbar";
import LeftMenu from '../../components/leftmenu/left-menu';
import RightMenu from '../../components/rightmenu/right-menu';
import * as Fetch from '../../components/Functions';

import Post from '../../components/Post.js';



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
	{/*Adjustable sidebar function end*/}

	// Track like, dislike, and comment countd for each post
	const [likes, setLikes] = useState({});
	const [dislikes, setDislikes] = useState({});
	const [comments, setComments] = useState({});
	const [userLiked, setUserLiked] = useState({});
	const [userDisliked, setUserDisliked] = useState({});
	
	//create post form state
	const [isCreatingPost, setIsCreatingPost] = useState(false);
	const [postTitle, setPostTitle] = useState('');
	const [postText, setPostText] = useState('');
	const [postImage, setPostImage] = useState(null);
	const [postVideo, setPostVideo] = useState(null);
	const [error, setError] = useState('');
	
	const toggleCreatePost = async () => {
		if (await Fetch.validateTokenWithRedirect()){
			setIsCreatingPost(!isCreatingPost)
		}
	};
	
	const handleCreatePost = async () => {
		if (!postText && !postImage && !postVideo) {
			setError('Must enter text, or upload an image or video.')
		}
		else {
			const newPost = {
				title: postTitle,
				text: postText, 
				// image: postImage ? URL.createObjectURL(postImage) : '', //URL for image
				// video: postVideo ? URL.createObjectURL(postVideo) : ''  //URL for the video
			};
			
			await Fetch.uploadPost(newPost);
			const updatedPosts = await Fetch.fetchAllPosts();
			setPosts(updatedPosts);
			
			// reset form once submission is done
			setPostText('');
			setPostImage(null);
			setPostVideo(null);
			setError('');
			setIsCreatingPost(false);
		}
	};


	const [posts, setPosts] = useState([]);
	// Fetches posts from database and saves to "posts"
	useEffect(() => {
	    const fetchAndSetPosts = async () => {
	        try {
	            const fetchedPosts = await Fetch.fetchAllPosts();
	            setPosts(fetchedPosts); 
	        } catch (error) {
	            console.error('Error fetching posts:', error);
	            setError('Failed to load posts.');
	        }
	    };

	    fetchAndSetPosts();
	}, []); 
	


	
	
	return (
		<>
		<div className="flex gap-6 pt-6">
			<div className="hidden xl:block w-[20%]">
				<LeftMenu type="home"/>
			</div>
			<main className={styles.mainContainer}>
				<div className={homepagestyles.contentContainer} style={{ marginRight: setSidebarWidth}}>
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
			</main>
			<div className="hidden lg:block w-[30%]">
				<RightMenu/>
			</div>
		</div>
		</>
	);
}

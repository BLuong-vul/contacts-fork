'use client';
import React, { useEffect, useState, useRef } from 'react';
import homepagestyles from './social-media-homepage.module.css'; 
import styles from '../styles/app.layout.css';

import Image from "next/image";
import Link from 'next/link';

import Navbar from "../../components/Navbar";
import LeftMenu from '../../components/leftmenu/left-menu';
import RightMenu from '../../components/rightmenu/right-menu';
import * as Fetch from '../../components/Functions';
import { validateTokenWithRedirect, validateToken } from '../../components/ValidationFunctions';

import Post from '../../components/PostContainer';



export default function Projects() {
	// Track like, dislike, and comment count for each post
	const [likes, setLikes] = useState({});
	const [dislikes, setDislikes] = useState({});
	const [comments, setComments] = useState({});
	const [userLiked, setUserLiked] = useState({});
	const [userDisliked, setUserDisliked] = useState({});
	
	//create post form state
	const [isCreatingPost, setIsCreatingPost] = useState(false);
	const [postTitle, setPostTitle] = useState('');
	const [postText, setPostText] = useState('');
	const [postMedia, setPostMedia] = useState(null);
	const [error, setError] = useState('');
	
	const [posts, setPosts] = useState([]);
	const [numPosts, setNumPosts] = useState(10)
	const numPostsRef = useRef(numPosts);

	const [filterOptions, setFilterOptions] = useState({
	    sortBy: 'date',
	    filterOption: {
	      beforeDate: '',
	      afterDate: '',
	    }
	  });

	const clearPosts = async () => {
		setPosts([]);
		setNumPosts(0);
	}

	const handleFilterChange = (newFilterOptions) => {
	    setFilterOptions(newFilterOptions);
	    console.log(newFilterOptions);
	    clearPosts();
	  };
	

	const toggleCreatePost = async () => {
		if (await validateTokenWithRedirect()){
			setIsCreatingPost(!isCreatingPost)
		}
	};
	
	const handleCreatePost = async () => {
		if (!postTitle){
			setError('Must included post title');
			return;
		}
		if (!postText && !postMedia) {
			setError('Must enter text, or upload an image or video.');
			return;
		}
		const formData = {
				title: postTitle,
				text: postText, 
				mediaFileName: null,
		};
		if (postMedia){
			const imageUrl = await Fetch.uploadMedia(postMedia);
			formData.mediaFileName = imageUrl;
			// console.log(imageUrl);
		}
		await Fetch.uploadPost(formData);
		const updatedPosts = await Fetch.fetchAllPosts();
		setPosts(updatedPosts);
		
		// reset form after submitting successfully
		setPostTitle('');
		setPostText('');
		setPostMedia(null);
		setError('');
		setIsCreatingPost(false);
	};

	// If reach bottom of page, fetch some more posts
	useEffect(()=> {
		numPostsRef.current = numPosts;
	}, [numPosts]);
	useEffect(() => {
	  	const handleScroll = async () => {
		    if (window.innerHeight + window.scrollY + 20 >= document.body.offsetHeight) {
		      const fetchedPosts = await Fetch.fetchAllPosts(0, numPostsRef.current + 10, filterOptions.sortBy, filterOptions.filterOption.beforeDate, filterOptions.filterOption.afterDate);
		      setNumPosts(numPostsRef.current + 10);
		      setPosts(fetchedPosts);
		    }
  		};
		// Add scroll listener
		window.addEventListener('scroll', handleScroll);
		// Clean up listener on unmount
		return () => { window.removeEventListener('scroll', handleScroll); };
	}, [filterOptions]);


	
	// Fetches posts from database and saves to "posts"
	useEffect(() => {
	    const fetchAndSetPosts = async () => {
	        try {
	            const fetchedPosts = await Fetch.fetchAllPosts(0, 10, filterOptions.sortBy, filterOptions.filterOption.beforeDate, filterOptions.filterOption.afterDate);
	            console.log(fetchedPosts);
	            setPosts(fetchedPosts); 
	        } catch (error) {
	            console.error('Error fetching posts:', error);
	            setError('Failed to load posts.');
	        }
	    };

	    fetchAndSetPosts();
	}, [filterOptions]); 
	


	
	
	return (
		<>
		<div className="flex gap-6 pt-6">
			<div className="hidden xl:block w-[20%]">
				<LeftMenu onFilterChange={handleFilterChange} type="home"/>
			</div>
			<main className={styles.mainContainer}>
				<div className={homepagestyles.contentContainer}>
					{/*button for creating post*/}
					<div className={homepagestyles.createPostContainer}>
						<button onClick={toggleCreatePost} className="text-white text-l p-2 rounded-md bg-blue-500 hover:bg-blue-700 transition duration-100">
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
								onChange={(e) => setPostMedia(e.target.files[0])}
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
					{posts.map(post => <Post key={post.postId} postData={post} />)}
	            </div>
			</div>
			</main>

			{/*<div className="hidden lg:block w-[30%]">
				<RightMenu/>
			</div>*/}

		</div>
		</>
	);
}

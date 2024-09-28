'use client';
import { useState } from 'react';
import homepagestyles from './social-media-homepage.module.css'; // adjust the path as necessary
import styles from '../styles/app.layout.css';
import Image from "next/image";
import Navbar from "../../components/Navbar";
import Link from 'next/link';

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
	const [comments, setCOmments] = useState({});
	const [userLiked, setUserLiked] = useState({});
	const [userDisliked, setUserDisliked] = useState({});
	
	//create post form state
	const [isCreatingPost, setIsCreatingPost] = useState(false);
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
	
	// handle likes
	const handleLike = (postId) => {
		// Check if the user has already liked the post
		if (userLiked[postId]) {
			// If liked, unlike the post
			setLikes(prev => ({ ...prev, [postId]: (prev[postId] || 1) - 1 }));
			setUserLiked(prev => ({ ...prev, [postId]: false }));
		} else {
			// If not liked, like the post (check if it's disliked to adjust the dislike count)
			setLikes(prev => ({ ...prev, [postId]: (prev[postId] || 0) + 1 }));
			setUserLiked(prev => ({ ...prev, [postId]: true }));

			if (userDisliked[postId]) {
				setDislikes(prev => ({ ...prev, [postId]: (prev[postId] || 1) - 1 }));
				setUserDisliked(prev => ({ ...prev, [postId]: false }));
			}
		}
	};
	
	// handle dislikes
	const handleDislike = (postId) => {
		// Check if the user has already disliked the post
		if (userDisliked[postId]) {
			// If disliked, remove dislike
			setDislikes(prev => ({ ...prev, [postId]: (prev[postId] || 1) - 1 }));
			setUserDisliked(prev => ({ ...prev, [postId]: false }));
		} else {
			// If not disliked, dislike the post (check if it's liked to adjust the like count)
			setDislikes(prev => ({ ...prev, [postId]: (prev[postId] || 0) + 1 }));
			setUserDisliked(prev => ({ ...prev, [postId]: true }));

			if (userLiked[postId]) {
				setLikes(prev => ({ ...prev, [postId]: (prev[postId] || 1) - 1 }));
				setUserLiked(prev => ({ ...prev, [postId]: false }));
			}
		}
	};

	
	// handle comments
	const handleComment = (postId) => {
		//place holder for handling comment functionality
	};
	
	//temporary posts
	const [posts, setPosts] = useState([
		{
            id: 1,
            text: "This is the first post.",
            image: "/path/to/image1.jpg", // Image for the post
            video: "" // No video in this post
        },
        {
            id: 2,
            text: "Here is a second post with a video!",
            image: "",
            video: "/path/to/video.mp4" // Video for this post
        },
        {
            id: 3,
            text: "Third post with another image.",
            image: "/path/to/image2.jpg", // Image for the post
            video: ""
        }
		// Add more posts as needed
	]);
	
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
						<textarea //text upload
							placeholder="Enter text"
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
                	{posts.map(post => (
                    	<div key={post.id} className={homepagestyles.post}>
                  	      <p>{post.text}</p>
               	           {post.image && (
         	                  <Image src={post.image} alt={`Post ${post.id} image`} width={400} height={300} />
       	                   )}
                           {post.video && (
                      	     <video width="400" height="300" controls>
                        	     <source src={post.video} type="video/mp4" />
                        	         Your browser does not support the video tag.
                             </video>
                         )}
                         {/* Like, Dislike, and Comment buttons */}
                         <div className={homepagestyles.postButtons}>
                         	<button onClick={() => handleLike(post.id)}>
								{userLiked[post.id] ? 'Unlike' : 'Like'} ({likes[post.id] || 0})
							</button>
							<button onClick={() => handleDislike(post.id)}>
								{userDisliked[post.id] ? 'Undislike' : 'Dislike'} ({dislikes[post.id] || 0})
							</button>
							<button onClick={() => handleComment(post.id)}>Comment</button>
                         </div>
                     </div>
                  ))}
            </div>
			{/*end posts display section*/}
		</div>
			<div>
        	<Image
				src="/vision_text.png"
				width={200}
				height={200} />
			<Image
				src="/logo.png"
				width={200}
				height={200} />
			</div>
		</main></>
	);
}

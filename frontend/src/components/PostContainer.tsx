'use client';
import React, { useState, useEffect } from "react";
import Image from "next/image";
import Comments from "./Comments";
import Link from 'next/link';
import * as Fetch from './Functions';
import { FaUser, FaThumbsUp, FaThumbsDown } from "react-icons/fa";

// Function to format date (you can customize the format)
const formatDate = (date) => {
    const options = { year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' };
    return new Date(date).toLocaleDateString(undefined, options);
};

const PostContainer = ({ postData }) => {
    // state variables for dynamic content
    const [post, setPost] = useState({
        image: null,
    });
    const [profilePicture, setProfilePicture] = useState(null);
    const[media, setMedia] = useState(null);

    const [likes, setLikes] = useState(postData.likeCount || 0);
    const [dislikes, setDislikes] = useState(postData.dislikeCount || 0);
    const [userRating, setUserRating] = useState(null);

    const [commentData, setCommentData] = useState([]);
    const [commentsVisible, setCommentsVisible] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        // fetch the user's vote on the post
        const fetchUserVote = async () => {
            const voteType = await Fetch.getVoteOnVotable(postData.postId);
            setUserRating(voteType); // voteType will be "LIKE", "DISLIKE", or null
        };

        // fetch comments
        const fetchComments = async () => {
            const commentData = await Fetch.getReplies(postData.postId);
            setCommentData(commentData);
            setIsLoading(false);
        };

        // try to load media based on mediaFileName
        if (postData?.mediaFileName) {
            const fetchMedia = async () => {
                try {
                    const mediaBlob = await Fetch.getMedia(postData.mediaFileName);
                    // console.log("THE BLOB: ", mediaBlob);
                    if (mediaBlob instanceof Blob){
                        const mediaUrl = URL.createObjectURL(mediaBlob);
                        setMedia(mediaUrl);
                        // console.log(mediaUrl);
                        setPost((prevPost) => ({ ...prevPost, image: mediaUrl }));
                    }
                } catch (error) {
                    console.error('Error loading media:', error);
                }
            };
            fetchMedia();
        }

        fetchUserVote();
        fetchComments();
    }, [postData.postId]); 

    useEffect(()=> {
        if (postData.postedBy?.profilePictureFileName){
            const fetchProfilePicture = async () => {
                const mediaBlob = await Fetch.getMedia(postData.postedBy.profilePictureFileName);
                if (mediaBlob instanceof Blob){
                    const mediaUrl = URL.createObjectURL(mediaBlob);
                    setProfilePicture(mediaUrl);
                }
            }
            fetchProfilePicture();
        }
    }, [postData?.postedBy?.profilePictureFileName]);

    const handleLike = () => {
        if (userRating === "LIKE") {
            Fetch.unvote(postData.postId);
            setLikes(likes - 1);
            setUserRating(null);
        } else {
            if (userRating === "DISLIKE") {
                setDislikes(dislikes - 1);
            }
            Fetch.likeVotable(postData.postId);
            setLikes(likes + 1);
            setUserRating("LIKE");
        }
    };

    const handleDislike = () => {
        if (userRating === "DISLIKE") {
            Fetch.unvote(postData.postId);
            setDislikes(dislikes - 1);
            setUserRating(null);
        } else {
            if (userRating === "LIKE") {
                setLikes(likes - 1);
            }
            Fetch.dislikeVotable(postData.postId);
            setDislikes(dislikes + 1);
            setUserRating("DISLIKE");
        }
    }; 

    const toggleComments = () => {
        setCommentsVisible((prevState) => !prevState);
    };

    return (
        <div className="p-4 bg-slate-700 shadow-md rounded-lg flex flex-col gap-4 mb-8 w-11/12">
            {/* User */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    {/* Profile image */}
                    {profilePicture ? (
                        <div className="w-10 h-10 rounded-full bg-slate-600">
                            <Image
                                src={profilePicture}
                                alt="Post profile picture"
                                width={100}
                                height={100}
                                className="w-10 h-10 rounded-full bg-slate-600 ring-1 ring-slate-900"
                            />
                        </div>
                    ) : (
                        <FaUser className="w-10 h-10 rounded-full bg-slate-600 ring-1 ring-slate-900"/>
                    )}
                    {/* Name */}
                    <Link href={`/social-media-app/profile/${postData?.postedBy?.username}`} className="text-slate-200 hover:text-slate-400">
                        {postData?.postedBy?.displayName || postData?.postedBy?.username}
                    </Link>
                </div>
                {/* Post Date */}
                <div className="text-slate-400 text-sm overflow-hidden text-ellipsis whitespace-nowrap ml-8">
                    {postData?.datePosted ? formatDate(postData.datePosted) : 'Date not available'}
                </div>
            </div>
            {postData?.title && (
                <Link href={`/social-media-app/posts/${postData.postId}`}>
                    <h2 className="text-3xl font-semibold text-slate-100 hover:text-slate-400">{postData.title}</h2>
                </Link>
            )}
            {/* Body */}
            <div className="flex flex-col gap-4">
                {(post?.image) && (
                    <div className="w-full min-h-96 relative">
                        <Image
                            src={post?.image}
                            alt="Post image"
                            fill
                            className="object-cover rounded-md"
                        />
                    </div>
                )}
                <p className="text-slate-200">{postData?.text}</p>
            </div>
            {/* Interaction */}
            <div className="flex items-center justify-between text-sm rounded-xl">
                <div className="flex gap-2">
                    <div onClick={handleLike} className="cursor-pointer flex items-center gap-2 bg-slate-800 hover:bg-slate-900 transition duration-100 active:bg-slate-950 p-2 rounded-xl">
                        {/* Likes */}
                        <FaThumbsUp width={16} height={16} className={`cursor-pointer ${userRating === "LIKE" ? "text-blue-500" : "text-slate-300"}`}  />
                        <span className="text-slate-300">
                            {likes}
                        </span>
                    </div>
                    <div onClick={handleDislike} className="cursor-pointer flex items-center gap-2 bg-slate-800 hover:bg-slate-900 transition duration-100 active:bg-slate-950 p-2 rounded-xl">
                        {/* Dislikes */}
                        <FaThumbsDown width={16} height={16} className={`cursor-pointer ${userRating === "DISLIKE" ? "text-blue-500" : "text-slate-300"}`} />
                        <span className="text-slate-300">
                            {dislikes}
                        </span>
                    </div>
                    <div onClick={toggleComments} className="ml-4 cursor-pointer flex items-center gap-2 bg-slate-800 hover:bg-slate-900 transition duration-100 active:bg-slate-950 p-2 rounded-xl">
                        {/* Comments */}
                        <Image src="/comment.png" alt="Comment" width={16} height={16} className="cursor-pointer" />
                        <span className="text-slate-300">
                            {commentData.length}
                            <span className="hidden md:inline"> Comments</span>
                        </span>
                    </div>
                </div>
            </div>
            {isLoading ? (
                <div className="text-slate-300">Loading comments...</div>
            ) : (
                commentsVisible && <Comments initialComments={commentData} postId={postData.postId} />
            )}
        </div>
    );
};

export default PostContainer;

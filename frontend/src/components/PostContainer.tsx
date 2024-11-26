'use client';
import React, { useState, useEffect } from "react";
import Image from "next/image";
import Comments from "./Comments";
import Link from 'next/link';
import * as Fetch from './Functions';

const PostContainer = ({ postData }) => {
    // State variables for dynamic content
    const [user, setUser] = useState({
        profileImage: "https://images.pexels.com/photos/29117255/pexels-photo-29117255/free-photo-of-woman-with-bicycle-and-tote-bag-on-urban-street.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
    });

    const [post, setPost] = useState({
        image: "https://images.pexels.com/photos/29092532/pexels-photo-29092532/free-photo-of-chic-photographer-capturing-istanbul-charm.jpeg?auto=compress&cs=tinysrgb&w=300&lazy=load",
    });

    const [likes, setLikes] = useState(postData.likeCount || 0);
    const [dislikes, setDislikes] = useState(postData.dislikeCount || 0);
    const [userRating, setUserRating] = useState(null);


    useEffect(() => {
        // Fetch the user's vote on the post
        const fetchUserVote = async () => {
            try {
                const voteType = await Fetch.getVoteOnVotable(postData.postId);
                setUserRating(voteType); // voteType will be "LIKE", "DISLIKE", or null
            } catch (error) {
                console.error("Error fetching user vote:", error);
            }
        };
        fetchUserVote();
        console.log(postData);
    }, [postData.postId]); 


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

    return (
        <div className="p-4 bg-slate-700 shadow-md rounded-lg flex flex-col gap-4 mb-8 w-11/12">
            {/* User */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    {/* Profile image */}
                    <Image
                        src={user.profileImage}
                        alt={`${postData?.postedBy?.username}'s profile`}
                        width={40}
                        height={40}
                        className="w-10 h-10 rounded-full"
                    />
                    {/* Name */}
                    <Link href={`/social-media-app/profile/${postData?.postedBy?.username}`} className="text-slate-200">
                        {postData?.postedBy?.username || "Unknown User"}
                    </Link>
                </div>
                {/*<Image src="/more.png" alt="More options" width={16} height={16} />*/}
            </div>
            {postData?.title && (
                <h2 className="text-3xl font-semibold text-slate-100">{postData.title}</h2>
            )}
            {/* Text */}
            <div className="flex flex-col gap-4">
                {postData?.image && (
                        <div className="w-full min-h-96 relative">
                            <Image
                                src={post.image}
                                alt="Post image"
                                fill
                                className="object-cover rounded-md"
                            />
                        </div>
                    )}
                <p className="text-slate-200">{postData?.text}</p>
            </div>
            {/* Interaction */}
            <div className="flex items-center justify-between text-sm rounded-xl bg-slate-800">
                <div className="flex gap-8">
                    <div className="flex items-center gap-2 bg-slate-800 p-2 rounded-xl ml-4">
                        {/* Likes */}
                        <Image src="/like.png" alt="Like" width={16} height={16} onClick={handleLike} className="cursor-pointer" />
                        {/* <span className="text-slate-300">|</span> */}
                        <span className="text-slate-300">
                            {likes}
                            <span className="hidden md:inline"> Likes</span>
                        </span>
                    </div>
                    <div className="flex items-center gap-2 bg-slate-800 p-2 rounded-xl">
                        {/* Dislikes */}
                        <Image src="/dislike.png" alt="Dislike" width={16} height={16} onClick={handleDislike} className="cursor-pointer" />
                        {/* <span className="text-slate-300">|</span> */}
                        <span className="text-slate-300">
                            {dislikes}
                            <span className="hidden md:inline"> Dislikes</span>
                        </span>
                    </div>
                    <div className="flex items-center gap-2 bg-slate-800 p-2 rounded-xl">
                        {/* Comments */}
                        <Image src="/comment.png" alt="Comment" width={16} height={16} className="cursor-pointer" />
                        {/* <span className="text-slate-300">|</span> */}
                        <span className="text-slate-300">
                            999
                            <span className="hidden md:inline"> Comments</span>
                        </span>
                    </div>
                </div>
            </div>
            <Comments />
        </div>
    );
};

export default PostContainer;

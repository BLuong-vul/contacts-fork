'use client';
import React, { useState } from "react";
import Image from "next/image";
import Comments from "./Comments";
import Link from 'next/link';

const PostContainer = ({ postData }) => {
    // State variables for dynamic content
    const [user, setUser] = useState({
        name: "Rick Ricky",
        profileImage: "https://images.pexels.com/photos/29117255/pexels-photo-29117255/free-photo-of-woman-with-bicycle-and-tote-bag-on-urban-street.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load",
    });

    const [post, setPost] = useState({
        text: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Quos repellat ut debitis iste aspernatur porro unde quam ipsam, cupiditate sequi expedita omnis molestiae.",
        image: "https://images.pexels.com/photos/29092532/pexels-photo-29092532/free-photo-of-chic-photographer-capturing-istanbul-charm.jpeg?auto=compress&cs=tinysrgb&w=300&lazy=load",
    });

    const [interaction, setInteraction] = useState({
        likes: 999,
        comments: 999,
        shares: 999,
    });

    return (
        <div className="p-4 bg-slate-700 shadow-md rounded-lg flex flex-col gap-4 mb-8 w-11/12">
            {/* User */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    {/* Profile image */}
                    <Image
                        src={user.profileImage}
                        alt={`${user.name}'s profile`}
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
                        <Image src="/like.png" alt="Like" width={16} height={16} className="cursor-pointer" />
                        {/* <span className="text-slate-300">|</span> */}
                        <span className="text-slate-300">
                            {postData.likeCount}
                            <span className="hidden md:inline"> Likes</span>
                        </span>
                    </div>
                    <div className="flex items-center gap-2 bg-slate-800 p-2 rounded-xl">
                        {/* Dislikes */}
                        <Image src="/dislike.png" alt="Dislike" width={16} height={16} className="cursor-pointer" />
                        {/* <span className="text-slate-300">|</span> */}
                        <span className="text-slate-300">
                            {postData.dislikeCount}
                            <span className="hidden md:inline"> Dislikes</span>
                        </span>
                    </div>
                    <div className="flex items-center gap-2 bg-slate-800 p-2 rounded-xl">
                        {/* Comments */}
                        <Image src="/comment.png" alt="Comment" width={16} height={16} className="cursor-pointer" />
                        {/* <span className="text-slate-300">|</span> */}
                        <span className="text-slate-300">
                            {interaction.comments}
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

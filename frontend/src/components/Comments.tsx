'use client';
import React, { useState } from "react";
import Image from "next/image";
import { FaPaperPlane } from "react-icons/fa";

const Comments = ({ initialComments }) => {
  const [comments, setComments] = useState(initialComments);

  return (
    <div className="">
      {/* Write */}
      <div className="flex items-center gap-4">
        <Image
          src="https://images.pexels.com/photos/28950896/pexels-photo-28950896/free-photo-of-fisherman-by-the-adriatic-sea-in-croatia.jpeg?auto=compress&cs=tinysrgb&w=300&lazy=load"
          alt=""
          width={32}
          height={32}
          className="w-8 h-8 rounded-full"
        />
        <div className="flex-1 flex items-center justify-between bg-slate-500 rounded-xl text-sm px-2 py-2 w-full">
          <input
            type="text"
            placeholder="Write a comment ..."
            className="bg-transparent outline-none flex-1 placeholder-slate-200 text-white"
          />
          < FaPaperPlane
            size={16}
            className="cursor-pointer"
            color="white"
          />
        </div>
      </div>

      {/* Comments */}
      <div className="mt-6">
        {comments.map((comment, index) => (
          <div
            key={index}
            className="flex gap-4 justify-between mt-4 border-b border-gray-600 pb-4"
          >
            {/* Avatar */}
            <Image
              src={comment.avatar}
              alt=""
              width={40}
              height={40}
              className="w-10 h-10 rounded-full"
            />
            {/* Description */}
            <div className="flex flex-col gap-2 flex-1">
              <span className="font-medium text-slate-200">
                {comment.username}
              </span>
              <p className="text-slate-200">{comment.text}</p>
              <div className="flex items-center gap-8 text-xs text-gray-500 mt-2">
                <div className="flex items-center gap-4">
                  <Image
                    src="/like.png"
                    alt=""
                    width={12}
                    height={12}
                    className="cursor-pointer w-4 h-4"
                  />
                  <span className="text-gray-300">|</span>
                  <span className="text-gray-500">{comment.likes} Likes</span>
                </div>
                <div className="cursor-pointer">Reply</div>
              </div>
            </div>
            {/* Icon */}
            <Image
              src="/more.png"
              alt=""
              width={16}
              height={16}
              className="cursor-pointer w-4 h-4"
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default Comments;

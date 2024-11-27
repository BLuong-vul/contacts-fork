'use client';
import React, { useState } from "react";
import Image from "next/image";
import { FaPaperPlane, FaUser } from "react-icons/fa";
import * as Fetch from "./Functions";

const Comments = ({ initialComments, postId }) => {
  const [comments, setComments] = useState(initialComments);

  const [commentField, setCommentField] = useState("");

  const handleCommentUpload = (text) => {
    if (text.trim() !== "") {
        console.log(text);
        Fetch.uploadReply(postId, text);
    }
    console.log(comments);
  };

  return (
    <div className="">
      {/* Write */}
      <div className="flex items-center gap-4">
        <FaUser
          className="w-8 h-8 rounded-full bg-slate-600"
        />
        <div className="flex-1 flex items-center justify-between bg-slate-500 rounded-xl text-sm px-2 py-2 w-full">
          <input
            type="text"
            placeholder="Write a comment ..."
            onChange={(e) => setCommentField(e.target.value)}
            className="bg-transparent outline-none flex-1 placeholder-slate-200 text-white"
          />
          < FaPaperPlane
            size={16}
            className="cursor-pointer"
            onClick={() => handleCommentUpload(commentField)}
            color="white"
          />
        </div>
      </div>

      {/* Comments */}
      <div className="mt-6">
        {comments.map(comment => (
          <div
            key={comment.replyId}
            className="flex gap-4 justify-between mt-4 border-b border-gray-600 pb-4"
          >
            {/* Avatar */}
            <FaUser
              className="w-10 h-10 rounded-full bg-slate-600"
            />
            {/* Description */}
            <div className="flex flex-col gap-2 flex-1">
              <span className="font-medium text-slate-200">
                {comment.author.displayName ? comment.author.displayName : comment.author.username}
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
                  <span className="text-gray-300">0 Likes</span>
                </div>
                <div className="cursor-pointer text-gray-300">Reply</div>
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

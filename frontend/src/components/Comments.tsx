'use client';
import React, { useState, useEffect } from "react";
import Image from "next/image";
import { FaPaperPlane, FaUser, FaThumbsUp, FaThumbsDown } from "react-icons/fa";
import * as Fetch from "./Functions";

const Comments = ({ initialComments, postId }) => {
  const [comments, setComments] = useState(initialComments);
  const [profilePictures, setProfilePictures] = useState({});
  const [userRating, setUserRating] = useState([]);
  const [likeCounts, setLikeCounts] = useState([]);
  const [dislikeCounts, setDislikeCounts] = useState([]);

  const [commentField, setCommentField] = useState("");

  const [replyField, setReplyField] = useState("");
  const [replyingTo, setReplyingTo] = useState(null); // State to track the comment being replied to

  // useEffect(() => {
  //   console.log(initialComments);
  // }, [postId]);

  const handleCommentUpload = async (text) => {
    if (text.trim() !== "") {
      const newComment = await Fetch.uploadReply(postId, text);
      setCommentField("");
      setComments((prevComments) => [...prevComments, newComment]);
    }
  };

    const handleReplyUpload = async(text, replyTo) => {
        if (text.trim() !== "") {
            const newReply = await Fetch.uploadReply(postId, text, replyTo);
            setReplyField("");
            setReplyingTo(null);
            setComments((prevComments) => {
            return prevComments.map(comment =>
              comment.id === replyTo
                ? { ...comment, replies: [...comment.replies, newReply] }
                : comment
            );
          });
        }
    }

    const toggleReplyField = (commentId) => {
      setReplyingTo(replyingTo === commentId ? null : commentId);
    };


    const fetchProfilePicture = async (author) => {
      if (author?.profilePictureFileName) {
        const mediaBlob = await Fetch.getMedia(author.profilePictureFileName);
        if (mediaBlob instanceof Blob) {
          const mediaUrl = URL.createObjectURL(mediaBlob);
          return mediaUrl;
        }
      }
      return null;
    };

    useEffect(() => {
      const fetchPictures = async () => {
        const updatedProfilePictures = {};

        const fetchProfilePicturesRecursively = async (comments) => {
          for (const comment of comments) {
            if (comment.author && !updatedProfilePictures[comment.id]) {
              const pictureUrl = await fetchProfilePicture(comment.author);
              updatedProfilePictures[comment.id] = pictureUrl;
            }

            if (comment.replies) {
              await fetchProfilePicturesRecursively(comment.replies);
            }
          }
        };

        await fetchProfilePicturesRecursively(comments);

        setProfilePictures(updatedProfilePictures);
      };
      fetchPictures();

      const fetchUserVotes = async () => {
        const updatedUserRatings = {};
        const updatedLikeCounts = {};
        const updatedDislikeCounts = {};

        const fetchVotesRecursively = async (comments) => {
          for (const comment of comments) {
            const voteType = await Fetch.getVoteOnReply(comment.id);
            updatedUserRatings[comment.id] = voteType;

            updatedLikeCounts[comment.id] = comment.likeCount;
            updatedDislikeCounts[comment.id] = comment.dislikeCount;

            if (comment.replies) {
              await fetchVotesRecursively(comment.replies);
            }
          }
        };
        await fetchVotesRecursively(comments);
        setUserRating(updatedUserRatings); 
        setLikeCounts(updatedLikeCounts); 
        setDislikeCounts(updatedDislikeCounts); 
      };
      fetchUserVotes();

    }, [comments]); 

    const handleLike = (replyId) => {
      if (userRating[replyId] === "LIKE") {
        // User is removing their like
        Fetch.unvoteReply(replyId);
        setLikeCounts((prev) => ({ ...prev, [replyId]: prev[replyId] - 1 }));
        setUserRating((prev) => ({ ...prev, [replyId]: null }));
      } else {
        if (userRating[replyId] === "DISLIKE") {
          // User is changing from dislike to like
          setDislikeCounts((prev) => ({ ...prev, [replyId]: prev[replyId] - 1 }));
        }
        Fetch.likeReply(replyId);
        setLikeCounts((prev) => ({ ...prev, [replyId]: (prev[replyId] || 0) + 1 }));
        setUserRating((prev) => ({ ...prev, [replyId]: "LIKE" }));
      }
    };

    const handleDislike = (replyId) => {
      if (userRating[replyId] === "DISLIKE") {
        // User is removing their like
        Fetch.unvoteReply(replyId);
        setDislikeCounts((prev) => ({ ...prev, [replyId]: prev[replyId] - 1 }));
        setUserRating((prev) => ({ ...prev, [replyId]: null }));
      } else {
        if (userRating[replyId] === "LIKE") {
          // User is changing from dislike to like
          setLikeCounts((prev) => ({ ...prev, [replyId]: prev[replyId] - 1 }));
        }
        Fetch.likeReply(replyId);
        setDislikeCounts((prev) => ({ ...prev, [replyId]: (prev[replyId] || 0) + 1 }));
        setUserRating((prev) => ({ ...prev, [replyId]: "DISLIKE" }));
      }
    };



    const renderReplies = (replies, depth = 0) => {
        return replies.map((reply) => (
          <div
            key={reply.id}
            style={{ marginLeft: `50px` }}
            className="border-b border-gray-600 pb-4"
          >
            <div className="flex gap-4 justify-between mt-4">
              {profilePictures[reply.id] ? (
                <Image
                  src={profilePictures[reply.id]}
                  alt="Comment profile picture"
                  width={100}
                  height={100}
                  className="w-10 h-10 rounded-full bg-slate-600 ring-1 ring-slate-900"
                />
              ) : (
                <FaUser className="w-10 h-10 rounded-full bg-slate-600" />
              )}

              <div className="flex flex-col gap-2 flex-1">
                <span className="font-medium text-slate-200">
                  {reply.author.displayName || reply.author.username}
                </span>
                <p className="text-slate-200">{reply.text}</p>
                <div className="flex items-center gap-2 text-xs text-gray-500">
                  {/* Likes */}
                  <div onClick={() => handleLike(reply.id)} className="cursor-pointer flex items-center gap-2 bg-slate-800 hover:bg-slate-900 transition duration-100 active:bg-slate-950 p-2 rounded-xl">
                      <FaThumbsUp width={16} height={16} className={`cursor-pointer ${userRating[reply.id] === "LIKE" ? "text-blue-500" : "text-slate-300"}`}/>
                      <span className="text-slate-300"> {likeCounts[reply.id]} </span>
                  </div>
                  {/* Dislikes */}
                  <div onClick={() => handleDislike(reply.id)} className="cursor-pointer flex items-center gap-2 bg-slate-800 hover:bg-slate-900 transition duration-100 active:bg-slate-950 p-2 rounded-xl">
                      <FaThumbsDown width={16} height={16} className={`cursor-pointer ${userRating[reply.id] === "DISLIKE" ? "text-blue-500" : "text-slate-300"}`}/>
                      <span className="text-slate-300"> {dislikeCounts[reply.id]} </span>
                  </div>
                  <div className="ml-4 cursor-pointer flex items-center gap-2 bg-slate-800 hover:bg-slate-900 transition duration-100 active:bg-slate-950 p-2 rounded-xl text-slate-300" onClick={() => toggleReplyField(reply.id)}>
                    <Image src="/comment.png" alt="Comment" width={16} height={16} className="cursor-pointer" />
                    Reply
                  </div>
                </div>
              </div>
            </div>

            {/* Render replies recursively */}
            {reply.replies && renderReplies(reply.replies, depth + 1)}

            {replyingTo === reply.id && (
              <div className="mt-4 flex items-center gap-4">
                <FaUser className="w-8 h-8 rounded-full bg-slate-600" />
                <div className="flex-1 flex items-center justify-between bg-slate-500 rounded-xl text-sm px-2 py-2 w-full">
                  <input
                    type="text"
                    placeholder="Write a reply ..."
                    value={replyField}
                    onChange={(e) => setReplyField(e.target.value)}
                    className="bg-transparent outline-none flex-1 placeholder-slate-200 text-white"
                  />
                  <FaPaperPlane
                    size={16}
                    className="cursor-pointer transition-colors duration 100"
                    onClick={() => handleReplyUpload(replyField, reply.id)}
                    color="white"
                    onMouseEnter={(e) => (e.currentTarget.style.color = '#3b83f6')}
                    onMouseLeave={(e) => (e.currentTarget.style.color = 'white')}
                  />
                </div>
              </div>
            )}
          </div>
        ));
    };

  return (
    <div className="">
      {/* Write */}
      <div className="flex items-center gap-4">
        <FaUser className="w-8 h-8 rounded-full bg-slate-600" />
        <div className="flex-1 flex items-center justify-between bg-slate-500 rounded-xl text-sm px-2 py-2 w-full">
          <input
            type="text"
            placeholder="Write a comment ..."
            onChange={(e) => setCommentField(e.target.value)}
            className="bg-transparent outline-none flex-1 placeholder-slate-200 text-white"
          />
          <FaPaperPlane
            size={16}
            className="cursor-pointer transition-colors duration 100"
            onClick={() => handleCommentUpload(commentField)}
            color="white"
            onMouseEnter={(e) => (e.currentTarget.style.color = '#3b83f6')}
            onMouseLeave={(e) => (e.currentTarget.style.color = 'white')}
          />
        </div>
      </div>

      {/* Comments */}
      <div className="mt-6">
        {comments.map(comment => (
          <div className="border-b border-gray-600 pb-4" key={comment.id}>
          <div className="flex gap-4 justify-between mt-4">
            {/* Avatar */}
            {profilePictures[comment.id] ? (
              <Image
                src={profilePictures[comment.id]}
                alt="Comment profile picture"
                width={100}
                height={100}
                className="w-10 h-10 rounded-full bg-slate-600 ring-1 ring-slate-900"
              />
            ) : (
              <FaUser className="w-10 h-10 rounded-full bg-slate-600" />
            )}

            {/* Description */}
            <div className="flex flex-col gap-2 flex-1">
              <span className="font-medium text-slate-200">
                {comment.author.displayName ? comment.author.displayName : comment.author.username}
              </span>
              <p className="text-slate-200">{comment.text}</p>
              <div className="flex items-center gap-2 text-xs text-gray-500">
                <div onClick={() => handleLike(comment.id)} className="cursor-pointer flex items-center gap-2 bg-slate-800 hover:bg-slate-900 transition duration-100 active:bg-slate-950 p-2 rounded-xl">
                    <FaThumbsUp width={16} height={16} className={`cursor-pointer ${userRating[comment.id] === "LIKE" ? "text-blue-500" : "text-slate-300"}`}  />
                    <span className="text-slate-300"> {likeCounts[comment.id]} </span>
                </div>
                <div onClick={() => handleDislike(comment.id)} className="cursor-pointer flex items-center gap-2 bg-slate-800 hover:bg-slate-900 transition duration-100 active:bg-slate-950 p-2 rounded-xl">
                    <FaThumbsDown width={16} height={16} className={`cursor-pointer ${userRating[comment.id] === "DISLIKE" ? "text-blue-500" : "text-slate-300"}`}  />
                    <span className="text-slate-300"> {dislikeCounts[comment.id]} </span>
                </div>
                <div className="ml-4 cursor-pointer flex items-center gap-2 bg-slate-800 hover:bg-slate-900 transition duration-100 active:bg-slate-950 p-2 rounded-xl text-slate-300" onClick={() => toggleReplyField(comment.id)}>
                  <Image src="/comment.png" alt="Comment" width={16} height={16} className="cursor-pointer" />
                  Reply
                </div>
              </div>
            </div>
            {/* Icon */}
            {/* <Image src="/more.png" alt="" width={16} height={16} className="cursor-pointer w-4 h-4" /> */}
          </div>

          {/* Render replies */}
          {comment.replies && renderReplies(comment.replies)}

          {/* Reply text field */}
          {replyingTo === comment.id && (
            <div className="mt-4 flex items-center gap-4">
              <FaUser className="w-8 h-8 rounded-full bg-slate-600" />
              <div className="flex-1 flex items-center justify-between bg-slate-500 rounded-xl text-sm px-2 py-2 w-full">
                <input
                  type="text"
                  placeholder="Write a reply ..."
                  value={replyField}
                  onChange={(e) => setReplyField(e.target.value)}
                  className="bg-transparent outline-none flex-1 placeholder-slate-200 text-white"
                />
                <FaPaperPlane
                  size={16}
                  className="cursor-pointer transition-colors duration 100"
                  onClick={() => handleReplyUpload(replyField, comment.id)}
                  color="white"
                  onMouseEnter={(e) => (e.currentTarget.style.color = '#3b83f6')}
                  onMouseLeave={(e) => (e.currentTarget.style.color = 'white')}
                />
              </div>
            </div>
          )}
          </div>
        ))}
      </div>
    </div>
  );
};
 
export default Comments;

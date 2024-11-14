import React, { useState } from "react";
import Image from "next/image";
// import styles from '../app/social-media-app/social-media-homepage.module.css';
import Link from 'next/link';

export default function Post({ postData }) {
    const [commentText, setCommentText] = useState("");
    const [userRating, setUserRating] = useState(null); // Track user's rating
    const [likes, setLikes] = useState(postData.likes || 0);
    const [dislikes, setDislikes] = useState(postData.dislikes || 0);
    const [comments, setComments] = useState(postData.comments || []);

    const handleLike = () => {
        if (userRating === "like") {
            setLikes(likes - 1);
            setUserRating(null);
        } else {
            if (userRating === "dislike") {
                setDislikes(dislikes - 1);
            }
            setLikes(likes + 1);
            setUserRating("like");
        }
    };

    const handleDislike = () => {
        if (userRating === "dislike") {
            setDislikes(dislikes - 1);
            setUserRating(null);
        } else {
            if (userRating === "like") {
                setLikes(likes - 1);
            }
            setDislikes(dislikes + 1);
            setUserRating("dislike");
        }
    };

    const handleComment = (text) => {
        if (text.trim() !== "") {
            setComments([...comments, { author: "LoggedUser", text }]);
            setCommentText("");
        }
    };

    const renderComments = (commentList, level = 0) => {
        return (
            <div className={styles.commentsContainer}>
                {commentList.map((comment, index) => (
                    <div key={index} className={styles.comment}>
                        <span className={styles.commentAuthor}>{comment.author}</span>
                        <p className={styles.commentText}>{comment.text}</p>

                        <ReplySection comment={comment} level={level} />
                        {comment.childReplies && comment.childReplies.length > 0 && (
                            renderComments(comment.childReplies, level + 1)
                        )}
                    </div>
                ))}
            </div>
        );
    };

    return (
        <div key={postData.id} className={styles.post}>
            <Link href={`/social-media-app/profile/${postData?.postedBy?.username}`} className={styles.postAuthor}>
                {postData?.postedBy?.username || "Unknown User"}
            </Link>
            <h3 className={styles.postTitle}>{postData.title}</h3>
            <p className={styles.postText}>{postData.text}</p>
            {postData.image && (
                <Image src={postData.image} alt={`Post ${postData.id} image`} width={400} height={300} />
            )}
            {postData.video && (
                <video width="400" height="300" controls>
                    <source src={postData.video} type="video/mp4" />
                    Your browser does not support the video tag.
                </video>
            )}

            <div className={styles.postButtons}>
                <button
                    onClick={handleLike}
                    className={userRating === "like" ? styles.activeButton : ""}
                >{`Like (${likes})`}</button>
                <button
                    onClick={handleDislike}
                    className={userRating === "dislike" ? styles.activeButton : ""}
                >{`Dislike (${dislikes})`}</button>
            </div>

            <div className={styles.createComment}>
                <input
                    type="text"
                    placeholder="Write a comment..."
                    value={commentText}
                    onChange={(e) => setCommentText(e.target.value)}
                    className={styles.commentInput}
                />
                <button onClick={() => handleComment(commentText)} className={styles.createCommentButton}>
                    Create Comment
                </button>
            </div>

            {renderComments(comments)}
        </div>
    );
}

function ReplySection({ comment, level }) {
    const [replyText, setReplyText] = useState("");
    const [showReplyInput, setShowReplyInput] = useState(false);

    const handleReply = () => {
        if (replyText.trim() !== "") {
            comment.childReplies = comment.childReplies || [];
            comment.childReplies.push({ author: "LoggedUser", text: replyText, childReplies: [] });
            setReplyText("");
            setShowReplyInput(false);
        }
    };

    return (
        <div className={styles.replySection}>
            <button className={styles.replyButton} onClick={() => setShowReplyInput(!showReplyInput)}>
                Reply
            </button>
            {showReplyInput && (
                <div className={styles.replyInputContainer} style={{ marginLeft: `${(level + 1) * 20}px` }}>
                    <input
                        type="text"
                        placeholder="Write a reply..."
                        value={replyText}
                        onChange={(e) => setReplyText(e.target.value)}
                        className={styles.replyInput}
                    />
                    <button onClick={handleReply} className={styles.submitReplyButton}>
                        Submit Reply
                    </button>
                </div>
            )}
        </div>
    );
}

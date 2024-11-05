/*import Image from "next/image";
import styles from './social-media-homepage.module.css';
import Link from 'next/link';
import { comment } from "postcss";*/
import React, { useState } from "react";
import Image from "next/image";
import styles from './social-media-homepage.module.css';
import Link from 'next/link';


export class Post {
    constructor(postData) {
        this.id = postData.id;
        this.title = postData.title;
        this.author = postData.postedBy.username;
        this.text = postData.text;
        this.image = postData.image;
        this.video = postData.video;
        this.likes = postData.likes || 0;
        this.dislikes = postData.dislikes || 0;
        this.comments = postData.comments || []; //Initializes comments
    }
    renderComments(level = 0){
        return(
            <div className={styles.commentsContainer}>
                {this.comments.map((comment, index) => (
                    <div key={index} className={styles.comment}>
                        <span className={styles.commentAuthor}>{comment.author}</span>
                        <p className={styles.commentText}>{comment.text}</p>

                        {/*Reply button*/}
                        <ReplySection comment={comment} level={level} />
                        {/* Render child replies recursively with increased indentation */}
                        {comment.childReplies && comment.childReplies.length > 0 && (
                            this.renderComments(comment.childReplies, level + 1)
                        )}
                    </div>
                ))}
            </div>
        );
    }
    render() {
        const [commentText, setCommentText] = useState("");
        const [userRating, setUserRating] = useState(null); // Track user's rating
        const handleLike = () => {
            if (userRating === "like") {
                this.likes -= 1;  // Remove like
                setUserRating(null);  // Reset rating
            } else {
                if (userRating === "dislike") {
                    this.dislikes -= 1;  // Remove dislike
                }
                this.likes += 1;  // Add like
                setUserRating("like");  // Update rating to like
            }
        };

        const handleDislike = () => {
            if (userRating === "dislike") {
                this.dislikes -= 1;  // Remove dislike
                setUserRating(null);  // Reset rating
            } else {
                if (userRating === "like") {
                    this.likes -= 1;  // Remove like
                }
                this.dislikes += 1;  // Add dislike
                setUserRating("dislike");  // Update rating to dislike
            }
        };

        const handleComment = (text) => {
            if (text.trim() !== "") {
                this.comments.push({ author: "LoggedUser", text });
                setCommentText("");
            }
        };
        return (
            <div key={this.id} className={styles.post}>
                <Link href={`/social-media-app/profile/${this.author}`} className={styles.postAuthor}>
                    {this.author}
                </Link>
                <h3 className={styles.postTitle}>{this.title}</h3>
                <p className={styles.postText}>{this.text}</p>
                {this.image && (
                    <Image src={this.image} alt={`Post ${this.id} image`} width={400} height={300} />
                )}
                {this.video && (
                    <video width="400" height="300" controls>
                        <source src={this.video} type="video/mp4" />
                        Your browser does not support the video tag.
                    </video>
                )}

                <div className={styles.postButtons}>
                    <button
                        onClick={handleLike}
                        className={userRating === "like" ? styles.activeButton : ""}
                    >{`Like (${this.likes})`}</button>
                    <button
                        onClick={handleDislike}
                        className={userRating === "dislike" ? styles.activeButton : ""}
                    >{`Dislike (${this.dislikes})`}</button>
                </div>

                {/* Create Comment Section */}
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

                {/* Render comments at the bottom of the post */}
                {this.renderComments()}
            </div>
        );
    }
}

// Reply Section component
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

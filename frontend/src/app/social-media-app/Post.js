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
    renderComments(){
        return(
            <div className={styles.commentsContainer}>
                {this.comments.map((comment, index) => (
                    <div key={index} className={styles.comment}>
                        <span className={styles.commentAuthor}>{comment.author}</span>
                        <p className={styles.commentText}>{comment.text}</p>
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

    // !!! This is for like/dislike buttons. They don't work yet, add them above later
    // <div className="postButtons">
    //     <button className={styles.postButtons}>{`Like (${this.likes})`}</button>
    //     <button>{`Dislike (${this.dislikes})`}</button>
    // </div>

    /*like() {
        this.likes += 1;
    }

    dislike() {
        this.dislikes += 1;
    }*/
}

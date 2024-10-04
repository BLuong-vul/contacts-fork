import Image from "next/image";
import styles from './social-media-homepage.module.css';


export class Post {
    constructor(postData) {
        this.id = postData.id;
        this.title = postData.title;
        this.text = postData.text;
        this.image = postData.image;
        this.video = postData.video;
        this.likes = postData.likes || 0;
        this.dislikes = postData.dislikes || 0;
    }

    render() {
        return (
            <div key={this.id} className={styles.post}>
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


            </div>
        );
    }

    // !!! This is for like/dislike buttons. They don't work yet, add them above later
    // <div className="postButtons">
    //     <button className={styles.postButtons}>{`Like (${this.likes})`}</button>
    //     <button>{`Dislike (${this.dislikes})`}</button>
    // </div>

    like() {
        this.likes += 1;
    }

    dislike() {
        this.dislikes += 1;
    }
}

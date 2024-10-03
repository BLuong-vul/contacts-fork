import Image from "next/image";

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
            <div key={this.id} className="post">
                <h3>{this.title}</h3>
                <p>{this.text}</p>
                {this.image && (
                    <Image src={this.image} alt={`Post ${this.id} image`} width={400} height={300} />
                )}
                {this.video && (
                    <video width="400" height="300" controls>
                        <source src={this.video} type="video/mp4" />
                        Your browser does not support the video tag.
                    </video>
                )}

                <div className="postButtons">
                    <button>{`Like (${this.likes})`}</button>
                    <button>{`Dislike (${this.dislikes})`}</button>
                </div>
            </div>
        );
    }

    like() {
        this.likes += 1;
    }

    dislike() {
        this.dislikes += 1;
    }
}

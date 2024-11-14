import React from "react";
import PostContainer from "./PostContainer";

const Feed = () => {
    return(
        <div className="p-4 bg-white shadow-md rounded-lg flex flex-col gap-12">
            <PostContainer/>
            <PostContainer/>

        </div>
    )
}

export default Feed;
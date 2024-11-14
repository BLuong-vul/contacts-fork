import React from "react";
import Image from "next/image";
import Comments from "./Comments";
const PostContainer = () => {
    return(
        <div className="flex flex-col gap-4">
            {/* User */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <Image src="https://images.pexels.com/photos/29117255/pexels-photo-29117255/free-photo-of-woman-with-bicycle-and-tote-bag-on-urban-street.jpeg?auto=compress&cs=tinysrgb&w=600&lazy=load" 
                    alt=""
                    width={40}
                    height={40}
                    className="w-10 h-10 rounded-full"
                    />
                    <span className="font-medium">Rick Ricky</span>
                </div>
                <Image src="/more.png" alt="" width={16} height={16}/>
            </div>
            {/* Description */}
            <div className="flex flex-col gap-4">
                <div className="w-full min-h-96 relative">
                <Image src="https://images.pexels.com/photos/29092532/pexels-photo-29092532/free-photo-of-chic-photographer-capturing-istanbul-charm.jpeg?auto=compress&cs=tinysrgb&w=300&lazy=load" 
                    alt=""
                    fill
                    className="object-cover rounded-md"
                    />
                </div>
                <p>
                    Lorem ipsum dolor sit amet consectetur adipisicing elit. Quos repellat ut debitis 
                    iste aspernatur porro unde quam ipsam, cupiditate sequi expedita omnis molestiae.
                </p>
            </div>
            {/* Interaction */}
            <div className="flex items-center justify-between text-sm my-4">
                <div className="flex gap-8">
                    <div className="flex items-center gap-4 bg-slate-50 p-2 rounded-xl">
                    {/* Likes */}
                    <Image src="/like.png" alt="" width={16} height={16} className="cursor-pointer"/>
                    <span className="text-gray-300">|</span>
                    <span className="text-gray-500">
                        999<span className="hidden md:inline"> Likes</span></span>
                    </div>
                    <div className="flex items-center gap-4 bg-slate-50 p-2 rounded-xl">
                    {/* Comments */}
                    <Image src="/comment.png" alt="" width={16} height={16} className="cursor-pointer"/>
                    <span className="text-gray-300">|</span>
                    <span className="text-gray-500">
                        999<span className="hidden md:inline"> Comments</span></span>
                    </div>
                    <div className="flex items-center gap-4 bg-slate-50 p-2 rounded-xl">
                    {/* Share */}
                    <Image src="/share.png" alt="" width={16} height={16} className="cursor-pointer"/>
                    <span className="text-gray-300">|</span>
                    <span className="text-gray-500">
                        999<span className="hidden md:inline"> Shares</span></span>
                    </div>
                </div>
            </div>
            <Comments/>
        </div>
    )
}

export default PostContainer;
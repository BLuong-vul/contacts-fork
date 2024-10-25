import React from "react";
import Link from "next/link";
import Image from "next/image";

const UserInfo = ({userId}:{userId:string}) => {
    return (
        <div className="p-4 bg-white rounded-lg shadow-md text-sm flex flex-col gap-4">
            <div className='flex justify-between items-center font-medium'>
                <span className="text-gray-500">User Media</span>
                <Link href="/" className="text-blue-500 text-xs">See All</Link>
            </div>
            {/** User Media Posts */}
            <div className='flex gap-4 justify-between flex-wrap'>
                <div className="relative w-1/5 h-24">
                    <Image src="https://images.pexels.com/photos/29006818/pexels-photo-29006818/free-photo-of-autumn-leaves-in-rippling-stream.jpeg?auto=compress&cs=tinysrgb&w=1200&lazy=load" alt="" fill className="object-cover rounded-md"/>
                </div>
                <div className="relative w-1/5 h-24">
                    <Image src="https://images.pexels.com/photos/29006818/pexels-photo-29006818/free-photo-of-autumn-leaves-in-rippling-stream.jpeg?auto=compress&cs=tinysrgb&w=1200&lazy=load" alt="" fill className="object-cover rounded-md"/>
                </div>
                <div className="relative w-1/5 h-24">
                    <Image src="https://images.pexels.com/photos/29006818/pexels-photo-29006818/free-photo-of-autumn-leaves-in-rippling-stream.jpeg?auto=compress&cs=tinysrgb&w=1200&lazy=load" alt="" fill className="object-cover rounded-md"/>
                </div>
                <div className="relative w-1/5 h-24">
                    <Image src="https://images.pexels.com/photos/29006818/pexels-photo-29006818/free-photo-of-autumn-leaves-in-rippling-stream.jpeg?auto=compress&cs=tinysrgb&w=1200&lazy=load" alt="" fill className="object-cover rounded-md"/>
                </div>
                <div className="relative w-1/5 h-24">
                    <Image src="https://images.pexels.com/photos/29006818/pexels-photo-29006818/free-photo-of-autumn-leaves-in-rippling-stream.jpeg?auto=compress&cs=tinysrgb&w=1200&lazy=load" alt="" fill className="object-cover rounded-md"/>
                </div>
            </div>
        </div>
    )
}

export default UserInfo
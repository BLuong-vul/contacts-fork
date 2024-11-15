'use client';
import { useEffect, useState } from "react";
import React from "react";
import Image from "next/image";
import Link from "next/link";
import * as Fetch from "../Functions";

const ProfileCard = () => {
  const [username, setUsername] = useState(""); 
  const [followerCount, setFollowerCount] = useState("");
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const init = async() => {
      try {
        const userInfo = await Fetch.getCurrentUserInfo();
        if (userInfo){
          setIsLoggedIn(true);
          setUsername(userInfo.username);
          setFollowerCount(userInfo.followerCount);
        } else {
          setIsLoggedIn(false);
        }
      } catch (error){}
    }
    init();
  }, []); 

  return (
    <div className='p-4 bg-white rounded-lg shadow-md text-sm flex flex-col gap-6'>
        <div className='h-20 relative'>
          {isLoggedIn ? (
            <>
              <Image 
                src="https://images.pexels.com/photos/26125152/pexels-photo-26125152/free-photo-of-the-milky-way-over-the-lake-at-night.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2" 
                alt="" 
                fill 
                className="rounded-md"
              />
              <Image 
                src="https://images.pexels.com/photos/13659374/pexels-photo-13659374.jpeg?auto=compress&cs=tinysrgb&w=1200&lazy=load" 
                alt="" 
                width={48} 
                height={48} 
                className="rounded-full object-cover w-12 h-12 absolute left-0 right-0 m-auto -bottom-6 ring-1 ring-white z-10"
              />
            </>
          ) : (
            <p className="text-black text-xl text-center absolute left-0 right-0 m-auto -bottom-2">Log in to view profile</p>
          )}
        </div>
        <div className="h-20 flex flex-col gap-2 items-center">
          <span className="font-semibold">{username}</span>
          <span className="text-xs text-gray-500">{isLoggedIn ? followerCount + ' Followers' : ''}</span>
          <Link
            href={isLoggedIn ? `/social-media-app/profile/${username}` : '/login'}
            className="bg-blue-500 text-white text-xs p-2 rounded-md"
          >
            {isLoggedIn ? 'My Profile' : 'Log In'}
          </Link>
        </div>
    </div>
  );
};

export default ProfileCard;
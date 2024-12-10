'use client';
import { useEffect, useState } from "react";
import React from "react";
import Image from "next/image";
import Link from "next/link";
import * as Fetch from "../Functions";
import { FaUser } from "react-icons/fa";

const ProfileCard = () => {
  const [username, setUsername] = useState(""); 
  const [followerCount, setFollowerCount] = useState("");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [profilePicture, setProfilePicture] = useState(null);
  const [bannerPicture, setBannerPicture] = useState(null);

  useEffect(() => {
    const init = async() => {
      try {
        const userInfo = await Fetch.getCurrentUserInfo();
        if (userInfo){
          setIsLoggedIn(true);
          setUsername(userInfo.username);
          setFollowerCount(userInfo.followerCount);

          // set profile picture
          const avatarBlob = await Fetch.getMedia(userInfo.profilePictureFileName);
          if (avatarBlob instanceof Blob){
              const mediaUrl = URL.createObjectURL(avatarBlob);
              setProfilePicture(mediaUrl);
          }

          // set banner
          const bannerBlob = await Fetch.getMedia(userInfo.bannerPictureFileName);
          if (bannerBlob instanceof Blob){
              const mediaUrl = URL.createObjectURL(bannerBlob);
              setBannerPicture(mediaUrl);
          }

        } else {
          setIsLoggedIn(false);
        }
      } catch (error){}
    }
    init();
  }, []); 

  return (
    <div className='p-4 bg-slate-600 rounded-lg shadow-md text-sm flex flex-col gap-6'>
        <div className='h-20 relative'>
          {isLoggedIn ? (
            <>
              {/* BANNER */}
              {bannerPicture ? (
                  <div>
                      <Image
                          src={bannerPicture}
                          alt="Post banner picture"
                          fill
                          className="rounded-md object-cover"
                      />
                  </div>
              ) : (
                  <div className="w-full h-full bg-slate-900 rounded-md object-cover" />
              )}
              {/* PROFILE PICTURE */}
              {(profilePicture!=null) ? (
                  <div className="w-10 h-10 rounded-full bg-slate-600">
                      <Image
                          src={profilePicture}
                          alt="Post profile picture"
                          width={100}
                          height={100}
                          className="rounded-full object-cover w-12 h-12 absolute left-0 right-0 m-auto -bottom-6 ring-1 ring-slate-900 z-10"
                      />
                  </div>
              ) : (
                  <FaUser className="w-10 h-10 rounded-full bg-slate-600 ring-1 ring-slate-900"/>
              )}
            </>
          ) : (
            <p className="text-white text-xl text-center absolute left-0 right-0 m-auto -bottom-2">Log in to view profile</p>
          )}
        </div>
        <div className="h-20 flex flex-col gap-2 items-center">
          <span className="font-semibold text-slate-200">{username}</span>
          <span className="text-xs text-slate-200">{isLoggedIn ? followerCount + ' Followers' : ''}</span>
          <Link
            href={isLoggedIn ? `/social-media-app/profile/${username}` : '/login'}
            className="text-white text-sm p-2 rounded-md bg-blue-500 hover:bg-blue-700 transition duration-100"
          >
            {isLoggedIn ? 'My Profile' : 'Log In'}
          </Link>
        </div>
    </div>
  );
};

export default ProfileCard;
'use client';
import { useEffect, useState } from "react";
import React from "react";
import Link from "next/link";
import Image from "next/image";
import * as Fetch from "../Functions"

const UserMedia = ({userData}:{userData:Record<string,any>}) => {
    // const [displayName, setDisplayName] = useState("");
    // const [username, setUsername] = useState("");
    // const [bio, setBio] = useState("");
    // const [occupation, setOccupation] = useState("");
    // const [location, setLocation] = useState("");
    // const [birthdate, setBirthdate] = useState("");
    // const [joinDate, setJoinDate] = useState("");

    // useEffect(()=>{
    //     setDisplayName(userData.displayName);
    //     setUsername(userData.username);
    //     setBio(userData.bio);
    //     setLocation(userData.location);
    //     setOccupation(userData.occupation);
    // })

    console.log(userData);


    return (
        <div className="p-4 bg-white rounded-lg shadow-md text-sm flex flex-col gap-4">
            <div className='flex justify-between items-center font-medium'>
                <span className="text-gray-500">User Information</span>
                <Link href="/" className="text-blue-500 text-xs">See All</Link>
            </div>
            <div className='flex flex-col gap-4 text-gray-500'>
                <div className='flex items-center gap-2'>
                    {/** Adjust user profile name*/}
                    <span className="text-xl text-black">{userData.displayName}</span>
                    {/** Adjust @username */}
                    <span className="text-sm">@{userData.username}</span>
                </div>
                {/** User Bio */}
                <p>
                    {userData.bio}
                </p>
                <div className='flex items items-center gap-2'>
                    <Image src="/map.png" alt="" width={16} height={16}/>
                    <span><b>{userData.location}</b></span>
                </div>
                <div className='flex items items-center gap-2'>
                    <Image src="/work.png" alt="" width={16} height={16}/>
                    <span><b>{userData.occupation}</b></span>
                </div>
                <div className='flex items items-center gap-2'>
                    <Image src="/school.png" alt="" width={16} height={16}/>
                    <span><b>School of School</b></span>
                </div>
                <div className="flex items-center justify-between">
                    <div className="flex gap-1 items-center">
                        <Image src="/link.png" alt="" width={16} height={16}/>
                        <a href="/" className="text-blue-500 font-medium">Web Link</a>
                    </div>
                    <div className="flex gap-1 items-center">
                        <Image src="/date.png" alt="" width={16} height={16}/>
                        <span>Birthday</span>
                    </div>
                </div>
                        {/*TODO: Make this actually work and then uncomment it*/}
                {/*<button className="bg-blue-500 text-white text-sm rounded-md p-2">Follow</button>*/}
                <Link href="/social-media-app/direct-messages">
                    <button className="bg-blue-500 text-white text-sm rounded-md p-2 items-center w-full">Message</button>
                </Link>
                <span className="text-red-400 self-end text-xs cursor-pointer">Block User</span>
            </div>
        </div>
    )
}

export default UserMedia

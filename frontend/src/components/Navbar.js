/** Need to import -> npm install @mui/material @emotion/react @emotion/styled @mui/icons-material */
'use client'
import { useState, useEffect } from 'react';
import * as React from "react";
import '../app/styles/app.layout.css'
import Link from "next/link";
import Image from "next/image";
import MobileMenu from "../app//styles/mobile.menu";
import * as Fetch from "./Functions";
import Tooltip from '@mui/material/Tooltip';


const Navbar = () => {
  const [username, setUsername] = useState("Login"); 
  const [accountUrl, setAccountUrl] = useState("/login");
  useEffect(() => {
    const init = async() => {
      if(!(await Fetch.validateToken())){
        setUsername("Login");
        setAccountUrl("/login");
      } else {
        setUsername("Account");
        setAccountUrl("/social-media-app/account");
      }
    }
    init();
  }, []); 

  return (
    <div className="h-24 flex items-center justify-between bg-[#223344]">
      {/* LEFT */}
      <div className="md:hidden lg:block w-[20%]">
        <Link href="/social-media-app" className="font-bold text-xl text-blue-600">
          CONT@CTS
        </Link>
      </div>
      {/* CENTER */}
      <div className="hidden md:flex w-[50%] text-sm items-center justify-between">
        {/* LINKS */}
        <div className="flex gap-6 text-gray-600">
          <Tooltip title="Home" arrow>
          	<Link href="/social-media-app" className="flex items-center gap-2">
            <Image
              src="/home.png"
              alt="Homepage"
              width={16}
              height={16}
              className="w-4 h-4"
            />
            <span>Home</span>
          </Link>
          </Tooltip>
          <Tooltip title="Followers/Following" arrow>
          	<Link href="/social-media-app/friends-list" className="flex items-center gap-2">
            <Image
              src="/friends.png"
              alt="Friends"
              width={16}
              height={16}
              className="w-4 h-4"
            />
            <span>Following</span>
          </Link>
          </Tooltip>
        </div>
        <div className='hidden xl:flex p-2 bg-slate-100 items-center rounded-xl'>
          <input type="text" placeholder="search..." className="bg-transparent outline-none"/>
          <Image src="/search.png" alt="" width={14} height={14}/>
        </div>
      </div>
      {/* RIGHT */}
      <div className="w-[30%] flex items-center gap-4 xl:gap-8 mr-8 justify-end">
            <div className="cursor-pointer">
              <Tooltip title="Follower/Following" arrow>
              	<Image src="/people.png" alt="" width={24} height={24} />
              </Tooltip>
            </div>
			<Tooltip title="Messages" arrow>
				<Link href="/social-media-app/direct-messages">
              	<Image src="/messages.png" alt="" width={20} height={20} />
            </Link>
			</Tooltip>
			<Tooltip title="Notifications" arrow>
				<div className="cursor-pointer">
              	<Image src="/notifications.png" alt="" width={20} height={20} />
            </div>
			</Tooltip>
            <div className="flex items-center gap-2 text-sm">
              <Image src="/login.png" alt="" width={20} height={20} />
              <Link href={accountUrl}>{username}</Link>
            </div>
            <MobileMenu/>
      </div>
    </div>
  );
};

export default Navbar;
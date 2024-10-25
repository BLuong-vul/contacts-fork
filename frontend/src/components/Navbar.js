/** Need to import -> npm install @mui/material @emotion/react @emotion/styled @mui/icons-material */
'use client'
import * as React from "react";
import '../app/styles/app.layout.css'
import Link from "next/link";
import Image from "next/image";
import MobileMenu from "../app//styles/mobile.menu";


const Navbar = () => {
  return (
    <div className="h-24 flex items-center justify-between">
      {/* LEFT */}
      <div className="md:hidden lg:block w-[20%]">
        <Link href="/" className="font-bold text-xl text-blue-600">
          CONT@CTS
        </Link>
      </div>
      {/* CENTER */}
      <div className="hidden md:flex w-[50%] text-sm items-center justify-between">
        {/* LINKS */}
        <div className="flex gap-6 text-gray-600">
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
        </div>
        <div className='hidden xl:flex p-2 bg-slate-100 items-center rounded-xl'>
          <input type="text" placeholder="search..." className="bg-transparent outline-none"/>
          <Image src="/search.png" alt="" width={14} height={14}/>
        </div>
      </div>
      {/* RIGHT */}
      <div className="w-[30%] flex items-center gap-4 xl:gap-8 justify-end">
            <div className="cursor-pointer">
              <Image src="/people.png" alt="" width={24} height={24} />
            </div>
            <Link href="/social-media-app/direct-messages">
              <Image src="/messages.png" alt="" width={20} height={20} />
            </Link>
            <div className="cursor-pointer">
              <Image src="/notifications.png" alt="" width={20} height={20} />
            </div>
            <div className="flex items-center gap-2 text-sm">
              <Image src="/login.png" alt="" width={20} height={20} />
              <Link href="/sign-up">Login/Register</Link>
            </div>
            <MobileMenu/>
      </div>
    </div>
  );
};

export default Navbar;
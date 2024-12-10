'use client'
import { useState, useEffect } from 'react';
import * as React from "react";
import '../app/styles/app.layout.css'
import Link from "next/link";
import Image from "next/image";
import MobileMenu from "../app//styles/mobile.menu";
import * as Fetch from "./Functions";
import { validateTokenWithRedirect, validateToken } from './ValidationFunctions';
import Tooltip from '@mui/material/Tooltip';

const Navbar = () => {
  const [username, setUsername] = useState("Login"); 
  const [accountUrl, setAccountUrl] = useState("/login");
  const [searchTerm, setSearchTerm] = useState("");
  const [searchPostResults, setSearchPostResults] = useState([]);
  const [searchUserResults, setSearchUserResults] = useState([]);
  const [isFocused, setIsFocused] = useState(false);

  useEffect(() => {
    const init = async() => {
      if(!(await validateToken())) {
        setUsername("Login");
        setAccountUrl("/login");
      } else {
        setUsername("Account");
        setAccountUrl("/social-media-app/account");
      }
    };
    init();
  }, []); 

  // Function to handle search input change
  const handleSearchChange = async (event) => {
    const query = event.target.value;
    setSearchTerm(query); // Update search term

    if (query.length > 0) {
      // Fetch search results when query is non-empty
      const postResults = await Fetch.searchPost(query); 
      const userResults = await Fetch.searchUser(query);
      setSearchPostResults(postResults); // Update search results state
      setSearchUserResults(userResults);
    } else {
      setSearchPostResults([]); // Clear results if query is empty
      setSearchUserResults([]);
    }
  };

  // Function to handle preview click
  const handlePreviewClick = () => {
    setSearchTerm(""); // Clear search term when a preview is clicked
    setSearchPostResults([]); // Optionally, clear search results
    setSearchUserResults([]); // Optionally, clear search results
  };

  return (
    <div className="h-24 flex items-center justify-between bg-slate-900">
      {/* LEFT */}
      <div className="md:hidden lg:block w-[20%]">
        <Link href="/social-media-app" className="ml-4 font-bold text-3xl text-blue-600">
          CONT@CTS
        </Link>
      </div>
      {/* CENTER */}
      <div className="hidden md:flex w-[50%] text-m items-center justify-between relative">
        {/* LINKS */}
        <div className="flex gap-6 text-slate-400">
          <Tooltip title="Home" arrow>
            <Link href="/social-media-app" className="flex items-center gap-2 hover:text-slate-100 transition duration-200">
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
            <Link href="/social-media-app/friends-list" className="flex items-center gap-2 hover:text-slate-100 transition duration-200">
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

        {/* SEARCH BAR */}
        <div className="w-3/6">
          <div className="hidden xl:flex p-2 bg-slate-500 text-slate-100 items-center rounded-xl">
            <input
              type="text"
              placeholder="search..."
              className="bg-transparent outline-none placeholder-slate-200 flex-grow"
              value={searchTerm}
              onChange={handleSearchChange}
              onFocus={() => setIsFocused(true)} 
              onBlur={() => setIsFocused(false)} 
            />
            <Image src="/search.png" alt="" width={14} height={14} />
          </div>

        {/* Search Preview */}
        {searchTerm && (searchPostResults.length > 0 || searchUserResults.length > 0) && (
          <div className="absolute w-3/6 bg-slate-700 rounded-xl shadow-lg z-40">
            {/* POSTS */}
            {searchPostResults.length > 0 && (
              <div className="max-h-64 overflow-y-auto">
                {searchPostResults.map((post, index) => (
                  <div key={index} className="p-4 border-b border-slate-600">
                    <Link 
                      href={`/social-media-app/posts/${post.postId}`} // TODO: CREATE PROPER POST PAGES
                      className="text-slate-100 hover:text-blue-400 group"
                      onClick={handlePreviewClick}
                    >
                      <h4 className="font-semibold">{post.title}</h4>
                      <div className="flex gap-4">
                        <p className="text-sm mr-4 text-slate-400 group-hover:text-blue-400">
                          {post.postedBy.displayName || post.postedBy.username}
                        </p>
                        <p className="text-sm text-slate-500 group-hover:text-blue-400">
                          @{post.postedBy.username}
                        </p>
                      </div>
                    </Link>
                  </div>
                ))}
              </div>
            )}
            {searchPostResults.length > 0 && searchUserResults.length > 0 && (
                  <div className="bg-slate-900 border-t border-slate-500 h-2 z-60"></div>
            )}
            {/* USERS */}
            {searchUserResults.length > 0 && (
              <div className="max-h-48 overflow-y-auto">
                {searchUserResults.map((user, index) => (
                  <div key={index} className="p-4 border-b border-slate-600">
                    <Link 
                      href={`/social-media-app/profile/${user.username}`} 
                      className="text-slate-100 hover:text-blue-400 group"
                      onClick={handlePreviewClick}
                    >
                      <div className="flex gap-4">
                        <p className="text-lg mr-4 text-slate-200 group-hover:text-blue-400">
                          {user.displayName || user.username}
                        </p>
                        <p className="text-lg text-slate-400 group-hover:text-blue-400">
                          @{user.username}
                        </p>
                      </div>
                    </Link>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        </div>
      </div>
      {/* RIGHT */}
      <div className="w-[30%] flex items-center gap-4 xl:gap-8 mr-8 justify-end">
        <Tooltip title="Messages" arrow>
          <Link href="/social-media-app/direct-messages" className="text-m flex gap-2 text-slate-400 hover:text-slate-100 transition duration-200">
            <Image src="/messages.png" alt="" width={20} height={20} />
            Messages
          </Link>
        </Tooltip>

        <div className="flex items-center gap-2 text-m text-slate-400 hover:text-slate-100 transition duration-200">
          <Image src="/login.png" alt="" width={20} height={20} />
          <Link href={accountUrl}>{username}</Link>
        </div>
        <MobileMenu/>
      </div>
    </div>
  );

};

export default Navbar;

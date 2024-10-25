'use client';
import { useEffect, useState } from "react";
import homepagestyles from '../../social-media-homepage.module.css'; 
import LeftMenu from '../../../../components/leftmenu/left-menu';
import UserInfo from '../../../../components/rightmenu/user-info';
import * as Fetch from '../../../../components/Functions';
import Image from "next/image";
import Link from "next/link";
import { Post } from '../../Post.js';


export default function ProfilePage({ params }) {
  const { username } = params;
  const [profileData, setProfileData] = useState(null);
  const [error, setError] = useState(null);
  const [postCount, setPostCount] = useState(0);
  const [followersCount, setFollowersCount] = useState(0);
  const [followingCount, setFollowingCount] = useState(0);
  const [posts, setPosts] = useState([]);
  const [isFollowing, setIsFollowing] = useState(false);

  // Fetch info for page
  // Also check if we are logged in. If we are, update isFollowing
  useEffect(() => {
    const init = async () => {
      try {
        const data = await Fetch.getPublicInfo(username);
        setProfileData(data);
        setFollowersCount(data.followerCount);
        setFollowingCount(data.followingCount);

        // Handle posts
        const posts = await Fetch.getPostsByUser(username);
        setPosts(posts);
        setPostCount(posts.length);

        // Check if we are logged in
        // console.log("DEBUG: checking login...");
        if (await Fetch.validateToken("THIS ARG IS FOR DEBUGGING CONVENIENCE, REMOVE IT LATER")){
          // console.log("DEBUG: logged in");
          //If logged in, check if already following
          const isFollowing = await Fetch.isFollowing(data.username);
          setIsFollowing(isFollowing);
        }
      } catch (error) {
        setError(error.message);
      }
    };
    init();
  }, [username]);
    
  // Handle follow button click
  const handleFollow = async () => {
    // Check if logged in
    if (Fetch.validateTokenWithRedirect("THIS ARG IS FOR DEBUGGING CONVENIENCE, REMOVE IT LATER")){
      //If logged in, check if already following
      const isFollowing = await Fetch.isFollowing(profileData.username);
      setIsFollowing(isFollowing);

      // Follow if we are not following. Unfollow if we are
      // TODO: If following fails, Fetch.followUser returns false, maybe use that to decide if we should change isFollowing
      if (!isFollowing){
        Fetch.followUser(profileData.userId);
        setIsFollowing(true);
        setFollowersCount(followersCount+1);
      } else {
        Fetch.unfollowUser(profileData.userId);
        setIsFollowing(false);
        setFollowersCount(followersCount-1);
      }
    }
  };



  if (error) return <div>Error: {error}</div>;
  if (!profileData) return <div>Loading...</div>;

  return (
    <div className="flex gap-6 pt-6">
      <div className="hidden xl:block w-[20%]">
        <LeftMenu type="profile" />
      </div>
      <div className="w-full lg:w-[70%] xl:w-[50%]">
        <div className="flex flex-col gap-6">
          <div className="flex flex-col items-center justify-center">
            <div className="w-full h-64 relative">
              <Image
                src="https://images.pexels.com/photos/28551762/pexels-photo-28551762/free-photo-of-stunning-italian-alps-mountain-landscape-at-sunset.jpeg?auto=compress&cs=tinysrgb&w=1200&lazy=load"
                alt=""
                fill
                className="rounded-md object-cover"
              />
              <Image
                src="https://images.pexels.com/photos/28210177/pexels-photo-28210177/free-photo-of-a-mountain-goat-standing-on-a-grassy-hill.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
                alt=""
                width={128}
                height={128}
                className="w-32 h-32 rounded-full absolute left-0 right-0 m-auto -bottom-16 ring-4 ring-white object-cover"
              />
            </div>
            {/** Name of user*/}
            <h1 className="mt-20 mb-4 text-2xl font-medium">
              {username}
            </h1>
            {/** Display for followers*/}
            <div className="flex items-center justify-center gap-12 mb-4">
              <div className="flex flex-col items-center">
                <span className="font-medium">{postCount}</span>
                <span className="text-sm">Posts</span>
              </div>
              <div className="flex flex-col items-center">
                <span className="font-medium">{followersCount}</span>
                <span className="text-sm">Followers</span>
              </div>
              <div className="flex flex-col items-center">
                <span className="font-medium">{followingCount}</span>
                <span className="text-sm">Following</span>
              </div>
            </div>
            {/** End display for followers */}
          </div>
          {/** FOLLOW BUTTON **/}
          <button
            onClick={handleFollow}
            className="bg-blue-500 text-white text-xs p-2 rounded-md"
          >
            {isFollowing ? 'Unfollow' : 'Follow'}
          </button>
          {/* Post display section */}
				<div className={homepagestyles.postsContainer}>
					{posts.map(post => post.render())}
	            </div>
        </div>
      </div>
      <div className="hidden lg:block w-[30%]">
        <UserInfo usernameKey={username} />
      </div>
    </div>
  );
}

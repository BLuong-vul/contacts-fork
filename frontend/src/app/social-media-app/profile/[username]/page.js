'use client';
import { useEffect, useState } from "react";
import LeftMenu from '../../../../components/leftmenu/left-menu';
import Image from "next/image";
import Link from "next/link";

export default function ProfilePage({ params }) {
  const { username } = params;
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        console.log(username);
        const res = await fetch(`http://localhost:8080/user/public-info?username=${username}`);
        if (!res.ok) throw new Error("Failed to fetch data");

        const data = await res.json();
        setProfile(data);
      } catch (error) {
        setError(error.message);
      }
    };

    fetchData();
  }, [username]);

  if (error) return <div>Error: {error}</div>;
  if (!profile) return <div>Loading...</div>;

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
                <span className="font-medium">999</span>
                <span className="text-sm">Posts</span>
              </div>
              <div className="flex flex-col items-center">
                <span className="font-medium">999</span>
                <span className="text-sm">Followers</span>
              </div>
              <div className="flex flex-col items-center">
                <span className="font-medium">999</span>
                <span className="text-sm">Following</span>
              </div>
            </div>
            {/** End display for followers */}
          </div>
          <Link href={`/social-media-app/profile/${username}`} className="bg-blue-500 text-white text-xs p-2 rounded-md">Follow</Link>
          {/* Adjust for post updates
          *<Feed username={user.username}/>*/}
        </div>
      </div>
      {/*<div className="hidden lg:block w-[30%]">
        <RightMenu user={user} />
      </div>*/}
    </div>
  );
}

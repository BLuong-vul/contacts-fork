import React from "react";
import Image from "next/image";
import Link from "next/link";

const ProfileCard = () => {
  return (
    <div className='p-4 bg-white rounded-lg shadow-md text-sm flex flex-col gap-6'>
        <div className='h-20 relative'>
            <Image src="https://images.pexels.com/photos/26125152/pexels-photo-26125152/free-photo-of-the-milky-way-over-the-lake-at-night.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2" alt="" fill className="rounded-md" object-cover/>
            <Image src="https://images.pexels.com/photos/13659374/pexels-photo-13659374.jpeg?auto=compress&cs=tinysrgb&w=1200&lazy=load" alt="" width={48} height={48} className="rounded-full object-cover w-12 h-12 absolute left-0 right-0 m-auto -bottom-6 ring-1 ring-white z-10"/>
        </div>
        <div className="h-20 flex flex-col gap-2 items-center">
          <span className="font-semibold">Captian Crunch</span>
          <span className="text-xs text-gray-500">123 Followers</span>
          <Link href="/social-media-app/user-profile" className="bg-blue-500 text-white text-xs p-2 rounded-md">My Profile</Link>
        </div>
    </div>
  );
};

export default ProfileCard;
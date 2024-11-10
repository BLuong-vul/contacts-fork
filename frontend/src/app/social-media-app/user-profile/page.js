{/*import Feed from "@/components/feed/Feed";*/}
import RightMenu from '../../../components/rightmenu/right-menu';
import LeftMenu from '../../../components/leftmenu/left-menu';
import Feed from "../../../components/Feed";
import Image from "next/image";
import Link from "next/link";
const userProfile = () => {
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
              Captian Crunch
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
          <Link href="/social-media-app/user-profile" className="bg-blue-500 text-white text-xs p-2 rounded-md">Follow</Link>
          {/* Adjust for post updates
          *<Feed username={user.username}/>*/}
          <Feed/>
        </div>
      </div>
      <div className="hidden lg:block w-[30%]">
        {/** <RightMenu user={user} />*/}
        <RightMenu/>
      </div>
    </div>
  );
};
export default userProfile;
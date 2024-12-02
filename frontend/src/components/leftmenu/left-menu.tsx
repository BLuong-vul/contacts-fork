import React from "react";
import Link from "next/link";
import Image from "next/image";
import ProfileCard from "@/components/leftmenu/profile-card";

const LeftMenu = ({ type }: { type: "home" | "profile" }) => {
  return (
    <div className="flex flex-col gap-6">
      {type === "home" && <ProfileCard />}
      <div className="p-4 bg-slate-600 rounded-lg shadow-md text-sm text-gray-500 flex flex-col gap-2 h-96">

      </div>
    </div>
  );
};

export default LeftMenu;
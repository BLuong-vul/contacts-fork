import React from "react";
import FollowRequests from "@/components/rightmenu/follow-request";

const RightMenu = ({userId} : {userId?: string}) => {
    return <div className="flex flex-col gap-6">
        <FollowRequests/>
    </div>;
};
export default RightMenu;
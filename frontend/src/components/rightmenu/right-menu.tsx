import React from "react";
import FollowRequests from "@/components/rightmenu/follow-request";
import UserInfo from "@/components/rightmenu/user-info";
import UserMedia from "@/components/rightmenu/user-media";


const RightMenu = ({userId} : {userId?: string}) => {
    return(
        <div className="flex flex-col gap-6">
            {/** WILL NOT SHOW UNLESS REGISTERED USER ID */}
        { userId ? (
            <>
            <UserInfo userId={userId}/>
            <UserMedia userId={userId}/>
            </>
        ) : null}
        <FollowRequests/>
    </div>
    );
};
export default RightMenu;
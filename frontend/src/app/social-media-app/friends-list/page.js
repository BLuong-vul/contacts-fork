'use client';
import { useState, useEffect } from 'react';
import styles from './friends-list.module.css';
import Link from 'next/link';
import * as Fetch from '../../../components/Functions';
import { FaUser } from "react-icons/fa";

export default function FollowersListPage() {
    const [userId, setUserId] = useState('');
    const [currentUsername, setCurrentUsername] = useState('');
    const [followingUsers, setFollowingUsers] = useState([]);
    const [followers, setFollowers] = useState([]);
    const [mutuals, setMutuals] = useState([]);
    const [profilePictures, setProfilePictures] = useState({}); // Store profile picture URLs

    // Function to fetch profile picture URL
    const fetchProfilePicture = async (user) => {
        if (user?.profilePictureFileName) {
            const mediaBlob = await Fetch.getMedia(user.profilePictureFileName);
            if (mediaBlob instanceof Blob) {
                return URL.createObjectURL(mediaBlob);
            }
        }
        return null;
    };

    // Initialize everything on mount
    useEffect(() => {
        const fetchUserId = async () => {
            try {
                const currentUserInfo = await Fetch.getCurrentUserInfo();
                if (!currentUserInfo) window.location.href = '/login';

                setUserId(currentUserInfo.userId);
                setCurrentUsername(currentUserInfo.username);

                // Fetch lists
                const [followingRes, followersRes] = await Promise.all([
                    Fetch.getFollowingList(),
                    Fetch.getFollowersList(),
                ]);

                setFollowingUsers(followingRes);
                setFollowers(followersRes);

                // Identify mutuals
                const mutualsFilter = followingRes.filter(user =>
                    followersRes.some(follower => follower.userId === user.userId)
                );
                setMutuals(mutualsFilter);

                // Fetch profile pictures
                const allUsers = [...followingRes, ...followersRes, ...mutualsFilter];
                const updatedPictures = {};

                for (const user of allUsers) {
                    if (!updatedPictures[user.userId]) {
                        updatedPictures[user.userId] = await fetchProfilePicture(user);
                    }
                }

                setProfilePictures(updatedPictures);
            } catch (error) {
                console.error('Error fetching data:', error);
                throw error;
            }
        };

        fetchUserId();
    }, []);

    return (
        <div className={styles.friendsContainer}>
            {/*Mutuals section */}
            <div className={styles.section}>
                <h1 className={styles.title}>Mutuals</h1>
                <ul className={styles.friendList}>
                    {mutuals.map(user => (
                        <li key={user.userId} href={`./profile/${user.username}`} className="mt-4 flex text-xl bg-slate-900 rounded-md p-2 cursor-pointer hover:bg-slate-950 transition duration-100 overflow-hidden text-ellipsis whitespace-nowrap">
                            {profilePictures[user.userId] ? (
                                <img
                                    src={profilePictures[user.userId]}
                                    alt={`${user.username}'s profile`}
                                    className="w-8 h-8 rounded-full ml-2 mr-4"
                                />
                            ) : (
                                <FaUser className="w-8 h-8 rounded-full bg-slate-600 ml-2 mr-4" />
                            )}
                            <a href={`./profile/${user.username}`} className="mr-4 text-slate-200"> {user.displayName || user.username} </a>
                            <a className="text-slate-400"> @{user.username} </a>
                        </li>
                    ))}
                </ul>
            </div>
            {/*Followers section */}
            <div className={styles.section}>
                <h1 className={styles.title}>Followers</h1>
                <ul className={styles.friendList}>
                    {followers.map(user => (
                        <li key={user.userId} href={`./profile/${user.username}`} className="mt-4 flex text-xl bg-slate-900 rounded-md p-2 cursor-pointer hover:bg-slate-950 transition duration-100 overflow-hidden text-ellipsis whitespace-nowrap">
                            {profilePictures[user.userId] ? (
                                <img
                                    src={profilePictures[user.userId]}
                                    alt={`${user.username}'s profile`}
                                    className="w-8 h-8 rounded-full ml-2 mr-4"
                                />
                            ) : (
                                <FaUser className="w-8 h-8 rounded-full bg-slate-600 ml-2 mr-4" />
                            )}
                            <a href={`./profile/${user.username}`} className="mr-4 text-slate-200"> {user.displayName || user.username} </a>
                            <a className="text-slate-400"> @{user.username} </a>
                        </li>
                    ))}
                </ul>
            </div>
            {/* Following Section */}
            <div className={styles.section}>
                <h1 className={styles.title}>Following</h1>
                <ul className={styles.friendList}>
                    {followingUsers.map(user => (
                        <li key={user.userId} href={`./profile/${user.username}`} className="mt-4 flex text-xl bg-slate-900 rounded-md p-2 cursor-pointer hover:bg-slate-950 transition duration-100 overflow-hidden text-ellipsis whitespace-nowrap">
                            {profilePictures[user.userId] ? (
                                <img
                                    src={profilePictures[user.userId]}
                                    alt={`${user.username}'s profile`}
                                    className="w-8 h-8 rounded-full ml-2 mr-4"
                                />
                            ) : (
                                <FaUser className="w-8 h-8 rounded-full bg-slate-600 ml-2 mr-4" />
                            )}
                            <a href={`./profile/${user.username}`} className="mr-4 text-slate-200"> {user.displayName || user.username} </a>
                            <a className="text-slate-400"> @{user.username} </a>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}

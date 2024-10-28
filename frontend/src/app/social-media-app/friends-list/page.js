'use client';
import { useState, useEffect } from 'react';
import styles from './friends-list.module.css'; // adjust the path as necessary
import Link from 'next/link';
import * as Fetch from '../../../components/Functions';

export default function FollowersListPage() {
    const [userId, setUserId] = useState('');
    const [currentUsername, setCurrentUsername] = useState('');
    const [followingUsers, setFollowingUsers] = useState([]);
    const [followers, setFollowers] = useState([]);

    // Initialize everything on mount
    useEffect(() => {
        const fetchUserId = async () => {
            try {
                const currentUserInfo = await Fetch.getCurrentUserInfo();

                setUserId(currentUserInfo.userId);
                setCurrentUsername(currentUserInfo.username);
                console.log(currentUserInfo);

                // Following users list
                const followingRes = await Fetch.getFollowingList();
                setFollowingUsers(followingRes);


                // Followers list
                const followersRes = await Fetch.getFollowersList();
                setFollowers(followersRes);
            } catch (error) {
                console.error('Error fetching ID:', error);
                throw error;
            }
        };

        fetchUserId();
    }, []);




    return (
        <div className={styles.friendsContainer}>
            <div className={styles.section}>
                <h1 className={styles.title}>Mutuals</h1>
                {/* Following Section*/}
            </div>
            {/*Followers section */}
            <div className={styles.section}>
                <h1 className={styles.title}>Followers</h1>
                <ul className={styles.friendList}>
                    {followers.map(user => (
                        <li key={user.userId} className={styles.friendItem}>
                            {user.username}
                        </li>
                    ))}
                </ul>
            </div>
            {/* Following Section*/}
            <div className={styles.section}>
                <h1 className={styles.title}>Following</h1>
                <ul className={styles.friendList}>
                    {followingUsers.map(user => (
                        <li key={user.userId} className={styles.friendItem}>
                            {user.username}
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}

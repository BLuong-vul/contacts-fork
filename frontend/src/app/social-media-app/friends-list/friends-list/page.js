'use client';
import { useState, useEffect } from 'react';
import styles from './friends-list.module.css'; // adjust the path as necessary
import Link from 'next/link';
import { validateTokenWithRedirect } from '../../../components/Functions';

export default function FollowersListPage() {
    const [userId, setUserId] = useState('');
    const [currentUsername, setCurrentUsername] = useState('');
    const [followingUsers, setFollowingUsers] = useState([]);
    const [followers, setFollowers] = useState([]);

    // Initialize everything on mount
    useEffect(() => {
        const fetchUserId = async () => {
            try {
                const token = localStorage.getItem('token');
                validateTokenWithRedirect(token);

                const res = await fetch(`/api/user/info`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });

                if (!res.ok) throw new Error('Failed to fetch ID: ' + res.statusText);

                const result = await res.json();
                setUserId(result.userId);
                setCurrentUsername(result.username);
                console.log(result);

                // Following users list
                const followingRes = await fetch('/api/user/following/list', {
                    headers: {
                      'Authorization': `Bearer ${token}`,
                    },
                });

                if (!followingRes.ok) throw new Error('Failed to fetch followed users: ' + followingRes.statusText);
                const followingUsersJson = await followingRes.json();
                setFollowingUsers(followingUsersJson);


                // Followers list
                const followersRes = await fetch('/api/user/followers/list', {
                    headers: {
                      'Authorization': `Bearer ${token}`,
                    },
                });

                if (!followersRes.ok) throw new Error('Failed to fetch followed users: ' + followersRes.statusText);
                const followersJson = await followersRes.json();
                setFollowers(followersJson);
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
            <div className={styles.section}>
            	<h1 className={styles.title}>Followers</h1>
            	{/*Followers section */}
            </div>
			<div className={styles.section}>
				<h1 className={styles.title}>Following</h1>
				{/* Following Section*/}
			</div>
        </div>
    );
}

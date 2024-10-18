'use client';
import { useState, useEffect } from 'react';
import styles from './friends-list.module.css'; // adjust the path as necessary
import Link from 'next/link';
import { validateTokenWithRedirect } from '../../../components/Functions';

export default function FriendsListPage() {
    const [userId, setUserId] = useState('');
    const [currentUsername, setCurrentUsername] = useState('');
    const [followedUsers, setFollowedUsers] = useState([]);

    // Initialize everything on mount
    useEffect(() => {
        const fetchUserId = async () => {
            try {
                const token = localStorage.getItem('token');
                validateTokenWithRedirect(token);

                const res = await fetch(`http://localhost:8080/user/info`, {
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


                const followedRes = await fetch('http://localhost:8080/user/following/list', {
                    headers: {
                      'Authorization': `Bearer ${token}`,
                    },
                });

                if (!followedRes.ok) throw new Error('Failed to fetch followed users: ' + followedRes.statusText);
                const followedUsersJson = await followedRes.json();
                setFollowedUsers(followedUsersJson);
            } catch (error) {
                console.error('Error fetching ID:', error);
                throw error;
            }
        };

        fetchUserId();
    }, []);




    return (
        <div className={styles.friendsContainer}>
            <h1 className={styles.title}>Your Friends</h1>
            
            {/* Friends list section */}
            <ul className={styles.friendList}>
                {followedUsers.map(user => (
                    <li key={user.userId} className={styles.friendItem}>
                        {user.username}
                    </li>
                ))}
            </ul>
        </div>
    );
}

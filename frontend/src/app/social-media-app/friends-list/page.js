import styles from './friends-list.module.css'; // adjust the path as necessary
import Link from 'next/link';

export default function FriendsListPage() {
    const friends = [
        { id: 1, name: "John Doe" },
        { id: 2, name: "Jane Smith" },
        { id: 3, name: "Emily Johnson" }
    ];

    return (
        <div className={styles.friendsContainer}>
            <h1 className={styles.title}>Your Friends</h1>
            
            {/* Link boxes section */}
            <nav className={styles.navContainer}>
            	<Link href="/social-media-app" className={styles.linkBox}>
            		Home
          		</Link>
                <Link href="/social-media-app/friends-list" className={styles.linkBox}>
            		Friends List
          		</Link>
            </nav>

            {/* Friends list section */}
            <ul className={styles.friendList}>
                {friends.map(friend => (
                    <li key={friend.id} className={styles.friendItem}>
                        {friend.name}
                    </li>
                ))}
            </ul>
        </div>
    );
}

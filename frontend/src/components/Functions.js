import Post from "./Post";



// Tries to unfollow a user by ID
// Returns true if successful, false otherwise
export async function unfollowUser(followeeId){
	try {
		const token = localStorage.getItem('token');
		const res = await fetch(`/api/user/unfollow/${followeeId}`, {
		method: 'POST',
		headers: {
			  'Authorization': `Bearer ${token}`,
			  'Content-Type': 'application/json',
			},
		});
		if (!res.ok) throw new Error('Failed to unfollow');

		// console.log("DEBUG: unfollowed successfully");
		return true;
	} catch (error) {
		console.error("Error unfollowing user");
		return false;
	}
}

// Tries to follow a user by ID
// Returns true if successful, false otherwise
export async function followUser(followeeId){
	try {
		const token = localStorage.getItem('token');
		const res = await fetch(`/api/user/follow/${followeeId}`, {
		method: 'POST',
		headers: {
			  'Authorization': `Bearer ${token}`,
			  'Content-Type': 'application/json',
			},
		});
		if (!res.ok) throw new Error('Failed to follow');

		// console.log("DEBUG: followed successfully");
		return true;
	} catch (error) {
		console.error("Error following user");
		return false;
	}
}

// TODO: This currently searches a following list. That should maybe be done on the backend instead. 
// Returns true if current user is following followee, else returns false
export async function isFollowing(followeeUsername){
	try{
		const token = localStorage.getItem('token');
		const followedRes = await fetch('/api/user/following/list', {
		  headers: {
		    'Authorization': `Bearer ${token}`,
		  },
		});
		if (!followedRes.ok) throw new Error("Failed to fetch followed users");
		const followedUsers = await followedRes.json();
		const isFollowing = followedUsers.some(user => user.username === followeeUsername);
		return isFollowing;
	} catch (error){
		console.error("Error fetching follower information");
		throw error;
	}
}


// Search for a user's public info based on username
// Returns it as json
export async function getPublicInfo(username){
	try{
		const res = await fetch(`/api/user/public-info?username=${username}`);
		if (!res.ok) throw new Error("Failed to fetch data");
		const data = await res.json();

		// console.log("DEBUG: " + data.followerCount);
		return data;
	} catch (error){
		console.error("Error fetching public info");
		throw error;
	}
}

// Fetches all posts made by a certain user
// Returns a list of Posts (see Post.js)
export async function getPostsByUser(username){
	try{
		const page=0;
		const size=10;

		const postRes = await fetch(`/api/post/by-user?username=${username}&page=${page}&size=${size}`);
		if (!postRes.ok) throw new Error('Network response not ok ' + postRes.statusText);
		
		const pagedData = await postRes.json();
		const posts = pagedData.content.map(postData => new Post(postData));
		return posts;
	} catch (error){
		console.error("Error fetching posts");
		throw error;
	}
}

// Checks for token and if it is valid
// Deletes token if not valid
// Returns true if valid, false if not
export async function validateToken(temp){
	const token = localStorage.getItem('token');
	if (!token){
		console.log('User not logged in.');
		return false;
	}

	try {
		const response = await fetch('/api/auth/validate', {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${token}`,
			},
		});

		if (!response.ok){
			throw new Error('Invalid token');
		}
		return true;
	} catch (error){
		console.log('Login expired.');
		localStorage.removeItem('token');
		return false;
	}
}

// Checks for token and if it is valid
// Deletes token if not valid
// Redirects if not valid
export async function validateTokenWithRedirect(){
	const token = localStorage.getItem('token');
	if (!token){
		console.log('User not logged in. Redirecting to login page.');
		window.location.href = '/login'; 
		return false;
	}

	try {
		const response = await fetch('/api/auth/validate', {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${token}`,
			},
		});

		if (!response.ok){
			throw new Error('Invalid token');
		}
		return true;
	} catch (error){
		console.error('Login expired. Redirecting to login page.');
		localStorage.removeItem('token');
		window.location.href = '/login';
		return false;
	}
}
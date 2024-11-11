import Post from "./Post";

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';

export async function updateBio(bio){
	validateTokenWithRedirect();
	const token = localStorage.getItem('token');

	try {
		const updateRes = await fetch(`${baseURL}/user/account/bio?bio=${bio}`, {
			method: 'POST',
		    headers: {
		      'Authorization': `Bearer ${token}`,
		    },
		});

		if (!updateRes.ok) throw new Error('Failed to update bio: ' + updateRes.statusText);

		return true;
	} catch (error){
		console.error('Error:', error);
		return false;
	}
}


export async function createAccount(userData){
	const { confirmPassword, ...userPayload } = userData;

	// Check if password and confirm password match
	if (userData.password !== confirmPassword) {
	  alert('Passwords do not match');
	  return;
	}

	try {							
	  const response = await fetch(`${baseURL}/auth/register`, {
	    method: 'POST',
	    headers: {
	      'Content-Type': 'application/json',
	    },
	    body: JSON.stringify(userPayload),
	  });

	  if (!response.ok) {
	    throw new Error(`Error: ${response.status}`);
	  }

	  const result = await response.json();
	  console.log('User created successfully:', result);
	  alert('Account creation successful!')

	  window.location.href = '/login';
	} catch (error) {
	  console.error('Error:', error);
	  alert('Account creation error')
	}
}


// !!! TODO: Make this actually log out the user in the backend too by blacklisting the token
export async function logout(){
	localStorage.removeItem('token');
}

// Returns JSON list of users following us
export async function getFollowersList(){
	const token = localStorage.getItem('token');
	validateTokenWithRedirect();

	const followerRes = await fetch(`${baseURL}/user/followers/list`, {
	    headers: {
	      'Authorization': `Bearer ${token}`,
	    },
	});

	if (!followerRes.ok) throw new Error('Failed to fetch followed users: ' + followerRes.statusText);

	const followersJson = await followerRes.json();
	return followersJson;
}

// Returns JSON list of users we are following
export async function getFollowingList(){
	const token = localStorage.getItem('token');
	validateTokenWithRedirect();

	const followingRes = await fetch(`${baseURL}/user/following/list`, {
	    headers: {
	      'Authorization': `Bearer ${token}`,
	    },
	});

	if (!followingRes.ok) throw new Error('Failed to fetch followed users: ' + followingRes.statusText);

	const followingUsersJson = await followingRes.json();
	return followingUsersJson;
}


// Gets info for the current logged in user, like ID and username
// Redirects to login page if unsuccessful
export async function getCurrentUserInfo(){
	const token = localStorage.getItem('token');
	validateTokenWithRedirect();

	const res = await fetch(`${baseURL}/user/info`, {
	    method: 'GET',
	    headers: {
	        'Authorization': `Bearer ${token}`,
	        'Content-Type': 'application/json',
	    },
	});

	if (!res.ok) throw new Error('Failed to fetch ID: ' + res.statusText);

	const result = await res.json();
	return result;
}

// Gets info for the current logged in user, like ID and username
// Returns null if not logged in
export async function tryGetCurrentUserInfo(){
	const token = localStorage.getItem('token');
	if (!validateToken()){
		return null;
	}

	const res = await fetch(`${baseURL}/user/info`, {
	    method: 'GET',
	    headers: {
	        'Authorization': `Bearer ${token}`,
	        'Content-Type': 'application/json',
	    },
	});

	if (!res.ok) throw new Error('Failed to fetch ID: ' + res.statusText);

	const result = await res.json();
	return result;
}


// Uploads a post to the database
// postDTO should be in this format:
			// const newPost = {
			// 	title: postTitle,
			// 	text: postText
			// };
export async function uploadPost(postDTO){
	if (!(await validateToken())){
		return false;
	}
	const token = localStorage.getItem('token');
	
	try {
		const response = await fetch(`${baseURL}/post/new`, {
			method: 'POST',
			headers: {
				'Authorization': `Bearer ${token}`,
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(postDTO)
		});

		if (!response.ok){
			throw new Error('Failed to create post: ' + response.statusText);
		}
		console.log("Post upload successful");
		return true;
	} catch (error){
		console.error('Error creating post:', error);
		throw error;
		return false;
	}
}


// Tries to fetch all posts
// 4 November: This now only returns the data and not actual post components.
export async function fetchAllPosts(page=0, size=10){
	const response = await fetch(`${baseURL}/post/all?page=${page}&size=${size}`);
	if (!response.ok){
		throw new Error('Network response not ok ' + response.statusText);
	}
	const pagedData = await response.json();

	// const posts = pagedData.content.map( ??? );
	return pagedData.content;
}


// Fetches all posts made by a certain user
// Returns a list of Posts (see Post.js)
export async function getPostsByUser(username){
	try{
		const page=0;
		const size=10;

		const postRes = await fetch(`${baseURL}/post/by-user?username=${username}&page=${page}&size=${size}`);
		if (!postRes.ok) throw new Error('Network response not ok ' + postRes.statusText);
		
		const pagedData = await postRes.json();
		// const posts = pagedData.content.map(postData => new Post(postData));
		return pagedData.content;
	} catch (error){
		console.error("Error fetching posts");
		throw error;
	}
}


// Tries to log in
// Returns true if successful, false otherwise
export async function login(username, password){
	try {
		const loginData = { username, password };
	    const response = await fetch(`${baseURL}/auth/login`, {
	        method: 'POST',
	        headers: {
	            'Content-Type': 'application/json',
	        },
	        body: JSON.stringify(loginData),
	    });

	    if (!response.ok) {
	        throw new Error('Invalid username or password');
	    }

	    const result = await response.json();

	    // Store JWT in localStorage
	    localStorage.setItem('token', result.jwt);

	    return true;
	} catch (error) {
	    console.error('Login error:', error);
	    return false;
	}
}

// Tries to unfollow a user by ID
// Returns true if successful, false otherwise
export async function unfollowUser(followeeId){
	try {
		const token = localStorage.getItem('token');
		const res = await fetch(`${baseURL}/user/unfollow/${followeeId}`, {
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
		const res = await fetch(`${baseURL}/user/follow/${followeeId}`, {
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
		const followedRes = await fetch(`${baseURL}/user/following/list`, {
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
		const res = await fetch(`${baseURL}/user/public-info?username=${username}`);
		if (!res.ok) throw new Error("Failed to fetch data");
		const data = await res.json();

		// console.log("DEBUG: " + data.followerCount);
		return data;
	} catch (error){
		console.error("Error fetching public info");
		throw error;
	}
}

// Checks for token and if it is valid
// Deletes token if not valid
// Returns true if valid, false if not
export async function validateToken(){
	const token = localStorage.getItem('token');
	if (!token){
		console.log('User not logged in.');
		return false;
	}

	try {
		const response = await fetch(`${baseURL}/auth/validate`, {
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
		const response = await fetch(`${baseURL}/auth/validate`, {
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

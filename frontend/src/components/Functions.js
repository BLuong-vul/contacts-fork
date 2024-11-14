import Post from "./Post";

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';


/* ===== FETCH HELPERS ===== */

// Use for fetches that do not require authentication
// Only returns the response. Remember to do .json() or .text() or whatever on the return value
async function fetchFromApi(endpoint){
	const response = await fetch(endpoint);
	if (!response.ok) throw new Error("Failed to fetch");
	return await response;
}

// Use for fetches that require authentication
// Only returns the response. Remember to do .json() or .text() or whatever on the return value
async function authFetchFromApi(endpoint, methodArg='GET', body=null) {
    const token = localStorage.getItem('token');
    const response = await fetch(endpoint, {
    	method: methodArg,
    	headers: {
    	    'Authorization': `Bearer ${token}`,
    	    'Content-Type': 'application/json',
    	},
    	body: body ? JSON.stringify(body) : null,
    });
    if (!response.ok) throw new Error("Failed authenticated fetch", response.statusText);
    return response;
}


/* ===== VALIDATION =====*/
// The purpose of separating validateToken() and validateTokenWithRedirect() 
// instead of just using validationHelper() is for clarity.
// If you see validationHelper(true) somewhere else, it's unclear what the "true" arg is doing.
// If you see "validateTokenWithRedirect" then it's obvious what's happening

// Checks for token validity
// Deletes token if not valid
// Returns true/false or redirects based on argument
async function validationHelper(redirect=false){
	const token = localStorage.getItem('token');
	if (!token){
		console.log('User not logged in.');
		if (redirect) window.location.href = '/login';
		return false;
	}

	try {
		await authFetchFromApi(`${baseURL}/auth/validate`);
		return true;
	} catch (error){
		console.log('Login expired.');
		localStorage.removeItem('token');
		if (redirect) window.location.href = '/login';
		return false;
	}
}

export async function validateToken(){
	return validationHelper();
}

export async function validateTokenWithRedirect(){
	return validationHelper(true);
}


/* ===== PROFILE CUSTOMIZATION UPDATES ===== */

// TODO:  Merge all of the update functions into one updateProfileInfo function. 
export async function updateDisplayName(displayName) {
    if (await validateTokenWithRedirect()) return false;
    try {
    	const res = await authFetchFromApi(`${baseURL}/user/account/displayName?displayName=${displayName}`, 'POST');
        return true;
    } catch (error) {
        throw new Error('Error during profile update:', error);
        return false;
    }
}

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


export async function updateOccupation(occupation) {
    validateTokenWithRedirect();
    const token = localStorage.getItem('token');

    try {
        const updateRes = await fetch(`${baseURL}/user/account/occupation?occupation=${occupation}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });

        if (!updateRes.ok) throw new Error('Failed to update occupation: ' + updateRes.statusText);

        return true;
    } catch (error) {
        console.error('Error:', error);
        return false;
    }
}

export async function updateLocation(location) {
    validateTokenWithRedirect();
    const token = localStorage.getItem('token');

    try {
        const updateRes = await fetch(`${baseURL}/user/account/location?location=${location}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });

        if (!updateRes.ok) throw new Error('Failed to update location: ' + updateRes.statusText);

        return true;
    } catch (error) {
        console.error('Error:', error);
        return false;
    }
}

export async function updateBirthdate(birthdate) {
    validateTokenWithRedirect();
    const token = localStorage.getItem('token');

    try {
        const updateRes = await fetch(`${baseURL}/user/account/birthdate?birthdate=${birthdate}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });

        if (!updateRes.ok) throw new Error('Failed to update birthdate: ' + updateRes.statusText);

        return true;
    } catch (error) {
        console.error('Error:', error);
        return false;
    }
}

/* ===== ACCOUNT MANAGEMENT ===== */


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

// Search for a user's public info based on username
// Returns it as json
export async function getPublicInfo(username){
	return await fetchFromApi(`${baseURL}/user/public-info?username=${username}`);
}


/* ===== POSTS ===== */


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
	console.log(pagedData.content);
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


/* ===== FOLLOWER / FOLLOWING ===== */


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




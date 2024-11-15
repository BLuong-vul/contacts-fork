import Post from "./Post";

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';


/* ===== FETCH HELPERS ===== */

// Use for fetches that do not require authentication
// Only returns the response. Remember to do .json() or .text() or whatever on the return value
async function fetchFromApi(endpoint, methodArg='GET', body=null){
	const response = await fetch(endpoint, {
		method: methodArg,
		headers: {
		    'Content-Type': 'application/json',
		},
		body: body ? JSON.stringify(body) : null,
	});
	if (!response.ok) throw new Error(`Failed to fetch: ${response.statusText}`);
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
    if (!response.ok) throw new Error(`Failed authenticated fetch: ${response.statusText}`);
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

// TODO:  Change the backend so that it accepts the values as body instead of like this. Just for parity/clarity/cleanliness
async function updateProfileInfo(attribute, value){
	if (!(await validateTokenWithRedirect())) return false;
	try {
		const res = await authFetchFromApi(`${baseURL}/user/account/${attribute}?${attribute}=${value}`, 'POST');
		return true;
	} catch (error){
		console.error('Error during profile update:', error);
		return false;
	}
}

// TODO: delete all of these and just use updateProfileInfo directly maybe?
export async function updateDisplayName(displayName) {
	return (await updateProfileInfo("displayName", displayName));
}

export async function updateBio(bio){
	return(await updateProfileInfo("bio", bio));
}

export async function updateOccupation(occupation) {
	return(await updateProfileInfo("occupation", occupation));
}

export async function updateLocation(location) {
	return(await updateProfileInfo("location", location));
}

export async function updateBirthdate(birthdate) {
	return(await updateProfileInfo("birthdate", birthdate));
}

/* ===== ACCOUNT MANAGEMENT ===== */


export async function createAccount(userData){
	const { confirmPassword, ...userPayload } = userData;

	// Check if password and confirm password match
	if (userData.password !== confirmPassword) {
	  alert('Passwords do not match');
	  return;
	}

	// TODO: CHECK REQUIRED FIELDS

	try {							
	  const response = await fetchFromApi(`${baseURL}/auth/register`, 'POST', userPayload);
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
		const response = await fetchFromApi(`${baseURL}/auth/login`, 'POST', loginData);

		const result = await response.json();
		localStorage.setItem('token', result.jwt);

		return true;
	} catch (error) {
	    console.error('Login error: ', error);
	    return false;
	}
}

// Returns JSON list of users following us
export async function getFollowersList(){
	validateTokenWithRedirect();
	try {
		const response = await authFetchFromApi(`${baseURL}/user/followers/list`);
		return await response.json();
	} catch (error){
		console.error('Failed to fetch followers list: ', error);
		return null;
	}
}

// Returns JSON list of users we are following
export async function getFollowingList(){
	validateTokenWithRedirect();
	try {
		const response = await authFetchFromApi(`${baseURL}/user/following/list`);
		return await response.json();
	} catch (error){
		console.error('Failed to fetch following list: ', error);
		return null;
	}
}


// Gets info for the current logged in user, like ID and username
// Returns null if unsuccessful
export async function getCurrentUserInfo(){
	if (!(await validateToken())){ return null; }
	try {
		const response = await authFetchFromApi(`${baseURL}/user/info`);
		return await response.json();
	} catch (error){
		console.error('Failed to fetch current user info: ', error);
		return null;
	}
}

// Search for a user's public info based on username
// Returns it as json
export async function getPublicInfo(username){
	return (await fetchFromApi(`${baseURL}/user/public-info?username=${username}`)).json();
}


/* ===== POSTS ===== */


// Uploads a post to the database
// postDTO should be in this format:
			// const newPost = {
			// 	title: postTitle,
			// 	text: postText
			//	...
			// };
export async function uploadPost(postDTO){
	try {
		const response = await authFetchFromApi(`${baseURL}/post/new`, 'POST', postDTO);
		console.log("Post upload successful");
		return true;
	} catch (error){
		console.error('Error creating post:', error);
		return false;
	}
}


// Tries to fetch all posts
// 4 November: This now only returns the data and not actual post components.
export async function fetchAllPosts(page=0, size=10){
	const response = await fetchFromApi(`${baseURL}/post/all?page=${page}&size=${size}`);
	const pagedData = await response.json();
	return pagedData.content;
}


// Fetches all posts made by a certain user
// 4 November: This now only returns the data and not actual post components.
export async function getPostsByUser(username, page=0, size=10){
	const response = await authFetchFromApi(`${baseURL}/post/by-user?username=${username}&page=${page}&size=${size}`);
	const pagedData = await response.json();
	return pagedData.content;
}


/* ===== FOLLOWER / FOLLOWING ===== */


// Tries to unfollow a user by ID
// Returns true if successful, false otherwise
export async function unfollowUser(followeeId){
	try {
		const response = await authFetchFromApi(`${baseURL}/user/unfollow/${followeeId}`, 'POST');
		return true;
	} catch (error) {
		console.error("Error unfollowing user: ", error);
		return false;
	}
}

// Tries to follow a user by ID
// Returns true if successful, false otherwise
export async function followUser(followeeId){
	try {
		const response = await authFetchFromApi(`${baseURL}/user/follow/${followeeId}`, 'POST');
		return true;
	} catch (error) {
		console.error("Error following user: ", error);
		return false;
	}
}

// TODO: This currently searches a following list. That should maybe be done on the backend instead. 
// Returns true if current user is following followee, else returns false
export async function isFollowing(followeeUsername){
	try{
		const response = await authFetchFromApi(`${baseURL}/user/following/list`);
		const followedUsers = await response.json();
		const isFollowing = followedUsers.some(user => user.username === followeeUsername);
		return isFollowing;
	} catch (error){
		console.error("Error fetching follower information: ", error);
		throw error;
	}
}




import Post from "./Post";
import { fetchFromApi, authFetchFromApi } from './FunctionHelpers';
import { validateToken, validateTokenWithRedirect } from './ValidationFunctions';
import { updateProfileInfo } from './ProfileUpdateFunctions';

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';


// Moved to ValidationFunctions.js
/* ===== VALIDATION ===== */

// async function validationHelper(redirect=false){
// 	const token = localStorage.getItem('token');
// 	if (!token){
// 		console.log('User not logged in.');
// 		if (redirect) window.location.href = '/login';
// 		return false;
// 	}

// 	try {
// 		await authFetchFromApi(`${baseURL}/auth/validate`);
// 		return true;
// 	} catch (error){
// 		console.log('Validation error: ', error);
// 		localStorage.removeItem('token');
// 		if (redirect) window.location.href = '/login';
// 		return false;
// 	}
// }

// export async function validateToken(){
// 	return validationHelper();
// }

// export async function validateTokenWithRedirect(){
// 	return validationHelper(true);
// }


/* ===== PROFILE CUSTOMIZATION UPDATES ===== */

// TODO:  Change the backend so that it accepts the values as body instead of like this. Just for parity/clarity/cleanliness
// export async function updateProfileInfo(attribute, value){
// 	if (!(await validateTokenWithRedirect())) return false;
// 	try {
// 		const res = await authFetchFromApi(`${baseURL}/user/account/${attribute}?${attribute}=${value}`, 'POST');
// 		return true;
// 	} catch (error){
// 		console.error('Error during profile update:', error);
// 		return false;
// 	}
// }

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
	await validateTokenWithRedirect();
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
	await validateTokenWithRedirect();
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
	try {
		const response = await fetchFromApi(`${baseURL}/user/public-info?username=${username}`);
		return await response.json();
	} catch (error) {
		throw new Error("Failed to fetch public info");
	}
	
}


/* ===== POSTS ===== */

export async function uploadMedia(file){
	const formData = new FormData();
	formData.append('file', file);

	const token = localStorage.getItem('token');
	try {
	    const response = await fetch(`${baseURL}/media/upload`, {
	        method: 'POST',
	        headers: {
	            'Authorization': `Bearer ${token}`,
	        },
	        body: formData,
	    });

	    if (!response.ok) {
	        throw new Error(`Failed to upload file: ${response.statusText}`);
	    }

	    console.log("File upload successful");
	    return true;
	} catch (error) {
	    console.error('Error uploading file:', error);
	    return false;
	}
}


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
	try {
		const response = await fetchFromApi(`${baseURL}/post/all?page=${page}&size=${size}`);
		const pagedData = await response.json();
		return pagedData.content;
	} catch (error){
		console.error("Failed to fetch posts: ", error);
		return [];
	}
	
}


// Fetches all posts made by a certain user
// 4 November: This now only returns the data and not actual post components.
export async function getPostsByUser(username, page=0, size=10){
	try {
		const response = await authFetchFromApi(`${baseURL}/post/by-user?username=${username}&page=${page}&size=${size}`);
		const pagedData = await response.json();
		return pagedData.content;
	} catch (error){
		console.error("Failed to fetch user posts: ", error);
		return [];
	}	
}

// voteType should be "LIKE" or "DISLIKE"
async function _vote(votableId, voteType) {
	await validateTokenWithRedirect();
	const voteDTO = {
	  votableId: votableId,
	  voteType: voteType, 
	};
	try { 
		const response = await authFetchFromApi(`${baseURL}/post/vote`, 'POST', voteDTO);
		return true;
	} catch (error){
		console.error("Error voting: ", error);
		return false;
	}
}

export async function likeVotable(votableId){
	return await _vote(votableId, "LIKE");
}

export async function dislikeVotable(votableId){
	return await _vote(votableId, "DISLIKE");
}

export async function unvote(votableId){
	if(!(await validateTokenWithRedirect())) return null;
	try{
		const response = await authFetchFromApi(`${baseURL}/post/unvote?votableId=${votableId}`, 'DELETE');
		return true;
	} catch (error){
		console.error("Error unvoting: ", error);
		return false;
	}
}

export async function getVoteOnVotable(votableId){
	if (!(await validateToken())) return null;
	try {
		const response = await authFetchFromApi(`${baseURL}/post/get-vote?votableId=${votableId}`);
		if (response.ok) {
            // Check if there is content before parsing JSON
            if (response.status === 200) {
                return await response.json(); // Return the vote type
            }
            return null; // No vote exists
        }
	} catch (error){
		console.error("Error checking vote: ", error);
	}
}

export async function uploadReply(postId, textContent, parentId=0){
	if(!(await validateTokenWithRedirect())) return null;
	if (textContent=="" || textContent==null) return null;
	const replyRequestDTO = {
		postId: postId,
		toReplyId: parentId,
		text: textContent
	}
	try{
		const response = await authFetchFromApi(`${baseURL}/replies/post/${postId}`, 'POST', replyRequestDTO);
		const comment = await response.json();
		return comment;
	} catch (error){
		console.error("Error creating reply: ", error);
		return false;
	}
}

export async function getReplies(postId){
	try{
		const response = await fetchFromApi(`${baseURL}/replies/post/${postId}`);
		const commentData = await response.json();
		return commentData;
	} catch (error){
		console.error('Error fetching replies: ', error);
		return false;
	}
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

// TODO: This currently searches a following list. That should probably be done on the backend instead. 
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




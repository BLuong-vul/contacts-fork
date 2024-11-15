jest.mock('../components/FunctionHelpers', () => ({
  fetchFromApi: jest.fn(),
  authFetchFromApi: jest.fn(),
}));
jest.mock('../components/ValidationFunctions', () => ({
  validateToken: jest.fn(),
  validateTokenWithRedirect: jest.fn(),
}));



// Dependencies
let fetchFromApi = require('../components/FunctionHelpers').fetchFromApi;
let authFetchFromApi = require('../components/FunctionHelpers').authFetchFromApi;
let validateToken = require('../components/ValidationFunctions').validateToken;
let validateTokenWithRedirect = require('../components/ValidationFunctions').validateTokenWithRedirect;

// Things being tested
let updateProfileInfo = require('../components/ProfileUpdateFunctions').updateProfileInfo;
let createAccount = require('../components/Functions').createAccount;
let logout = require('../components/Functions').logout;
let login = require('../components/Functions').login;
let getFollowersList = require('../components/Functions').getFollowersList;
let getFollowingList = require('../components/Functions').getFollowingList;
let getCurrentUserInfo = require('../components/Functions').getCurrentUserInfo;
let getPublicInfo = require('../components/Functions').getPublicInfo;
let uploadPost = require('../components/Functions').uploadPost;
let fetchAllPosts = require('../components/Functions').fetchAllPosts;
let getPostsByUser = require('../components/Functions').getPostsByUser;
let unfollowUser = require('../components/Functions').unfollowUser;
let followUser = require('../components/Functions').followUser;
let isFollowing = require('../components/Functions').isFollowing;



// Setting up globals
Storage.prototype.getItem = jest.fn();
Storage.prototype.setItem = jest.fn();
Storage.prototype.removeItem = jest.fn();
Storage.prototype.clear = jest.fn();
global.window = {
  location: {
    href: '',
  },
  assign: jest.fn(),
};
global.alert = jest.fn();

beforeEach(() => {
  jest.clearAllMocks(); // clear previous mocks
  global.fetch = jest.fn(); // mock global fetch
});

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';


// Testing


// updateProfileInfo(attribute, value)
describe('updateProfileInfo', () => {
  // Test 1
  it('should return true if token is valid and API request succeeds', async () => {
    validateTokenWithRedirect.mockResolvedValue(true);
    
    authFetchFromApi.mockResolvedValue({ ok: true });

    const attribute = 'displayName';
    const value = 'Test User';
    
    const result = await updateProfileInfo(attribute, value);

    expect(result).toBe(true);
    expect(validateTokenWithRedirect).toHaveBeenCalled();
    expect(authFetchFromApi).toHaveBeenCalledWith(
      `${process.env.BASE_API_URL || 'http://localhost:8080'}/user/account/displayName?displayName=Test User`,
      'POST'
    );
  });

  // Test 2
  it('should return false if token is invalid and redirect is triggered', async () => {
    validateTokenWithRedirect.mockResolvedValue(false);
    
    const result = await updateProfileInfo('displayName', 'Test User');

    expect(result).toBe(false);
    expect(validateTokenWithRedirect).toHaveBeenCalled();
    expect(authFetchFromApi).not.toHaveBeenCalled(); 
  });

  // Test 3
  it('should return false if API request fails (e.g., network issue)', async () => {
    validateTokenWithRedirect.mockResolvedValue(true);

    authFetchFromApi.mockRejectedValue(new Error('Network Error'));

    const result = await updateProfileInfo('displayName', 'Test User');

    expect(result).toBe(false);
    expect(validateTokenWithRedirect).toHaveBeenCalled();
    expect(authFetchFromApi).toHaveBeenCalledWith(
      `${process.env.BASE_API_URL || 'http://localhost:8080'}/user/account/displayName?displayName=Test User`,
      'POST'
    );
  });
});

// createAccount(userData)
describe('createAccount', () => {
  // Test 1
  it('should show an alert if passwords do not match', async () => {
    const userData = {
      username: 'testUser',
      password: 'password123',
      confirmPassword: 'differentPassword', 
    };

    await createAccount(userData);

    expect(alert).toHaveBeenCalledWith('Passwords do not match');
    expect(fetchFromApi).not.toHaveBeenCalled(); 
  });

  // Test 2
  it('should call fetchFromApi with correct data and redirect on success', async () => {
    const userData = {
      username: 'testUser',
      password: 'password123',
      confirmPassword: 'password123',
    };

    const mockResponse = {
      json: jest.fn().mockResolvedValue({ success: true }),
    };
    fetchFromApi.mockResolvedValue(mockResponse);

    // For testing page redirect
    const mockLocationAssign = jest.fn();
    Object.defineProperty(window, 'location', {
      value: {
        ...window.location,
        assign: mockLocationAssign, 
      },
      writable: true, 
    });
    const mockLocation = jest.fn();
    window.location.href = mockLocation;

    await createAccount(userData);

    expect(fetchFromApi).toHaveBeenCalledWith(
      `${process.env.BASE_API_URL || 'http://localhost:8080'}/auth/register`,
      'POST',
      { username: 'testUser', password: 'password123' }
    );
    expect(alert).toHaveBeenCalledWith('Account creation successful!');
    expect(window.location.href).toBe('/login');
  });

  // Test 3
  it('should handle API errors and show an alert if account creation fails', async () => {
    const userData = {
      username: 'testUser',
      password: 'password123',
      confirmPassword: 'password123',
    };

    fetchFromApi.mockRejectedValue(new Error('API error'));

    // For testing page redirect
    const mockLocationAssign = jest.fn();
    Object.defineProperty(window, 'location', {
      value: {
        ...window.location,
        assign: mockLocationAssign, 
      },
      writable: true, 
    });
    const mockLocation = jest.fn();
    window.location.href = mockLocation;

    await createAccount(userData);

    expect(fetchFromApi).toHaveBeenCalledWith(
      `${process.env.BASE_API_URL || 'http://localhost:8080'}/auth/register`,
      'POST',
      { username: 'testUser', password: 'password123' }
    );
    expect(alert).toHaveBeenCalledWith('Account creation error');
    expect(window.location.href).not.toBe('/login');
  });
});

// logout()
describe('logout', () => {
  // Test 1
  it('should remove the token from localStorage', () => {
    logout();
    expect(localStorage.removeItem).toHaveBeenCalledWith('token');
  });
});

// login()
describe('login', () => {
  // Test 1
  it('should store the token in localStorage and return true on successful login', async () => {
    const mockToken = 'mock-jwt-token';
    const mockResponse = {
      json: jest.fn().mockResolvedValue({ jwt: mockToken }),
    };

    fetchFromApi.mockResolvedValue(mockResponse);

    const loginResult = await login('testUser', 'testPassword');

    expect(fetchFromApi).toHaveBeenCalledWith(
      `${process.env.BASE_API_URL || 'http://localhost:8080'}/auth/login`,
      'POST',
      { username: 'testUser', password: 'testPassword' }
    );
    expect(localStorage.setItem).toHaveBeenCalledWith('token', mockToken);
    expect(loginResult).toBe(true);
  });

  // Test 2
  it('should return false and not set token in localStorage if login fails', async () => {
    fetchFromApi.mockRejectedValue(new Error('Login error'));

    const loginResult = await login('testUser', 'wrongPassword');

    expect(fetchFromApi).toHaveBeenCalledWith(
      `${process.env.BASE_API_URL || 'http://localhost:8080'}/auth/login`,
      'POST',
      { username: 'testUser', password: 'wrongPassword' }
    );
    expect(localStorage.setItem).not.toHaveBeenCalled(); 
    expect(loginResult).toBe(false);
  });
});

// getFollowersList()
describe('getFollowersList', () => {
  // Test 1
  it('should fetch the followers list and return the data on success', async () => {
    const mockFollowersList = [{ id: 1, name: 'Follower1' }, { id: 2, name: 'Follower2' }];
    const mockResponse = {
      json: jest.fn().mockResolvedValue(mockFollowersList),
    };

    validateTokenWithRedirect.mockResolvedValue(true);
    authFetchFromApi.mockResolvedValue(mockResponse);

    const result = await getFollowersList();

    expect(validateTokenWithRedirect).toHaveBeenCalled();
    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/followers/list`);
    expect(result).toEqual(mockFollowersList);
  });

  // Test 2
  it('should return null and log an error if fetching followers list fails', async () => {
    console.error = jest.fn(); // Mock console.error
    validateTokenWithRedirect.mockResolvedValue(true);
    authFetchFromApi.mockRejectedValue(new Error('Fetch failed'));

    const result = await getFollowersList();

    expect(validateTokenWithRedirect).toHaveBeenCalled();
    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/followers/list`);
    expect(console.error).toHaveBeenCalledWith('Failed to fetch followers list: ', expect.any(Error));
    expect(result).toBeNull();
  });
});

// getFollowingList()
describe('getFollowingList', () => {
  // Test 1
  it('should fetch the following list and return the data on success', async () => {
    const mockFollowingList = [{ id: 1, name: 'Following1' }, { id: 2, name: 'Following2' }];
    const mockResponse = {
      json: jest.fn().mockResolvedValue(mockFollowingList),
    };

    validateTokenWithRedirect.mockResolvedValue(true);
    authFetchFromApi.mockResolvedValue(mockResponse);

    const result = await getFollowingList();

    expect(validateTokenWithRedirect).toHaveBeenCalled();
    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/following/list`);
    expect(result).toEqual(mockFollowingList);
  });

  // Test 2
  it('should return null and log an error if fetching following list fails', async () => {
    console.error = jest.fn(); // Mock console.error
    validateTokenWithRedirect.mockResolvedValue(true);
    authFetchFromApi.mockRejectedValue(new Error('Fetch failed'));

    const result = await getFollowingList();

    expect(validateTokenWithRedirect).toHaveBeenCalled();
    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/following/list`);
    expect(console.error).toHaveBeenCalledWith('Failed to fetch following list: ', expect.any(Error));
    expect(result).toBeNull();
  });
});

// getCurrentUserInfo()
describe('getCurrentUserInfo', () => {
  // Test 1
  it('should fetch current user info and return the data on success', async () => {
    const mockUserInfo = { id: 1, username: 'TestUser' };
    const mockResponse = {
      json: jest.fn().mockResolvedValue(mockUserInfo),
    };

    validateToken.mockResolvedValue(true);
    authFetchFromApi.mockResolvedValue(mockResponse);

    const result = await getCurrentUserInfo();

    expect(validateToken).toHaveBeenCalled();
    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/info`);
    expect(result).toEqual(mockUserInfo);
  });

  // Test 2
  it('should return null and log an error if fetching current user info fails', async () => {
    console.error = jest.fn(); // Mock console.error
    validateToken.mockResolvedValue(true);
    authFetchFromApi.mockRejectedValue(new Error('Fetch failed'));

    const result = await getCurrentUserInfo();

    expect(validateToken).toHaveBeenCalled();
    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/info`);
    expect(console.error).toHaveBeenCalledWith('Failed to fetch current user info: ', expect.any(Error));
    expect(result).toBeNull();
  });

  // Test 3
  it('should return null if the token is invalid', async () => {
    validateToken.mockResolvedValue(false);

    const result = await getCurrentUserInfo();

    expect(validateToken).toHaveBeenCalled();
    expect(result).toBeNull();
  });
});

// getPublicInfo(username)
describe('getPublicInfo', () => {
  // Test 1
  it('should fetch public info and return the data on success', async () => {
    const mockPublicInfo = { id: 1, username: 'PublicUser', bio: 'Some bio info' };
    const mockResponse = {
      json: jest.fn().mockResolvedValue(mockPublicInfo),
    };

    fetchFromApi.mockResolvedValue(mockResponse);

    const result = await getPublicInfo('PublicUser');

    expect(fetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/public-info?username=PublicUser`);
    expect(result).toEqual(mockPublicInfo);
  });

  // Test 2
  it('should throw an error if fetching public info fails', async () => {
    fetchFromApi.mockRejectedValue(new Error('Fetch failed'));

    await expect(getPublicInfo('NonExistentUser')).rejects.toThrow('Failed to fetch public info');
  });
});

// uploadPost(postDTO)
describe('uploadPost', () => {
  // Test 1
  it('should upload a post and return true on success', async () => {
    const mockPostDTO = { title: 'Test Post', text: 'This is a test post.' };
    const mockResponse = { json: jest.fn().mockResolvedValue({ success: true }) };

    authFetchFromApi.mockResolvedValue(mockResponse);

    const result = await uploadPost(mockPostDTO);

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/post/new`, 'POST', mockPostDTO);
    expect(result).toBe(true);
  });

  // Test 2
  it('should return false and log an error if uploading a post fails', async () => {
    const mockPostDTO = { title: 'Test Post', text: 'This is a test post.' };
    console.error = jest.fn(); 

    authFetchFromApi.mockRejectedValue(new Error('Upload failed'));

    const result = await uploadPost(mockPostDTO);

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/post/new`, 'POST', mockPostDTO);
    expect(console.error).toHaveBeenCalledWith('Error creating post:', expect.any(Error));
    expect(result).toBe(false);
  });
});

// fetchAllPosts(page=0, size=10)
describe('fetchAllPosts', () => {
  // Test 1
  it('should fetch all posts and return the content on success', async () => {
    const mockResponse = {
      json: jest.fn().mockResolvedValue({ content: [{ title: 'Post 1' }, { title: 'Post 2' }] }),
    };

    fetchFromApi.mockResolvedValue(mockResponse);

    const result = await fetchAllPosts();

    expect(fetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/post/all?page=0&size=10`);
    expect(result).toEqual([{ title: 'Post 1' }, { title: 'Post 2' }]);
  });

  // Test 2
  it('should return an empty array if no posts are returned', async () => {
    const mockResponse = {
      json: jest.fn().mockResolvedValue({ content: [] }),
    };

    fetchFromApi.mockResolvedValue(mockResponse);

    const result = await fetchAllPosts();

    expect(fetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/post/all?page=0&size=10`);
    expect(result).toEqual([]);
  });

  // Test 3
  it('should handle errors and return an empty array if fetching fails', async () => {
    console.error = jest.fn(); 

    fetchFromApi.mockRejectedValue(new Error('Failed to fetch posts'));

    const result = await fetchAllPosts();

    expect(fetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/post/all?page=0&size=10`);
    expect(console.error).toHaveBeenCalledWith('Failed to fetch posts: ', expect.any(Error));
    expect(result).toEqual([]);
  });
});

// getPostsByUser(username, page=0, size=10)
describe('getPostsByUser', () => {
  // Test 1
  it('should fetch posts by a specific user and return the content on success', async () => {
    const mockResponse = {
      json: jest.fn().mockResolvedValue({ content: [{ title: 'User Post 1' }, { title: 'User Post 2' }] }),
    };

    authFetchFromApi.mockResolvedValue(mockResponse);

    const result = await getPostsByUser('testuser');

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/post/by-user?username=testuser&page=0&size=10`);
    expect(result).toEqual([{ title: 'User Post 1' }, { title: 'User Post 2' }]);
  });

  // Test 2
  it('should return an empty array if the user has no posts', async () => {
    const mockResponse = {
      json: jest.fn().mockResolvedValue({ content: [] }),
    };

    authFetchFromApi.mockResolvedValue(mockResponse);

    const result = await getPostsByUser('testuser');

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/post/by-user?username=testuser&page=0&size=10`);
    expect(result).toEqual([]);
  });

  // Test 3
  it('should handle errors and return an empty array if fetching fails', async () => {
    console.error = jest.fn(); // Mock console.error

    authFetchFromApi.mockRejectedValue(new Error('Failed to fetch user posts'));

    const result = await getPostsByUser('testuser');

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/post/by-user?username=testuser&page=0&size=10`);
    expect(console.error).toHaveBeenCalledWith('Failed to fetch user posts: ', expect.any(Error));
    expect(result).toEqual([]);
  });
});

// unfollowUser(followeeId)
describe('unfollowUser', () => {
  // Test 1
  it('should unfollow a user and return true on success', async () => {
    authFetchFromApi.mockResolvedValue({});

    const result = await unfollowUser('12345');

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/unfollow/12345`, 'POST');
    expect(result).toBe(true);
  });

  // Test 2
  it('should return false if unfollowing fails', async () => {
    console.error = jest.fn(); // Mock console.error

    authFetchFromApi.mockRejectedValue(new Error('Failed to unfollow user'));

    const result = await unfollowUser('12345');

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/unfollow/12345`, 'POST');
    expect(console.error).toHaveBeenCalledWith("Error unfollowing user: ", expect.any(Error));
    expect(result).toBe(false);
  });
});

// followUser(followeeId)
describe('followUser', () => {
  // Test 1
  it('should follow a user and return true on success', async () => {
    authFetchFromApi.mockResolvedValue({});

    const result = await followUser('12345');

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/follow/12345`, 'POST');
    expect(result).toBe(true);
  });

  // Test 2
  it('should return false if following fails', async () => {
    console.error = jest.fn(); // Mock console.error

    authFetchFromApi.mockRejectedValue(new Error('Failed to follow user'));

    const result = await followUser('12345');

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/follow/12345`, 'POST');
    expect(console.error).toHaveBeenCalledWith("Error following user: ", expect.any(Error));
    expect(result).toBe(false);
  });
});

// isFollowing(followeeeUsername)
describe('isFollowing', () => {
  // Test 1
  it('should return true if the current user is following the specified user', async () => {
    const mockResponse = {
      json: jest.fn().mockResolvedValue([
        { username: 'testuser1' },
        { username: 'testuser2' },
      ]),
    };

    authFetchFromApi.mockResolvedValue(mockResponse);

    const result = await isFollowing('testuser2');

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/following/list`);
    expect(result).toBe(true);
  });

  // Test 2
  it('should return false if the current user is not following the specified user', async () => {
    const mockResponse = {
      json: jest.fn().mockResolvedValue([
        { username: 'testuser1' },
        { username: 'testuser2' },
      ]),
    };

    authFetchFromApi.mockResolvedValue(mockResponse);

    const result = await isFollowing('testuser3');

    expect(authFetchFromApi).toHaveBeenCalledWith(`${process.env.BASE_API_URL || 'http://localhost:8080'}/user/following/list`);
    expect(result).toBe(false);
  });

  // Test 3
  it('should throw an error if fetching the following list fails', async () => {
    console.error = jest.fn(); // Mock console.error

    authFetchFromApi.mockRejectedValue(new Error('Failed to fetch following list'));

    await expect(isFollowing('testuser2')).rejects.toThrow('Failed to fetch following list');
    expect(console.error).toHaveBeenCalledWith("Error fetching follower information: ", expect.any(Error));
  });
});
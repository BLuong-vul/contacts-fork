jest.mock('../components/FunctionHelpers', () => ({
  fetchFromApi: jest.fn(),
  authFetchFromApi: jest.fn(),
}));

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';

let fetchFromApi = require('../components/FunctionHelpers').fetchFromApi;
let authFetchFromApi = require('../components/FunctionHelpers').authFetchFromApi;

let validateToken = require('../components/ValidationFunctions').validateToken;
let validateTokenWithRedirect = require('../components/ValidationFunctions').validateTokenWithRedirect;


Storage.prototype.getItem = jest.fn();
Storage.prototype.setItem = jest.fn();
Storage.prototype.removeItem = jest.fn();
global.window = {
  location: {
    href: '',
  },
  assign: jest.fn(),
};

beforeEach(() => {
  jest.clearAllMocks(); // Clear previous mocks
  global.fetch = jest.fn(); // Mock global fetch
});


describe('validateToken', () => {
  // Test 1
  it('should return true if the token is valid', async () => {
      localStorage.getItem.mockReturnValue('validToken');
      authFetchFromApi.mockResolvedValue(true);

      const result = await validateToken();

      expect(result).toBe(true);
      expect(localStorage.getItem).toHaveBeenCalledWith('token');
      expect(authFetchFromApi).toHaveBeenCalledWith(`${baseURL}/auth/validate`);
    });

  // Test 2
  it('should return false if there is no token', async () => {
    localStorage.getItem.mockReturnValue(null);
    const result = await validateToken();
    expect(result).toBe(false);
    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(authFetchFromApi).not.toHaveBeenCalled();
  });

  // Test 3
  it('should return false and remove token if the API call fails', async () => {
      localStorage.getItem.mockReturnValue('validToken');
      authFetchFromApi.mockRejectedValue(new Error('Validation error'));

      const result = await validateToken();

      expect(result).toBe(false);
      expect(localStorage.getItem).toHaveBeenCalledWith('token');
      expect(authFetchFromApi).toHaveBeenCalledWith(`${baseURL}/auth/validate`);
      expect(localStorage.removeItem).toHaveBeenCalledWith('token');
    });
});


describe('validateTokenWithRedirect', () => {
  // Test 1
  it('should redirect to login if the token is invalid', async () => {
      localStorage.getItem.mockReturnValue('validToken');
      authFetchFromApi.mockRejectedValue(new Error('Validation error'));

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

      const result = await validateTokenWithRedirect();

      expect(result).toBe(false);
      expect(localStorage.getItem).toHaveBeenCalledWith('token');
      expect(authFetchFromApi).toHaveBeenCalledWith(`${baseURL}/auth/validate`);
      expect(localStorage.removeItem).toHaveBeenCalledWith('token');
      expect(window.location.href).toBe('/login');
    });

  // Test 2
  it('should redirect if no token is present', async () => {
      localStorage.getItem.mockReturnValue(null);

      const result = await validateTokenWithRedirect();

      expect(result).toBe(false);
      // expect(localStorage.getItem).toHaveBeenCalledWith('token'); //TODO: Test this in future. Need to separate the validation functions to their own file
      expect(authFetchFromApi).not.toHaveBeenCalled();
      expect(window.location.href).toBe('/login');
    });
});
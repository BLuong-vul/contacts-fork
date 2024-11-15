jest.mock('../components/FunctionHelpers', () => ({
  fetchFromApi: jest.fn(),
  authFetchFromApi: jest.fn(),
}));

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';

let fetchFromApi = require('../components/FunctionHelpers').fetchFromApi;
let authFetchFromApi = require('../components/FunctionHelpers').authFetchFromApi;

let validationHelper = require('../components/ValidationFunctions').validationHelper;
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
  jest.clearAllMocks(); // clear previous mocks
  global.fetch = jest.fn(); // mock global fetch
});

describe('validationHelper', () => {
  it('should return false and not redirect if token is missing', async () => {
    localStorage.getItem.mockReturnValue(null);

    const result = await validationHelper();

    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(result).toBe(false);
    // expect(window.location.href).toBe('');
  });

  it('should return false and redirect if token is missing and redirect is true', async () => {
    localStorage.getItem.mockReturnValue(null);

    const result = await validationHelper(true);

    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(result).toBe(false);
    // expect(window.location.href).toBe('/login');
  });

  it('should return true if token exists and validation succeeds', async () => {
    localStorage.getItem.mockReturnValue('mock-token');
    authFetchFromApi.mockResolvedValue({});

    const result = await validationHelper();

    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(authFetchFromApi).toHaveBeenCalledWith(`${baseURL}/auth/validate`);
    expect(result).toBe(true);
    expect(localStorage.removeItem).not.toHaveBeenCalled();
    // expect(window.location.href).toBe('');
  });

  it('should return false, remove token, and not redirect if validation fails', async () => {
    localStorage.getItem.mockReturnValue('mock-token');
    authFetchFromApi.mockRejectedValue(new Error('Validation failed'));

    const result = await validationHelper();

    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(authFetchFromApi).toHaveBeenCalledWith(`${baseURL}/auth/validate`);
    expect(localStorage.removeItem).toHaveBeenCalledWith('token');
    expect(result).toBe(false);
    // expect(window.location.href).toBe('');
  });

  it('should return false, remove token, and redirect if validation fails and redirect is true', async () => {
    localStorage.getItem.mockReturnValue('mock-token');
    authFetchFromApi.mockRejectedValue(new Error('Validation failed'));

    const result = await validationHelper(true);

    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(authFetchFromApi).toHaveBeenCalledWith(`${baseURL}/auth/validate`);
    expect(localStorage.removeItem).toHaveBeenCalledWith('token');
    expect(result).toBe(false);
    // expect(window.location.href).toBe('/login');
  });
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
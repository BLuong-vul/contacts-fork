jest.mock('../components/FunctionHelpers', () => ({
  fetchFromApi: jest.fn(),
  authFetchFromApi: jest.fn(),
}));
jest.mock('../components/ValidationFunctions', () => ({
  validateToken: jest.fn(),
  validateTokenWithRedirect: jest.fn(),
}));
jest.mock('../components/ProfileUpdateFunctions', () => ({
  updateProfileInfo: jest.fn(),
}));


// Dependencies
let fetchFromApi = require('../components/FunctionHelpers').fetchFromApi;
let authFetchFromApi = require('../components/FunctionHelpers').authFetchFromApi;
let validateToken = require('../components/ValidationFunctions').validateToken;
let validateTokenWithRedirect = require('../components/ValidationFunctions').validateTokenWithRedirect;
let updateProfileInfo = require('../components/ProfileUpdateFunctions').updateProfileInfo;

// Things being tested
let updateDisplayName = require('../components/Functions').updateDisplayName;
let updateBio = require('../components/Functions').updateBio;
let updateOccupation = require('../components/Functions').updateOccupation;
let updateLocation = require('../components/Functions').updateLocation;
let updateBirthdate = require('../components/Functions').updateBirthdate;



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


describe('Profile update wrapper functions', () => {
  it('should call updateProfileInfo with correct parameters for updateDisplayName', async () => {
    updateProfileInfo.mockResolvedValue(true);

    const result = await updateDisplayName('NewDisplayName');

    expect(updateProfileInfo).toHaveBeenCalledWith('displayName', 'NewDisplayName');
    expect(result).toBe(true);
  });

  it('should call updateProfileInfo with correct parameters for updateBio', async () => {
    updateProfileInfo.mockResolvedValue(true);

    const result = await updateBio('New bio content');

    expect(updateProfileInfo).toHaveBeenCalledWith('bio', 'New bio content');
    expect(result).toBe(true);
  });

  it('should call updateProfileInfo with correct parameters for updateOccupation', async () => {
    updateProfileInfo.mockResolvedValue(true);

    const result = await updateOccupation('Software Engineer');

    expect(updateProfileInfo).toHaveBeenCalledWith('occupation', 'Software Engineer');
    expect(result).toBe(true);
  });

  it('should call updateProfileInfo with correct parameters for updateLocation', async () => {
    updateProfileInfo.mockResolvedValue(true);

    const result = await updateLocation('New York, NY');

    expect(updateProfileInfo).toHaveBeenCalledWith('location', 'New York, NY');
    expect(result).toBe(true);
  });

  it('should call updateProfileInfo with correct parameters for updateBirthdate', async () => {
    updateProfileInfo.mockResolvedValue(true);

    const result = await updateBirthdate('2000-01-01');

    expect(updateProfileInfo).toHaveBeenCalledWith('birthdate', '2000-01-01');
    expect(result).toBe(true);
  });

  it('should handle failure scenarios correctly for updateDisplayName', async () => {
    updateProfileInfo.mockResolvedValue(false);

    const result = await updateDisplayName('FailingName');

    expect(updateProfileInfo).toHaveBeenCalledWith('displayName', 'FailingName');
    expect(result).toBe(false);
  });

  it('should handle failure scenarios correctly for updateBio', async () => {
    updateProfileInfo.mockResolvedValue(false);

    const result = await updateBio('Failing bio content');

    expect(updateProfileInfo).toHaveBeenCalledWith('bio', 'Failing bio content');
    expect(result).toBe(false);
  });

  it('should handle failure scenarios correctly for updateOccupation', async () => {
    updateProfileInfo.mockResolvedValue(false);

    const result = await updateOccupation('Failing Occupation');

    expect(updateProfileInfo).toHaveBeenCalledWith('occupation', 'Failing Occupation');
    expect(result).toBe(false);
  });

  it('should handle failure scenarios correctly for updateLocation', async () => {
    updateProfileInfo.mockResolvedValue(false);

    const result = await updateLocation('Failing Location');

    expect(updateProfileInfo).toHaveBeenCalledWith('location', 'Failing Location');
    expect(result).toBe(false);
  });

  it('should handle failure scenarios correctly for updateBirthdate', async () => {
    updateProfileInfo.mockResolvedValue(false);

    const result = await updateBirthdate('1999-12-31');

    expect(updateProfileInfo).toHaveBeenCalledWith('birthdate', '1999-12-31');
    expect(result).toBe(false);
  });
});
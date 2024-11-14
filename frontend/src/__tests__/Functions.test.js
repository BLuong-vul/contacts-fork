import { render, screen, fireEvent } from '@testing-library/react';
import * as Functions from '../components/Functions.js';

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';

global.fetch = jest.fn();

describe('Functions.getPublicInfo', () => {
  afterEach(() => {
    fetch.mockClear();
  });

  it('should fetch data successfully', async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ data: 'sample data' }),
    });

    const username = "testUsername";

    const result = await Functions.getPublicInfo(username);
    expect(result).toEqual({ data: 'sample data' });
    expect(fetch).toHaveBeenCalledWith(`${baseURL}/user/public-info?username=${username}`);
  });

  it('should throw an error if fetch fails', async () => {
    fetch.mockResolvedValueOnce({
      ok: false,
    });

    await expect(Functions.getPublicInfo('api.example/data')).rejects.toThrow('Failed to fetch data');
  });
});

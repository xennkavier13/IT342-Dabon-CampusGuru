import api from '@shared/services/api';

const API_BASE = 'http://localhost:8080/api';

export const googleAuthService = {
  /**
   * Returns the full URL to initiate Google OAuth.
   * We open this in the current window (not via API call) since the backend redirects to Google.
   */
  getConnectUrl(): string {
    const token = localStorage.getItem('authToken');
    return `${API_BASE}/google-auth/connect?token=${token}`;
  },

  /**
   * Checks whether the current tutor has connected their Google Calendar.
   */
  async getConnectionStatus(): Promise<{ connected: boolean }> {
    const response = await api.get<{ connected: boolean }>('/google-auth/status');
    return response.data;
  },
};

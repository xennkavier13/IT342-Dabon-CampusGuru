export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  institutionalEmail: string;
  password: string;
  firstName: string;
  lastName: string;
  role: 'LEARNER' | 'TUTOR' | 'ADMIN';
}

export interface AuthResponse {
  userId: string | null;
  message: string;
  username?: string;
}

export interface User {
  userId: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

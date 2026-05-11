export interface SignUpRequest {
  email: string;
  password: string;
}

export interface SignInRequest {
  email: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface SignUpResponse {
  userId: string;
  email: string;
}

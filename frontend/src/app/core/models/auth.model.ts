export interface SignUpRequest {
  email: string;
  password: string;
}

export interface SignInRequest {
  email: string;
  password: string;
}

export interface ExpiryResponse {
  expiresIn: number;
}

export interface SignUpResponse {
  userId: string;
  email: string;
}

export interface CurrentUserResponse {
  email: string;
}

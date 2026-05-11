import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { SignInRequest, SignUpRequest, TokenResponse, SignUpResponse } from '../models/auth.model';
import { environment } from '../../../environments/environment';

// SECURITY NOTE: Storing JWT in localStorage makes it accessible to JavaScript
// and thus vulnerable to XSS attacks. This is an acceptable trade-off for a
// local-only MVP. Before any public deployment, migrate to HttpOnly cookies.
// See docs/architecture/adr/ADR-002-jwt-localstorage.md for details.
const ACCESS_TOKEN_KEY = 'taskly_access_token';
const REFRESH_TOKEN_KEY = 'taskly_refresh_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = `${environment.apiUrl}/api/v1/auth`;

  private readonly _accessToken = signal<string | null>(localStorage.getItem(ACCESS_TOKEN_KEY));

  readonly isAuthenticated = computed(() => this._accessToken() !== null);
  readonly accessToken = this._accessToken.asReadonly();

  signUp(request: SignUpRequest): Observable<SignUpResponse> {
    return this.http.post<SignUpResponse>(`${this.apiUrl}/sign-up`, request);
  }

  signIn(request: SignInRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.apiUrl}/sign-in`, request).pipe(
      tap(response => this.storeTokens(response))
    );
  }

  refresh(): Observable<TokenResponse> {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    return this.http.post<TokenResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => this.storeTokens(response))
    );
  }

  signOut(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    this._accessToken.set(null);
    this.router.navigate(['/auth/sign-in']);
  }

  getAccessToken(): string | null {
    return this._accessToken();
  }

  private storeTokens(response: TokenResponse): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
    this._accessToken.set(response.accessToken);
  }
}

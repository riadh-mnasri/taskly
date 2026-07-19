import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, catchError, firstValueFrom, of, switchMap } from 'rxjs';
import { SignInRequest, SignUpRequest, ExpiryResponse, SignUpResponse, CurrentUserResponse } from '../models/auth.model';
import { environment } from '../../../environments/environment';

// Session is an HttpOnly cookie set by the backend (see ADR-002) — the token
// itself is never readable from JS. Auth state is derived by asking the
// backend who's logged in (`/me`), not by decoding a token client-side.
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = `${environment.apiUrl}/api/v1/auth`;

  // undefined = not checked yet, null = confirmed anonymous
  private readonly _currentUser = signal<CurrentUserResponse | null | undefined>(undefined);
  private authCheck$: Promise<boolean> | null = null;

  readonly isAuthenticated = computed(() => !!this._currentUser());

  readonly currentUserName = computed(() => {
    const email = this._currentUser()?.email ?? '';
    if (!email) return '';
    const namePart = email.split('@')[0].split('.')[0];
    return namePart.charAt(0).toUpperCase() + namePart.slice(1);
  });

  signUp(request: SignUpRequest): Observable<SignUpResponse> {
    return this.http.post<SignUpResponse>(`${this.apiUrl}/sign-up`, request);
  }

  signIn(request: SignInRequest): Observable<ExpiryResponse> {
    this.authCheck$ = null;
    return this.http.post<ExpiryResponse>(`${this.apiUrl}/sign-in`, request);
  }

  signOut(): void {
    this.http.post(`${this.apiUrl}/sign-out`, {}).subscribe({
      complete: () => this.finishSignOut(),
      error: () => this.finishSignOut()
    });
  }

  /** Resolves once whether the user is logged in, caching the result so `/me` is called at most once. */
  ensureAuthChecked(): Promise<boolean> {
    if (!this.authCheck$) {
      this.authCheck$ = firstValueFrom(
        this.me().pipe(
          catchError(() =>
            this.http.post(`${this.apiUrl}/refresh`, {}).pipe(
              switchMap(() => this.me()),
              catchError(() => of(null))
            )
          )
        )
      ).then(user => {
        this._currentUser.set(user);
        return user !== null;
      });
    }
    return this.authCheck$;
  }

  private me(): Observable<CurrentUserResponse> {
    return this.http.get<CurrentUserResponse>(`${this.apiUrl}/me`);
  }

  private finishSignOut(): void {
    this.authCheck$ = null;
    this._currentUser.set(null);
    this.router.navigate(['/auth/sign-in']);
  }
}

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not be authenticated before ensureAuthChecked resolves', () => {
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('resolves authenticated true when /me succeeds', async () => {
    const promise = service.ensureAuthChecked();
    httpMock.expectOne('/api/v1/auth/me').flush({ email: 'demo@taskly.app' });

    expect(await promise).toBeTrue();
    expect(service.isAuthenticated()).toBeTrue();
    expect(service.currentUserName()).toBe('Demo');
  });

  it('falls back to /refresh then retries /me when the first /me call fails', async () => {
    const promise = service.ensureAuthChecked();
    httpMock.expectOne('/api/v1/auth/me').flush(null, { status: 401, statusText: 'Unauthorized' });
    httpMock.expectOne('/api/v1/auth/refresh').flush({ expiresIn: 900 });
    httpMock.expectOne('/api/v1/auth/me').flush({ email: 'demo@taskly.app' });

    expect(await promise).toBeTrue();
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('resolves authenticated false when /me and /refresh both fail', async () => {
    const promise = service.ensureAuthChecked();
    httpMock.expectOne('/api/v1/auth/me').flush(null, { status: 401, statusText: 'Unauthorized' });
    httpMock.expectOne('/api/v1/auth/refresh').flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(await promise).toBeFalse();
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('caches the auth check so /me is only called once', async () => {
    const first = service.ensureAuthChecked();
    httpMock.expectOne('/api/v1/auth/me').flush({ email: 'demo@taskly.app' });
    await first;

    const second = service.ensureAuthChecked();
    expect(await second).toBeTrue();
    httpMock.expectNone('/api/v1/auth/me');
  });

  it('signOut clears state and navigates to sign-in', async () => {
    const promise = service.ensureAuthChecked();
    httpMock.expectOne('/api/v1/auth/me').flush({ email: 'demo@taskly.app' });
    await promise;

    const navigateSpy = spyOn(router, 'navigate');
    service.signOut();
    httpMock.expectOne('/api/v1/auth/sign-out').flush({});

    expect(service.isAuthenticated()).toBeFalse();
    expect(navigateSpy).toHaveBeenCalledWith(['/auth/sign-in']);
  });
});

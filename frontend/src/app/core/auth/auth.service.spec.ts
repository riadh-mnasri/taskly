import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not be authenticated initially when no token in localStorage', () => {
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should be authenticated when token exists in localStorage at service init', () => {
    localStorage.setItem('taskly_access_token', 'test-token');
    // Re-create service within TestBed to pick up localStorage state
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [AuthService]
    });
    const freshService = TestBed.inject(AuthService);
    expect(freshService.isAuthenticated()).toBeTrue();
    expect(freshService.getAccessToken()).toBe('test-token');
  });

  it('should store tokens and set authenticated after sign-in', () => {
    const mockResponse = {
      accessToken: 'access-token',
      refreshToken: 'refresh-token',
      expiresIn: 900
    };

    service.signIn({ email: 'test@test.com', password: 'Password1!' }).subscribe(() => {
      expect(service.isAuthenticated()).toBeTrue();
      expect(localStorage.getItem('taskly_access_token')).toBe('access-token');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/sign-in');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should clear tokens on sign-out', () => {
    localStorage.setItem('taskly_access_token', 'test-token');

    service.signOut();

    expect(service.isAuthenticated()).toBeFalse();
    expect(localStorage.getItem('taskly_access_token')).toBeNull();
  });

  it('should return access token via signal after sign-in', () => {
    const mockResponse = { accessToken: 'my-token', refreshToken: 'r', expiresIn: 900 };
    service.signIn({ email: 'a@b.com', password: 'P1!' }).subscribe(() => {
      expect(service.getAccessToken()).toBe('my-token');
    });
    const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/sign-in');
    req.flush(mockResponse);
  });
});

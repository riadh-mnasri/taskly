import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <mat-card class="w-full max-w-md">
        <mat-card-header class="pb-4 flex-col items-center text-center">
          <div class="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center mb-3">
            <mat-icon class="text-white text-2xl">school</mat-icon>
          </div>
          <mat-card-title class="text-2xl font-bold">Create your account</mat-card-title>
          <mat-card-subtitle>Start managing your tasks today</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <form [formGroup]="form" (ngSubmit)="submit()" class="flex flex-col gap-4">

            <div *ngIf="error" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
              {{ error }}
            </div>

            <mat-form-field appearance="outline">
              <mat-label>Email address</mat-label>
              <input matInput type="email" formControlName="email" autocomplete="email">
              <mat-icon matPrefix>email</mat-icon>
              <mat-error *ngIf="form.get('email')?.hasError('required')">Email is required</mat-error>
              <mat-error *ngIf="form.get('email')?.hasError('email')">Enter a valid email</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline">
              <mat-label>Password</mat-label>
              <input matInput [type]="showPassword ? 'text' : 'password'" formControlName="password" autocomplete="new-password">
              <mat-icon matPrefix>lock</mat-icon>
              <button mat-icon-button matSuffix type="button" (click)="showPassword = !showPassword">
                <mat-icon>{{ showPassword ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              <mat-hint>At least 8 chars, with uppercase, lowercase, and a digit</mat-hint>
              <mat-error *ngIf="form.get('password')?.hasError('required')">Password is required</mat-error>
              <mat-error *ngIf="form.get('password')?.hasError('minlength')">At least 8 characters</mat-error>
            </mat-form-field>

            <button mat-raised-button color="primary" type="submit"
                    [disabled]="form.invalid || loading" class="py-2">
              <mat-spinner *ngIf="loading" diameter="20" class="inline-block mr-2"></mat-spinner>
              {{ loading ? 'Creating account...' : 'Create account' }}
            </button>
          </form>
        </mat-card-content>

        <mat-card-footer class="text-center py-4">
          <p class="text-sm text-gray-600">
            Already have an account?
            <a routerLink="/auth/sign-in" class="text-blue-600 font-medium hover:underline">Sign in</a>
          </p>
        </mat-card-footer>
      </mat-card>
    </div>
  `
})
export class SignUpComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  loading = false;
  error: string | null = null;
  showPassword = false;

  submit(): void {
    if (this.form.invalid) return;

    this.loading = true;
    this.error = null;

    const { email, password } = this.form.value;
    this.authService.signUp({ email: email!, password: password! }).subscribe({
      next: () => {
        this.authService.signIn({ email: email!, password: password! }).subscribe({
          next: () => this.router.navigate(['/dashboard']),
          error: () => this.router.navigate(['/auth/sign-in'])
        });
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 409) {
          this.error = 'This email is already registered. Please sign in.';
        } else if (err.error?.detail) {
          this.error = err.error.detail;
        } else {
          this.error = 'Registration failed. Please try again.';
        }
      }
    });
  }
}

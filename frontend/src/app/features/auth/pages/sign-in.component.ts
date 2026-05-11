import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="min-h-screen flex" style="background: linear-gradient(135deg, #4c1d95 0%, #6d28d9 40%, #7c3aed 70%, #a855f7 100%);">

      <!-- Left panel — branding -->
      <div class="hidden lg:flex flex-col justify-center items-center flex-1 p-12 text-white">
        <div class="max-w-sm text-center">
          <div class="text-7xl mb-6">📚</div>
          <h1 class="text-4xl font-extrabold mb-4 leading-tight">Taskly</h1>
          <p class="text-xl text-purple-200 mb-8">Tes devoirs, tes examens, ta vie scolaire — tout au même endroit.</p>
          <div class="flex flex-col gap-3 text-left">
            <div class="flex items-center gap-3 bg-white bg-opacity-10 rounded-2xl px-4 py-3">
              <span class="text-2xl">✅</span>
              <span class="text-purple-100">Gère tes tâches sans stress</span>
            </div>
            <div class="flex items-center gap-3 bg-white bg-opacity-10 rounded-2xl px-4 py-3">
              <span class="text-2xl">📅</span>
              <span class="text-purple-100">Ne rate plus aucune deadline</span>
            </div>
            <div class="flex items-center gap-3 bg-white bg-opacity-10 rounded-2xl px-4 py-3">
              <span class="text-2xl">🏆</span>
              <span class="text-purple-100">Vois ta progression au fil des jours</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right panel — form -->
      <div class="flex flex-col justify-center flex-1 lg:max-w-md p-8 bg-white lg:rounded-l-3xl">
        <div class="w-full max-w-sm mx-auto">

          <!-- Logo (mobile only) -->
          <div class="flex items-center gap-3 mb-8 lg:hidden">
            <span class="text-4xl">📚</span>
            <span class="text-2xl font-extrabold text-violet-700">Taskly</span>
          </div>

          <h2 class="text-3xl font-extrabold text-gray-900 mb-1">Bon retour ! 👋</h2>
          <p class="text-gray-500 mb-8">Connecte-toi pour voir tes tâches du jour.</p>

          <div *ngIf="error"
               class="flex items-center gap-2 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-sm mb-4">
            <mat-icon class="text-red-500" style="font-size:18px;width:18px;height:18px;">error_outline</mat-icon>
            {{ error }}
          </div>

          <form [formGroup]="form" (ngSubmit)="submit()" class="flex flex-col gap-4">

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Adresse e-mail</mat-label>
              <input matInput type="email" formControlName="email" autocomplete="email">
              <mat-icon matSuffix class="text-violet-400">email</mat-icon>
              <mat-error *ngIf="form.get('email')?.hasError('required')">L'e-mail est requis</mat-error>
              <mat-error *ngIf="form.get('email')?.hasError('email')">E-mail invalide</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Mot de passe</mat-label>
              <input matInput [type]="showPassword ? 'text' : 'password'"
                     formControlName="password" autocomplete="current-password">
              <button mat-icon-button matSuffix type="button" (click)="showPassword = !showPassword">
                <mat-icon class="text-violet-400">{{ showPassword ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              <mat-error *ngIf="form.get('password')?.hasError('required')">Le mot de passe est requis</mat-error>
            </mat-form-field>

            <!-- Demo hint -->
            <div class="flex items-center gap-2 bg-violet-50 border border-violet-200 rounded-xl px-4 py-3 text-sm">
              <span>🔑</span>
              <div>
                <span class="font-semibold text-violet-700">Compte démo :</span>
                <span class="text-violet-600 ml-1">demo&#64;taskly.app / Demo1234!</span>
              </div>
            </div>

            <button
              mat-raised-button
              type="submit"
              [disabled]="form.invalid || loading"
              class="py-3 rounded-xl font-bold text-base"
              style="background: linear-gradient(135deg, #6d28d9, #a855f7); color: white; box-shadow: 0 4px 20px rgba(109,40,217,0.4);">
              <mat-spinner *ngIf="loading" diameter="18" class="inline-block mr-2"
                           style="display:inline-block;vertical-align:middle;"></mat-spinner>
              {{ loading ? 'Connexion...' : 'Se connecter 🚀' }}
            </button>
          </form>

          <p class="text-center text-sm text-gray-500 mt-6">
            Pas encore de compte ?
            <a routerLink="/auth/sign-up" class="text-violet-600 font-semibold hover:underline ml-1">Créer un compte</a>
          </p>
        </div>
      </div>
    </div>
  `
})
export class SignInComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  loading = false;
  error: string | null = null;
  showPassword = false;

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = null;
    const { email, password } = this.form.value;
    this.authService.signIn({ email: email!, password: password! }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => {
        this.loading = false;
        this.error = err.status === 401
          ? 'E-mail ou mot de passe incorrect.'
          : 'Connexion impossible. Réessaie dans un instant.';
      }
    });
  }
}

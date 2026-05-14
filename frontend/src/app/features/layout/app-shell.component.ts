import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../../core/auth/auth.service';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule
  ],
  styles: [`
    .nav-link {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 16px;
      border-radius: 12px;
      margin: 2px 12px;
      color: rgba(255,255,255,0.7);
      font-size: 14px;
      font-weight: 500;
      text-decoration: none;
      transition: all 0.15s ease;
      cursor: pointer;
    }
    .nav-link:hover {
      background: rgba(255,255,255,0.12);
      color: white;
    }
    .nav-link.active {
      background: rgba(255,255,255,0.18);
      color: white;
      font-weight: 700;
      box-shadow: inset 0 0 0 1px rgba(255,255,255,0.15);
    }
    .nav-link mat-icon {
      font-size: 20px;
      width: 20px;
      height: 20px;
    }
  `],
  template: `
    <div class="flex h-screen overflow-hidden" style="background:#f5f3ff;">

      <!-- Sidebar -->
      <aside *ngIf="!isHandset()"
             class="w-60 flex flex-col shrink-0 h-full"
             style="background: linear-gradient(180deg, #3b0764 0%, #4c1d95 30%, #5b21b6 70%, #6d28d9 100%);">

        <!-- Logo -->
        <div class="flex items-center gap-3 px-5 py-6">
          <div class="w-10 h-10 rounded-xl flex items-center justify-center text-xl"
               style="background:rgba(255,255,255,0.15);">
            📚
          </div>
          <div>
            <div class="text-white font-extrabold text-lg leading-none">Taskly</div>
            <div class="text-purple-300 text-xs mt-0.5">Mon espace scolaire</div>
          </div>
        </div>

        <!-- Nav -->
        <nav class="flex-1 py-2">
          <a class="nav-link" routerLink="/dashboard" routerLinkActive="active">
            <mat-icon>dashboard</mat-icon>
            <span>Dashboard</span>
          </a>
          <a class="nav-link" routerLink="/tasks" routerLinkActive="active">
            <mat-icon>checklist</mat-icon>
            <span>Mes tâches</span>
          </a>
          <a class="nav-link" routerLink="/kanban" routerLinkActive="active">
            <mat-icon>view_kanban</mat-icon>
            <span>Kanban</span>
          </a>
          <a class="nav-link" routerLink="/calendar" routerLinkActive="active">
            <mat-icon>calendar_month</mat-icon>
            <span>Calendrier</span>
          </a>
        </nav>

        <!-- User -->
        <div class="border-t mx-4 mb-2" style="border-color:rgba(255,255,255,0.1);padding-top:16px;">
          <button
            class="nav-link w-full"
            style="width:calc(100% - 0px);margin:0;"
            [matMenuTriggerFor]="userMenu">
            <div class="w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold"
                 style="background:rgba(255,255,255,0.2);color:white;">
              😊
            </div>
            <div class="flex-1 text-left">
              <div class="text-white text-sm font-medium">Mon compte</div>
            </div>
            <mat-icon style="font-size:16px;width:16px;height:16px;color:rgba(255,255,255,0.5);">expand_more</mat-icon>
          </button>
          <mat-menu #userMenu="matMenu">
            <button mat-menu-item (click)="signOut()">
              <mat-icon>logout</mat-icon> Se déconnecter
            </button>
          </mat-menu>
        </div>

        <!-- Copyright -->
        <div class="px-5 pb-4 text-center">
          <p style="color:rgba(255,255,255,0.35);font-size:11px;line-height:1.5;">
            © {{ currentYear }} WeHighTech<br>Tous droits réservés
          </p>
        </div>
      </aside>

      <!-- Main area -->
      <div class="flex flex-col flex-1 overflow-hidden">

        <!-- Mobile top bar -->
        <header *ngIf="isHandset()"
                class="flex items-center gap-3 px-4 py-3 shadow-sm"
                style="background:linear-gradient(135deg,#4c1d95,#6d28d9);">
          <span class="text-white font-extrabold text-lg">📚 Taskly</span>
          <span class="flex-1"></span>
          <button mat-icon-button [matMenuTriggerFor]="mobileMenu">
            <mat-icon class="text-white">menu</mat-icon>
          </button>
          <mat-menu #mobileMenu="matMenu">
            <a mat-menu-item routerLink="/dashboard"><mat-icon>dashboard</mat-icon> Dashboard</a>
            <a mat-menu-item routerLink="/tasks"><mat-icon>checklist</mat-icon> Mes tâches</a>
            <a mat-menu-item routerLink="/kanban"><mat-icon>view_kanban</mat-icon> Kanban</a>
            <a mat-menu-item routerLink="/calendar"><mat-icon>calendar_month</mat-icon> Calendrier</a>
            <button mat-menu-item (click)="signOut()"><mat-icon>logout</mat-icon> Se déconnecter</button>
            <div style="padding:8px 16px;font-size:11px;color:#9ca3af;border-top:1px solid #f3f4f6;margin-top:4px;">
              © {{ currentYear }} WeHighTech
            </div>
          </mat-menu>
        </header>

        <!-- Page content -->
        <main class="flex-1 overflow-auto">
          <router-outlet />
        </main>
      </div>
    </div>
  `
})
export class AppShellComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly breakpointObserver = inject(BreakpointObserver);

  readonly currentYear = new Date().getFullYear();

  readonly isHandset = toSignal(
    this.breakpointObserver.observe(Breakpoints.Handset).pipe(map(r => r.matches)),
    { initialValue: false }
  );

  signOut(): void {
    this.authService.signOut();
  }
}

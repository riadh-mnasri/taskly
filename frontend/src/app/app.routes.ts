import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    children: [
      {
        path: 'sign-in',
        loadComponent: () =>
          import('./features/auth/pages/sign-in.component').then(m => m.SignInComponent)
      },
      {
        path: 'sign-up',
        loadComponent: () =>
          import('./features/auth/pages/sign-up.component').then(m => m.SignUpComponent)
      },
      { path: '', redirectTo: 'sign-in', pathMatch: 'full' }
    ]
  },
  {
    path: '',
    loadComponent: () =>
      import('./features/layout/app-shell.component').then(m => m.AppShellComponent),
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/tasks/pages/dashboard.page').then(m => m.DashboardPage)
      },
      {
        path: 'tasks',
        loadComponent: () =>
          import('./features/tasks/pages/tasks-list.page').then(m => m.TasksListPage)
      },
      {
        path: 'kanban',
        loadComponent: () =>
          import('./features/tasks/pages/kanban.page').then(m => m.KanbanPage)
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: '/dashboard' }
];

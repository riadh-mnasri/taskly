import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { TaskStore } from '../data/task.store';
import { TaskCardComponent } from '../components/task-card.component';
import { TaskFormDialogComponent } from '../components/task-form-dialog.component';
import { Task, TaskFormResult } from '../../../core/models/task.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    TaskCardComponent,
  ],
  template: `
    <div class="p-6 max-w-6xl mx-auto">

      <!-- Header -->
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-3xl font-extrabold text-gray-900">
            {{ greeting }} {{ timeEmoji }}
          </h1>
          <p class="text-gray-400 text-sm mt-1">{{ today }}</p>
        </div>
        <button
          mat-raised-button
          (click)="openNewTask()"
          class="rounded-xl font-bold px-5 py-2"
          style="background:linear-gradient(135deg,#6d28d9,#a855f7);color:white;box-shadow:0 4px 16px rgba(109,40,217,0.35);">
          <mat-icon>add</mat-icon> Nouvelle tâche
        </button>
      </div>

      <!-- Summary cards -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-10">

        <!-- Today -->
        <a routerLink="/tasks"
           class="block rounded-2xl p-5 cursor-pointer transition-transform hover:-translate-y-1 hover:shadow-xl"
           style="background:linear-gradient(135deg,#4c1d95,#7c3aed);text-decoration:none;">
          <div class="flex items-center justify-between mb-3">
            <span class="text-4xl">📅</span>
            <span class="text-5xl font-black text-white">{{ store.todayTasks().length }}</span>
          </div>
          <p class="text-purple-200 font-semibold text-sm">Aujourd'hui</p>
          <p class="text-purple-300 text-xs mt-0.5">tâches dues ce jour</p>
        </a>

        <!-- This week -->
        <a routerLink="/tasks"
           class="block rounded-2xl p-5 cursor-pointer transition-transform hover:-translate-y-1 hover:shadow-xl"
           style="background:linear-gradient(135deg,#1e40af,#3b82f6);text-decoration:none;">
          <div class="flex items-center justify-between mb-3">
            <span class="text-4xl">📆</span>
            <span class="text-5xl font-black text-white">{{ store.thisWeekTasks().length }}</span>
          </div>
          <p class="text-blue-200 font-semibold text-sm">Cette semaine</p>
          <p class="text-blue-300 text-xs mt-0.5">tâches à venir</p>
        </a>

        <!-- Urgent -->
        <a routerLink="/tasks"
           class="block rounded-2xl p-5 cursor-pointer transition-transform hover:-translate-y-1 hover:shadow-xl"
           style="background:linear-gradient(135deg,#991b1b,#ef4444);text-decoration:none;">
          <div class="flex items-center justify-between mb-3">
            <span class="text-4xl">🔥</span>
            <span class="text-5xl font-black text-white">{{ store.urgentTasks().length }}</span>
          </div>
          <p class="text-red-200 font-semibold text-sm">Urgentes</p>
          <p class="text-red-300 text-xs mt-0.5">priorité haute</p>
        </a>
      </div>

      <!-- Motivation banner -->
      <div *ngIf="store.urgentTasks().length === 0 && store.todayTasks().length === 0"
           class="rounded-2xl p-5 mb-8 flex items-center gap-4"
           style="background:linear-gradient(135deg,#ecfdf5,#d1fae5);border:1px solid #a7f3d0;">
        <span class="text-4xl">🎉</span>
        <div>
          <p class="font-bold text-emerald-800">Rien d'urgent aujourd'hui !</p>
          <p class="text-emerald-600 text-sm">Tu es à jour. Profite ou prends de l'avance sur tes prochains devoirs.</p>
        </div>
      </div>

      <!-- Task sections -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">

        <!-- Today -->
        <div>
          <h2 class="font-bold text-gray-700 mb-4 flex items-center gap-2 text-base">
            <span class="text-xl">📅</span> Aujourd'hui
            <span class="ml-auto bg-violet-100 text-violet-700 text-xs font-bold px-2 py-0.5 rounded-full">
              {{ store.todayTasks().length }}
            </span>
          </h2>
          <div *ngIf="store.todayTasks().length === 0"
               class="text-center py-8 text-gray-300 text-sm border-2 border-dashed border-gray-200 rounded-2xl">
            Rien pour aujourd'hui 😌
          </div>
          <app-task-card
            *ngFor="let task of store.todayTasks().slice(0, 3)"
            [task]="task"
            (edit)="openEditTask($event)"
            (delete)="deleteTask($event)"
            (markDone)="markDone($event)"
          />
          <a *ngIf="store.todayTasks().length > 3" routerLink="/tasks"
             class="text-sm text-violet-600 hover:underline mt-1 block font-medium">
            +{{ store.todayTasks().length - 3 }} autres →
          </a>
        </div>

        <!-- This week -->
        <div>
          <h2 class="font-bold text-gray-700 mb-4 flex items-center gap-2 text-base">
            <span class="text-xl">📆</span> Cette semaine
            <span class="ml-auto bg-blue-100 text-blue-700 text-xs font-bold px-2 py-0.5 rounded-full">
              {{ store.thisWeekTasks().length }}
            </span>
          </h2>
          <div *ngIf="store.thisWeekTasks().length === 0"
               class="text-center py-8 text-gray-300 text-sm border-2 border-dashed border-gray-200 rounded-2xl">
            Rien cette semaine 🏖️
          </div>
          <app-task-card
            *ngFor="let task of store.thisWeekTasks().slice(0, 3)"
            [task]="task"
            (edit)="openEditTask($event)"
            (delete)="deleteTask($event)"
            (markDone)="markDone($event)"
          />
        </div>

        <!-- Urgent -->
        <div>
          <h2 class="font-bold text-gray-700 mb-4 flex items-center gap-2 text-base">
            <span class="text-xl">🔥</span> Urgentes
            <span class="ml-auto bg-red-100 text-red-700 text-xs font-bold px-2 py-0.5 rounded-full">
              {{ store.urgentTasks().length }}
            </span>
          </h2>
          <div *ngIf="store.urgentTasks().length === 0"
               class="text-center py-8 text-gray-300 text-sm border-2 border-dashed border-gray-200 rounded-2xl">
            Aucune urgence 🎊
          </div>
          <app-task-card
            *ngFor="let task of store.urgentTasks().slice(0, 3)"
            [task]="task"
            (edit)="openEditTask($event)"
            (delete)="deleteTask($event)"
            (markDone)="markDone($event)"
          />
        </div>
      </div>
    </div>
  `
})
export class DashboardPage implements OnInit {
  readonly store = inject(TaskStore);
  private readonly dialog = inject(MatDialog);

  readonly today = new Date().toLocaleDateString('fr-FR', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });

  get greeting(): string {
    const h = new Date().getHours();
    if (h < 12) return 'Bonjour !';
    if (h < 18) return 'Salut !';
    return 'Bonsoir !';
  }

  get timeEmoji(): string {
    const h = new Date().getHours();
    if (h < 12) return '☀️';
    if (h < 18) return '🌤️';
    return '🌙';
  }

  ngOnInit(): void {
    this.store.loadTasks();
  }

  openNewTask(): void {
    const ref = this.dialog.open(TaskFormDialogComponent, {
      data: {},
      maxWidth: '640px',
      width: '95vw'
    });
    ref.afterClosed().subscribe((result: TaskFormResult | undefined) => {
      if (result) this.store.createTask(result.request);
    });
  }

  openEditTask(task: Task): void {
    const ref = this.dialog.open(TaskFormDialogComponent, {
      data: { task },
      maxWidth: '640px',
      width: '95vw'
    });
    ref.afterClosed().subscribe((result: TaskFormResult | undefined) => {
      if (result) this.store.updateTask(task.id, result.request);
    });
  }

  deleteTask(task: Task): void {
    if (confirm(`Supprimer "${task.title}" ?`)) {
      this.store.deleteTask(task.id);
    }
  }

  markDone(task: Task): void {
    this.store.updateTaskStatus(task.id, 'DONE');
  }
}

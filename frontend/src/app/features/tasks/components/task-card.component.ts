import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Task, TASK_TYPE_LABELS } from '../../../core/models/task.model';
import { PriorityBadgeComponent } from './priority-badge.component';

@Component({
  selector: 'app-task-card',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule,
    PriorityBadgeComponent
  ],
  styles: [`
    .task-card {
      background: white;
      border-radius: 16px;
      padding: 14px 16px;
      margin-bottom: 10px;
      border-left: 4px solid transparent;
      box-shadow: 0 2px 12px rgba(99,102,241,0.08);
      transition: box-shadow 0.15s ease, transform 0.15s ease;
    }
    .task-card:hover {
      box-shadow: 0 6px 24px rgba(99,102,241,0.15);
      transform: translateY(-1px);
    }
    .task-card.high   { border-left-color: #ef4444; }
    .task-card.medium { border-left-color: #f97316; }
    .task-card.low    { border-left-color: #10b981; }
    .task-card.done-card { opacity: 0.55; }
  `],
  template: `
    <div class="task-card kanban-card"
         [class]="priorityClass"
         [class.done-card]="task.status === 'DONE'">

      <div class="flex justify-between items-start mb-2">
        <span class="text-xs font-semibold uppercase tracking-widest"
              [style.color]="typeColor">{{ typeLabel }}</span>
        <button mat-icon-button [matMenuTriggerFor]="menu"
                style="width:28px;height:28px;line-height:28px;margin:-4px -8px 0 0;"
                (click)="$event.stopPropagation()">
          <mat-icon style="font-size:18px;width:18px;height:18px;color:#9ca3af;">more_vert</mat-icon>
        </button>
        <mat-menu #menu="matMenu">
          <button mat-menu-item (click)="edit.emit(task)">
            <mat-icon>edit</mat-icon> Modifier
          </button>
          <button mat-menu-item (click)="markDone.emit(task)" [disabled]="task.status === 'DONE'">
            <mat-icon>check_circle</mat-icon> Marquer terminé
          </button>
          <button mat-menu-item (click)="delete.emit(task)" style="color:#ef4444;">
            <mat-icon style="color:#ef4444;">delete</mat-icon> Supprimer
          </button>
        </mat-menu>
      </div>

      <h4 class="font-bold text-gray-900 mb-1 text-sm leading-tight"
          [class.line-through]="task.status === 'DONE'"
          [class.text-gray-400]="task.status === 'DONE'">
        {{ task.title }}
      </h4>

      <p class="text-xs text-gray-400 mb-3">{{ task.subject }}</p>

      <div class="flex items-center justify-between">
        <app-priority-badge [priority]="task.priority" />
        <span class="text-xs font-medium flex items-center gap-1"
              [class.text-red-500]="isOverdue"
              [class.text-gray-400]="!isOverdue">
          <mat-icon style="font-size:12px;width:12px;height:12px;">{{ isOverdue ? 'warning' : 'schedule' }}</mat-icon>
          {{ formatDate(task.dueDate) }}
        </span>
      </div>

      <div class="flex items-center gap-1 mt-2 text-xs text-gray-300">
        <mat-icon style="font-size:12px;width:12px;height:12px;">timer</mat-icon>
        {{ task.estimatedDurationMinutes }} min
      </div>
    </div>
  `
})
export class TaskCardComponent {
  @Input({ required: true }) task!: Task;
  @Output() edit = new EventEmitter<Task>();
  @Output() delete = new EventEmitter<Task>();
  @Output() markDone = new EventEmitter<Task>();

  get typeLabel(): string {
    return TASK_TYPE_LABELS[this.task.type];
  }

  get typeColor(): string {
    const colors: Record<string, string> = {
      HOMEWORK: '#7c3aed', EXAM: '#dc2626', PROJECT: '#0891b2',
      REVISION: '#059669', HEALTH: '#d97706', OTHER: '#6b7280'
    };
    return colors[this.task.type] ?? '#6b7280';
  }

  get priorityClass(): string {
    return this.task.priority.toLowerCase();
  }

  get isOverdue(): boolean {
    const today = new Date().toISOString().split('T')[0];
    return this.task.dueDate < today && this.task.status !== 'DONE';
  }

  formatDate(dateStr: string): string {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const due = new Date(dateStr + 'T00:00:00');
    const diff = Math.round((due.getTime() - today.getTime()) / 86400000);
    if (diff === 0) return "Aujourd'hui";
    if (diff === 1) return 'Demain';
    if (diff === -1) return 'Hier';
    if (diff < 0) return `${Math.abs(diff)}j de retard`;
    if (diff <= 7) return `dans ${diff}j`;
    return due.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' });
  }
}

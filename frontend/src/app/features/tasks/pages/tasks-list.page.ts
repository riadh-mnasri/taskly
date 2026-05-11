import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { TaskStore } from '../data/task.store';
import { PriorityBadgeComponent } from '../components/priority-badge.component';
import { TaskFormDialogComponent } from '../components/task-form-dialog.component';
import { Task, CreateTaskRequest, TASK_STATUS_LABELS } from '../../../core/models/task.model';

@Component({
  selector: 'app-tasks-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatChipsModule,
    PriorityBadgeComponent
  ],
  template: `
    <div class="p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-900">My Tasks</h1>
        <button mat-raised-button color="primary" (click)="openNewTask()">
          <mat-icon>add</mat-icon> New Task
        </button>
      </div>

      <div class="flex gap-6">
        <!-- Filters sidebar -->
        <div class="w-56 shrink-0">
          <mat-card>
            <mat-card-content class="pt-4">
              <h3 class="font-semibold text-gray-700 mb-3">Filters</h3>
              <form [formGroup]="filterForm" class="flex flex-col gap-3">
                <mat-form-field appearance="outline" class="w-full">
                  <mat-label>Priority</mat-label>
                  <mat-select formControlName="priority">
                    <mat-option value="">All</mat-option>
                    <mat-option value="HIGH">High</mat-option>
                    <mat-option value="MEDIUM">Medium</mat-option>
                    <mat-option value="LOW">Low</mat-option>
                  </mat-select>
                </mat-form-field>

                <mat-form-field appearance="outline" class="w-full">
                  <mat-label>Status</mat-label>
                  <mat-select formControlName="status">
                    <mat-option value="">All</mat-option>
                    <mat-option value="TODO">To Do</mat-option>
                    <mat-option value="IN_PROGRESS">In Progress</mat-option>
                    <mat-option value="DONE">Done</mat-option>
                  </mat-select>
                </mat-form-field>

                <mat-form-field appearance="outline" class="w-full">
                  <mat-label>Subject</mat-label>
                  <input matInput formControlName="subject" placeholder="e.g., Math">
                </mat-form-field>

                <mat-form-field appearance="outline" class="w-full">
                  <mat-label>Sort by</mat-label>
                  <mat-select formControlName="sort">
                    <mat-option value="dueDate">Due Date</mat-option>
                    <mat-option value="priority">Priority</mat-option>
                  </mat-select>
                </mat-form-field>

                <button mat-stroked-button type="button" (click)="clearFilters()">
                  Clear Filters
                </button>
              </form>
            </mat-card-content>
          </mat-card>
        </div>

        <!-- Task table -->
        <div class="flex-1 overflow-auto">
          <div *ngIf="store.loading()" class="flex justify-center py-8">
            <mat-spinner diameter="40"></mat-spinner>
          </div>

          <mat-card *ngIf="!store.loading()">
            <table mat-table [dataSource]="store.tasks()" class="w-full">

              <ng-container matColumnDef="title">
                <th mat-header-cell *matHeaderCellDef class="font-semibold">Title</th>
                <td mat-cell *matCellDef="let task">
                  <div class="py-2">
                    <div class="font-medium text-gray-900">{{ task.title }}</div>
                    <div class="text-xs text-gray-500">{{ task.subject }}</div>
                  </div>
                </td>
              </ng-container>

              <ng-container matColumnDef="priority">
                <th mat-header-cell *matHeaderCellDef>Priority</th>
                <td mat-cell *matCellDef="let task">
                  <app-priority-badge [priority]="task.priority" />
                </td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>Status</th>
                <td mat-cell *matCellDef="let task">
                  <span class="status-chip" [class]="'status-' + task.status.toLowerCase().replace('_', '-')">
                    {{ getStatusLabel(task.status) }}
                  </span>
                </td>
              </ng-container>

              <ng-container matColumnDef="dueDate">
                <th mat-header-cell *matHeaderCellDef>Due Date</th>
                <td mat-cell *matCellDef="let task">
                  <span [class.text-red-500]="isOverdue(task)" class="text-sm">
                    {{ formatDate(task.dueDate) }}
                  </span>
                </td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef></th>
                <td mat-cell *matCellDef="let task">
                  <div class="flex items-center gap-1 justify-end">
                    <button mat-icon-button matTooltip="Mark as Done"
                            [disabled]="task.status === 'DONE'"
                            (click)="markDone(task)">
                      <mat-icon class="text-green-600">check_circle</mat-icon>
                    </button>
                    <button mat-icon-button matTooltip="Edit" (click)="openEditTask(task)">
                      <mat-icon class="text-blue-500">edit</mat-icon>
                    </button>
                    <button mat-icon-button matTooltip="Delete" (click)="deleteTask(task)">
                      <mat-icon class="text-red-500">delete</mat-icon>
                    </button>
                  </div>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns; sticky: true"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"
                  class="hover:bg-gray-50 transition-colors"></tr>

              <tr class="mat-row" *matNoDataRow>
                <td class="mat-cell text-center py-8 text-gray-400" colspan="5">
                  No tasks found. Create your first task!
                </td>
              </tr>
            </table>
          </mat-card>
        </div>
      </div>
    </div>
  `
})
export class TasksListPage implements OnInit {
  readonly store = inject(TaskStore);
  private readonly dialog = inject(MatDialog);
  private readonly fb = inject(FormBuilder);

  readonly displayedColumns = ['title', 'priority', 'status', 'dueDate', 'actions'];
  readonly statusLabels = TASK_STATUS_LABELS;

  filterForm = this.fb.group({
    priority: [''],
    status: [''],
    subject: [''],
    sort: ['dueDate']
  });

  ngOnInit(): void {
    this.store.loadTasks();
    this.filterForm.valueChanges.subscribe(() => this.applyFilters());
  }

  applyFilters(): void {
    const { priority, status, subject, sort } = this.filterForm.value;
    this.store.loadTasks({
      priority: priority || undefined,
      status: status || undefined,
      subject: subject || undefined,
      sort: sort || 'dueDate'
    } as any);
  }

  getStatusLabel(status: string): string {
    return TASK_STATUS_LABELS[status as keyof typeof TASK_STATUS_LABELS] ?? status;
  }

  clearFilters(): void {
    this.filterForm.reset({ sort: 'dueDate' });
  }

  openNewTask(): void {
    const ref = this.dialog.open(TaskFormDialogComponent, {
      data: {},
      maxWidth: '640px',
      width: '95vw'
    });
    ref.afterClosed().subscribe((result: CreateTaskRequest | undefined) => {
      if (result) this.store.createTask(result).then(() => this.applyFilters());
    });
  }

  openEditTask(task: Task): void {
    const ref = this.dialog.open(TaskFormDialogComponent, {
      data: { task },
      maxWidth: '640px',
      width: '95vw'
    });
    ref.afterClosed().subscribe((result: CreateTaskRequest | undefined) => {
      if (result) this.store.updateTask(task.id, result);
    });
  }

  deleteTask(task: Task): void {
    if (confirm(`Delete "${task.title}"?`)) {
      this.store.deleteTask(task.id);
    }
  }

  markDone(task: Task): void {
    this.store.updateTaskStatus(task.id, 'DONE');
  }

  isOverdue(task: Task): boolean {
    const today = new Date().toISOString().split('T')[0];
    return task.dueDate < today && task.status !== 'DONE';
  }

  formatDate(dateStr: string): string {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const due = new Date(dateStr + 'T00:00:00');
    const diffDays = Math.round((due.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Tomorrow';
    if (diffDays < 0) return `${Math.abs(diffDays)}d overdue`;
    if (diffDays <= 7) return `in ${diffDays}d`;
    return due.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }
}

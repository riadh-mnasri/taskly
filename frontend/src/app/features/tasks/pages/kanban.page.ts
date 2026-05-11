import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
  transferArrayItem
} from '@angular/cdk/drag-drop';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { TaskStore } from '../data/task.store';
import { TaskService } from '../data/task.service';
import { TaskCardComponent } from '../components/task-card.component';
import { TaskFormDialogComponent } from '../components/task-form-dialog.component';
import { Task, TaskStatus, CreateTaskRequest } from '../../../core/models/task.model';

@Component({
  selector: 'app-kanban',
  standalone: true,
  imports: [
    CommonModule,
    DragDropModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    TaskCardComponent
  ],
  template: `
    <div class="p-6 max-w-7xl mx-auto">

      <!-- Header -->
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-3xl font-extrabold text-gray-900">Tableau Kanban 🗂️</h1>
          <p class="text-gray-400 text-sm mt-1">Glisse tes tâches pour changer leur statut</p>
        </div>
        <button mat-raised-button (click)="openNewTask()"
                class="rounded-xl font-bold px-5 py-2"
                style="background:linear-gradient(135deg,#6d28d9,#a855f7);color:white;box-shadow:0 4px 16px rgba(109,40,217,0.35);">
          <mat-icon>add</mat-icon> Nouvelle tâche
        </button>
      </div>

      <div *ngIf="store.loading()" class="flex justify-center py-16">
        <mat-spinner diameter="40"></mat-spinner>
      </div>

      <div *ngIf="!store.loading()" class="flex gap-5 overflow-x-auto pb-6">

        <!-- TODO -->
        <div class="flex-1 min-w-72">
          <div class="rounded-2xl px-5 py-4 mb-3 flex items-center justify-between"
               style="background:linear-gradient(135deg,#4c1d95,#6d28d9)">
            <div class="flex items-center gap-2">
              <span class="text-xl">📋</span>
              <h2 class="font-bold text-white text-sm">À faire</h2>
            </div>
            <span class="bg-white text-violet-700 text-xs font-black px-2.5 py-1 rounded-full"
                  style="opacity:0.9">{{ todoList.length }}</span>
          </div>
          <div cdkDropList
               id="TODO"
               [cdkDropListData]="todoList"
               cdkDropListConnectedTo="IN_PROGRESS DONE"
               [cdkDropListConnectedTo]="['IN_PROGRESS','DONE']"
               class="min-h-32 rounded-2xl p-3"
               style="background:#f5f3ff;"
               (cdkDropListDropped)="onDrop($event)">
            <div cdkDrag *ngFor="let task of todoList; trackBy: trackTask" [cdkDragData]="task">
              <app-task-card [task]="task"
                (edit)="openEditTask($event)" (delete)="deleteTask($event)" (markDone)="markDone($event)"/>
              <div *cdkDragPlaceholder class="h-16 rounded-xl border-2 border-dashed border-violet-300 mb-2 bg-violet-50"></div>
            </div>
            <div *ngIf="todoList.length === 0" class="text-center py-10 text-sm border-2 border-dashed rounded-2xl"
                 style="color:#c4b5fd;border-color:#ddd6fe;">Dépose ici ✨</div>
          </div>
        </div>

        <!-- IN_PROGRESS -->
        <div class="flex-1 min-w-72">
          <div class="rounded-2xl px-5 py-4 mb-3 flex items-center justify-between"
               style="background:linear-gradient(135deg,#1e40af,#3b82f6)">
            <div class="flex items-center gap-2">
              <span class="text-xl">⚡</span>
              <h2 class="font-bold text-white text-sm">En cours</h2>
            </div>
            <span class="bg-white text-blue-700 text-xs font-black px-2.5 py-1 rounded-full"
                  style="opacity:0.9">{{ inProgressList.length }}</span>
          </div>
          <div cdkDropList
               id="IN_PROGRESS"
               [cdkDropListData]="inProgressList"
               [cdkDropListConnectedTo]="['TODO','DONE']"
               class="min-h-32 rounded-2xl p-3"
               style="background:#eff6ff;"
               (cdkDropListDropped)="onDrop($event)">
            <div cdkDrag *ngFor="let task of inProgressList; trackBy: trackTask" [cdkDragData]="task">
              <app-task-card [task]="task"
                (edit)="openEditTask($event)" (delete)="deleteTask($event)" (markDone)="markDone($event)"/>
              <div *cdkDragPlaceholder class="h-16 rounded-xl border-2 border-dashed border-blue-300 mb-2 bg-blue-50"></div>
            </div>
            <div *ngIf="inProgressList.length === 0" class="text-center py-10 text-sm border-2 border-dashed rounded-2xl"
                 style="color:#93c5fd;border-color:#bfdbfe;">Dépose ici ✨</div>
          </div>
        </div>

        <!-- DONE -->
        <div class="flex-1 min-w-72">
          <div class="rounded-2xl px-5 py-4 mb-3 flex items-center justify-between"
               style="background:linear-gradient(135deg,#065f46,#10b981)">
            <div class="flex items-center gap-2">
              <span class="text-xl">✅</span>
              <h2 class="font-bold text-white text-sm">Terminé</h2>
            </div>
            <span class="bg-white text-emerald-700 text-xs font-black px-2.5 py-1 rounded-full"
                  style="opacity:0.9">{{ doneList.length }}</span>
          </div>
          <div cdkDropList
               id="DONE"
               [cdkDropListData]="doneList"
               [cdkDropListConnectedTo]="['TODO','IN_PROGRESS']"
               class="min-h-32 rounded-2xl p-3"
               style="background:#ecfdf5;"
               (cdkDropListDropped)="onDrop($event)">
            <div cdkDrag *ngFor="let task of doneList; trackBy: trackTask" [cdkDragData]="task">
              <app-task-card [task]="task"
                (edit)="openEditTask($event)" (delete)="deleteTask($event)" (markDone)="markDone($event)"/>
              <div *cdkDragPlaceholder class="h-16 rounded-xl border-2 border-dashed border-emerald-300 mb-2 bg-emerald-50"></div>
            </div>
            <div *ngIf="doneList.length === 0" class="text-center py-10 text-sm border-2 border-dashed rounded-2xl"
                 style="color:#6ee7b7;border-color:#a7f3d0;">Dépose ici ✨</div>
          </div>
        </div>

      </div>
    </div>
  `
})
export class KanbanPage implements OnInit {
  readonly store = inject(TaskStore);
  private readonly taskService = inject(TaskService);
  private readonly dialog = inject(MatDialog);

  // Local mutable arrays — CDK mutates these directly via transferArrayItem
  todoList: Task[] = [];
  inProgressList: Task[] = [];
  doneList: Task[] = [];

  ngOnInit(): void {
    this.store.loadTasks().then(() => this.syncFromStore());
  }

  private syncFromStore(): void {
    this.todoList = [...this.store.todoTasks()];
    this.inProgressList = [...this.store.inProgressTasks()];
    this.doneList = [...this.store.doneTasks()];
  }

  trackTask(_: number, task: Task): string {
    return task.id;
  }

  onDrop(event: CdkDragDrop<Task[]>): void {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      return;
    }

    // Immediately move item in local arrays so CDK can animate correctly
    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex
    );

    const task: Task = event.item.data;
    const targetStatus = event.container.id as TaskStatus;

    // Persist to API — on failure, re-sync from server
    this.taskService.updateStatus(task.id, targetStatus).subscribe({
      error: () => this.store.loadTasks().then(() => this.syncFromStore())
    });
  }

  openNewTask(): void {
    const ref = this.dialog.open(TaskFormDialogComponent, {
      data: {}, maxWidth: '640px', width: '95vw'
    });
    ref.afterClosed().subscribe((result: CreateTaskRequest | undefined) => {
      if (result) this.store.createTask(result).then(() => this.syncFromStore());
    });
  }

  openEditTask(task: Task): void {
    const ref = this.dialog.open(TaskFormDialogComponent, {
      data: { task }, maxWidth: '640px', width: '95vw'
    });
    ref.afterClosed().subscribe((result: CreateTaskRequest | undefined) => {
      if (result) this.store.updateTask(task.id, result).then(() => this.syncFromStore());
    });
  }

  deleteTask(task: Task): void {
    if (confirm(`Supprimer "${task.title}" ?`)) {
      this.store.deleteTask(task.id).then(() => this.syncFromStore());
    }
  }

  markDone(task: Task): void {
    this.taskService.updateStatus(task.id, 'DONE').subscribe({
      next: () => this.store.loadTasks().then(() => this.syncFromStore())
    });
  }
}

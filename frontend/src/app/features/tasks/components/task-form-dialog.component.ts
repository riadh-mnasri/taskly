import { Component, HostListener, Inject, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Task, Priority, TaskType, TaskStatus, CreateTaskRequest, TaskFormResult } from '../../../core/models/task.model';

export interface TaskFormDialogData {
  task?: Task;
  prefillDate?: Date;
}

@Component({
  selector: 'app-task-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatTooltipModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule
  ],
  template: `
    <div style="background:linear-gradient(135deg,#4c1d95,#6d28d9);padding:20px 24px 16px;">
      <h2 style="color:white;font-size:18px;font-weight:800;margin:0;">
        {{ isEdit ? '✏️ Modifier la tâche' : '✨ Nouvelle tâche' }}
      </h2>
    </div>

    <mat-dialog-content class="task-form-content">
      <form [formGroup]="form" class="flex flex-col gap-4 pt-4">

        <mat-form-field appearance="outline">
          <mat-label>Titre</mat-label>
          <input matInput formControlName="title" placeholder="ex : Exercices de maths">
          <mat-error *ngIf="form.get('title')?.hasError('required')">Le titre est requis</mat-error>
          <mat-error *ngIf="form.get('title')?.hasError('maxlength')">200 caractères max</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Description (optionnelle)</mat-label>
          <textarea matInput formControlName="description" rows="2" placeholder="Détails..."></textarea>
        </mat-form-field>

        <div class="grid grid-cols-2 gap-4">
          <mat-form-field appearance="outline">
            <mat-label>Matière</mat-label>
            <input matInput formControlName="subject" placeholder="ex : Mathématiques">
            <mat-error *ngIf="form.get('subject')?.hasError('required')">La matière est requise</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Type</mat-label>
            <mat-select formControlName="type">
              <mat-option value="HOMEWORK">📝 Devoir</mat-option>
              <mat-option value="EXAM">📖 Examen</mat-option>
              <mat-option value="PROJECT">🛠️ Projet</mat-option>
              <mat-option value="PERSONAL">⭐ Personnel</mat-option>
              <mat-option value="HEALTH">💪 Sport / Santé</mat-option>
            </mat-select>
          </mat-form-field>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <mat-form-field appearance="outline">
            <mat-label>Priorité</mat-label>
            <mat-select formControlName="priority">
              <mat-option value="HIGH">🔴 Haute</mat-option>
              <mat-option value="MEDIUM">🟠 Moyenne</mat-option>
              <mat-option value="LOW">🟢 Basse</mat-option>
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Date limite</mat-label>
            <input matInput [matDatepicker]="picker" formControlName="dueDate"
                   [min]="minDate" placeholder="jj/mm/aaaa" readonly
                   style="cursor:pointer" (click)="picker.open()">
            <mat-datepicker-toggle matIconSuffix [for]="picker"
              matTooltip="Ouvrir le calendrier"></mat-datepicker-toggle>
            <mat-datepicker #picker [touchUi]="isMobile" startView="month"
                            color="primary"></mat-datepicker>
            <mat-hint>Cliquez sur l'icône pour ouvrir le calendrier</mat-hint>
            <mat-error *ngIf="form.get('dueDate')?.hasError('required')">La date est requise</mat-error>
          </mat-form-field>
        </div>

        <div [class.grid]="isEdit" [class.grid-cols-2]="isEdit" [class.gap-4]="isEdit">
          <mat-form-field appearance="outline">
            <mat-label>Durée estimée (minutes)</mat-label>
            <input matInput type="number" formControlName="estimatedDurationMinutes" min="1" max="480">
            <mat-hint>1 – 480 min</mat-hint>
            <mat-error *ngIf="form.get('estimatedDurationMinutes')?.hasError('required')">Requis</mat-error>
            <mat-error *ngIf="form.get('estimatedDurationMinutes')?.hasError('min')">Min 1 min</mat-error>
            <mat-error *ngIf="form.get('estimatedDurationMinutes')?.hasError('max')">Max 480 min</mat-error>
          </mat-form-field>

          <!-- Status — edit mode only -->
          <mat-form-field *ngIf="isEdit" appearance="outline">
            <mat-label>Statut</mat-label>
            <mat-select formControlName="status">
              <mat-option value="TODO">📋 À faire</mat-option>
              <mat-option value="IN_PROGRESS">⚡ En cours</mat-option>
              <mat-option value="DONE">✅ Terminé</mat-option>
            </mat-select>
          </mat-form-field>
        </div>

      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end" style="padding:12px 24px;">
      <button mat-button mat-dialog-close style="color:#6b7280;">Annuler</button>
      <button mat-raised-button (click)="submit()" [disabled]="form.invalid"
              style="background:linear-gradient(135deg,#6d28d9,#a855f7);color:white;border-radius:10px;font-weight:700;">
        {{ isEdit ? 'Enregistrer' : 'Créer la tâche' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .task-form-content {
      min-width: 480px;
      max-width: 600px;
      padding: 0 24px;
    }
    @media (max-width: 600px) {
      .task-form-content { min-width: unset; width: 100%; }
      .grid { grid-template-columns: 1fr !important; }
    }
  `]
})
export class TaskFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);

  form!: FormGroup;
  minDate = new Date();
  isMobile = window.innerWidth < 768;

  @HostListener('window:resize')
  onResize() { this.isMobile = window.innerWidth < 768; }

  get isEdit(): boolean {
    return !!this.data.task;
  }

  constructor(
    private dialogRef: MatDialogRef<TaskFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TaskFormDialogData
  ) {}

  ngOnInit(): void {
    const task = this.data.task;
    this.form = this.fb.group({
      title:                    [task?.title ?? '',             [Validators.required, Validators.maxLength(200)]],
      description:              [task?.description ?? ''],
      subject:                  [task?.subject ?? '',           [Validators.required, Validators.maxLength(100)]],
      priority:                 [task?.priority ?? 'MEDIUM' as Priority, Validators.required],
      type:                     [task?.type ?? 'HOMEWORK' as TaskType,   Validators.required],
      dueDate:                  [task ? new Date(task.dueDate + 'T00:00:00') : (this.data.prefillDate ?? null), Validators.required],
      estimatedDurationMinutes: [task?.estimatedDurationMinutes ?? 30, [Validators.required, Validators.min(1), Validators.max(480)]],
      status:                   [task?.status ?? 'TODO' as TaskStatus]
    });
  }

  submit(): void {
    if (this.form.invalid) return;

    const value = this.form.value;
    const dueDate = value.dueDate instanceof Date
      ? value.dueDate.toISOString().split('T')[0]
      : value.dueDate;

    const request: CreateTaskRequest = {
      title:                    value.title,
      description:              value.description || null,
      subject:                  value.subject,
      priority:                 value.priority,
      type:                     value.type,
      dueDate,
      estimatedDurationMinutes: Number(value.estimatedDurationMinutes)
    };

    const result: TaskFormResult = { request };

    // Include status change only when editing and status has changed
    if (this.isEdit && value.status !== this.data.task!.status) {
      result.newStatus = value.status as TaskStatus;
    }

    this.dialogRef.close(result);
  }
}

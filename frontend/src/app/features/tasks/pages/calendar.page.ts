import { Component, OnInit, ViewChild, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FullCalendarModule, FullCalendarComponent } from '@fullcalendar/angular';
import { CalendarOptions, EventClickArg, EventInput } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin, { DateClickArg } from '@fullcalendar/interaction';
import frLocale from '@fullcalendar/core/locales/fr';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { TaskStore } from '../data/task.store';
import { TaskFormDialogComponent } from '../components/task-form-dialog.component';
import { Task, TaskFormResult, Priority } from '../../../core/models/task.model';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, FullCalendarModule, MatButtonModule, MatIconModule],
  template: `
    <div class="p-6 max-w-6xl mx-auto">

      <!-- Header -->
      <div class="flex items-center justify-between mb-5">
        <div>
          <h1 class="text-2xl font-extrabold text-gray-900">📅 Calendrier</h1>
          <p class="text-gray-400 text-sm mt-0.5">Cliquez sur un jour pour ajouter une tâche</p>
        </div>
        <button mat-raised-button (click)="openNewTask()"
          style="background:linear-gradient(135deg,#6d28d9,#a855f7);color:white;border-radius:10px;font-weight:700;box-shadow:0 4px 16px rgba(109,40,217,0.3);">
          <mat-icon>add</mat-icon> Nouvelle tâche
        </button>
      </div>

      <!-- Legend -->
      <div class="flex gap-5 mb-4 flex-wrap">
        <div class="flex items-center gap-1.5">
          <span class="w-3 h-3 rounded-full" style="background:#ef4444;display:inline-block"></span>
          <span class="text-xs text-gray-500 font-medium">Haute priorité</span>
        </div>
        <div class="flex items-center gap-1.5">
          <span class="w-3 h-3 rounded-full" style="background:#f97316;display:inline-block"></span>
          <span class="text-xs text-gray-500 font-medium">Priorité moyenne</span>
        </div>
        <div class="flex items-center gap-1.5">
          <span class="w-3 h-3 rounded-full" style="background:#22c55e;display:inline-block"></span>
          <span class="text-xs text-gray-500 font-medium">Basse priorité</span>
        </div>
        <div class="flex items-center gap-1.5">
          <span class="w-3 h-3 rounded-full" style="background:#9ca3af;display:inline-block"></span>
          <span class="text-xs text-gray-500 font-medium">Terminée</span>
        </div>
      </div>

      <!-- Calendar -->
      <div class="bg-white rounded-2xl shadow-sm overflow-hidden" style="border:1px solid #e5e7eb;">
        <full-calendar #calendar [options]="calendarOptions"></full-calendar>
      </div>
    </div>
  `,
  styles: [`
    :host ::ng-deep .fc { font-family: inherit; }
    :host ::ng-deep .fc-toolbar-title {
      font-size: 1.1rem; font-weight: 800; text-transform: capitalize;
    }
    :host ::ng-deep .fc-button-primary {
      background: #6d28d9 !important;
      border-color: #6d28d9 !important;
      border-radius: 8px !important;
      font-weight: 600 !important;
      box-shadow: none !important;
    }
    :host ::ng-deep .fc-button-primary:hover {
      background: #5b21b6 !important; border-color: #5b21b6 !important;
    }
    :host ::ng-deep .fc-button-primary:not(:disabled).fc-button-active {
      background: #4c1d95 !important; border-color: #4c1d95 !important;
    }
    :host ::ng-deep .fc-daygrid-event {
      border-radius: 6px !important; font-size: 12px;
      padding: 2px 5px; cursor: pointer; border: none !important;
    }
    :host ::ng-deep .fc-event-title { font-weight: 600; }
    :host ::ng-deep .fc-day-today .fc-daygrid-day-number {
      background: #6d28d9; color: white; border-radius: 50%;
      width: 24px; height: 24px; display: flex; align-items: center;
      justify-content: center; font-weight: 700;
    }
    :host ::ng-deep .fc-day-today { background: rgba(109,40,217,0.04) !important; }
    :host ::ng-deep .fc-col-header-cell {
      font-weight: 700; font-size: 13px; text-transform: capitalize; padding: 10px 0;
    }
    :host ::ng-deep .fc-daygrid-day:hover { background: rgba(109,40,217,0.03); cursor: pointer; }
    :host ::ng-deep .done-event { opacity: 0.55; text-decoration: line-through; }
    :host ::ng-deep .fc-toolbar { padding: 16px 20px; }
    :host ::ng-deep .fc-daygrid-day-number { padding: 6px 8px; font-size: 13px; }
    :host ::ng-deep .fc-more-link { color: #6d28d9; font-weight: 700; font-size: 11px; }
  `]
})
export class CalendarPage implements OnInit {
  @ViewChild('calendar') calendarRef!: FullCalendarComponent;

  private readonly store = inject(TaskStore);
  private readonly dialog = inject(MatDialog);

  readonly calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    locale: frLocale,
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,dayGridWeek'
    },
    buttonText: { today: "Aujourd'hui", month: 'Mois', week: 'Semaine' },
    height: 'auto',
    fixedWeekCount: false,
    dayMaxEvents: 4,
    displayEventTime: false,
    eventClick: (arg: EventClickArg) => this.onEventClick(arg),
    dateClick: (arg: DateClickArg) => this.onDateClick(arg),
    events: [],
  };

  constructor() {
    effect(() => {
      const events: EventInput[] = this.store.tasks().map(t => this.taskToEvent(t));
      const api = this.calendarRef?.getApi();
      if (api) {
        api.removeAllEventSources();
        api.addEventSource(events);
      } else {
        (this.calendarOptions as any).events = events;
      }
    });
  }

  ngOnInit(): void {
    this.store.loadTasks();
  }

  private taskToEvent(task: Task): EventInput {
    return {
      id: task.id,
      title: task.title,
      date: task.dueDate,
      backgroundColor: this.priorityColor(task.priority, task.status),
      borderColor: this.priorityColor(task.priority, task.status),
      classNames: task.status === 'DONE' ? ['done-event'] : [],
      extendedProps: { task },
    };
  }

  private priorityColor(priority: Priority, status: string): string {
    if (status === 'DONE') return '#9ca3af';
    switch (priority) {
      case 'HIGH':   return '#ef4444';
      case 'MEDIUM': return '#f97316';
      case 'LOW':    return '#22c55e';
    }
  }

  onEventClick(arg: EventClickArg): void {
    const task: Task = arg.event.extendedProps['task'];
    const ref = this.dialog.open(TaskFormDialogComponent, {
      data: { task }, maxWidth: '640px', width: '95vw'
    });
    ref.afterClosed().subscribe((result: TaskFormResult | undefined) => {
      if (result) this.store.updateTask(task.id, result.request);
    });
  }

  onDateClick(arg: DateClickArg): void {
    const ref = this.dialog.open(TaskFormDialogComponent, {
      data: { prefillDate: arg.date }, maxWidth: '640px', width: '95vw'
    });
    ref.afterClosed().subscribe((result: TaskFormResult | undefined) => {
      if (result) this.store.createTask(result.request);
    });
  }

  openNewTask(): void {
    const ref = this.dialog.open(TaskFormDialogComponent, {
      data: {}, maxWidth: '640px', width: '95vw'
    });
    ref.afterClosed().subscribe((result: TaskFormResult | undefined) => {
      if (result) this.store.createTask(result.request);
    });
  }
}

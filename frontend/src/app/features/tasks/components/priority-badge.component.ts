import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Priority, PRIORITY_LABELS } from '../../../core/models/task.model';

@Component({
  selector: 'app-priority-badge',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  template: `
    <span class="priority-badge" [class]="'priority-' + priority.toLowerCase()"
          [attr.aria-label]="'Priority: ' + label">
      <mat-icon style="font-size: 14px; width: 14px; height: 14px; line-height: 14px;">
        {{ icon }}
      </mat-icon>
      {{ label }}
    </span>
  `
})
export class PriorityBadgeComponent {
  @Input({ required: true }) priority!: Priority;

  get label(): string {
    return PRIORITY_LABELS[this.priority];
  }

  get icon(): string {
    switch (this.priority) {
      case 'HIGH': return 'arrow_upward';
      case 'MEDIUM': return 'remove';
      case 'LOW': return 'arrow_downward';
    }
  }
}

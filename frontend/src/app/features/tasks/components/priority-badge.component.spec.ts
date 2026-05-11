import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PriorityBadgeComponent } from './priority-badge.component';

describe('PriorityBadgeComponent', () => {
  let fixture: ComponentFixture<PriorityBadgeComponent>;
  let component: PriorityBadgeComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PriorityBadgeComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(PriorityBadgeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    component.priority = 'HIGH';
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should show "High" label for HIGH priority', () => {
    component.priority = 'HIGH';
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('High');
  });

  it('should show "Medium" label for MEDIUM priority', () => {
    component.priority = 'MEDIUM';
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('Medium');
  });

  it('should show "Low" label for LOW priority', () => {
    component.priority = 'LOW';
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('Low');
  });

  it('should have aria-label with priority', () => {
    component.priority = 'HIGH';
    fixture.detectChanges();
    const badge: HTMLElement = fixture.nativeElement.querySelector('.priority-badge');
    expect(badge.getAttribute('aria-label')).toBe('Priority: High');
  });

  it('should apply priority-high CSS class for HIGH', () => {
    component.priority = 'HIGH';
    fixture.detectChanges();
    const badge: HTMLElement = fixture.nativeElement.querySelector('.priority-badge');
    expect(badge.classList).toContain('priority-high');
  });

  it('should apply priority-low CSS class for LOW', () => {
    component.priority = 'LOW';
    fixture.detectChanges();
    const badge: HTMLElement = fixture.nativeElement.querySelector('.priority-badge');
    expect(badge.classList).toContain('priority-low');
  });

  it('should return upward arrow icon for HIGH priority', () => {
    component.priority = 'HIGH';
    expect(component.icon).toBe('arrow_upward');
  });

  it('should return downward arrow icon for LOW priority', () => {
    component.priority = 'LOW';
    expect(component.icon).toBe('arrow_downward');
  });
});

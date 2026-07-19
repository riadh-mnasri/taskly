import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';
import { Task } from '../../../core/models/task.model';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;

  const mockTask: Task = {
    id: '123',
    title: 'Math homework',
    description: null,
    subject: 'Mathematics',
    priority: 'HIGH',
    status: 'TODO',
    type: 'HOMEWORK',
    dueDate: '2026-05-15',
    estimatedDurationMinutes: 45,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService]
    });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should GET tasks from API', () => {
    service.list().subscribe(tasks => {
      expect(tasks).toEqual([mockTask]);
    });

    const req = httpMock.expectOne('/api/v1/tasks');
    expect(req.request.method).toBe('GET');
    req.flush([mockTask]);
  });

  it('should POST to create a task', () => {
    const createReq = {
      title: 'New task',
      description: null,
      subject: 'Science',
      priority: 'MEDIUM' as const,
      type: 'HOMEWORK' as const,
      dueDate: '2026-05-20',
      estimatedDurationMinutes: 30
    };

    service.create(createReq).subscribe(task => {
      expect(task.title).toBe('New task');
    });

    const req = httpMock.expectOne('/api/v1/tasks');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.title).toBe('New task');
    req.flush({ ...mockTask, title: 'New task' });
  });

  it('should DELETE a task', () => {
    service.delete('123').subscribe();

    const req = httpMock.expectOne('/api/v1/tasks/123');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should PATCH task status', () => {
    service.updateStatus('123', 'DONE').subscribe(task => {
      expect(task.status).toBe('DONE');
    });

    const req = httpMock.expectOne('/api/v1/tasks/123/status');
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body.status).toBe('DONE');
    req.flush({ ...mockTask, status: 'DONE' });
  });

  it('should apply filter params to GET request', () => {
    service.list({ priority: 'HIGH', status: 'TODO' }).subscribe();

    const req = httpMock.expectOne(r =>
      r.url === '/api/v1/tasks' &&
      r.params.get('priority') === 'HIGH' &&
      r.params.get('status') === 'TODO'
    );
    req.flush([]);
  });
});

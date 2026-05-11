import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, CreateTaskRequest, UpdateTaskRequest, TaskFilter, TaskStatus } from '../../../core/models/task.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/v1/tasks`;

  list(filter?: TaskFilter): Observable<Task[]> {
    let params = new HttpParams();
    if (filter?.priority) params = params.set('priority', filter.priority);
    if (filter?.status) params = params.set('status', filter.status);
    if (filter?.subject) params = params.set('subject', filter.subject);
    if (filter?.sort) params = params.set('sort', filter.sort);
    if (filter?.direction) params = params.set('direction', filter.direction);
    return this.http.get<Task[]>(this.baseUrl, { params });
  }

  getById(id: string): Observable<Task> {
    return this.http.get<Task>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateTaskRequest): Observable<Task> {
    return this.http.post<Task>(this.baseUrl, request);
  }

  update(id: string, request: UpdateTaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updateStatus(id: string, status: TaskStatus): Observable<Task> {
    return this.http.patch<Task>(`${this.baseUrl}/${id}/status`, { status });
  }
}

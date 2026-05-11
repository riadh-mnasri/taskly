import { inject } from '@angular/core';
import { signalStore, withState, withMethods, withComputed, patchState } from '@ngrx/signals';
import { computed } from '@angular/core';
import { Task, TaskFilter, CreateTaskRequest, UpdateTaskRequest, TaskStatus } from '../../../core/models/task.model';
import { TaskService } from './task.service';
import { lastValueFrom } from 'rxjs';

interface TaskState {
  tasks: Task[];
  loading: boolean;
  error: string | null;
  filter: TaskFilter;
}

const initialState: TaskState = {
  tasks: [],
  loading: false,
  error: null,
  filter: {}
};

export const TaskStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed((store) => ({
    todoTasks: computed(() => store.tasks().filter(t => t.status === 'TODO')),
    inProgressTasks: computed(() => store.tasks().filter(t => t.status === 'IN_PROGRESS')),
    doneTasks: computed(() => store.tasks().filter(t => t.status === 'DONE')),
    todayTasks: computed(() => {
      const today = new Date().toISOString().split('T')[0];
      return store.tasks().filter(t => t.dueDate === today && t.status !== 'DONE');
    }),
    thisWeekTasks: computed(() => {
      const today = new Date();
      const weekEnd = new Date(today);
      weekEnd.setDate(today.getDate() + 7);
      const todayStr = today.toISOString().split('T')[0];
      const weekEndStr = weekEnd.toISOString().split('T')[0];
      return store.tasks().filter(t =>
        t.dueDate >= todayStr && t.dueDate <= weekEndStr && t.status !== 'DONE'
      );
    }),
    urgentTasks: computed(() =>
      store.tasks().filter(t => t.priority === 'HIGH' && t.status !== 'DONE')
    )
  })),
  withMethods((store, taskService = inject(TaskService)) => ({
    async loadTasks(filter?: TaskFilter): Promise<void> {
      patchState(store, { loading: true, error: null });
      try {
        const tasks = await lastValueFrom(taskService.list(filter));
        patchState(store, { tasks, loading: false });
      } catch (err) {
        patchState(store, { loading: false, error: 'Failed to load tasks' });
      }
    },

    async createTask(request: CreateTaskRequest): Promise<Task> {
      patchState(store, { loading: true, error: null });
      try {
        const task = await lastValueFrom(taskService.create(request));
        patchState(store, { tasks: [...store.tasks(), task], loading: false });
        return task;
      } catch (err) {
        patchState(store, { loading: false, error: 'Failed to create task' });
        throw err;
      }
    },

    async updateTask(id: string, request: UpdateTaskRequest): Promise<Task> {
      patchState(store, { loading: true, error: null });
      try {
        const updated = await lastValueFrom(taskService.update(id, request));
        patchState(store, {
          tasks: store.tasks().map(t => t.id === id ? updated : t),
          loading: false
        });
        return updated;
      } catch (err) {
        patchState(store, { loading: false, error: 'Failed to update task' });
        throw err;
      }
    },

    async deleteTask(id: string): Promise<void> {
      patchState(store, { loading: true, error: null });
      try {
        await lastValueFrom(taskService.delete(id));
        patchState(store, {
          tasks: store.tasks().filter(t => t.id !== id),
          loading: false
        });
      } catch (err) {
        patchState(store, { loading: false, error: 'Failed to delete task' });
        throw err;
      }
    },

    async updateTaskStatus(id: string, status: TaskStatus): Promise<void> {
      try {
        const updated = await lastValueFrom(taskService.updateStatus(id, status));
        patchState(store, {
          tasks: store.tasks().map(t => t.id === id ? updated : t)
        });
      } catch (err) {
        patchState(store, { error: 'Failed to update task status' });
        throw err;
      }
    },

    setFilter(filter: TaskFilter): void {
      patchState(store, { filter });
    }
  }))
);

export type Priority = 'HIGH' | 'MEDIUM' | 'LOW';
export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';
export type TaskType = 'HOMEWORK' | 'EXAM' | 'PROJECT' | 'PERSONAL' | 'HEALTH';

export interface Task {
  id: string;
  title: string;
  description: string | null;
  subject: string;
  priority: Priority;
  status: TaskStatus;
  type: TaskType;
  dueDate: string; // ISO date string YYYY-MM-DD
  estimatedDurationMinutes: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTaskRequest {
  title: string;
  description: string | null;
  subject: string;
  priority: Priority;
  type: TaskType;
  dueDate: string;
  estimatedDurationMinutes: number;
}

export interface UpdateTaskRequest extends CreateTaskRequest {}

export interface TaskFormResult {
  request: CreateTaskRequest;
  newStatus?: TaskStatus;
}

export interface TaskFilter {
  priority?: Priority;
  status?: TaskStatus;
  subject?: string;
  sort?: string;
  direction?: 'asc' | 'desc';
}

export const PRIORITY_LABELS: Record<Priority, string> = {
  HIGH: 'High',
  MEDIUM: 'Medium',
  LOW: 'Low'
};

export const TASK_TYPE_LABELS: Record<TaskType, string> = {
  HOMEWORK: 'Homework',
  EXAM: 'Exam',
  PROJECT: 'Project',
  PERSONAL: 'Personal',
  HEALTH: 'Health'
};

export const TASK_STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: 'To Do',
  IN_PROGRESS: 'In Progress',
  DONE: 'Done'
};

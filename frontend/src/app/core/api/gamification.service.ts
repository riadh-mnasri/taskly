import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface BadgeResponse {
  code: string;
  label: string;
  description: string;
  emoji: string;
  earnedAt: string;
}

export interface UserProgressResponse {
  xp: number;
  level: number;
  levelName: string;
  xpForCurrentLevel: number;
  xpForNextLevel: number;
  totalTasksDone: number;
  badges: BadgeResponse[];
}

export interface DayStatResponse {
  date: string;
  count: number;
  xpGained: number;
}

export interface StatsResponse {
  last7Days: DayStatResponse[];
  last30Days: DayStatResponse[];
  streak: number;
  xpThisWeek: number;
  xpLastWeek: number;
  completionsThisWeek: number;
}

@Injectable({ providedIn: 'root' })
export class GamificationService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api/v1/gamification`;

  getProgress(): Observable<UserProgressResponse> {
    return this.http.get<UserProgressResponse>(`${this.apiUrl}/me`);
  }

  getStats(): Observable<StatsResponse> {
    return this.http.get<StatsResponse>(`${this.apiUrl}/me/stats`);
  }
}

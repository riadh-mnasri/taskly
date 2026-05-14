import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

@Injectable({ providedIn: 'root' })
export class GamificationService {
  private readonly http = inject(HttpClient);

  getProgress(): Observable<UserProgressResponse> {
    return this.http.get<UserProgressResponse>('/api/v1/gamification/me');
  }
}

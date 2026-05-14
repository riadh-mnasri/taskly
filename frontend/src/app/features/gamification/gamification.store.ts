import { inject } from '@angular/core';
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';
import { lastValueFrom } from 'rxjs';
import { GamificationService, UserProgressResponse } from '../../core/api/gamification.service';

interface GamificationState {
  progress: UserProgressResponse | null;
  newBadges: string[];
}

const initialState: GamificationState = {
  progress: null,
  newBadges: []
};

export const GamificationStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store, service = inject(GamificationService)) => ({
    async loadProgress(): Promise<void> {
      try {
        const prev = store.progress();
        const progress = await lastValueFrom(service.getProgress());
        const newBadges = prev
          ? progress.badges
              .filter(b => !prev.badges.some(pb => pb.code === b.code))
              .map(b => `${b.emoji} ${b.label}`)
          : [];
        patchState(store, { progress, newBadges });
      } catch {
        // silently ignore — gamification is non-critical
      }
    },
    clearNewBadges(): void {
      patchState(store, { newBadges: [] });
    }
  }))
);

import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, inject, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { GamificationService, StatsResponse } from '../../core/api/gamification.service';
import {
  Chart, BarController, BarElement, CategoryScale, LinearScale, Tooltip, Legend,
  LineController, LineElement, PointElement, Filler
} from 'chart.js';

Chart.register(
  BarController, BarElement, CategoryScale, LinearScale, Tooltip, Legend,
  LineController, LineElement, PointElement, Filler
);

const FR_DAYS = ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'];

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  template: `
    <div class="p-6 max-w-4xl mx-auto">

      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-2xl font-extrabold text-gray-900">📊 Statistiques</h1>
        <p class="text-gray-400 text-sm mt-0.5">Ta progression sur les 7 derniers jours</p>
      </div>

      <ng-container *ngIf="stats; else loading">

        <!-- KPI cards -->
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-8">

          <!-- Streak -->
          <div class="rounded-2xl p-4 flex flex-col items-center justify-center text-center"
               style="background:linear-gradient(135deg,#7c3aed,#a855f7);">
            <div class="text-4xl mb-1">🔥</div>
            <div class="text-3xl font-black text-white">{{ stats.streak }}</div>
            <div class="text-purple-200 text-xs font-semibold mt-0.5">Jours consécutifs</div>
          </div>

          <!-- Completions this week -->
          <div class="rounded-2xl p-4 flex flex-col items-center justify-center text-center"
               style="background:linear-gradient(135deg,#1e40af,#3b82f6);">
            <div class="text-4xl mb-1">✅</div>
            <div class="text-3xl font-black text-white">{{ stats.completionsThisWeek }}</div>
            <div class="text-blue-200 text-xs font-semibold mt-0.5">Tâches cette semaine</div>
          </div>

          <!-- XP this week -->
          <div class="rounded-2xl p-4 flex flex-col items-center justify-center text-center"
               style="background:linear-gradient(135deg,#065f46,#10b981);">
            <div class="text-4xl mb-1">⚡</div>
            <div class="text-3xl font-black text-white">{{ stats.xpThisWeek }}</div>
            <div class="text-emerald-200 text-xs font-semibold mt-0.5">XP cette semaine</div>
          </div>

          <!-- XP trend -->
          <div class="rounded-2xl p-4 flex flex-col items-center justify-center text-center"
               [style.background]="xpTrend >= 0
                 ? 'linear-gradient(135deg,#92400e,#f59e0b)'
                 : 'linear-gradient(135deg,#7f1d1d,#ef4444)'">
            <div class="text-4xl mb-1">{{ xpTrend >= 0 ? '📈' : '📉' }}</div>
            <div class="text-3xl font-black text-white">
              {{ xpTrend >= 0 ? '+' : '' }}{{ xpTrend }}
            </div>
            <div class="text-yellow-100 text-xs font-semibold mt-0.5">vs semaine préc.</div>
          </div>
        </div>

        <!-- Bar chart -->
        <div class="bg-white rounded-2xl shadow-sm p-6" style="border:1px solid #e5e7eb;">
          <h2 class="font-bold text-gray-700 mb-4 text-sm flex items-center gap-2">
            <mat-icon style="font-size:18px;width:18px;height:18px;color:#7c3aed;">bar_chart</mat-icon>
            Tâches complétées — 7 derniers jours
          </h2>
          <div style="position:relative;height:220px;">
            <canvas #chartCanvas></canvas>
          </div>
        </div>

        <!-- Trend line chart (30 days) -->
        <div class="bg-white rounded-2xl shadow-sm p-6 mt-6" style="border:1px solid #e5e7eb;">
          <h2 class="font-bold text-gray-700 mb-4 text-sm flex items-center gap-2">
            <mat-icon style="font-size:18px;width:18px;height:18px;color:#7c3aed;">show_chart</mat-icon>
            Tendance de productivité — 30 derniers jours
          </h2>
          <div style="position:relative;height:260px;">
            <canvas #trendCanvas></canvas>
          </div>
        </div>

        <!-- Streak message -->
        <div *ngIf="stats.streak >= 3"
             class="mt-4 rounded-2xl p-4 flex items-center gap-3"
             style="background:linear-gradient(135deg,#fffbeb,#fef3c7);border:1px solid #fde68a;">
          <span class="text-3xl">🔥</span>
          <div>
            <p class="font-bold text-amber-800">{{ streakMessage }}</p>
            <p class="text-amber-600 text-sm">Continue comme ça !</p>
          </div>
        </div>

        <div *ngIf="stats.streak === 0"
             class="mt-4 rounded-2xl p-4 flex items-center gap-3"
             style="background:linear-gradient(135deg,#f5f3ff,#ede9fe);border:1px solid #ddd6fe;">
          <span class="text-3xl">💡</span>
          <div>
            <p class="font-bold text-purple-800">Lance ta série !</p>
            <p class="text-purple-600 text-sm">Complète une tâche aujourd'hui pour démarrer une série.</p>
          </div>
        </div>

      </ng-container>

      <ng-template #loading>
        <div class="flex justify-center items-center h-48 text-gray-400">Chargement...</div>
      </ng-template>

    </div>
  `
})
export class StatsPage implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('chartCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('trendCanvas') trendCanvasRef!: ElementRef<HTMLCanvasElement>;

  private readonly service = inject(GamificationService);
  private readonly cdr = inject(ChangeDetectorRef);
  stats: StatsResponse | null = null;
  private chart: Chart | null = null;
  private trendChart: Chart | null = null;

  get xpTrend(): number {
    return (this.stats?.xpThisWeek ?? 0) - (this.stats?.xpLastWeek ?? 0);
  }

  get streakMessage(): string {
    const s = this.stats?.streak ?? 0;
    if (s >= 7) return `Incroyable ! ${s} jours de suite !`;
    if (s >= 5) return `Super ${s} jours consécutifs !`;
    return `${s} jours de suite, bravo !`;
  }

  ngOnInit(): void {
    this.service.getStats().subscribe(stats => {
      this.stats = stats;
      this.cdr.detectChanges();
      this.renderChart();
      this.renderTrendChart();
    });
  }

  ngAfterViewInit(): void {}

  ngOnDestroy(): void {
    this.chart?.destroy();
    this.trendChart?.destroy();
  }

  private renderChart(): void {
    if (!this.canvasRef || !this.stats) return;

    const labels = this.stats.last7Days.map(d => {
      const date = new Date(d.date + 'T00:00:00');
      return FR_DAYS[date.getDay()];
    });
    const data = this.stats.last7Days.map(d => d.count);
    const maxVal = Math.max(...data, 1);

    this.chart?.destroy();
    this.chart = new Chart(this.canvasRef.nativeElement, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          data,
          backgroundColor: data.map(v =>
            v === 0 ? 'rgba(196,181,253,0.3)' : 'rgba(124,58,237,0.85)'
          ),
          borderRadius: 8,
          borderSkipped: false,
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false }, tooltip: {
          callbacks: {
            label: ctx => { const v = ctx.parsed.y ?? 0; return ` ${v} tâche${v > 1 ? 's' : ''}`; }
          }
        }},
        scales: {
          x: { grid: { display: false }, border: { display: false } },
          y: {
            beginAtZero: true,
            max: maxVal + 1,
            ticks: { stepSize: 1, precision: 0 },
            grid: { color: 'rgba(0,0,0,0.05)' },
            border: { display: false }
          }
        }
      }
    });
  }

  private renderTrendChart(): void {
    if (!this.trendCanvasRef || !this.stats) return;

    const days = this.stats.last30Days;
    const labels = days.map(d => {
      const date = new Date(d.date + 'T00:00:00');
      return `${date.getDate()}/${date.getMonth() + 1}`;
    });
    const completions = days.map(d => d.count);
    const xp = days.map(d => d.xpGained);

    this.trendChart?.destroy();
    this.trendChart = new Chart(this.trendCanvasRef.nativeElement, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'Tâches complétées',
            data: completions,
            borderColor: '#7c3aed',
            backgroundColor: 'rgba(124,58,237,0.1)',
            borderWidth: 2,
            pointRadius: 0,
            tension: 0.3,
            fill: true,
            yAxisID: 'y'
          },
          {
            label: 'XP gagné',
            data: xp,
            borderColor: '#10b981',
            backgroundColor: 'rgba(16,185,129,0.1)',
            borderWidth: 2,
            pointRadius: 0,
            tension: 0.3,
            fill: true,
            yAxisID: 'y1'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: {
          legend: { position: 'top', labels: { boxWidth: 12, font: { size: 11 } } }
        },
        scales: {
          x: {
            grid: { display: false },
            border: { display: false },
            ticks: { maxTicksLimit: 10 }
          },
          y: {
            beginAtZero: true,
            position: 'left',
            ticks: { stepSize: 1, precision: 0 },
            grid: { color: 'rgba(0,0,0,0.05)' },
            border: { display: false },
            title: { display: true, text: 'Tâches', color: '#7c3aed', font: { size: 11 } }
          },
          y1: {
            beginAtZero: true,
            position: 'right',
            grid: { display: false },
            border: { display: false },
            title: { display: true, text: 'XP', color: '#10b981', font: { size: 11 } }
          }
        }
      }
    });
  }
}

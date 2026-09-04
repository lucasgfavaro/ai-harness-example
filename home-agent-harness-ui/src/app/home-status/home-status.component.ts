import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { Subscription, interval, startWith, switchMap } from 'rxjs';
import { HomeStatusService } from './home-status.service';
import { HomeSummary } from './home-status.models';

@Component({
  selector: 'app-home-status',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home-status.component.html',
  styleUrl: './home-status.component.scss'
})
export class HomeStatusComponent implements OnInit, OnDestroy {
  summary = signal<HomeSummary | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  private subscription?: Subscription;

  constructor(private readonly homeStatusService: HomeStatusService) {}

  ngOnInit(): void {
    this.subscription = interval(5000)
      .pipe(
        startWith(0),
        switchMap(() => {
          this.loading.set(true);
          return this.homeStatusService.fetchSummary();
        })
      )
      .subscribe({
        next: (summary) => {
          this.summary.set(summary);
          this.error.set(null);
          this.loading.set(false);
        },
        error: (err) => {
          const message = err?.error?.message ?? err?.message ?? 'Error al obtener el estado del hogar';
          this.error.set(message);
          this.loading.set(false);
        }
      });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  refresh(): void {
    this.loading.set(true);
    this.homeStatusService.fetchSummary().subscribe({
      next: (summary) => {
        this.summary.set(summary);
        this.error.set(null);
        this.loading.set(false);
      },
      error: (err) => {
        const message = err?.error?.message ?? err?.message ?? 'Error al obtener el estado del hogar';
        this.error.set(message);
        this.loading.set(false);
      }
    });
  }
}


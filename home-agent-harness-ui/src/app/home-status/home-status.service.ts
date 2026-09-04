import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HomeSummary } from './home-status.models';

@Injectable({ providedIn: 'root' })
export class HomeStatusService {
  private readonly baseUrl = '/api/home/summary';

  constructor(private readonly http: HttpClient) {}

  fetchSummary(): Observable<HomeSummary> {
    return this.http.get<HomeSummary>(this.baseUrl);
  }
}


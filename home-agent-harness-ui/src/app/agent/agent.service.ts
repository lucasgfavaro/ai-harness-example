import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AgentRequest, AgentResult } from './agent.models';

@Injectable({ providedIn: 'root' })
export class AgentService {
  private readonly baseUrl = '/agent';

  constructor(private readonly http: HttpClient) {}

  run(request: string, dryRun: boolean): Observable<AgentResult> {
    const body: AgentRequest = { request, dryRun };
    return this.http.post<AgentResult>(this.baseUrl, body);
  }
}


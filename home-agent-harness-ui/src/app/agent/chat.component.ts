import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AgentService } from './agent.service';
import { ChatMessage } from './agent.models';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent {
  messages = signal<ChatMessage[]>([]);
  input = '';
  dryRun = true;
  loading = signal(false);
  error = signal<string | null>(null);

  constructor(private readonly agentService: AgentService) {}

  send(): void {
    const text = this.input.trim();
    if (!text || this.loading()) {
      return;
    }

    this.messages.update((msgs) => [...msgs, { role: 'user', text }]);
    this.input = '';
    this.loading.set(true);
    this.error.set(null);

    this.agentService.run(text, this.dryRun).subscribe({
      next: (result) => {
        this.messages.update((msgs) => [
          ...msgs,
          { role: 'agent', text: result.answer, steps: result.steps }
        ]);
        this.loading.set(false);
      },
      error: (err) => {
        const message = err?.error?.message ?? err?.message ?? 'Error desconocido al contactar al agente';
        this.error.set(message);
        this.loading.set(false);
      }
    });
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }
}


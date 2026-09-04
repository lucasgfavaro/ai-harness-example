import { Component } from '@angular/core';
import { ChatComponent } from './agent/chat.component';
import { HomeStatusComponent } from './home-status/home-status.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ChatComponent, HomeStatusComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'home-agent-harness-ui';
}

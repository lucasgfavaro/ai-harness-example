export interface AgentRequest {
  request: string;
  dryRun: boolean;
}

export interface AgentResult {
  answer: string;
  dryRun: boolean;
  steps: string[];
}

export interface ChatMessage {
  role: 'user' | 'agent';
  text: string;
  steps?: string[];
}


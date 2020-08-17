export type RemoteData<E, D> =
  | { status: 'NOT_ASKED' }
  | { status: 'LOADING' }
  | { status: 'FAILURE'; error: E }
  | { status: 'SUCCESS'; data: D };

export interface Execution {
  executionId: string;
  executionDate: string;
  executedModelName: string;
  executionType: string;
  executionSucceeded: boolean;
  executorName: string;
}

export interface Executions {
  headers: Execution[];
  total: number;
}

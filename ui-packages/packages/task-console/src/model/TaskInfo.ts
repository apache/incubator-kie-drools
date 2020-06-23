import { UserTaskInstance } from '../graphql/types';

export interface TaskInfo {
  readonly task: UserTaskInstance;
  readonly processInstanceEndPoint: string;

  getTaskEndPoint(): string;
}

export class TaskInfoImpl implements TaskInfo {
  public readonly task: UserTaskInstance;
  public readonly processInstanceEndPoint: string;

  constructor(task: UserTaskInstance, processInstanceEndPoint: string) {
    this.task = task;
    this.processInstanceEndPoint = processInstanceEndPoint;
  }

  getTaskEndPoint(): string {
    return `${this.processInstanceEndPoint}/${this.task.processInstanceId}/${this.task.referenceName}/${this.task.id}`;
  }
}

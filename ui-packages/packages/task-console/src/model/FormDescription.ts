export interface FormActionDescription {
  name: string;
  phase?: string;
  outputs: string[];
  primary?: boolean;
}

export interface FormDescription {
  schema: any;
  actions?: FormActionDescription[];
}

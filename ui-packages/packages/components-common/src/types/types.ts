/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
export enum SCHEMA_VERSION {
  DRAFT_7 = 'http://json-schema.org/draft-07/schema#',
  DRAFT_2019_09 = 'https://json-schema.org/draft/2019-09/schema'
}
export interface FormRendererApi {
  doReset: () => void;
}

export interface FormFilter {
  formNames: string[];
}

export enum FormType {
  HTML = 'HTML',
  TSX = 'TSX'
}

export type FormInfo = {
  type: FormType;
  name: string;
  lastModified: Date;
};

export type FormConfiguration = {
  resources: FormResources;
  schema: string;
};

export type FormResources = {
  scripts: Record<string, string>;
  styles: Record<string, string>;
};

export interface FormDisplayerInitArgs {
  form: Form;
  data?: any;
  context?: Record<string, any>;
}

export type FormSubmitContext = {
  params?: Record<string, string>;
};

export enum FormOpenedState {
  OPENED = 'opened',
  ERROR = 'error'
}

export type FormOpened = {
  state: FormOpenedState;
  size: FormSize;
};

export type FormSize = {
  width: number;
  height: number;
};

export enum FormSubmitResponseType {
  SUCCESS = 'success',
  FAILURE = 'failure'
}

export type FormSubmitResponse = {
  type: FormSubmitResponseType;
  info: any;
};

export interface Association {
  origin: string;
  envelopeServerId: string;
}

export interface Form {
  formInfo: FormInfo;
  source: string;
  configuration: FormConfiguration;
}

export interface FormContent {
  source: string;
  configuration: FormConfiguration;
}

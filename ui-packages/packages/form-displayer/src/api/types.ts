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

export interface FormResources {
  scripts: {
    [key: string]: string;
  };
  styles: {
    [key: string]: string;
  };
}
interface FormConfiguration {
  schema: string;
  resources: FormResources;
}

export interface Form {
  formInfo: FormInfo;
  source: string;
  configuration: FormConfiguration;
}

export enum FormType {
  HTML = 'HTML',
  TSX = 'TSX'
}

export interface FormInfo {
  name: string;
  type: FormType;
  lastModified: Date;
}

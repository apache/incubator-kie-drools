/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export interface FormDisplayerEnvelopeApi {
  formDisplayer__init(
    association: Association,
    initArgs: FormDisplayerInitArgs
  ): Promise<void>;
  formDisplayer__notify(formContent: FormArgs): Promise<void>;
}

export interface Association {
  origin: string;
  envelopeServerId: string;
}

export interface FormDisplayerInitArgs {
  formContent: FormArgs;
  formData: FormInfo;
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

export interface sourceArgs {
  'source-content': string;
}
export interface FormArgs {
  source: sourceArgs;
  name: string;
  formConfiguration: FormConfiguration;
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

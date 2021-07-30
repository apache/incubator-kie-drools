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

import { ProcessInstanceState } from '@kogito-apps/management-console-shared';
export interface ProcessListEnvelopeApi {
  processList__init(
    association: Association,
    initArgs: ProcessListInitArgs
  ): Promise<void>;
}

export interface ProcessInstanceFilter {
  status: ProcessInstanceState[];
  businessKey?: string[];
}

export interface Association {
  origin: string;
  envelopeServerId: string;
}

export enum OrderBy {
  ASC = 'ASC',
  DESC = 'DESC'
}
export interface SortBy {
  processName?: OrderBy;
  state?: OrderBy;
  start?: OrderBy;
  lastUpdate?: OrderBy;
}
export interface QueryPage {
  offset: number;
  limit: number;
}

export interface ProcessListState {
  filters: ProcessInstanceFilter;
  sortBy: SortBy;
}

export interface ProcessListInitArgs {
  initialState: ProcessListState;
}

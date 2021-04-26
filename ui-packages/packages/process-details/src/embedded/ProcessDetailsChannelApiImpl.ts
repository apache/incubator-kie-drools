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

import { ProcessDetailsChannelApi, ProcessDetailsDriver } from '../api';
import { ProcessInstance } from '@kogito-apps/management-console-shared';

export class ProcessDetailsChannelApiImpl implements ProcessDetailsChannelApi {
  constructor(private readonly driver: ProcessDetailsDriver) {}

  processDetails__initialLoad(): Promise<void> {
    return this.driver.initialLoad();
  }

  processDetails__processDetailsQuery(id: string): Promise<ProcessInstance> {
    return this.driver.processDetailsQuery(id);
  }
}

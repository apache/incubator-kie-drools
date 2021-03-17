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

import axios from 'axios';
import { GraphQL, User } from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;
import { getTaskEndpointSecurityParams } from '../../../Utils';

export class TaskFormSubmit {
  constructor(
    private readonly task: UserTaskInstance,
    private readonly user: User,
    private readonly successCallback: (phase: string) => void,
    private readonly errorCallback?: (
      phase: string,
      errorMessage?: string
    ) => void
  ) {}

  public submit = async (phase: string, data: any): Promise<void> => {
    const notifySuccess = () => {
      this.successCallback(phase);
    };

    const notifyError = (errorMessage: string) => {
      this.errorCallback(phase, errorMessage);
    };

    const endpoint = `${
      this.task.endpoint
    }?phase=${phase}&${getTaskEndpointSecurityParams(this.user)}`;

    try {
      const response = await axios.post(endpoint, data, {
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json'
        }
      });
      if (response.status === 200) {
        notifySuccess();
      } else {
        notifyError(response.data);
      }
    } catch (error) {
      const message = error.response ? error.response.data : error.message;
      notifyError(message);
    }
  };
}

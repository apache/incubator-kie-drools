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

import React from 'react';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { GraphQL } from '@kogito-apps/consoles-common';
import UserTaskInstance = GraphQL.UserTaskInstance;
import { EmbeddedTaskForm, TaskFormSchema } from '@kogito-apps/task-form';
import { useTaskFormGatewayApi } from '../../../channel/TaskForms/TaskFormContext';

interface Props {
  userTask: UserTaskInstance;
  onSubmitSuccess: (message: string) => void;
  onSubmitError: (message: string, details?: string) => void;
}

const TaskFormContainer: React.FC<Props & OUIAProps> = ({
  userTask,
  onSubmitSuccess,
  onSubmitError,
  ouiaId,
  ouiaSafe
}) => {
  const gatewayApi = useTaskFormGatewayApi();

  return (
    <EmbeddedTaskForm
      {...componentOuiaProps(ouiaId, 'task-form-container', ouiaSafe)}
      userTask={userTask}
      driver={{
        doSubmit(phase?: string, payload?: any): Promise<any> {
          return gatewayApi
            .doSubmit(userTask, phase, payload)
            .then(result => onSubmitSuccess(phase))
            .catch(error => {
              const message = error.response
                ? error.response.data
                : error.message;
              onSubmitError(phase, message);
            });
        },
        getTaskFormSchema(): Promise<TaskFormSchema> {
          return gatewayApi.getTaskFormSchema(userTask);
        }
      }}
      targetOrigin={'*'}
    />
  );
};

export default TaskFormContainer;

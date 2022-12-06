/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { WorkflowFormGatewayApi } from '../../../channel/WorkflowForm/WorkflowFormGatewayApi';
import { useWorkflowFormGatewayApi } from '../../../channel/WorkflowForm/WorkflowFormContext';
import {
  EmbeddedWorkflowForm,
  WorkflowDefinition
} from '@kogito-apps/workflow-form';

interface WorkflowFormContainerProps {
  workflowDefinitionData: WorkflowDefinition;
  onSubmitSuccess: (id: string) => void;
  onSubmitError: (details?: string) => void;
  onResetForm: () => void;
}
const WorkflowFormContainer: React.FC<WorkflowFormContainerProps &
  OUIAProps> = ({
    workflowDefinitionData,
    onSubmitSuccess,
    onSubmitError,
    onResetForm,
    ouiaId,
    ouiaSafe
  }) => {
    const gatewayApi: WorkflowFormGatewayApi = useWorkflowFormGatewayApi();
    
    return (
      <EmbeddedWorkflowForm
        {...componentOuiaProps(ouiaId, 'workflow-form-container', ouiaSafe)}
        driver={{
          async getCustomWorkflowSchema(): Promise<Record<string, any>> {
            return gatewayApi.getCustomWorkflowSchema(workflowDefinitionData.workflowName);
          },
          async startWorkflowCloudEvent(formData: any): Promise<void> {
            return gatewayApi
              .startWorkflowCloudEvent(formData)
              .then((id: string) => {
                onSubmitSuccess(`A workflow with business key ${id} was triggered successfully.`);
              })
              .catch(error => {
                console.error(error?.response);
                const message =
                  error?.response?.data?.message +
                  ' ' +
                  error?.response?.data?.cause ||
                  error?.message ||
                  'Unknown error. More details in the developer tools console.';
                onSubmitError(message);
              });
          },
          async resetBusinessKey() {
            onResetForm();
          },
          async startWorkflowRest(data: Record<string, any>, endpoint: string): Promise<void> {
            return gatewayApi.startWorkflowRest(data, endpoint).then((id: string) => {
              onSubmitSuccess(`A workflow with id ${id} was triggered successfully.`)
            }).catch((error) => {
              console.error(error?.response);
              const message =
                error?.response?.data?.message +
                ' ' +
                error?.response?.data?.cause ||
                error?.message ||
                'Unknown error. More details in the developer tools console.';
              onSubmitError(message);
            });
          }
        }}
        targetOrigin={'*'}
        workflowDefinition={{
          workflowName: workflowDefinitionData.workflowName,
          endpoint: workflowDefinitionData.endpoint
        }}
      />
    );
  };

export default WorkflowFormContainer;

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
import { ProcessFormGatewayApi } from '../../../channel/ProcessForm/ProcessFormGatewayApi';
import { useProcessFormGatewayApi } from '../../../channel/ProcessForm/ProcessFormContext';
import { EmbeddedProcessForm } from '@kogito-apps/process-form';
import { ProcessDefinition } from '@kogito-apps/process-definition-list';

interface ProcessFormContainerProps {
  processDefinitionData: ProcessDefinition;
  onSubmitSuccess: (id: string) => void;
  onSubmitError: (details?: string) => void;
}
const ProcessFormContainer: React.FC<ProcessFormContainerProps & OUIAProps> = ({
  processDefinitionData,
  onSubmitSuccess,
  onSubmitError,
  ouiaId,
  ouiaSafe
}) => {
  const gatewayApi: ProcessFormGatewayApi = useProcessFormGatewayApi();
  return (
    <EmbeddedProcessForm
      {...componentOuiaProps(ouiaId, 'process-form-container', ouiaSafe)}
      driver={{
        getProcessFormSchema(
          processDefinitionData: ProcessDefinition
        ): Promise<any> {
          return gatewayApi.getProcessFormSchema(processDefinitionData);
        },
        async startProcess(formData: any): Promise<void> {
          return gatewayApi
            .startProcess(formData, processDefinitionData)
            .then((id: string) => {
              gatewayApi.setBusinessKey('');
              onSubmitSuccess(id);
            })
            .catch((error) => {
              const message = error.response
                ? error.response.data
                : error.message;
              onSubmitError(message);
            });
        }
      }}
      targetOrigin={'*'}
      processDefinition={processDefinitionData}
    />
  );
};

export default ProcessFormContainer;

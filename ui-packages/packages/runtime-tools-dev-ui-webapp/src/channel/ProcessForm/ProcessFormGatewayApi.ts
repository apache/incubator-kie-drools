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
import { ProcessDefinition } from '@kogito-apps/process-definition-list';
import { getProcessSchema, getCustomForm, startProcessInstance } from '../apis';
import { Form } from '@kogito-apps/components-common/dist/types';

export interface ProcessFormGatewayApi {
  getProcessFormSchema(processDefinitionData: ProcessDefinition): Promise<any>;
  getCustomForm(processDefinitionData: ProcessDefinition): Promise<Form>;
  startProcess(
    formData: any,
    processDefinitionData: ProcessDefinition
  ): Promise<string>;
  setBusinessKey(bk: string): void;
  getBusinessKey(): string;
}

export class ProcessFormGatewayApiImpl implements ProcessFormGatewayApi {
  private businessKey: string;
  constructor() {
    this.businessKey = '';
  }

  setBusinessKey(bk: string): void {
    this.businessKey = bk;
  }

  getBusinessKey(): string {
    return this.businessKey;
  }

  getProcessFormSchema(
    processDefinitionData: ProcessDefinition
  ): Promise<Record<string, any>> {
    return getProcessSchema(processDefinitionData);
  }

  getCustomForm(processDefinitionData: ProcessDefinition): Promise<Form> {
    return getCustomForm(processDefinitionData);
  }

  startProcess(
    formData: any,
    processDefinitionData: ProcessDefinition
  ): Promise<string> {
    return startProcessInstance(
      formData,
      this.businessKey,
      processDefinitionData
    );
  }
}

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
import { getCustomWorkflowSchema, startWorkflowRest } from '../apis';

export interface WorkflowFormGatewayApi {
  setBusinessKey(bk: string): void;
  getBusinessKey(): string;
  getCustomWorkflowSchema(workflowName: string): Promise<Record<string, any>>;
  startWorkflow(endpoint: string, data: Record<string, any>): Promise<string>;
}

export class WorkflowFormGatewayApiImpl implements WorkflowFormGatewayApi {
  private businessKey: string;
  private readonly baseUrl: string;
  private readonly openApiPath: string;

  constructor(baseUrl: string, openApiPath: string) {
    this.businessKey = '';
    this.baseUrl = baseUrl;
    this.openApiPath = openApiPath;
  }

  setBusinessKey(bk: string): void {
    this.businessKey = bk;
  }

  getBusinessKey(): string {
    return this.businessKey;
  }

  getCustomWorkflowSchema(workflowName: string): Promise<Record<string, any>> {
    return getCustomWorkflowSchema(
      this.baseUrl,
      this.openApiPath,
      workflowName
    );
  }

  startWorkflow(endpoint: string, data: Record<string, any>): Promise<string> {
    return startWorkflowRest(data, endpoint, this.businessKey);
  }
}

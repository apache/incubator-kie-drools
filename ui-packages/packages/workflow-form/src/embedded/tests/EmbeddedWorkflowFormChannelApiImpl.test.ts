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

import { WorkflowFormDriver } from '../../api';
import { EmbeddedWorkflowFormChannelApiImpl } from '../EmbeddedWorkflowFormChannelApiImpl';
import { MockedWorkflowFormDriver } from './mocks/Mocks';

let driver: WorkflowFormDriver;
let api: EmbeddedWorkflowFormChannelApiImpl;

describe('EmbeddedWorkflowFormChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    driver = new MockedWorkflowFormDriver();
    api = new EmbeddedWorkflowFormChannelApiImpl(driver);
  });

  it('WorkflowForm__doSubmit', () => {
    const formJSON = {
      something: 'something'
    };
    api.workflowForm__startWorkflow(formJSON);

    expect(driver.startWorkflow).toHaveBeenCalledWith(formJSON);
  });
});

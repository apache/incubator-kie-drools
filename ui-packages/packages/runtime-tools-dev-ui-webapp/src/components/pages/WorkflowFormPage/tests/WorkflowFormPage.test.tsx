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
import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import WorkflowFormPage from '../WorkflowFormPage';
import { BrowserRouter } from 'react-router-dom';
import {
  WorkflowFormGatewayApi,
  WorkflowFormGatewayApiImpl
} from '../../../../channel/WorkflowForm/WorkflowFormGatewayApi';
import * as WorkflowFormContext from '../../../../channel/WorkflowForm/WorkflowFormContext';
import * as DevUIAppContext from '../../../contexts/DevUIAppContext';
import * as ProcessFormContext from '../../../../channel/ProcessForm/ProcessFormContext';

jest.mock('../../../containers/WorkflowFormContainer/WorkflowFormContainer');

jest.mock('@kogito-apps/workflow-form', () => ({
  ...jest.requireActual('@kogito-apps/workflow-form'),
  EmbeddedWorkflowForm: () => {
    return <div />;
  }
}));

const mockHistoryPush = jest.fn();

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useHistory: () => ({
    push: mockHistoryPush,
    location: {
      state: {
        workflowDefinition: {
          workflowName: 'workflow1',
          endpoint: 'http://localhost:4000'
        }
      }
    }
  })
}));
const MockWorkflowFormGatewayApi = jest.fn<WorkflowFormGatewayApi, []>(() => ({
  setBusinessKey: jest.fn(),
  getBusinessKey: jest.fn(),
  getCustomWorkflowSchema: jest.fn(),
  startWorkflow: jest.fn()
}));

const workflowFormGatewayApi = new MockWorkflowFormGatewayApi();

jest
  .spyOn(WorkflowFormContext, 'useWorkflowFormGatewayApi')
  .mockImplementation(() => workflowFormGatewayApi);

const MockProcessFormGatewayApi = jest.fn<ProcessFormGatewayApi, []>(() => ({
  setBusinessKey: jest.fn(),
  getBusinessKey: jest.fn(),
  currentBusinessKey: ''
}));

const gatewayApi = new MockProcessFormGatewayApi();

jest
  .spyOn(ProcessFormContext, 'useProcessFormGatewayApi')
  .mockImplementation(() => gatewayApi);

const MockDevUIApp = jest.fn(() => ({
  getDevUIUrl: jest.fn()
}));

const devUIApp = new MockDevUIApp();

jest
  .spyOn(DevUIAppContext, 'useDevUIAppContext')
  .mockImplementation(() => devUIApp);

jest
  .spyOn(WorkflowFormContext, 'useWorkflowFormGatewayApi')
  .mockImplementation(() => new WorkflowFormGatewayApiImpl('baseUrl'));

describe('WorkflowFormPage tests', () => {
  const props = {
    ouiaId: 'workflow-form',
    ouiaSafe: true
  };
  it('Snapshot', async () => {
    const container = render(
      <BrowserRouter>
        <WorkflowFormPage {...props} />
      </BrowserRouter>
    ).container;

    expect(container).toMatchSnapshot();

    const checkWorkflowFormPage = screen.getByText('Start New Workflow');
    expect(checkWorkflowFormPage).toBeTruthy();
  });
  it('Alert component checks', () => {
    render(
      <BrowserRouter>
        <WorkflowFormPage {...props} />
      </BrowserRouter>
    ).container;

    const goToButton = screen.getByText('Go to workflow list');
    fireEvent.click(goToButton);
    expect(mockHistoryPush).toHaveBeenCalledWith('/Processes');
  });

  it('Test close Alert component', () => {
    render(
      <BrowserRouter>
        <WorkflowFormPage {...props} />
      </BrowserRouter>
    ).container;

    const closeButton = screen.getByTestId('close-button');
    fireEvent.click(closeButton);

    expect(() => screen.getByLabelText('Danger Alert')).toThrow(
      'Unable to find a label with the text of: Danger Alert'
    );
  });
});

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
import { mount } from 'enzyme';
import WorkflowFormContainer from '../WorkflowFormContainer';
import * as WorkflowFormContext from '../../../../channel/WorkflowForm/WorkflowFormContext';
import { WorkflowFormGatewayApi } from '../../../../channel/WorkflowForm/WorkflowFormGatewayApi';
import DevUIAppContextProvider from '../../../contexts/DevUIAppContextProvider';
import {
  DefaultUser,
  User
} from '@kogito-apps/consoles-common/dist/environment/auth';
import { EmbeddedWorkflowForm } from '@kogito-apps/workflow-form';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-code-editor', () =>
  Object.assign(jest.requireActual('@patternfly/react-code-editor'), {
    CodeEditor: () => {
      return <MockedComponent />;
    }
  })
);

const MockedWorkflowFormGatewayApi = jest.fn<WorkflowFormGatewayApi, []>(
  () => ({
    setBusinessKey: jest.fn(),
    getBusinessKey: jest.fn(),
    getCustomWorkflowSchema: jest.fn(),
    startWorkflow: jest.fn()
  })
);

let gatewayApi: WorkflowFormGatewayApi;

jest
  .spyOn(WorkflowFormContext, 'useWorkflowFormGatewayApi')
  .mockImplementation(() => gatewayApi);

const getWrapper = () => {
  return mount(
    <DevUIAppContextProvider users={[user]} {...appContextProps}>
      <WorkflowFormContainer {...props} />
    </DevUIAppContextProvider>
  );
};

const props = {
  workflowDefinitionData: {
    workflowName: 'workflow1',
    endpoint: 'http://localhost:4000'
  },
  onSubmitSuccess: jest.fn(),
  onSubmitError: jest.fn(),
  onResetForm: jest.fn()
};

const user: User = new DefaultUser('jon', []);
const appContextProps = {
  devUIUrl: 'http://localhost:9000',
  openApiPath: '/mocked',
  isProcessEnabled: false,
  isTracingEnabled: false,
  omittedProcessTimelineEvents: [],
  isStunnerEnabled: false,
  availablePages: [],
  customLabels: {
    singularProcessLabel: 'test-singular',
    pluralProcessLabel: 'test-plural'
  },
  diagramPreviewSize: { width: 100, height: 100 }
};

describe('WorkflowFormContainer tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new MockedWorkflowFormGatewayApi();
  });

  it('Snapshot', () => {
    const wrapper = getWrapper();
    expect(wrapper.find('WorkflowFormContainer')).toMatchSnapshot();

    const forwardRef = wrapper.find(EmbeddedWorkflowForm);

    expect(forwardRef.props().driver).not.toBeNull();

    expect(forwardRef.props().targetOrigin).toBe('http://localhost:9000');
  });

  it('test get custom workflow schema', () => {
    const wrapper = getWrapper();
    const forwardRef = wrapper.find(EmbeddedWorkflowForm);
    forwardRef.props().driver['getCustomWorkflowSchema']();
    expect(gatewayApi.getCustomWorkflowSchema).toHaveBeenCalled();
  });

  it('test start workflow rest - success', () => {
    gatewayApi.startWorkflow = jest
      .fn()
      .mockImplementation(() => Promise.resolve('1234'));

    const wrapper = getWrapper();
    const forwardRef = wrapper.find(EmbeddedWorkflowForm);

    forwardRef.props().driver['startWorkflow']('/endpoint', {});
    expect(gatewayApi.startWorkflow).toHaveBeenCalled();
  });

  it('test start workflow rest - failure', () => {
    gatewayApi.startWorkflow = jest.fn().mockImplementation(() =>
      Promise.reject({
        response: {
          data: {
            message: 'error',
            cause: 'error cause'
          }
        }
      })
    );
    const wrapper = getWrapper();
    const forwardRef = wrapper.find(EmbeddedWorkflowForm);

    forwardRef.props().driver['startWorkflow']('/endpoint', {});
    expect(gatewayApi.startWorkflow).toHaveBeenCalled();
  });

  it('test start workflow no form - success', () => {
    gatewayApi.startWorkflow = jest
      .fn()
      .mockImplementation(() => Promise.resolve('1234'));
    const wrapper = getWrapper();
    const forwardRef = wrapper.find(EmbeddedWorkflowForm);

    forwardRef.props().driver['startWorkflow']('/endpoint', {});
    expect(gatewayApi.startWorkflow).toHaveBeenCalled();
  });

  it('test start workflow no form - failure', () => {
    gatewayApi.startWorkflow = jest.fn().mockImplementation(() =>
      Promise.reject({
        response: {
          data: {
            message: 'error',
            cause: 'error cause'
          }
        }
      })
    );
    const wrapper = getWrapper();
    const forwardRef = wrapper.find(EmbeddedWorkflowForm);

    forwardRef.props().driver['startWorkflow']('/endpoint', {});
    expect(gatewayApi.startWorkflow).toHaveBeenCalled();
  });

  it('test reset businesskey', () => {
    const wrapper = getWrapper();
    const forwardRef = wrapper.find(EmbeddedWorkflowForm);

    forwardRef.props().driver['resetBusinessKey']();
    expect(props.onResetForm).toHaveBeenCalled();
  });
});

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

const MockedWorkflowFormGatewayApi = jest.fn<WorkflowFormGatewayApi, []>(() => ({
  startWorkflowCloudEvent: jest.fn(),
  setBusinessKey: jest.fn(),
  getBusinessKey: jest.fn(),
  getCustomWorkflowSchema: jest.fn(),
  startWorkflowRest: jest.fn()
}));

let gatewayApi;

jest
  .spyOn(WorkflowFormContext, 'useWorkflowFormGatewayApi')
  .mockImplementation(() => gatewayApi);

const getWrapper = () => {
  return mount(<WorkflowFormContainer {...props} />);
}

const props = {
  workflowDefinitionData: {
    workflowName: 'workflow1',
    endpoint: 'http://localhost:4000'
  },
  onSubmitSuccess: jest.fn(),
  onSubmitError: jest.fn(),
  onResetForm: jest.fn()
};

describe('WorkflowFormContainer tests', () => {

  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new MockedWorkflowFormGatewayApi();
  })

  it('Snapshot', () => {
    const wrapper = getWrapper();
    expect(wrapper).toMatchSnapshot();

    const forwardRef = wrapper.childAt(0);

    expect(forwardRef.props().driver).not.toBeNull();

    expect(forwardRef.props().targetOrigin).toBe('*');
  });

  it('test get custom workflow schema', () => {
    const getCustomWorkflowSchemaSpy = jest.spyOn(gatewayApi, 'getCustomWorkflowSchema');
    const wrapper = getWrapper();
    const forwardRef = wrapper.childAt(0);
    const workflowName = 'expression';
    forwardRef.props().driver['getCustomWorkflowSchema'](workflowName);
    expect(getCustomWorkflowSchemaSpy).toHaveBeenCalled();
  })

  it('test start workflow rest - success', ()=>{
    gatewayApi.startWorkflowRest = jest.fn().mockImplementation(()=> Promise.resolve('1234'));
    const startWorkflowRestSpy = jest.spyOn(gatewayApi, 'startWorkflowRest');
    const wrapper = getWrapper();
    const forwardRef = wrapper.childAt(0);

    forwardRef.props().driver['startWorkflowRest']();
    expect(startWorkflowRestSpy).toHaveBeenCalled();
  })

  it('test start workflow rest - failure', ()=>{
    gatewayApi.startWorkflowRest = jest.fn().mockImplementation(()=> Promise.reject({response:{
      data:{
        message:"error",
        cause:"error cause"
      }
    }}));
    const startWorkflowRestSpy = jest.spyOn(gatewayApi, 'startWorkflowRest');
    const wrapper = getWrapper();
    const forwardRef = wrapper.childAt(0);

    forwardRef.props().driver['startWorkflowRest']();
    expect(startWorkflowRestSpy).toHaveBeenCalled();
  })

  it('test start workflow cloud event - success', ()=>{
    gatewayApi.startWorkflowCloudEvent = jest.fn().mockImplementation(()=> Promise.resolve('1234'));
    const startWorkflowSpy = jest.spyOn(gatewayApi, 'startWorkflowCloudEvent');
    const wrapper = getWrapper();
    const forwardRef = wrapper.childAt(0);

    forwardRef.props().driver['startWorkflowCloudEvent']();
    expect(startWorkflowSpy).toHaveBeenCalled();
  })

  it('test start workflow cloud event - failure', ()=>{
    gatewayApi.startWorkflowCloudEvent = jest.fn().mockImplementation(()=> Promise.reject({response:{
      data:{
        message:"error",
        cause:"error cause"
      }
    }}));
    const startWorkflowSpy = jest.spyOn(gatewayApi, 'startWorkflowCloudEvent');
    const wrapper = getWrapper();
    const forwardRef = wrapper.childAt(0);

    forwardRef.props().driver['startWorkflowCloudEvent']();
    expect(startWorkflowSpy).toHaveBeenCalled();
  })

  it('test reset businesskey', ()=>{
    const wrapper = getWrapper();
    const forwardRef = wrapper.childAt(0);

    forwardRef.props().driver['resetBusinessKey']();
    expect(props.onResetForm).toHaveBeenCalled();
  })

});

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
import { act } from 'react-dom/test-utils';
import wait from 'waait';
import WorkflowForm, { WorkflowFormProps } from '../WorkflowForm';
import { WorkflowFormDriver } from '../../../../api';
import { mount } from 'enzyme';
import { MockedWorkflowFormDriver } from '../../../../embedded/tests/mocks/Mocks';

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

let props: WorkflowFormProps;
let startWorkflowSpy;
const getWorkflowFormDriver = (): WorkflowFormDriver => {
  const driver = new MockedWorkflowFormDriver();
  startWorkflowSpy = jest.spyOn(driver, 'startWorkflowCloudEvent');
  startWorkflowSpy.mockReturnValue(Promise.resolve('newKey'));
  props.driver = driver;
  return driver;
};

const getWorkflowFormWrapper = () => {
  return mount(<WorkflowForm {...props} />).find('WorkflowForm');
};

describe('WorkflowForm Test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      driver: null,
      workflowDefinition: {
        workflowName: 'workflow1',
        endpoint: 'http://localhost:4000/hiring'
      }
    };
  });

  it('Workflow Form rendering', async () => {
    const driver = getWorkflowFormDriver();
    let wrapper;
    await act(async () => {
      wrapper = getWorkflowFormWrapper();
      wait();
    });

    expect(wrapper).toMatchSnapshot();

    const workflowForm = wrapper.find('Form');
    expect(workflowForm.exists()).toBeTruthy();

    expect(workflowForm.props().enabled).toBeFalsy();

    const formData = {
      type: 'participants',
      data: '{ "username": "myuser" }'
    };

    await act(async () => {
      workflowForm
        .find('Button[variant="primary"]')
        .props()
        .onClick(formData);
      wait();
    });
    expect(driver.startWorkflowCloudEvent).toHaveBeenCalled();
  });
});

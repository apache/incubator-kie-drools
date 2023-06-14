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
import { WorkflowFormDriver } from '../../../../api';
import { mount } from 'enzyme';
import { MockedWorkflowFormDriver } from '../../../../embedded/tests/mocks/Mocks';
import CustomWorkflowForm, {
  CustomWorkflowFormProps
} from '../CustomWorkflowForm';
import { workflowSchema } from '../../../tests/mocks/Mocks';

let props: CustomWorkflowFormProps;
let startWorkflowRestSpy;
const getWorkflowFormDriver = (): WorkflowFormDriver => {
  const driver = new MockedWorkflowFormDriver();
  startWorkflowRestSpy = jest.spyOn(driver, 'startWorkflow');
  startWorkflowRestSpy.mockReturnValue(Promise.resolve('newKey'));
  props.driver = driver;
  return driver;
};

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    FormRenderer: () => {
      return <MockedComponent />;
    },
    FormFooter: () => {
      return <MockedComponent />;
    }
  })
);

const getWorkflowFormWrapper = () => {
  return mount(<CustomWorkflowForm {...props} />).find('CustomWorkflowForm');
};

describe('CustomWorkflowForm Test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      driver: null,
      workflowDefinition: {
        workflowName: 'workflow1',
        endpoint: 'http://localhost:4000/hiring'
      },
      customFormSchema: workflowSchema
    };
  });

  it('Custom Workflow Form rendering', async () => {
    const driver = getWorkflowFormDriver();
    let wrapper;
    await act(async () => {
      wrapper = getWorkflowFormWrapper();
      wait();
    });
    expect(wrapper).toMatchSnapshot();

    const customWorkflowForm = wrapper.find('CustomWorkflowForm');
    expect(customWorkflowForm.exists()).toBeTruthy();

    expect(customWorkflowForm.props().enabled).toBeFalsy();

    await act(async () => {
      customWorkflowForm.find('FormRenderer').props()['onSubmit']();
      wait();
    });
    expect(driver.startWorkflow).toHaveBeenCalled();
  });

  it('Custom Workflow Form - loading', async () => {
    jest.spyOn(window, 'setTimeout');
    jest.useFakeTimers();

    const driver = new MockedWorkflowFormDriver();
    startWorkflowRestSpy = jest.spyOn(driver, 'startWorkflow');
    startWorkflowRestSpy.mockReturnValue(
      new Promise((resolve) => setTimeout(() => resolve('newKey'), 1000))
    );
    props.driver = driver;

    let wrapper;
    act(() => {
      wrapper = getWorkflowFormWrapper();
    });

    const customWorkflowForm = wrapper.find('CustomWorkflowForm');

    act(() => {
      customWorkflowForm.find('FormRenderer').props()['onSubmit']();
    });

    expect(driver.startWorkflow).toHaveBeenCalled();

    expect(wrapper.update()).toMatchSnapshot();

    await act(async () => {
      Promise.resolve().then(() => jest.advanceTimersByTime(2000));
      new Promise((resolve) => {
        setTimeout(resolve, 2000);
      });
    });

    expect(wrapper.update()).toMatchSnapshot();

    jest.useRealTimers();
  });
});

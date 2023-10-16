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
import { act } from 'react-dom/test-utils';
import wait from 'waait';
import { WorkflowFormDriver } from '../../../../api';
import { fireEvent, render, screen } from '@testing-library/react';
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

const getWorkflowFormWrapper = () => {
  return render(<CustomWorkflowForm {...props} />).container;
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
    let container;
    await act(async () => {
      container = getWorkflowFormWrapper();
      wait();
    });
    expect(container).toMatchSnapshot();
    const checkCustomWorkflowForm = container.querySelector(
      '[data-ouia-component-type="custom-workflow-form"]'
    );
    expect(checkCustomWorkflowForm).toBeTruthy();

    await act(async () => {
      fireEvent.click(container.querySelector('[type="submit"]'));
      wait();
    });
    expect(driver.startWorkflow).toHaveBeenCalled();
  });

  it('Custom Workflow Form with reset', async () => {
    const driver = getWorkflowFormDriver();
    let container;
    await act(async () => {
      container = getWorkflowFormWrapper();
      wait();
    });
    expect(container).toMatchSnapshot();
    const checkCustomWorkflowForm = container.querySelector(
      '[data-ouia-component-type="custom-workflow-form"]'
    );
    expect(checkCustomWorkflowForm).toBeTruthy();
    await act(async () => {
      fireEvent.click(container.querySelector('[type="reset"]'));
      wait();
    });
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

    let container;
    act(() => {
      container = getWorkflowFormWrapper();
    });

    const checkCustomWorkflowForm = container.querySelector(
      '[data-ouia-component-type="custom-workflow-form"]'
    );
    expect(checkCustomWorkflowForm).toBeTruthy();

    await act(async () => {
      fireEvent.click(container.querySelector('[type="submit"]'));
      wait();
    });

    expect(driver.startWorkflow).toHaveBeenCalled();

    expect(container).toMatchSnapshot();

    await act(async () => {
      Promise.resolve().then(() => jest.advanceTimersByTime(2000));
      new Promise((resolve) => {
        setTimeout(resolve, 2000);
      });
    });

    expect(container).toMatchSnapshot();

    jest.useRealTimers();
  });
});

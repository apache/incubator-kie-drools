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
import CloudEventForm from '../CloudEventForm';
import { fireEvent, render, screen } from '@testing-library/react';
import { CloudEventFormDriver } from '../../../../api';
import { CloudEventCustomHeadersEditorApi } from '../../CloudEventCustomHeadersEditor/CloudEventCustomHeadersEditor';
import { act } from 'react-dom/test-utils';
import wait from 'waait';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-code-editor/dist/js/components/CodeEditor', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-code-editor'), {
    CodeEditor: () => {
      return <MockedComponent />;
    },
    Language: () => {
      return {
        json: 'json'
      };
    }
  })
);

jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    ExclamationCircleIcon: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@patternfly/react-core/dist/js/components/Select', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Select: () => {
      return <MockedComponent />;
    },
    SelectOption: () => {
      return <MockedComponent />;
    },
    SelectVariant: {
      single: 'single'
    }
  })
);

jest.mock('@patternfly/react-core/dist/js/components/TextInput', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    TextInput: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('../../CloudEventFieldLabelIcon/CloudEventFieldLabelIcon');

const driver: CloudEventFormDriver = {
  triggerCloudEvent: jest.fn()
};

const headersEditorApi: CloudEventCustomHeadersEditorApi = {
  reset: jest.fn(),
  getCustomHeaders: jest.fn()
};

const headers = {
  header1: 'value1',
  header2: 'value2'
};

jest.spyOn(React, 'useRef').mockReturnValue({
  current: { reset: jest.fn(), getCustomHeaders: jest.fn() }
});

const triggerCloudEventSpy = jest
  .spyOn(driver, 'triggerCloudEvent')
  .mockReturnValue(Promise.resolve());
jest.spyOn(headersEditorApi, 'getCustomHeaders').mockReturnValue(headers);

describe('CloudEventForm tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('Snapshot - new instance', () => {
    const { container } = render(
      <CloudEventForm isNewInstanceEvent={true} driver={driver} />
    );

    expect(container.querySelector('[for="endpoint"]')).toBeTruthy();
    expect(container.querySelector('[for="eventType"]')).toBeTruthy();
    expect(container.querySelector('[for="eventSource"]')).toBeTruthy();
    expect(container.querySelector('[for="instanceId"]')).toBeFalsy();
    expect(container.querySelector('[for="businessKey"]')).toBeTruthy();
    expect(container).toMatchSnapshot();
  });

  it('Snapshot - new instance -  default values', () => {
    const { container } = render(
      <CloudEventForm
        driver={driver}
        isNewInstanceEvent={true}
        defaultValues={{
          cloudEventSource: '/test/source'
        }}
      />
    );

    expect(container.querySelector('[for="eventSource"]')).toBeTruthy();

    expect(container.querySelector('[for="instanceId"]')).toBeFalsy();

    expect(container).toMatchSnapshot();
  });

  it('Snapshot - send cloud event', () => {
    const { container } = render(<CloudEventForm driver={driver} />);

    expect(container.querySelector('[for="endpoint"]')).toBeTruthy();
    expect(container.querySelector('[for="eventType"]')).toBeTruthy();
    expect(container.querySelector('[for="eventSource"]')).toBeTruthy();
    expect(container.querySelector('[for="instanceId"]')).toBeTruthy();
    expect(container.querySelector('[for="businessKey"]')).toBeFalsy();

    expect(container).toMatchSnapshot();
  });

  it('Snapshot - send cloud event -  default values', () => {
    const { container } = render(
      <CloudEventForm
        driver={driver}
        defaultValues={{
          instanceId: '1234',
          cloudEventSource: '/test/source'
        }}
      />
    );

    expect(container.querySelector('[for="eventSource"]')).toBeTruthy();

    expect(container.querySelector('[for="instanceId"]')).toBeTruthy();

    expect(container).toMatchSnapshot();
  });

  it('Trigger - Validation failure', () => {
    const { container } = render(
      <CloudEventForm driver={driver} isNewInstanceEvent={true} />
    );
    expect(container.querySelector('[for="endpoint"]')).toBeTruthy();
    expect(container.querySelector('[for="eventType"]')).toBeTruthy();

    fireEvent.click(screen.getByText('Trigger'));

    expect(headersEditorApi.reset).not.toHaveBeenCalled();
    expect(driver.triggerCloudEvent).not.toHaveBeenCalled();
  });

  it('Trigger - success', async () => {
    const { container } = render(
      <CloudEventForm
        driver={driver}
        defaultValues={{
          instanceId: '1234',
          cloudEventSource: '/test/source'
        }}
      />
    );
    expect(container.querySelector('[for="eventType"]')).toBeTruthy();

    expect(
      container.querySelector(
        '[data-ouia-component-type="custom-headers-editor"]'
      )
    ).toBeTruthy();

    const triggerButton = screen.getByText('Trigger');

    expect(triggerButton).toBeTruthy();

    fireEvent.click(screen.getByText('Trigger'));
  });

  it('Reset', async () => {
    const { container } = render(
      <CloudEventForm
        driver={driver}
        defaultValues={{
          cloudEventSource: '/test/source',
          instanceId: '1234'
        }}
      />
    );
    expect(container.querySelector('[for="endpoint"]')).toBeTruthy();
    expect(container.querySelector('[for="eventType"]')).toBeTruthy();
    expect(
      container.querySelector(
        '[data-ouia-component-type="custom-headers-editor"]'
      )
    ).toBeTruthy();

    await act(async () => {
      fireEvent.click(screen.getByText('Reset'));
      wait();
    });

    expect(container.querySelector('[for="endpoint"]')).toBeTruthy();
    expect(container.querySelector('[for="eventType"]')).toBeTruthy();
  });

  it('Reset- without default value', async () => {
    const { container } = render(<CloudEventForm driver={driver} />);
    expect(container.querySelector('[for="endpoint"]')).toBeTruthy();
    expect(container.querySelector('[for="eventType"]')).toBeTruthy();
    expect(
      container.querySelector(
        '[data-ouia-component-type="custom-headers-editor"]'
      )
    ).toBeTruthy();
    await act(async () => {
      fireEvent.click(screen.getByText('Reset'));
      wait();
    });
  });
});

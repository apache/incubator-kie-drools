/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import CloudEventForm from '../CloudEventForm';
import { mount } from 'enzyme';
import { CloudEventFormDriver } from '../../../../api';
import CloudEventCustomHeadersEditor, {
  CloudEventCustomHeadersEditorApi
} from '../../CloudEventCustomHeadersEditor/CloudEventCustomHeadersEditor';
import { CodeEditor } from '@patternfly/react-code-editor/dist/js/components/CodeEditor';
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

jest.mock('@patternfly/react-core/dist/js/components/Button', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Button: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@patternfly/react-core/dist/js/components/Select', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Select: () => {
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
jest.mock('../../CloudEventCustomHeadersEditor/CloudEventCustomHeadersEditor');
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

const triggerCloudEventSpy = jest
  .spyOn(driver, 'triggerCloudEvent')
  .mockReturnValue(Promise.resolve());
jest.spyOn(headersEditorApi, 'getCustomHeaders').mockReturnValue(headers);
jest.mock('react', () => {
  const originReact = jest.requireActual('react');
  return {
    ...originReact,
    useRef: jest.fn(() => ({ current: headersEditorApi }))
  };
});

function findFieldById(wrapper, fieldId: string) {
  return wrapper.findWhere((child) => child.props().id === fieldId);
}

describe('CloudEventForm tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('Snapshot - new instance', () => {
    const wrapper = mount(
      <CloudEventForm isNewInstanceEvent={true} driver={driver} />
    );

    expect(findFieldById(wrapper, 'method').exists()).toBeTruthy();
    expect(findFieldById(wrapper, 'endpoint').exists()).toBeTruthy();
    expect(findFieldById(wrapper, 'eventType').exists()).toBeTruthy();

    const sourceField = findFieldById(wrapper, 'eventSource');
    expect(sourceField.exists()).toBeTruthy();
    expect(sourceField.at(0).props().value).toBe('/from/form');

    expect(findFieldById(wrapper, 'instanceId').exists()).toBeFalsy();
    expect(findFieldById(wrapper, 'businessKey').exists()).toBeTruthy();
    expect(wrapper.find(CloudEventCustomHeadersEditor).exists()).toBeTruthy();
    expect(wrapper.find(CodeEditor).exists()).toBeTruthy();

    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot - new instance -  default values', () => {
    const wrapper = mount(
      <CloudEventForm
        driver={driver}
        isNewInstanceEvent={true}
        defaultValues={{
          cloudEventSource: '/test/source'
        }}
      />
    );

    const sourceField = findFieldById(wrapper, 'eventSource');
    expect(sourceField.exists()).toBeTruthy();
    expect(sourceField.at(0).props().value).toBe('/test/source');

    const instanceIdField = findFieldById(wrapper, 'instanceId');
    expect(instanceIdField.exists()).toBeFalsy();

    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot - send cloud event', () => {
    const wrapper = mount(<CloudEventForm driver={driver} />);

    expect(findFieldById(wrapper, 'method').exists()).toBeTruthy();
    expect(findFieldById(wrapper, 'endpoint').exists()).toBeTruthy();
    expect(findFieldById(wrapper, 'eventType').exists()).toBeTruthy();

    const sourceField = findFieldById(wrapper, 'eventSource');
    expect(sourceField.exists()).toBeTruthy();
    expect(sourceField.at(0).props().value).toBe('/from/form');

    expect(findFieldById(wrapper, 'instanceId').exists()).toBeTruthy();
    expect(findFieldById(wrapper, 'businessKey').exists()).toBeFalsy();
    expect(wrapper.find(CloudEventCustomHeadersEditor).exists()).toBeTruthy();
    expect(wrapper.find(CodeEditor).exists()).toBeTruthy();

    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot - send cloud event -  default values', () => {
    const wrapper = mount(
      <CloudEventForm
        driver={driver}
        defaultValues={{
          instanceId: '1234',
          cloudEventSource: '/test/source'
        }}
      />
    );

    const sourceField = wrapper.findWhere(
      (child) => child.props().id == 'eventSource'
    );
    expect(sourceField.exists()).toBeTruthy();
    expect(sourceField.at(0).props().value).toBe('/test/source');

    const instanceIdField = wrapper.findWhere(
      (child) => child.props().id == 'instanceId'
    );
    expect(instanceIdField.exists()).toBeTruthy();
    expect(instanceIdField.at(0).props().value).toBe('1234');

    expect(wrapper).toMatchSnapshot();
  });

  it('Trigger - Validation failure', () => {
    let wrapper = mount(<CloudEventForm driver={driver} />);
    const endpointField = findFieldById(wrapper, 'endpoint');
    expect(endpointField.exists()).toBeTruthy();
    const eventTypeField = findFieldById(wrapper, 'eventType');
    expect(eventTypeField.exists()).toBeTruthy();
    const eventDataField = wrapper.find(CodeEditor);
    expect(eventDataField.exists()).toBeTruthy();

    act(() => {
      endpointField.at(0).props()['onChange']('');
      eventTypeField.at(0).props()['onChange']('');
      eventTypeField.at(0).props()['onChange']('this is wrong');
    });

    wrapper = wrapper.update();

    const triggerButton = wrapper.findWhere(
      (child) => child.key() === 'triggerCloudEventButton'
    );
    expect(triggerButton.exists()).toBeTruthy();

    act(() => {
      triggerButton.prop('onClick')();
    });

    expect(headersEditorApi.getCustomHeaders).toHaveBeenCalled();
    expect(headersEditorApi.reset).not.toHaveBeenCalled();
    expect(driver.triggerCloudEvent).not.toHaveBeenCalled();
  });

  it('Trigger - success', async () => {
    let wrapper = mount(<CloudEventForm driver={driver} />);
    const eventTypeField = findFieldById(wrapper, 'eventType');
    expect(eventTypeField.exists()).toBeTruthy();
    const eventDataField = wrapper.find(CodeEditor);
    expect(eventDataField.exists()).toBeTruthy();

    const eventType = 'test';
    const eventData = JSON.stringify({
      name: 'Bart',
      lastName: 'Simpson'
    });

    act(() => {
      eventTypeField.at(0).props()['onChange'](eventType);
      eventDataField.props()['onChange'];
    });

    wrapper = wrapper.update();

    const triggerButton = wrapper.findWhere(
      (child) => child.key() === 'triggerCloudEventButton'
    );
    expect(triggerButton.exists()).toBeTruthy();

    await act(async () => {
      await triggerButton.prop('onClick')(undefined);
      wait();
    });

    wrapper = wrapper.update();

    expect(headersEditorApi.getCustomHeaders).toHaveBeenCalled();
    expect(headersEditorApi.reset).toHaveBeenCalled();
    expect(driver.triggerCloudEvent).toHaveBeenCalled();

    const eventRequest = triggerCloudEventSpy.mock.calls[0][0];

    expect(eventRequest).toHaveProperty('endpoint', '/');
    expect(eventRequest).toHaveProperty('method', 'POST');
    expect(eventRequest).toHaveProperty('data', '');
    expect(eventRequest).toHaveProperty('headers');
    expect(eventRequest.headers).toHaveProperty('type', eventType);
    expect(eventRequest.headers).toHaveProperty('source', '/from/form');
    expect(eventRequest.headers).toHaveProperty('extensions');
    expect(eventRequest.headers.extensions).toHaveProperty('header1', 'value1');
    expect(eventRequest.headers.extensions).toHaveProperty('header2', 'value2');
  });

  it('Reset', async () => {
    let wrapper = mount(<CloudEventForm driver={driver} />);
    let endpointField = findFieldById(wrapper, 'endpoint');
    let eventTypeField = findFieldById(wrapper, 'eventType');
    let eventDataField = wrapper.find(CodeEditor);

    const eventType = 'test';
    const eventData = JSON.stringify({
      name: 'Bart',
      lastName: 'Simpson'
    });

    act(() => {
      endpointField.at(0).props()['onChange']('');
      eventTypeField.at(0).props()['onChange'](eventType);
      eventDataField.props()['onChange'];
    });

    wrapper = wrapper.update();

    const resetButton = wrapper.findWhere(
      (child) => child.key() === 'resetCloudEventFormButton'
    );
    expect(resetButton.exists()).toBeTruthy();

    await act(async () => {
      await resetButton.prop('onClick')(undefined);
      wait();
    });

    wrapper = wrapper.update();

    expect(headersEditorApi.reset).toHaveBeenCalled();

    endpointField = findFieldById(wrapper, 'endpoint');
    eventTypeField = findFieldById(wrapper, 'eventType');
    eventDataField = wrapper.find(CodeEditor);

    expect(endpointField.at(0).props().value).toBe('/');
    expect(eventTypeField.at(0).props().value).toBe('');
    expect(eventDataField.at(0).props().code).toBe('');
  });
});

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import _ from 'lodash';
import wait from 'waait';
import ProcessForm, { ProcessFormProps } from '../ProcessForm';
import { ProcessFormDriver } from '../../../../api';
import { mount } from 'enzyme';
import { MockedProcessFormDriver } from '../../../../embedded/tests/mocks/Mocks';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import { ConfirmTravelForm } from './mocks/ConfirmTravelForm';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common/dist/components/ServerErrors', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    ServerErrors: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@kogito-apps/components-common/dist/components/FormRenderer', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    FormRenderer: () => {
      return <MockedComponent />;
    }
  })
);

let props: ProcessFormProps;
let driverGetProcessFormSchemaSpy;
const getProcessFormDriver = (schema?: any): ProcessFormDriver => {
  const driver = new MockedProcessFormDriver();
  driverGetProcessFormSchemaSpy = jest.spyOn(driver, 'getProcessFormSchema');
  driverGetProcessFormSchemaSpy.mockReturnValue(Promise.resolve(schema));
  props.driver = driver;
  return driver;
};

const getProcessFormWrapper = () => {
  return mount(<ProcessForm {...props} />).find('ProcessForm');
};

describe('ProcessForm Test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      isEnvelopeConnectedToChannel: true,
      driver: null,
      processDefinition: {
        processName: 'process1',
        endpoint: 'http://localhost:4000/hiring'
      }
    };
  });

  it('Envelope not connected', () => {
    const driver = getProcessFormDriver();

    props.isEnvelopeConnectedToChannel = false;
    const wrapper = getProcessFormWrapper();

    expect(wrapper).toMatchSnapshot();

    expect(driver.getProcessFormSchema).not.toHaveBeenCalled();

    const spinner = wrapper.find(KogitoSpinner);
    expect(spinner.exists()).toBeTruthy();

    const renderer = wrapper.find('MockedFormRenderer');
    expect(renderer.exists()).toBeFalsy();
  });

  it('Process Form rendering', async () => {
    const schema = _.cloneDeep(ConfirmTravelForm);
    const driver = getProcessFormDriver(schema);
    let wrapper;
    await act(async () => {
      wrapper = getProcessFormWrapper();
      wait();
    });
    const driverStartProcessSpy = jest.spyOn(driver, 'startProcess');
    driverStartProcessSpy.mockReturnValue(Promise.resolve());
    wrapper = wrapper.update().find('ProcessForm');

    expect(wrapper).toMatchSnapshot();

    expect(driver.getProcessFormSchema).toHaveBeenCalled();

    const ProcessForm = wrapper.find('FormRenderer');
    expect(ProcessForm.exists()).toBeTruthy();

    expect(ProcessForm.props().enabled).toBeFalsy();
    expect(ProcessForm.props().formSchema).toStrictEqual(schema);

    const formData = {
      candidate: {
        name: 'person1',
        age: 15
      },
      it_approval: true
    };

    await act(async () => {
      ProcessForm.props().onSubmit(formData);
      wait();
    });
    expect(driverStartProcessSpy).toHaveBeenCalled();
  });
});

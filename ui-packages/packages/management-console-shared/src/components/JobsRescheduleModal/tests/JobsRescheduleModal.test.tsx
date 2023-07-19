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
import JobsRescheduleModal from '../JobsRescheduleModal';
import { JobStatus } from '../../../types';
import { mount } from 'enzyme';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { act } from 'react-dom/test-utils';
import * as MockDate from 'mockdate';
jest.mock('react-datetime-picker');
// tslint:disable: no-string-literal
// tslint:disable: no-unexpected-multiline
const props = {
  actionType: 'Job Reschedule',
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: JobStatus.Executed,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: 1,
    repeatLimit: 3,
    scheduledId: '0',
    retries: 0,
    lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
    expirationTime: new Date('2020-08-27T03:35:50.147Z')
  },
  isModalOpen: true,
  handleModalToggle: jest.fn(),
  modalAction: [
    <Button key="cancel-reschedule" variant="secondary">
      Cancel
    </Button>
  ],
  rescheduleError: '404 Not found',
  setRescheduleError: jest.fn(),
  handleJobReschedule: jest.fn()
};

const props2 = {
  actionType: 'Job Reschedule',
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: JobStatus.Executed,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: null,
    repeatLimit: null,
    scheduledId: '0',
    retries: 0,
    lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
    expirationTime: new Date('2020-08-27T03:35:50.147Z')
  },
  isModalOpen: true,
  handleModalToggle: jest.fn(),
  modalAction: [
    <Button key="cancel-reschedule" variant="secondary">
      Cancel
    </Button>
  ],
  rescheduleError: '404 Not found',
  setRescheduleError: jest.fn(),
  handleJobReschedule: jest.fn()
};

describe('Job reschedule modal tests', () => {
  beforeEach(() => {
    const DATE_TO_USE = new Date('2017-02-02T12:54:59.218Z');
    MockDate.set(DATE_TO_USE);
  });
  it('test job reschedule modal', async () => {
    const wrapper = mount(<JobsRescheduleModal {...props} />).find(
      'JobsRescheduleModal'
    );
    expect(wrapper).toMatchSnapshot();
    wrapper.find('#Time-now').first().simulate('click');
    const value: any = '2020-08-27T03:35:50.147Z';
    await act(async () => {
      wrapper.find('DateTimePicker').props()['onChange'](value);
    });
    const date = new Date('2020-08-27T03:35:50.147Z');
    expect(wrapper.find('DateTimePicker').props()['value']).toEqual(date);
    const event: any = { target: { value: '303300' } };
    await act(async () => {
      wrapper.find('#repeat-interval-input').first().props()['onChange'](event);
    });
    expect(
      wrapper.find('#repeat-interval-input').first().props()['isDisabled']
    ).toEqual(false);
    await act(async () => {
      wrapper.find('#repeat-limit-input').first().props()['onChange'](event);
    });
    expect(
      wrapper.find('#repeat-limit-input').first().props()['isDisabled']
    ).toEqual(false);
    await act(async () => {
      wrapper.find('#apply-button').at(0).simulate('click');
    });
    expect(props.handleJobReschedule).toHaveBeenCalled();
    wrapper.update();
  });
  it('test reschedule with null interval/limit', () => {
    const wrapper = mount(<JobsRescheduleModal {...props2} />).find(
      'JobsRescheduleModal'
    );
    expect(wrapper).toMatchSnapshot();
  });
});

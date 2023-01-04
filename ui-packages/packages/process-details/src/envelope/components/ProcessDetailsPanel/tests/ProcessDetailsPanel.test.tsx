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
import { shallow } from 'enzyme';
import {
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared';
import ProcessDetailsPanel from '../ProcessDetailsPanel';
import TestProcessDetailsDriver from '../../../tests/mocks/TestProcessDetailsDriver';
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20

const driver = new TestProcessDetailsDriver(
  '2d962eef-45b8-48a9-ad4e-9cde0ad6af89'
);

const mockMath = Object.create(global.Math);
mockMath.random = () => 0.5;
global.Math = mockMath;

const processInstance1: ProcessInstance = {
  id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af89',
  processId: '',
  state: ProcessInstanceState.Active,
  parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
  endpoint: 'test',
  businessKey: 'Tr21',
  start: new Date('2019-10-22T03:40:44.089Z'),
  serviceUrl: 'http://localhost:4000/',
  end: new Date('2019-10-22T03:40:44.089Z'),
  nodes: [],
  parentProcessInstance: null,
  childProcessInstances: [],
  lastUpdate: new Date('2019-10-22T03:40:44.089Z')
};

const processInstance2: ProcessInstance = {
  id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
  processId: 'hotelBooking',
  state: ProcessInstanceState.Active,
  parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
  endpoint: 'test',
  start: null,
  serviceUrl: 'http://localhost:4000/',
  nodes: [],
  end: new Date('2019-10-22T03:40:44.089Z'),
  parentProcessInstance: {
    id: '2d962eef-45b8-48a9-ad4e-11-22',
    processName: 'Travels22',
    businessKey: 'Tra11',
    processId: 'Travels33',
    state: ProcessInstanceState.Completed,
    endpoint: 'http://localhost:4000/',
    serviceUrl: 'http://localhost:4000/',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
        enter: new Date('2019-10-22T03:37:30.798Z'),
        exit: new Date('2019-10-22T03:37:30.798Z'),
        type: 'EndNode'
      }
    ],
    start: new Date('2019-10-22T03:40:44.089Z'),
    lastUpdate: new Date('2019-10-22T03:40:44.089Z')
  },
  childProcessInstances: [
    {
      id: '23944e2-874R22-48a9-abcd-11-22',
      processName: 'Travels33',
      businessKey: 'Tra33',
      processId: 'Travels33',
      state: ProcessInstanceState.Completed,
      endpoint: 'http://localhost:4000/',
      serviceUrl: 'http://localhost:4000/',
      start: new Date('2019-10-22T03:40:44.089Z'),
      nodes: [
        {
          nodeId: '1',
          name: 'End Event 1',
          definitionId: 'EndEvent_1',
          id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
          enter: new Date('2019-10-22T03:37:30.798Z'),
          exit: new Date('2019-10-22T03:37:30.798Z'),
          type: 'EndNode'
        }
      ],
      lastUpdate: new Date('2019-10-22T03:40:44.089Z')
    }
  ],
  lastUpdate: new Date('2019-10-22T03:40:44.089Z')
};
const props = {
  processInstance: processInstance1,
  driver: driver
};

const props2 = {
  processInstance: processInstance2,
  driver: driver
};

describe('ProcessDetailsPanel component tests', () => {
  it('Snapshot testing with basic data loaded', () => {
    const wrapper = shallow(<ProcessDetailsPanel {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('should find a paragraph', () => {
    const wrapper = shallow(<ProcessDetailsPanel {...props2} />);
    expect(wrapper.find('Text').at(1).prop('component')).toBe('p');
  });
});

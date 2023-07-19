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
import ProcessDetailsNodeTrigger from '../ProcessDetailsNodeTrigger';
import {
  DropdownToggle,
  DropdownItem
} from '@patternfly/react-core/dist/js/components/Dropdown';
import { FlexItem } from '@patternfly/react-core/dist/js/layouts/Flex';
import { act } from 'react-dom/test-utils';
import { mount } from 'enzyme';
import {
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared';
import TestProcessDetailsDriver from '../../../tests/mocks/TestProcessDetailsDriver';
import wait from 'waait';
jest.mock('../../ProcessDetailsErrorModal/ProcessDetailsErrorModal');

const processInstanceData: ProcessInstance = {
  id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
  processId: 'travels',
  businessKey: 'T1234',
  parentProcessInstanceId: null,
  parentProcessInstance: null,
  processName: 'travels',
  roles: [],
  state: ProcessInstanceState.Error,
  rootProcessInstanceId: null,
  addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
  start: new Date('2019-10-22T03:40:44.089Z'),
  end: new Date('2019-10-22T05:40:44.089Z'),
  lastUpdate: new Date('2019-10-22T05:40:44.089Z'),
  error: {
    nodeDefinitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8',
    message: 'Something went wrong'
  },
  serviceUrl: 'http://localhost:4000',
  endpoint: 'http://localhost:4000',
  variables:
    '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
  nodes: [
    {
      nodeId: '1',
      name: 'End Event 1',
      definitionId: 'EndEvent_1',
      id: '870bdda0-be04-4e59-bb0b-f9b665eaacc9',
      enter: new Date('2019-10-22T03:37:38.586Z'),
      exit: new Date('2019-10-22T03:37:38.586Z'),
      type: 'EndNode'
    },
    {
      nodeId: '2',
      name: 'Confirm travel',
      definitionId: 'UserTask_2',
      id: '6b4a4fe9-4aab-4e8c-bb79-27b8b6b88d1f',
      enter: new Date('2019-10-22T03:37:30.807Z'),
      exit: new Date('2019-10-22T03:37:38.586Z'),
      type: 'HumanTaskNode'
    }
  ],
  milestones: [],
  childProcessInstances: []
};

const mockTriggerableNodes = [
  {
    nodeDefinitionId: '_BDA56801-1155-4AF2-94D4-7DAADED2E3C0',
    name: 'Send visa application',
    id: 1,
    type: 'ActionNode',
    uniqueId: '1'
  },
  {
    nodeDefinitionId: '_175DC79D-C2F1-4B28-BE2D-B583DFABF70D',
    name: 'Book',
    id: 2,
    type: 'Split',
    uniqueId: '2'
  },
  {
    nodeDefinitionId: '_E611283E-30B0-46B9-8305-768A002C7518',
    name: 'visasrejected',
    id: 3,
    type: 'EventNode',
    uniqueId: '3'
  }
];

const getNodeTriggerWrapper = async () => {
  let wrapper;
  await act(async () => {
    wrapper = mount(
      <ProcessDetailsNodeTrigger
        processInstanceData={processInstanceData}
        driver={new TestProcessDetailsDriver('123')}
      />
    );
    await wait(0);
    wrapper = wrapper.update().find('ProcessDetailsNodeTrigger');
  });
  return wrapper;
};

describe('Process details node trigger component tests', () => {
  it('snapshot testing with none selected', async () => {
    const wrapper = await getNodeTriggerWrapper();
    expect(wrapper).toMatchSnapshot();
  });

  it('select a node test ', async () => {
    let wrapper = await getNodeTriggerWrapper();
    await act(async () => {
      wrapper.find(DropdownToggle).find('button').simulate('click');
    });
    wrapper = wrapper.update();

    await act(async () => {
      wrapper.find(DropdownItem).at(1).simulate('click');
    });
    wrapper = wrapper.update();
    // snapshot with data displayed
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(FlexItem).length).toEqual(3);
    // Node name displayed
    expect(
      wrapper
        .find(FlexItem)
        .find('h6')
        .at(0)
        .children()
        .contains('Node name : ')
    ).toBeTruthy();
    // Node type displayed
    expect(
      wrapper
        .find(FlexItem)
        .find('h6')
        .at(1)
        .children()
        .contains('Node type : ')
    ).toBeTruthy();
    // Node id displayed
    expect(
      wrapper.find(FlexItem).find('h6').at(2).children().contains('Node id : ')
    ).toBeTruthy();
  });

  it('Node trigger success tests', async () => {
    const driver = new TestProcessDetailsDriver('123');
    const driverMockNodeTriggerSuccess = jest.spyOn(
      driver,
      'handleNodeTrigger'
    );
    const driverMockGetTriggerableNode = jest.spyOn(
      driver,
      'getTriggerableNodes'
    );
    driverMockGetTriggerableNode.mockResolvedValue(mockTriggerableNodes);
    driverMockNodeTriggerSuccess.mockResolvedValue();
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <ProcessDetailsNodeTrigger
          processInstanceData={processInstanceData}
          driver={new TestProcessDetailsDriver('123')}
        />
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsNodeTrigger');
    });

    await act(async () => {
      wrapper.find(DropdownToggle).find('button').simulate('click');
    });
    wrapper = wrapper.update();

    await act(async () => {
      wrapper.find(DropdownItem).at(1).simulate('click');
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper.find('#trigger').find('button').simulate('click');
    });
    wrapper = wrapper.update();
    wrapper = wrapper.find('MockedProcessDetailsErrorModal');
    // takes snapshot of the success modal
    expect(wrapper).toMatchSnapshot();
    // check the modal content
    expect(
      wrapper.find('MockedProcessDetailsErrorModal').props()['errorString']
    ).toEqual('The node Book was triggered successfully');
  });

  it('failed to retrieve nodes', async () => {
    const driver = new TestProcessDetailsDriver('123');
    const driverMockGetTriggerableNode = jest.spyOn(
      driver,
      'getTriggerableNodes'
    );
    driverMockGetTriggerableNode.mockRejectedValue({ message: '404 error' });
    let wrapper = null;
    await act(async () => {
      wrapper = mount(
        <ProcessDetailsNodeTrigger
          processInstanceData={processInstanceData}
          driver={driver}
        />
      ).find('ProcessDetailsNodeTrigger');
    });
    wrapper = wrapper.update().find('MockedProcessDetailsErrorModal');
    expect(wrapper).toMatchSnapshot();
    expect(
      wrapper.find('MockedProcessDetailsErrorModal').exists()
    ).toBeTruthy();
    expect(
      wrapper.find('MockedProcessDetailsErrorModal').props()['errorString']
    ).toEqual('Retrieval of nodes failed with error: 404 error');
  });
});

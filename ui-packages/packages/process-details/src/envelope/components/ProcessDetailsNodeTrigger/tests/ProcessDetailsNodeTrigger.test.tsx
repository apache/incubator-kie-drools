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
import { act } from 'react-dom/test-utils';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import {
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared/dist/types';
import TestProcessDetailsDriver from '../../../tests/mocks/TestProcessDetailsDriver';
jest.mock('../../ProcessDetailsErrorModal/ProcessDetailsErrorModal');
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
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

describe('Process details node trigger component tests', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('snapshot testing with none selected', async () => {
    let container;
    await act(async () => {
      container = render(
        <ProcessDetailsNodeTrigger
          processInstanceData={processInstanceData}
          driver={new TestProcessDetailsDriver('123')}
        />
      );
    });
    expect(container).toMatchSnapshot();
  });

  it('select a node and trigger ', async () => {
    let container;
    await act(async () => {
      container = render(
        <ProcessDetailsNodeTrigger
          processInstanceData={processInstanceData}
          driver={new TestProcessDetailsDriver('123')}
        />
      );
    });
    await act(async () => {
      fireEvent.click(screen.getByTestId('toggle-id'));
    });
    await waitFor(() => screen.getAllByText('Book'));
    await act(async () => {
      fireEvent.click(screen.getAllByText('Book')[0]);
    });
    await act(async () => {
      fireEvent.click(screen.getByTestId('trigger'));
    });
    await new Promise((r) => setTimeout(r, 1000));
    await waitFor(() => screen.getAllByText('Node id :'));
    expect(screen.getAllByText('Node id :')[0]).toBeTruthy();
    expect(screen.getAllByText('Node name :')[0]).toBeTruthy();
    expect(screen.getAllByText('Node type :')[0]).toBeTruthy();
  });
});

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
import {
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared/dist/types';

export const childProcessInstances: ProcessInstance[] = [
  {
    id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
    processId: 'hotelBooking',
    businessKey: 'HT221',
    parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    parentProcessInstance: {
      id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processName: 'travels',
      processId: 'Travels',
      businessKey: 'QWWE1',
      state: ProcessInstanceState.Active,
      endpoint: 'http://localhost:4000',
      start: new Date('2019-10-22T05:40:44.089Z'),
      lastUpdate: new Date('2019-10-22T05:40:44.089Z'),
      nodes: []
    },
    processName: 'HotelBooking',
    rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    roles: [],
    state: ProcessInstanceState.Completed,
    start: new Date('2019-10-22T05:40:44.089Z'),
    end: new Date('2019-10-22T05:40:44.089Z'),
    lastUpdate: new Date('2019-10-22T05:40:44.089Z'),
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
      message: 'some thing went wrong'
    },
    addons: [],
    variables:
      '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: [],
    childProcessInstances: []
  },
  {
    id: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf863',
    processId: 'flightBooking',
    businessKey: 'PT221',
    parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    parentProcessInstance: {
      id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processName: 'travels',
      processId: 'Travels',
      businessKey: 'QWWE1',
      state: ProcessInstanceState.Active,
      endpoint: 'http://localhost:4000',
      start: new Date('2019-10-22T05:40:44.089Z'),
      lastUpdate: new Date('2019-10-22T05:40:44.089Z'),
      nodes: []
    },
    processName: 'FlightBooking',
    rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    roles: [],
    state: ProcessInstanceState.Error,
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    error: {
      nodeDefinitionId: '_a23e6c20-02c2-4c2b-8c5c-e988a0adf87c',
      message:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim idest laborum.'
    },
    start: new Date('2019-10-22T05:40:44.089Z'),
    lastUpdate: new Date('2019-10-22T05:40:44.089Z'),
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: [],
    childProcessInstances: []
  },
  {
    id: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf862',
    processId: 'flightBooking',
    businessKey: 'BVS12',
    parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    parentProcessInstance: {
      id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processName: 'travels',
      processId: 'Travels',
      businessKey: 'QWWE1',
      state: ProcessInstanceState.Active,
      endpoint: 'http://localhost:4000',
      start: new Date('2019-10-22T05:40:44.089Z'),
      lastUpdate: new Date('2019-10-22T05:40:44.089Z'),
      nodes: []
    },
    processName: 'FlightBooking',
    rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    roles: [],
    state: ProcessInstanceState.Completed,
    serviceUrl: null,
    start: new Date('2019-10-22T05:40:44.089Z'),
    lastUpdate: new Date('2019-10-22T05:40:44.089Z'),
    endpoint: 'http://localhost:4000',
    error: {
      nodeDefinitionId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf823',
      message: 'some thing went wrong'
    },
    addons: [],
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
    nodes: [],
    childProcessInstances: []
  }
];

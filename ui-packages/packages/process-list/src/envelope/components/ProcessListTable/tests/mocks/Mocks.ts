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
import {
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared/dist/types';
export const ProcessInstances: ProcessInstance[] = [
  {
    id: '538f9feb-5a14-4096-b791-2055b38da7c6',
    processId: 'travels',
    businessKey: 'Tra234',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Error,
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    start: new Date('2019-10-22T03:40:44.089Z'),
    error: {
      nodeDefinitionId: '__a1e139d5-4e77-48c9-84ae-34578e9817n',
      message: 'Something went wrong'
    },
    lastUpdate: new Date('2019-10-22T03:40:44.089Z'),
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-23T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-23T22:00:00Z[UTC]","city":"New York","country":"US","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"New York","country":"US","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Berlin","country":"Germany","street":"Bakers","zipCode":"100200"},"email":"cristiano@redhat.com","firstName":"Cristiano","lastName":"Nicolai","nationality":"German"}}',
    nodes: [],
    milestones: [],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eajabbcc',
        processId: 'travels',
        businessKey: 'TP444',
        parentProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
        parentProcessInstance: null,
        rootProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
        processName: 'FlightBooking test 2',
        roles: [],
        state: ProcessInstanceState.Active,
        serviceUrl: 'http://localhost:4000',
        lastUpdate: new Date('2019-10-22T03:40:44.089Z'),
        endpoint: 'http://localhost:4000',
        addons: ['process-management'],
        error: {
          nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
          message: 'Something went wrong'
        },
        start: new Date('2019-10-22T03:40:44.089Z'),
        end: new Date('2019-10-22T03:40:44.089Z'),
        variables:
          '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
        nodes: [],
        milestones: [],
        childProcessInstances: [],
        isSelected: true,
        isOpen: false,
        errorMessage: ''
      }
    ]
  },
  {
    id: '538f9feb-5a14-4096-b791-2055b38da7c69',
    processId: 'travels',
    businessKey: 'Tra234',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Error,
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    start: new Date('2019-10-22T03:40:44.089Z'),
    error: {
      nodeDefinitionId: '__a1e139d5-4e77-48c9-84ae-34578e9817n',
      message: 'Something went wrong'
    },
    lastUpdate: new Date('2019-10-22T03:40:44.089Z'),
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-23T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-23T22:00:00Z[UTC]","city":"New York","country":"US","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"New York","country":"US","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Berlin","country":"Germany","street":"Bakers","zipCode":"100200"},"email":"cristiano@redhat.com","firstName":"Cristiano","lastName":"Nicolai","nationality":"German"}}',
    nodes: [],
    milestones: [],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eajabbcc',
        processId: 'travels',
        businessKey: 'TP444',
        parentProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
        parentProcessInstance: null,
        rootProcessInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
        processName: 'FlightBooking test 2',
        roles: [],
        state: ProcessInstanceState.Active,
        serviceUrl: 'http://localhost:4000',
        lastUpdate: new Date('2019-10-22T03:40:44.089Z'),
        endpoint: 'http://localhost:4000',
        addons: ['process-management'],
        error: {
          nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
          message: 'Something went wrong'
        },
        start: new Date('2019-10-22T03:40:44.089Z'),
        end: new Date('2019-10-22T03:40:44.089Z'),
        variables:
          '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
        nodes: [],
        milestones: [],
        childProcessInstances: [],
        isSelected: false,
        isOpen: false
      }
    ]
  }
];

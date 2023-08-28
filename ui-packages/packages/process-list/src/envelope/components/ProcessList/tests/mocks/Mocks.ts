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

export const processInstances: ProcessInstance[] = [
  {
    id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
    processId: 'travels',
    businessKey: 'HT221',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
    roles: [],
    state: ProcessInstanceState.Active,
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
    processId: 'travels',
    businessKey: 'PT221',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
    processId: 'travels',
    businessKey: 'BVS12',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'ppki-043e-375a-9f52-vfr4567',
    processId: 'travels',
    businessKey: 'PP32',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'ppki-043e-375a-9f52-vfr45678',
    processId: 'travels',
    businessKey: 'REW1',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'cfg43-043e-375a-9f52-uuy678',
    processId: 'travels',
    businessKey: 'VFR3',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'asw23-043e-375a-9f52-kjui890',
    processId: 'travels',
    businessKey: 'GGR3',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'fdr54-043e-375a-9f52-jhy786',
    processId: 'travels',
    businessKey: 'FFDE3',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'gty67-043e-375a-9f52-kwwq12',
    processId: 'travels',
    businessKey: 'VVCD3',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'xxzs2-043e-375a-9f52-zzaqwe12',
    processId: 'travels',
    businessKey: 'HHYT5',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'qqw32-043e-375a-9f52-00o982',
    processId: 'travels',
    businessKey: '55UUY',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'wqe3-043e-375a-9f52-jhy675',
    processId: 'travels',
    businessKey: 'AASQW',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'bbvgt56-043e-375a-9f52-mmnhy2',
    processId: 'travels',
    businessKey: 'VZAWQ',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'wwq1-043e-375a-9f52-bbght1',
    processId: 'travels',
    businessKey: 'KKKJUU',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: 'htrf5-043e-375a-9f52-k11qwe3',
    processId: 'travels',
    businessKey: 'MMNHG',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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
  },
  {
    id: '1wewf2-043e-375a-9f52-sdfvg321',
    processId: 'travels',
    businessKey: 'DDSWQ',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'Travels',
    rootProcessInstanceId: null,
    roles: [],
    state: ProcessInstanceState.Active,
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

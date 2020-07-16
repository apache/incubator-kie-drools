import React from 'react';
import ProcessListTableItems from '../ProcessListTableItems';
import {
  GraphQL,
  getWrapperAsync,
  getWrapper,
  KogitoEmptyState
} from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import GetChildInstancesDocument = GraphQL.GetChildInstancesDocument;
import { BrowserRouter } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';
import {
  DataListToggle,
  DataListContent,
  EmptyStateBody,
  DataListItemRow,
  Dropdown,
  KebabToggle,
  DropdownItem,
  DataListCheck
} from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';
import axios from 'axios';
jest.mock('axios');
import * as Utils from '../../../../utils/Utils';
const mockedAxios = axios as jest.Mocked<typeof axios>;
jest.mock('../../../Atoms/ProcessListModal/ProcessListModal');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoEmptyState: () => {
    return <MockedComponent />;
  }
}));

jest.mock('@patternfly/react-icons', () => ({
  ...jest.requireActual('@patternfly/react-icons'),
  ErrorCircleOIcon: () => {
    return <MockedComponent />;
  },
  InfoCircleIcon: () => {
    return <MockedComponent />;
  }
}));

/* tslint:disable */
const initData1 = {
  ProcessInstances: [
    {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
      processId: 'travels',
      businessKey: null,
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels1',
      roles: [],
      state: ProcessInstanceState.Active,
      rootProcessInstanceId: null,
      serviceUrl: 'http://localhost:4000',
      endpoint: 'http://localhost:4000/',
      addons: ['process-management'],
      error: {
        nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
        message: 'Something went wrong'
      },
      start: '2019-10-22T03:40:44.089Z',
      lastUpdate: '2019-12-25T03:40:44.089Z',
      end: null,
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
      nodes: [
        {
          nodeId: '1',
          name: 'Book Flight',
          definitionId: 'CallActivity_2',
          id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.146Z',
          type: 'SubProcessNode'
        }
      ],
      childProcessInstances: [
        {
          id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
          processName: 'FlightBooking',
          businessKey: null
        }
      ]
    },
    {
      id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
      processId: 'flightBooking',
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      isChecked: false,
      lastUpdate: '2019-10-22T03:40:44.089Z',
      processName: 'FlightBooking',
      rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      roles: [],
      state: ProcessInstanceState.Completed,
      endpoint: 'http://localhost:4000',
      addons: ['process-management'],
      error: {
        nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
        message: 'Something went wrong'
      },
      start: '2019-10-22T03:40:44.089Z',
      serviceUrl: '2019-10-22T03:40:44.089Z',
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
      nodes: [
        {
          nodeId: '1',
          name: 'End Event 1',
          definitionId: 'EndEvent_1',
          id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
          enter: '2019-10-22T04:43:01.144Z',
          exit: '2019-10-22T04:43:01.144Z',
          type: 'EndNode'
        }
      ],
      childProcessInstances: []
    }
  ]
};

const initData2 = {
  ProcessInstances: [
    {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
      processId: 'travels',
      businessKey: null,
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels1',
      roles: [],
      state: ProcessInstanceState.Error,
      rootProcessInstanceId: null,
      serviceUrl: 'http://localhost:4000',
      endpoint: 'http://localhost:4000/',
      addons: ['process-management'],
      error: {
        nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
        message: 'Something went wrong'
      },
      start: '2019-10-22T03:40:44.089Z',
      lastUpdate: '2019-12-25T03:40:44.089Z',
      end: null,
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
      nodes: [
        {
          nodeId: '1',
          name: 'Book Flight',
          definitionId: 'CallActivity_2',
          id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.146Z',
          type: 'SubProcessNode'
        }
      ],
      isChecked: true,
      childDataList: [
        {
          id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
          isChecked: true
        }
      ],
      childProcessInstances: []
    }
  ]
};

const initData3 = {
  ProcessInstances: [
    {
      id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
      processId: 'flightBooking',
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      isChecked: false,
      lastUpdate: '2019-10-22T03:40:44.089Z',
      processName: 'FlightBooking',
      rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      roles: [],
      state: ProcessInstanceState.Completed,
      endpoint: 'http://localhost:4000',
      addons: ['process-management'],
      error: {
        nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
        message: 'Something went wrong'
      },
      start: '2019-10-22T03:40:44.089Z',
      serviceUrl: '2019-10-22T03:40:44.089Z',
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
      nodes: [
        {
          nodeId: '1',
          name: 'End Event 1',
          definitionId: 'EndEvent_1',
          id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
          enter: '2019-10-22T04:43:01.144Z',
          exit: '2019-10-22T04:43:01.144Z',
          type: 'EndNode'
        }
      ],
      childProcessInstances: []
    }
  ]
};

const initData4 = {
  ProcessInstances: [
    {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
      processId: 'travels',
      businessKey: null,
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels1',
      roles: [],
      state: ProcessInstanceState.Error,
      rootProcessInstanceId: null,
      serviceUrl: 'http://localhost:4000',
      endpoint: 'http://localhost:4000/',
      addons: ['process-management'],
      error: {
        nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
        message: 'Something went wrong'
      },
      start: '2019-10-22T03:40:44.089Z',
      lastUpdate: '2019-12-25T03:40:44.089Z',
      end: null,
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
      nodes: [
        {
          nodeId: '1',
          name: 'Book Flight',
          definitionId: 'CallActivity_2',
          id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.146Z',
          type: 'SubProcessNode'
        }
      ],
      isChecked: true,
      childDataList: [
        {
          id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
          isChecked: true,
          addons: ['process-management']
        }
      ],
      childProcessInstances: []
    }
  ]
};

const initData5 = {
  ProcessInstances: [
    {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
      processId: 'travels',
      businessKey: null,
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels1',
      roles: [],
      state: ProcessInstanceState.Error,
      rootProcessInstanceId: null,
      serviceUrl: 'http://localhost:4000',
      endpoint: 'http://localhost:4000/',
      addons: ['process-management'],
      error: {
        nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
        message: 'Something went wrong'
      },
      start: '2019-10-22T03:40:44.089Z',
      lastUpdate: '2019-12-25T03:40:44.089Z',
      end: null,
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
      nodes: [
        {
          nodeId: '1',
          name: 'Book Flight',
          definitionId: 'CallActivity_2',
          id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.146Z',
          type: 'SubProcessNode'
        }
      ],
      isChecked: false,
      childDataList: [
        {
          id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
          isChecked: false,
          addons: ['process-management']
        }
      ],
      childProcessInstances: []
    }
  ]
};

const props1 = {
  id: 0,
  filters: { status: [ProcessInstanceState.Active], businessKey: [] },
  processInstanceData: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels1',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000/',
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      }
    ],
    childProcessInstances: [
      {
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processId: 'flightBooking',
        businessKey: null,
        parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
        parentProcessInstance: {
          id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
          processName: 'travels',
          businessKey: null,
          processId: 'travels',
          state: ProcessInstanceState.Completed,
          endpoint: 'http://localhost:4000/',
          start: '2019-10-22T03:40:44.089Z',
          lastUpdate: '2019-12-25T03:40:44.089Z',
          nodes: [
            {
              nodeId: '1',
              name: 'Book Flight',
              definitionId: 'CallActivity_2',
              id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
              enter: '2019-10-22T04:43:01.143Z',
              exit: '2019-10-22T04:43:01.146Z',
              type: 'SubProcessNode'
            }
          ]
        },
        processName: 'FlightBooking',
        rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
        roles: [],
        state: ProcessInstanceState.Completed,
        serviceUrl: null,
        endpoint: 'http://localhost:4000',
        addons: ['process-management'],
        error: {
          nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
          message: 'Something went wrong'
        },
        start: '2019-10-22T03:40:44.089Z',
        lastUpdate: '2019-10-22T05:40:44.089Z',
        variables:
          '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
        nodes: [
          {
            nodeId: '1',
            name: 'End Event 1',
            definitionId: 'EndEvent_1',
            id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
            enter: '2019-10-22T04:43:01.144Z',
            exit: '2019-10-22T04:43:01.144Z',
            type: 'EndNode'
          }
        ],
        childProcessInstances: []
      }
    ]
  },
  initData: initData1,
  setInitData: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: {},
  loadingInitData: false,
  setIsAllChecked: jest.fn(),
  selectedNumber: 0,
  setSelectedNumber: jest.fn()
};

const props2 = {
  id: 0,
  filters: { status: [ProcessInstanceState.Active], businessKey: [] },
  processInstanceData: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels1',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000/',
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    lastUpdate: '2019-12-25T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      }
    ],
    childProcessInstances: []
  },
  initData: initData1,
  setInitData: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: {},
  loadingInitData: false,
  setIsAllChecked: jest.fn(),
  selectedNumber: 0,
  setSelectedNumber: jest.fn()
};

const props3 = {
  id: 0,
  filters: { status: [ProcessInstanceState.Error], businessKey: [] },
  processInstanceData: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels1',
    roles: [],
    state: ProcessInstanceState.Error,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000/',
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: null,
    lastUpdate: null,
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      }
    ],
    childProcessInstances: []
  },
  initData: initData2,
  setInitData: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: {
    '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1': {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1'
    }
  },
  loadingInitData: false,
  setIsAllChecked: jest.fn(),
  selectedNumber: 1,
  setSelectedNumber: jest.fn()
};

const props4 = {
  id: 0,
  filters: { status: [ProcessInstanceState.Error], businessKey: [] },
  processInstanceData: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels1',
    roles: [],
    state: ProcessInstanceState.Error,
    rootProcessInstanceId: null,
    endpoint: null,
    serviceUrl: null,
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: null,
    lastUpdate: null,
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      }
    ],
    childProcessInstances: []
  },
  initData: initData2,
  setInitData: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: {
    '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1': {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1'
    }
  },
  loadingInitData: false,
  setIsAllChecked: jest.fn(),
  selectedNumber: 1,
  setSelectedNumber: jest.fn()
};

const props5 = {
  id: 0,
  filters: { status: [ProcessInstanceState.Error], businessKey: [] },
  processInstanceData: {
    id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
    processId: 'flightBooking',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    isChecked: true,
    lastUpdate: '2019-10-22T03:40:44.089Z',
    processName: 'FlightBooking',
    rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    roles: [],
    state: ProcessInstanceState.Completed,
    endpoint: 'http://localhost:4000',
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    serviceUrl: '2019-10-22T03:40:44.089Z',
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'End Event 1',
        definitionId: 'EndEvent_1',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'EndNode'
      }
    ],
    childProcessInstances: []
  },
  initData: initData3,
  setInitData: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: {
    'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e': {
      id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e'
    }
  },
  loadingInitData: false,
  setIsAllChecked: jest.fn(),
  selectedNumber: 1,
  setSelectedNumber: jest.fn()
};

const props6 = {
  id: 0,
  filters: { status: [ProcessInstanceState.Error], businessKey: [] },
  processInstanceData: {
    id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels1',
    roles: [],
    state: ProcessInstanceState.Error,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000/',
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: null,
    lastUpdate: null,
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      }
    ],
    childProcessInstances: []
  },
  initData: initData4,
  setInitData: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: {
    'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e': {
      id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e'
    }
  },
  loadingInitData: false,
  setIsAllChecked: jest.fn(),
  selectedNumber: 1,
  setSelectedNumber: jest.fn()
};

const props7 = {
  id: 0,
  filters: { status: [ProcessInstanceState.Error], businessKey: [] },
  processInstanceData: {
    id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
    processId: 'travels',
    businessKey: null,
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels1',
    roles: [],
    state: ProcessInstanceState.Error,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000/',
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: null,
    lastUpdate: null,
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'SubProcessNode'
      }
    ],
    childProcessInstances: []
  },
  initData: initData5,
  setInitData: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: {
    'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e': {
      id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e'
    }
  },
  loadingInitData: false,
  setIsAllChecked: jest.fn(),
  selectedNumber: 1,
  setSelectedNumber: jest.fn()
};

const mocks1 = [
  {
    request: {
      query: GetChildInstancesDocument,
      variables: {
        rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1'
      }
    },
    result: {
      data: {
        ProcessInstances: [
          {
            id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
            processId: 'flightBooking',
            businessKey: null,
            parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
            processName: 'FlightBooking',
            rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1',
            roles: [],
            state: ProcessInstanceState.Completed,
            serviceUrl: 'http://localhost:4000',
            addons: ['process-management'],
            error: {
              nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
              message: 'Something went wrong'
            },
            start: '2019-10-22T03:40:44.089Z',
            lastUpdate: '2019-10-22T05:40:44.089Z'
          }
        ]
      }
    }
  }
];

const mocks2 = [
  {
    request: {
      query: GetChildInstancesDocument,
      variables: {
        rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1'
      }
    },
    result: {
      data: {
        ProcessInstances: []
      }
    }
  }
];

const mocks3 = [
  {
    request: {
      query: GetChildInstancesDocument,
      variables: {
        rootProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b1'
      }
    },
    error: new Error('some error occured')
  }
];

describe('ProcessListTableItems component tests', () => {
  it('snapshot tests for no child instances', async () => {
    let wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks2} addTypename={false}>
        <BrowserRouter>
          <ProcessListTableItems {...props2} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessListTableItems'
    );
    await act(async () => {
      wrapper.find(DataListToggle).simulate('click');
    });
    wrapper = wrapper.update();
    wrapper = wrapper.find(DataListContent);
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(KogitoEmptyState).props()['body']).toEqual(
      'This process has no related sub processes'
    );
  });

  it('snapshot tests with child instances', async () => {
    let wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks1} addTypename={false}>
        <BrowserRouter>
          <ProcessListTableItems {...props1} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessListTableItems'
    );
    await act(async () => {
      wrapper.find(DataListToggle).simulate('click');
    });
    wrapper = wrapper.update();
    wrapper = wrapper.find('ProcessListTableItems');
    expect(
      wrapper
        .find(DataListContent)
        .at(1)
        .props()['id']
    ).toEqual(
      'kie-datalist-expand-' + mocks1[0].result.data.ProcessInstances[0].id
    );
  });

  it('snapshot tests with error in query', async () => {
    let wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks3} addTypename={false}>
        <BrowserRouter>
          <ProcessListTableItems {...props2} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessListTableItems'
    );
    await act(async () => {
      wrapper.find(DataListToggle).simulate('click');
    });
    wrapper = wrapper.update();
    expect(
      wrapper
        .find(EmptyStateBody)
        .children()
        .html()
        .includes('An error occurred while accessing data.')
    ).toBeTruthy();
  });

  it('snapshot tests with an error(status) process instance', async () => {
    let wrapper = getWrapper(
      <BrowserRouter>
        <ProcessListTableItems {...props3} />
      </BrowserRouter>,
      'ProcessListTableItems'
    );
    wrapper = wrapper.find('ErrorPopover');
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot tests with disabled popup', async () => {
    let wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks2} addTypename={false}>
        <BrowserRouter>
          <ProcessListTableItems {...props4} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessListTableItems'
    );
    await act(async () => {
      wrapper.find(DataListToggle).simulate('click');
    });
    wrapper = wrapper.update();
    wrapper = wrapper.find('DisablePopup');
    expect(wrapper).toMatchSnapshot();
  });

  describe('Skip click tests', () => {
    const handleSkipSpy = jest.spyOn(Utils, 'handleSkip');
    it('Skip button click success ', async () => {
      mockedAxios.post.mockResolvedValue({});
      let wrapper = getWrapper(
        <BrowserRouter>
          <ProcessListTableItems {...props3} />
        </BrowserRouter>,
        'ProcessListTableItems'
      );
      await act(async () => {
        wrapper
          .find(DataListItemRow)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper
          .find(DropdownItem)
          .at(1)
          .simulate('click');
      });
      wrapper = wrapper.update();
      expect(handleSkipSpy).toHaveBeenCalled();
      expect(
        wrapper
          .find('MockedProcessListModal')
          .at(0)
          .props()['modalContent']
      ).toEqual(
        'The process ' +
          props3.processInstanceData.processName +
          ' was successfully skipped.'
      );
    });
    it('Skip button click failure ', async () => {
      mockedAxios.post.mockRejectedValue({ message: '404 error' });
      let wrapper = getWrapper(
        <BrowserRouter>
          <ProcessListTableItems {...props3} />
        </BrowserRouter>,
        'ProcessListTableItems'
      );
      await act(async () => {
        wrapper
          .find(DataListItemRow)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper
          .find(DropdownItem)
          .at(1)
          .simulate('click');
      });
      wrapper = wrapper.update();
      expect(handleSkipSpy).toHaveBeenCalled();
      expect(
        wrapper
          .find('MockedProcessListModal')
          .at(0)
          .props()['modalContent']
      ).toEqual(
        'The process ' +
          props3.processInstanceData.processName +
          ' failed to skip. Message: "404 error"'
      );
    });
  });

  describe('Retry click tests', () => {
    const handleRetrySpy = jest.spyOn(Utils, 'handleRetry');
    it('Retry button click success ', async () => {
      mockedAxios.post.mockResolvedValue({});
      let wrapper = getWrapper(
        <BrowserRouter>
          <ProcessListTableItems {...props3} />
        </BrowserRouter>,
        'ProcessListTableItems'
      );
      await act(async () => {
        wrapper
          .find(DataListItemRow)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper
          .find(DropdownItem)
          .at(0)
          .simulate('click');
      });
      wrapper = wrapper.update();
      expect(handleRetrySpy).toHaveBeenCalled();
      expect(
        wrapper
          .find('MockedProcessListModal')
          .at(0)
          .props()['modalContent']
      ).toEqual(
        'The process ' +
          props3.processInstanceData.processName +
          ' was successfully re-executed.'
      );
    });
    it('Retry button click failure ', async () => {
      mockedAxios.post.mockRejectedValue({ message: '404 error' });
      let wrapper = getWrapper(
        <BrowserRouter>
          <ProcessListTableItems {...props3} />
        </BrowserRouter>,
        'ProcessListTableItems'
      );
      await act(async () => {
        wrapper
          .find(DataListItemRow)
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper
          .find(DropdownItem)
          .at(0)
          .simulate('click');
      });
      wrapper = wrapper.update();
      expect(handleRetrySpy).toHaveBeenCalled();
      expect(
        wrapper
          .find('MockedProcessListModal')
          .at(0)
          .props()['modalContent']
      ).toEqual(
        'The process ' +
          props3.processInstanceData.processName +
          ' failed to re-execute. Message: "404 error"'
      );
    });
  });

  /* tslint:disable */
  it('on checbox click test - isChecked=true', async () => {
    let wrapper = getWrapper(
      <BrowserRouter>
        <ProcessListTableItems {...props3} />
      </BrowserRouter>,
      'ProcessListTableItems'
    );
    const event = {
      target: {}
    } as React.ChangeEvent<HTMLInputElement>;
    wrapper
      .find(DataListCheck)
      .props()
      ['onChange'](true, event);

    expect(props3.setAbortedObj).toHaveBeenCalled();
    expect(props3.setInitData).toHaveBeenCalled();
    expect(props3.initData.ProcessInstances[0].isChecked).toBeFalsy();
  });

  it('on checbox click test - isChecked=false', async () => {
    let wrapper = getWrapper(
      <BrowserRouter>
        <ProcessListTableItems {...props5} />
      </BrowserRouter>,
      'ProcessListTableItems'
    );
    const event = {
      target: {}
    } as React.ChangeEvent<HTMLInputElement>;
    wrapper
      .find(DataListCheck)
      .props()
      ['onChange'](true, event);
    expect(props3.setAbortedObj).toHaveBeenCalled();
    expect(props5.initData.ProcessInstances[0].isChecked).toBeTruthy();
    expect(props3.setInitData).toHaveBeenCalled();
  });

  it('on checbox click test on child - isChecked=false ', async () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <ProcessListTableItems {...props6} />
      </BrowserRouter>,
      'ProcessListTableItems'
    );
    const event = {
      target: {}
    } as React.ChangeEvent<HTMLInputElement>;
    wrapper
      .find(DataListCheck)
      .props()
      ['onChange'](true, event);
    expect(props3.setAbortedObj).toHaveBeenCalled();
    expect(props3.setInitData).toHaveBeenCalled();
    expect(props6.initData.ProcessInstances[0].isChecked).toBeTruthy();
  });
  it('on checbox click test on child - isChecked=true ', async () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <ProcessListTableItems {...props7} />
      </BrowserRouter>,
      'ProcessListTableItems'
    );
    const event = {
      target: {}
    } as React.ChangeEvent<HTMLInputElement>;
    wrapper
      .find(DataListCheck)
      .props()
      ['onChange'](true, event);
    expect(props3.setAbortedObj).toHaveBeenCalled();
    expect(props3.setInitData).toHaveBeenCalled();
    expect(props6.initData.ProcessInstances[0].isChecked).toBeTruthy();
  });
});

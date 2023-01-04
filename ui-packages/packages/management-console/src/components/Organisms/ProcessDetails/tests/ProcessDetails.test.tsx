import React from 'react';
import { shallow } from 'enzyme';
import ProcessDetails from '../ProcessDetails';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;

const props = {
  data: {
    ProcessInstances: [
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af89',
        processId: '',
        state: ProcessInstanceState.Active,
        parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
        endpoint: 'test',
        businessKey: 'Tr21',
        start: '2019-10-22T03:40:44.089Z',
        end: '2019-10-22T03:40:44.089Z',
        parentProcessInstance: null,
        childProcessInstances: [],
        lastUpdate: '2019-10-22T03:40:44.089Z'
      }
    ]
  },
  from: { prev: '' },
  loading: true,
  childLoading: true,
  parentLoading: true,
  childResult: [
    {
      id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eaccd',
      processName: 'FlightBooking test 1',
      parentProcessInstanceId: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e'
    }
  ],
  parentResult: {
    parentProcessInstanceId: null,
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processName: 'travels'
  }
};

const props2 = {
  loading: true,
  data: {
    ProcessInstances: [
      {
        id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
        processId: 'hotelBooking',
        state: ProcessInstanceState.Active,
        parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
        endpoint: 'test',
        start: null,
        serviceUrl: 'http://localhost:4000/',
        end: '2019-10-22T03:40:44.089Z',
        parentProcessInstance: {
          id: '2d962eef-45b8-48a9-ad4e-11-22',
          processName: 'Travels22',
          businessKey: 'Tra11',
          processId: 'Travels33',
          state: ProcessInstanceState.Completed,
          endpoint: 'http://localhost:4000/',
          servuceUrl: 'http://localhost:4000/',
          nodes: [
            {
              nodeId: '1',
              name: 'End Event 1',
              definitionId: 'EndEvent_1',
              id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
              enter: '2019-10-22T03:37:30.798Z',
              exit: '2019-10-22T03:37:30.798Z',
              type: 'EndNode'
            }
          ],
          start: '2019-10-22T03:40:44.089Z',
          lastUpdate: '2019-10-22T03:40:44.089Z'
        },
        childProcessInstances: [
          {
            id: '23944e2-874R22-48a9-abcd-11-22',
            processName: 'Travels33',
            businessKey: 'Tra33',
            processId: 'Travels33',
            state: ProcessInstanceState.Completed,
            endpoint: 'http://localhost:4000/',
            servuceUrl: 'http://localhost:4000/',
            start: '2019-10-22T03:40:44.089Z',
            nodes: [
              {
                nodeId: '1',
                name: 'End Event 1',
                definitionId: 'EndEvent_1',
                id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
                enter: '2019-10-22T03:37:30.798Z',
                exit: '2019-10-22T03:37:30.798Z',
                type: 'EndNode'
              }
            ],
            lastUpdate: '2019-10-22T03:40:44.089Z'
          }
        ],
        lastUpdate: '2019-10-22T03:40:44.089Z'
      }
    ]
  },
  from: {},
  childLoading: true,
  parentLoading: true,
  parentResult: {
    parentProcessInstanceId: null,
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processName: 'travels'
  },

  childResult: []
};

describe('ProcessDetails component tests', () => {
  it('Snapshot testing with basic data loaded', () => {
    const wrapper = shallow(<ProcessDetails {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('should find a paragraph', () => {
    const wrapper = shallow(<ProcessDetails {...props2} />);
    expect(wrapper.find('Text').at(1).prop('component')).toBe('p');
  });
});

import React from 'react';
import { shallow } from 'enzyme';
import ProcessDetails from '../ProcessDetails';
import { ProcessInstanceState } from '../../../../graphql/types';

const props = {
  data: {
    ProcessInstances: [
      {
        id: '',
        processId: '',
        state: ProcessInstanceState.Active,
        parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
        endpoint: 'test',
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
        start: '2019-10-22T03:40:44.089Z',
        end: '2019-10-22T03:40:44.089Z',
        parentProcessInstance: null,
        childProcessInstances: [],
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

describe('Process Details component', () => {
  it('Snapshot tests', () => {
    const wrapper = shallow(<ProcessDetails {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('Sample test case', () => {
    const wrapper = shallow(<ProcessDetails {...props2} />);
    expect(
      wrapper
        .find('Text')
        .at(1)
        .prop('component')
    ).toBe('p');
  });
});

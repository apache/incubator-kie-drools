import React from 'react';
import { shallow } from 'enzyme';
import DataToolbarComponent from '../DataToolbarComponent';
import { ProcessInstanceState } from '../../../../graphql/types';

const initData = {
  ProcessInstances: [
    {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      processId: 'travels',
      parentProcessInstanceId: null,
      parentProcessInstance: null,
      processName: 'travels',
      roles: [],
      state: ProcessInstanceState.Active,
      rootProcessInstanceId: null,
      endpoint: 'http://localhost:4000',
      addons: [
        'jobs-management',
        'prometheus-monitoring',
        'process-management'
      ],
      error: {
        nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
        message: 'Something went wrong'
      },
      start: '2019-10-22T03:40:44.089Z',
      end: null,
      variables:
        '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
      nodes: [
        {
          name: 'Book Flight',
          definitionId: 'CallActivity_2',
          id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.146Z',
          type: 'SubProcessNode'
        },
        {
          name: 'Confirm travel',
          definitionId: 'UserTask_2',
          id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
          enter: '2019-10-22T04:43:01.148Z',
          exit: null,
          type: 'HumanTaskNode'
        },
        {
          name: 'Join',
          definitionId: 'ParallelGateway_2',
          id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
          enter: '2019-10-22T04:43:01.148Z',
          exit: '2019-10-22T04:43:01.148Z',
          type: 'Join'
        },
        {
          name: 'Book Hotel',
          definitionId: 'CallActivity_1',
          id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
          enter: '2019-10-22T04:43:01.146Z',
          exit: '2019-10-22T04:43:01.148Z',
          type: 'SubProcessNode'
        },
        {
          name: 'Book',
          definitionId: 'ParallelGateway_1',
          id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.146Z',
          type: 'Split'
        },
        {
          name: 'Join',
          definitionId: 'ExclusiveGateway_2',
          id: 'b2761011-3043-4f48-82bd-1395bf651a91',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.143Z',
          type: 'Join'
        },
        {
          name: 'is visa required',
          definitionId: 'ExclusiveGateway_1',
          id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
          enter: '2019-10-22T04:43:01.143Z',
          exit: '2019-10-22T04:43:01.143Z',
          type: 'Split'
        },
        {
          name: 'Visa check',
          definitionId: 'BusinessRuleTask_1',
          id: '1baa5de4-47cc-45a8-8323-005388191e4f',
          enter: '2019-10-22T04:43:01.135Z',
          exit: '2019-10-22T04:43:01.143Z',
          type: 'RuleSetNode'
        },
        {
          name: 'StartProcess',
          definitionId: 'StartEvent_1',
          id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
          enter: '2019-10-22T04:43:01.135Z',
          exit: '2019-10-22T04:43:01.135Z',
          type: 'StartNode'
        }
      ],
      childProcessInstances: [
        {
          id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
          processName: 'FlightBooking'
        },
        {
          id: '2d962eef-45b8-48a9-ad4e-9cde0ad6af88',
          processName: 'HotelBooking'
        }
      ]
    }
  ]
};

const props = {
  checkedArray: ['ACTIVE', 'COMPLETED', 'ERROR', 'ABORTED', 'SUSPENDED'],
  filterClick: jest.fn(),
  setCheckedArray: jest.fn(),
  setIsStatusSelected: jest.fn(),
  filters: ['ACTIVE,COMPLETED'],
  setFilters: jest.fn(),
  setInitData: jest.fn(),
  handleAbortAll: jest.fn(),
  setAbortedObj: jest.fn(),
  abortedObj: { '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': 'travels' },
  initData,
  getProcessInstances: jest.fn(),
  pageSize: 10,
  setFilteredData: jest.fn(),
  setSearchWord: jest.fn(),
  searchWord: '',
  setIsClearAllClicked: jest.fn(),
  handleCheckAll: jest.fn(),
  isAllChecked: false,
  setIsAllChecked: jest.fn(),
  setSelectedNumber: jest.fn(),
  selectedNumber: 0
};

describe('DataToolbar component tests', () => {
  it('Snapshot tests', () => {
    const wrapper = shallow(<DataToolbarComponent {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
});

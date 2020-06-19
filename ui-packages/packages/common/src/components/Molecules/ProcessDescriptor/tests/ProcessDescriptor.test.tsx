import React from 'react';
import { shallow } from 'enzyme';
import ProcessDescriptor from './../ProcessDescriptor';
import { GraphQL } from '../../../../graphql/types';
import ProcessInstanceState = GraphQL.ProcessInstanceState;

const processInstanceData1 = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  processId: 'hotelBooking',
  businessKey: 'T1234HotelBooking01',
  parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
  parentProcessInstance: null,
  processName: 'HotelBooking',
  rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
  roles: [],
  state: ProcessInstanceState.Completed,
  start: '2019-10-22T03:40:44.089Z',
  lastUpdate: '2019-10-22T03:40:44.089Z',
  end: '2019-10-22T05:40:44.089Z',
  endpoint: 'http://localhost:4000',
  error: {
    nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
    message: 'some thing went wrong'
  },
  addons: [],
  variables:
    '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
  nodes: [
    {
      nodeId: '1',
      name: 'End Event 1',
      definitionId: 'EndEvent_1',
      id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
      enter: '2019-10-22T03:37:30.798Z',
      exit: '2019-10-22T03:37:30.798Z',
      type: 'EndNode'
    },
    {
      nodeId: '2',
      name: 'Book hotel',
      definitionId: 'ServiceTask_1',
      id: '41b3f49e-beb3-4b5f-8130-efd28f82b971',
      enter: '2019-10-22T03:37:30.795Z',
      exit: '2019-10-22T03:37:30.798Z',
      type: 'WorkItemNode'
    },
    {
      nodeId: '2',
      name: 'StartProcess',
      definitionId: 'StartEvent_1',
      id: '4165a571-2c79-4fd0-921e-c6d5e7851b67',
      enter: '2019-10-22T03:37:30.793Z',
      exit: '2019-10-22T03:37:30.795Z',
      type: 'StartNode'
    }
  ],
  childProcessInstances: []
};

const processInstanceData2 = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  processId: 'hotelBooking',
  businessKey: null,
  parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
  parentProcessInstance: null,
  processName: 'HotelBooking',
  rootProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
  roles: [],
  state: ProcessInstanceState.Completed,
  start: '2019-10-22T03:40:44.089Z',
  lastUpdate: '2019-10-22T03:40:44.089Z',
  end: '2019-10-22T05:40:44.089Z',
  endpoint: 'http://localhost:4000',
  error: {
    nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
    message: 'some thing went wrong'
  },
  addons: [],
  variables:
    '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
  nodes: [
    {
      nodeId: '1',
      name: 'End Event 1',
      definitionId: 'EndEvent_1',
      id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
      enter: '2019-10-22T03:37:30.798Z',
      exit: '2019-10-22T03:37:30.798Z',
      type: 'EndNode'
    },
    {
      nodeId: '2',
      name: 'Book hotel',
      definitionId: 'ServiceTask_1',
      id: '41b3f49e-beb3-4b5f-8130-efd28f82b971',
      enter: '2019-10-22T03:37:30.795Z',
      exit: '2019-10-22T03:37:30.798Z',
      type: 'WorkItemNode'
    },
    {
      nodeId: '2',
      name: 'StartProcess',
      definitionId: 'StartEvent_1',
      id: '4165a571-2c79-4fd0-921e-c6d5e7851b67',
      enter: '2019-10-22T03:37:30.793Z',
      exit: '2019-10-22T03:37:30.795Z',
      type: 'StartNode'
    }
  ],
  childProcessInstances: []
};
describe('ProcessDescriptor component tests', () => {
  it('snapshot testing for business key available', () => {
    const wrapper = shallow(
      <ProcessDescriptor processInstanceData={processInstanceData1} />
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing for buisness key null', () => {
    const wrapper = shallow(
      <ProcessDescriptor processInstanceData={processInstanceData2} />
    );
    expect(wrapper).toMatchSnapshot();
  });
});

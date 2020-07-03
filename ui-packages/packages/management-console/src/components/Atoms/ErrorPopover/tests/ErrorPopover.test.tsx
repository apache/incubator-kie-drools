import React from 'react';
import { mount } from 'enzyme';
import ErrorPopover from '../ErrorPopover';
import { GraphQL, getWrapper } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
const props1 = {
  processInstanceData: {
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
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    lastUpdate: '2019-10-22T03:40:44.089Z',
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
        enter: '2019-10-22T03:37:38.586Z',
        exit: '2019-10-22T03:37:38.586Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '6b4a4fe9-4aab-4e8c-bb79-27b8b6b88d1f',
        enter: '2019-10-22T03:37:30.807Z',
        exit: '2019-10-22T03:37:38.586Z',
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: 'dd33de7c-c39c-484a-83a8-3e1b007fce95',
        enter: '2019-10-22T03:37:30.793Z',
        exit: '2019-10-22T03:37:30.803Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '4',
        name: 'Join',
        definitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8',
        id: '08c153e8-2766-4675-81f7-29943efdf411',
        enter: '2019-10-22T03:37:30.806Z',
        exit: '2019-10-22T03:37:30.807Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '683cf307-f082-4a8e-9c85-d5a11b13903a',
        enter: '2019-10-22T03:37:30.803Z',
        exit: '2019-10-22T03:37:30.806Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'cf057e58-4113-46c0-be13-6de42ea8377e',
        enter: '2019-10-22T03:37:30.792Z',
        exit: '2019-10-22T03:37:30.803Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: '415a52c0-dc1f-4a93-9238-862dc8072262',
        enter: '2019-10-22T03:37:30.792Z',
        exit: '2019-10-22T03:37:30.792Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: '52d64298-3f28-4aba-a812-dba4077c9665',
        enter: '2019-10-22T03:37:30.79Z',
        exit: '2019-10-22T03:37:30.792Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '6fdee287-08f6-49c2-af2d-2d125ba76ab7',
        enter: '2019-10-22T03:37:30.755Z',
        exit: '2019-10-22T03:37:30.79Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: 'd98c1762-9d3c-4228-9ffc-bc3f423079c0',
        enter: '2019-10-22T03:37:30.753Z',
        exit: '2019-10-22T03:37:30.754Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  onSkipClick: jest.fn(),
  onRetryClick: jest.fn()
};

const props2 = {
  processInstanceData: {
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
    start: '2019-10-22T03:40:44.089Z',
    end: '2019-10-22T05:40:44.089Z',
    lastUpdate: '2019-10-22T03:40:44.089Z',
    error: null,
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
        enter: '2019-10-22T03:37:38.586Z',
        exit: '2019-10-22T03:37:38.586Z',
        type: 'EndNode'
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '6b4a4fe9-4aab-4e8c-bb79-27b8b6b88d1f',
        enter: '2019-10-22T03:37:30.807Z',
        exit: '2019-10-22T03:37:38.586Z',
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: 'dd33de7c-c39c-484a-83a8-3e1b007fce95',
        enter: '2019-10-22T03:37:30.793Z',
        exit: '2019-10-22T03:37:30.803Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '4',
        name: 'Join',
        definitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8',
        id: '08c153e8-2766-4675-81f7-29943efdf411',
        enter: '2019-10-22T03:37:30.806Z',
        exit: '2019-10-22T03:37:30.807Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '683cf307-f082-4a8e-9c85-d5a11b13903a',
        enter: '2019-10-22T03:37:30.803Z',
        exit: '2019-10-22T03:37:30.806Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'cf057e58-4113-46c0-be13-6de42ea8377e',
        enter: '2019-10-22T03:37:30.792Z',
        exit: '2019-10-22T03:37:30.803Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: '415a52c0-dc1f-4a93-9238-862dc8072262',
        enter: '2019-10-22T03:37:30.792Z',
        exit: '2019-10-22T03:37:30.792Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: '52d64298-3f28-4aba-a812-dba4077c9665',
        enter: '2019-10-22T03:37:30.79Z',
        exit: '2019-10-22T03:37:30.792Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '6fdee287-08f6-49c2-af2d-2d125ba76ab7',
        enter: '2019-10-22T03:37:30.755Z',
        exit: '2019-10-22T03:37:30.79Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: 'd98c1762-9d3c-4228-9ffc-bc3f423079c0',
        enter: '2019-10-22T03:37:30.753Z',
        exit: '2019-10-22T03:37:30.754Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  onSkipClick: jest.fn(),
  onRetryClick: jest.fn()
};

describe('Errorpopover component tests', () => {
  it('snapshot testing with error object', () => {
    const wrapper = getWrapper(<ErrorPopover {...props1} />, 'ErrorPopover');
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing without error object', () => {
    const wrapper = getWrapper(<ErrorPopover {...props2} />, 'ErrorPopover');
    expect(wrapper).toMatchSnapshot();
  });
  it('handle skip test', async () => {
    const wrapper = mount(<ErrorPopover {...props2} />);
    wrapper
      .find('Popover')
      .prop('footerContent')[0]
      .props.onClick();

    expect(props2.onSkipClick).toHaveBeenCalled();
  });
  it('handle Retry test', async () => {
    const wrapper = mount(<ErrorPopover {...props2} />);
    wrapper
      .find('Popover')
      .prop('footerContent')[1]
      .props.onClick();
    expect(props2.onRetryClick).toHaveBeenCalled();
  });
});

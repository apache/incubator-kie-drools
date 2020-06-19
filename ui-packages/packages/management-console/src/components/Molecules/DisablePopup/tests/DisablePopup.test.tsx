import React from 'react';
import { shallow } from 'enzyme';
import DisablePopup from '../DisablePopup';
import { Checkbox } from '@patternfly/react-core';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;

const props1 = {
  processInstanceData: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0blmnop',
    processId: 'travels',
    businessKey: 'Tr1122',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-12-22T03:40:44.089Z',
    lastUpdate: '2019-12-22T03:40:44.089Z',
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
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  component: <Checkbox id="test" />
};
const props2 = {
  processInstanceData: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0blmnop',
    processId: 'travels',
    businessKey: 'Tr1122',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-12-22T03:40:44.089Z',
    lastUpdate: '2019-12-22T03:40:44.089Z',
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
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  component: <Checkbox id="test" />
};

const props3 = {
  processInstanceData: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0blmnop',
    processId: 'travels',
    businessKey: 'Tr1122',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-12-22T03:40:44.089Z',
    lastUpdate: '2019-12-22T03:40:44.089Z',
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
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  component: <Checkbox id="test" />
};

const props4 = {
  processInstanceData: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0blmnop',
    processId: 'travels',
    businessKey: 'Tr1122',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    addons: [],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: '2019-12-22T03:40:44.089Z',
    lastUpdate: '2019-12-22T03:40:44.089Z',
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
      },
      {
        nodeId: '2',
        name: 'Confirm travel',
        definitionId: 'UserTask_2',
        id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
        enter: '2019-10-22T04:43:01.148Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        nodeId: '3',
        name: 'Join',
        definitionId: 'ParallelGateway_2',
        id: 'fd2e12d5-6a4b-4c75-9f31-028d3f032a95',
        enter: '2019-10-22T04:43:01.148Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'Join'
      },
      {
        nodeId: '4',
        name: 'Book Hotel',
        definitionId: 'CallActivity_1',
        id: '7f7d74c1-78f7-49be-b5ad-8d132f46a49c',
        enter: '2019-10-22T04:43:01.146Z',
        exit: '2019-10-22T04:43:01.148Z',
        type: 'SubProcessNode'
      },
      {
        nodeId: '5',
        name: 'Book',
        definitionId: 'ParallelGateway_1',
        id: 'af0d984c-4abd-4f5c-83a8-426e6b3d102a',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.146Z',
        type: 'Split'
      },
      {
        nodeId: '6',
        name: 'Join',
        definitionId: 'ExclusiveGateway_2',
        id: 'b2761011-3043-4f48-82bd-1395bf651a91',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Join'
      },
      {
        nodeId: '7',
        name: 'is visa required',
        definitionId: 'ExclusiveGateway_1',
        id: 'a91a2600-d0cd-46ff-a6c6-b3081612d1af',
        enter: '2019-10-22T04:43:01.143Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'Split'
      },
      {
        nodeId: '8',
        name: 'Visa check',
        definitionId: 'BusinessRuleTask_1',
        id: '1baa5de4-47cc-45a8-8323-005388191e4f',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.143Z',
        type: 'RuleSetNode'
      },
      {
        nodeId: '9',
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
        enter: '2019-10-22T04:43:01.135Z',
        exit: '2019-10-22T04:43:01.135Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  component: <Checkbox id="test" />
};

describe('DisablePopup component tests', () => {
  it('snapshot testing for no service URL and only process-management addon', () => {
    const wrapper = shallow(<DisablePopup {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing for no service URL and no process-management addon', () => {
    const wrapper = shallow(<DisablePopup {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing for service URL and process-management addon available', () => {
    const wrapper = shallow(<DisablePopup {...props3} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing for no process-management addon and only service URL', () => {
    const wrapper = shallow(<DisablePopup {...props4} />);
    expect(wrapper).toMatchSnapshot();
  });
});

import React from 'react';
import { shallow } from 'enzyme';
import ProcessDetailsTimeline from '../ProcessDetailsTimeline';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;

const props1 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    serviceUrl: '2019-10-22T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        name: 'End Event 1',
        definitionId: 'abc-efg-hij',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'EndNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: '2019-10-22T04:43:01.144Z',
        exit: '2019-10-22T04:43:01.144Z',
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  setModalTitle: jest.fn(),
  setTitleType: jest.fn(),
  setModalContent: jest.fn(),
  handleSkipModalToggle: jest.fn(),
  handleRetryModalToggle: jest.fn()
};

const props2 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: '2019-10-22T03:40:44.089Z',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        name: 'End Event 1',
        definitionId: 'abc-efg-hij',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: '2019-10-22T04:43:01.144Z',
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: '2019-10-22T04:43:01.144Z',
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: '2019-10-22T04:43:01.144Z',
        exit: null,
        type: 'HumanTaskNode'
      }
    ],
    childProcessInstances: []
  },
  setModalTitle: jest.fn(),
  setTitleType: jest.fn(),
  setModalContent: jest.fn(),
  handleSkipModalToggle: jest.fn(),
  handleRetryModalToggle: jest.fn()
};
/* tslint:disable */

describe('ProcessDetailsTimeline component tests', () => {
  it('Snapshot testing for service url available', () => {
    const wrapper = shallow(<ProcessDetailsTimeline {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot testing for no service url', () => {
    const wrapper = shallow(<ProcessDetailsTimeline {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('onSelect click test', () => {
    const wrapper = shallow(<ProcessDetailsTimeline {...props2} />);

    const event = {
      currentTarget: {}
    } as React.ChangeEvent<HTMLInputElement>;
    wrapper
      .find('#dropdown-kebab')
      .props()
      ['onSelect'](event);
    expect(wrapper.find('#dropdown-kebab').props()['isOpen']).toBeTruthy();
  });

  it('onToggle click test', () => {
    const wrapper = shallow(<ProcessDetailsTimeline {...props2} />);
    wrapper
      .find('#dropdown-kebab')
      .props()
      ['toggle']['props']['onToggle']();
  });

  it('handle and handle retry click test', () => {
    const wrapper = shallow(<ProcessDetailsTimeline {...props2} />);

    wrapper
      .find('#dropdown-kebab')
      .props()
      ['dropdownItems'][0]['props']['onClick']();
    wrapper
      .find('#dropdown-kebab')
      .props()
      ['dropdownItems'][1]['props']['onClick']();
  });
});

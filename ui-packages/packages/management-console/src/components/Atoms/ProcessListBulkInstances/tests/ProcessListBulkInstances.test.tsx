import React from 'react';
import { shallow } from 'enzyme';
import ProcessListBulkInstances from '../ProcessListBulkInstances';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;

const props1 = {
  operationResult: {
    messages: {
      successMessage: 'Aborted',
      ignoredMessage:
        'These processes were ignored because they were completed or aborted',
      noProcessMessage: 'No processes were aborted'
    },
    results: {
      successInstances: {
        '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
          id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          processId: 'travels',
          businessKey: null,
          parentProcessInstanceId: null,
          parentProcessInstance: null,
          processName: 'travels',
          roles: [],
          state: ProcessInstanceState.Active,
          rootProcessInstanceId: null,
          serviceUrl: 'http://localhost:4000',
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
          lastUpdated: '2019-10-22T03:40:44.089Z',
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
        }
      } as any,
      failedInstances: {} as any,
      ignoredInstances: {
        'e735128t-6tt7-4aa8-9ec0-e18e19809e0b': {
          id: 'e735128t-6tt7-4aa8-9ec0-e18e19809e0b',
          processId: 'travels',
          parentProcessInstanceId: null,
          parentProcessInstance: null,
          processName: 'travels',
          roles: [],
          businessKey: null,
          state: 'COMPLETED',
          rootProcessInstanceId: null,
          serviceUrl: null,
          endpoint: 'http://localhost:4000',
          addons: ['process-management'],
          error: {
            nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
            message: 'Something went wrong'
          },
          start: '2019-12-22T03:40:44.089Z',
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
        }
      } as any
    },
    functions: {
      perform: jest.fn(),
      changeProcessStatus: jest.fn()
    }
  }
};

const props2 = {
  operationResult: {
    messages: {
      successMessage: 'Aborted',
      ignoredMessage:
        'These processes were ignored because they were completed or aborted',
      noProcessMessage: 'No processes were aborted'
    },
    results: {
      successMessage: 'Aborted',
      ignoredMessage:
        'These processes were ignored because they were completed or aborted',
      successInstances: {} as any,
      failedInstances: {} as any,
      ignoredInstances: {
        'e735128t-6tt7-4aa8-9ec0-e18e19809e0b': {
          id: 'e735128t-6tt7-4aa8-9ec0-e18e19809e0b',
          processId: 'travels',
          parentProcessInstanceId: null,
          parentProcessInstance: null,
          processName: 'travels',
          roles: [],
          businessKey: null,
          state: 'COMPLETED',
          rootProcessInstanceId: null,
          serviceUrl: null,
          endpoint: 'http://localhost:4000',
          addons: ['process-management'],
          error: {
            nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
            message: 'Something went wrong'
          },
          start: '2019-12-22T03:40:44.089Z',
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
        }
      } as any
    },
    functions: {
      perform: jest.fn(),
      changeProcessStatus: jest.fn()
    }
  }
};

const props3 = {
  operationResult: {
    messages: {
      successMessage: 'Aborted',
      ignoredMessage:
        'These processes were ignored because they were completed or aborted',
      noProcessMessage: 'No processes were aborted'
    },
    results: {
      successMessage: 'Aborted',
      ignoredMessage:
        'These processes were ignored because they were completed or aborted',
      successInstances: {
        '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
          id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          processId: 'travels',
          businessKey: null,
          parentProcessInstanceId: null,
          parentProcessInstance: null,
          processName: 'travels',
          roles: [],
          state: ProcessInstanceState.Active,
          rootProcessInstanceId: null,
          serviceUrl: 'http://localhost:4000',
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
          lastUpdated: '2019-10-22T03:40:44.089Z',
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
        } as any
      },
      failedInstances: {},
      ignoredInstances: {}
    },
    functions: {
      perform: jest.fn(),
      changeProcessStatus: jest.fn()
    }
  }
};

const props4 = {
  operationResult: {
    messages: {
      successMessage: 'Aborted',
      ignoredMessage:
        'These processes were ignored because they were completed or aborted',
      noProcessMessage: 'No processes were aborted',
      warningMessage:
        'Note: The process status has been updated. The list may appear inconsistent until you refresh any applied filters.'
    },
    results: {
      successInstances: {
        '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
          id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          processId: 'travels',
          businessKey: null,
          parentProcessInstanceId: null,
          parentProcessInstance: null,
          processName: 'travels',
          roles: [],
          state: ProcessInstanceState.Active,
          rootProcessInstanceId: null,
          serviceUrl: 'http://localhost:4000',
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
          lastUpdated: '2019-10-22T03:40:44.089Z',
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
        } as any,
        'ceb1234-6ae4-deb444-9ec0-neb9809e0b': {
          id: '8ceb1234-6ae4-deb444-9ec0-neb9809e0b',
          processId: 'travels1',
          businessKey: null,
          parentProcessInstanceId: null,
          parentProcessInstance: null,
          processName: 'travels1',
          roles: [],
          state: ProcessInstanceState.Active,
          rootProcessInstanceId: null,
          serviceUrl: 'http://localhost:4000',
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
          lastUpdated: '2019-10-22T03:40:44.089Z',
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
        } as any
      },
      failedInstances: {
        'ceb1234-6ae4-deb444-9ec0-neb9809e0b': {
          id: '8ceb1234-6ae4-deb444-9ec0-neb9809e0b',
          processId: 'travels1',
          businessKey: null,
          parentProcessInstanceId: null,
          parentProcessInstance: null,
          processName: 'travels1',
          roles: [],
          state: ProcessInstanceState.Active,
          rootProcessInstanceId: null,
          serviceUrl: 'http://localhost:4000',
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
          lastUpdated: '2019-10-22T03:40:44.089Z',
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
        } as any
      },
      ignoredInstances: {}
    },
    functions: {
      perform: jest.fn(),
      changeProcessStatus: jest.fn()
    }
  }
};

describe('ProcessBulkList component tests', () => {
  it('snapshot testing multi-abort with aborted and skipped instances ', () => {
    const wrapper = shallow(<ProcessListBulkInstances {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing multi-abort with no aborted instances and only skipped instances', () => {
    const wrapper = shallow(<ProcessListBulkInstances {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing multi-abort with no skipped instances', () => {
    const wrapper = shallow(<ProcessListBulkInstances {...props3} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing for a single abort instance', () => {
    const wrapper = shallow(<ProcessListBulkInstances {...props4} />);
    expect(wrapper).toMatchSnapshot();
  });
});

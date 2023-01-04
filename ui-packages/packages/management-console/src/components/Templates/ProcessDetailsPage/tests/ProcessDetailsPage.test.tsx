import React from 'react';
import * as H from 'history';
import ProcessDetailsPage from '../ProcessDetailsPage';
import { MockedProvider } from '@apollo/react-testing';
import { BrowserRouter } from 'react-router-dom';
import { GraphQL } from '@kogito-apps/common';
import { mount } from 'enzyme';
import GetProcessInstanceByIdDocument = GraphQL.GetProcessInstanceByIdDocument;
import GetJobsByProcessInstanceIdDocument = GraphQL.GetJobsByProcessInstanceIdDocument;
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import MilestoneStatus = GraphQL.MilestoneStatus;
import { Button } from '@patternfly/react-core';
import axios from 'axios';
jest.mock('axios');
import * as Utils from '../../../../utils/Utils';
import { act } from 'react-dom/test-utils';
import _ from 'lodash';
import InlineSVG from 'react-inlinesvg';
import wait from 'waait';
// tslint:disable: no-string-literal
const mockedAxios = axios as jest.Mocked<typeof axios>;
jest.mock('../../../Atoms/ProcessListModal/ProcessListModal');
jest.mock('../../../Atoms/BulkList/BulkList');
jest.mock('../../../Organisms/ProcessDetails/ProcessDetails');
jest.mock(
  '../../../Organisms/ProcessDetailsProcessDiagram/ProcessDetailsProcessDiagram'
);
jest.mock(
  '../../../Organisms/ProcessDetailsMilestones/ProcessDetailsMilestones'
);
jest.mock('../../../Organisms/ProcessDetailsJobsPanel/ProcessDetailsJobsPanel');
jest.mock(
  '../../../Organisms/ProcessDetailsProcessVariables/ProcessDetailsProcessVariables'
);
jest.mock('../../../Organisms/ProcessDetailsTimeline/ProcessDetailsTimeline');
jest.mock(
  '../../../Organisms/ProcessDetailsNodeTrigger/ProcessDetailsNodeTrigger'
);
const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    OnRunningIcon: () => {
      return <MockedComponent />;
    },
    CheckCircleIcon: () => {
      return <MockedComponent />;
    },
    BanIcon: () => {
      return <MockedComponent />;
    },
    PausedIcon: () => {
      return <MockedComponent />;
    },
    ErrorCircleOIcon: () => {
      return <MockedComponent />;
    },
    AngleRightIcon: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@kogito-apps/common', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/common'), {
    ItemDescriptor: () => {
      return <MockedComponent />;
    },
    KogitoSpinner: () => {
      return <MockedComponent />;
    },
    ServerErrors: () => {
      return <MockedComponent />;
    }
  })
);

const props = {
  match: {
    params: {
      instanceID: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
    },
    url: '',
    isExact: false,
    path: ''
  },
  location: H.createLocation(''),
  history: H.createBrowserHistory()
};
props.location.state = {
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: []
  }
};
const props1 = {
  match: {
    params: {
      instanceID: '8035b580-6ae4-4aa8-9ec0-e18e19809e0bc'
    },
    url: '',
    isExact: false,
    path: ''
  },
  location: H.createLocation(''),
  history: H.createBrowserHistory()
};
props.location.state = {
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: ['tra']
  }
};
const mocks1 = [
  {
    request: {
      query: GetProcessInstanceByIdDocument,
      variables: {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      },
      fetchPolicy: 'network-only'
    },
    result: {
      loading: false,
      data: {
        ProcessInstances: [
          {
            id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
            processId: 'Travels',
            processName: 'travels',
            businessKey: null,
            parentProcessInstanceId: null,
            parentProcessInstance: null,
            roles: [],
            variables:
              '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
            state: ProcessInstanceState.Active,
            start: '2019-10-22T03:40:44.089Z',
            lastUpdate: '2019-10-22T03:40:44.089Z',
            end: null,
            endpoint: 'http://localhost:4000',
            addons: ['process-management'],
            serviceUrl: 'http://localhost:4000',
            error: {
              nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
              message: 'Something went wrong'
            },
            childProcessInstances: [],
            nodes: [
              {
                id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
                nodeId: '9',
                name: 'StartProcess',
                enter: '2019-10-22T04:43:01.135Z',
                exit: '2019-10-22T04:43:01.135Z',
                type: 'StartNode',
                definitionId: 'StartEvent_1'
              }
            ],
            milestones: [
              {
                id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
                name: 'Milestone 1: Order placed',
                status: MilestoneStatus['Active'],
                __typename: 'Milestones'
              },
              {
                id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
                name: 'Milestone 2: Order shipped',
                status: MilestoneStatus['Available'],
                __typename: 'Milestones'
              },
              {
                id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
                name: 'Manager decision',
                status: MilestoneStatus['Completed'],
                __typename: 'Milestones'
              }
            ]
          }
        ]
      }
    }
  },
  {
    request: {
      query: GetJobsByProcessInstanceIdDocument,
      variables: {
        processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      },
      fetchPolicy: 'network-only'
    },
    result: {
      loading: false,
      refetch: jest.fn(),
      data: {
        Jobs: [
          {
            id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            processId: 'travels',
            processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
            rootProcessId: null,
            status: 'EXECUTED',
            priority: 0,
            callbackEndpoint:
              'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            repeatInterval: null,
            repeatLimit: null,
            scheduledId: '0',
            retries: 0,
            lastUpdate: '2020-08-27T03:35:50.147Z',
            expirationTime: null,
            endpoint: 'http://localhost:4000',
            nodeInstanceId: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9',
            executionCounter: 3
          },
          {
            id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            processId: 'travels',
            processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
            rootProcessId: null,
            status: 'SCHEDULED',
            priority: 0,
            callbackEndpoint:
              'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            repeatInterval: null,
            repeatLimit: null,
            scheduledId: '0',
            retries: 0,
            lastUpdate: '2020-08-27T03:35:50.147Z',
            expirationTime: null,
            endpoint: 'http://localhost:4000',
            nodeInstanceId: '2f588da5-a323-4111-9017-3093ef9319d1',
            executionCounter: 4
          }
        ]
      }
    }
  }
];

const mocks2 = [
  {
    request: {
      query: GetProcessInstanceByIdDocument,
      variables: {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      },
      fetchPolicy: 'network-only'
    },
    result: {
      data: {
        ProcessInstances: [
          {
            id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
            processId: 'Travels',
            processName: 'travels',
            businessKey: null,
            parentProcessInstanceId: null,
            parentProcessInstance: null,
            roles: [],
            variables:
              '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
            state: ProcessInstanceState.Error,
            start: '2019-10-22T03:40:44.089Z',
            lastUpdate: '2019-10-22T03:40:44.089Z',
            end: null,
            endpoint: 'http://localhost:4000',
            addons: [],
            serviceUrl: null,
            error: {
              nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
              message: 'Something went wrong'
            },
            childProcessInstances: [],
            nodes: [
              {
                id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
                nodeId: '9',
                name: 'StartProcess',
                enter: '2019-10-22T04:43:01.135Z',
                exit: '2019-10-22T04:43:01.135Z',
                type: 'StartNode',
                definitionId: 'StartEvent_1'
              }
            ],
            milestones: [
              {
                id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
                name: 'Milestone 1: Order placed',
                status: MilestoneStatus['Active'],
                __typename: 'Milestones'
              },
              {
                id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
                name: 'Milestone 2: Order shipped',
                status: MilestoneStatus['Available'],
                __typename: 'Milestones'
              },
              {
                id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
                name: 'Manager decision',
                status: MilestoneStatus['Completed'],
                __typename: 'Milestones'
              }
            ]
          }
        ]
      }
    }
  },
  {
    request: {
      query: GetJobsByProcessInstanceIdDocument,
      variables: {
        processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      },
      fetchPolicy: 'network-only'
    },
    result: {
      loading: false,
      refetch: jest.fn(),
      data: {
        Jobs: [
          {
            id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            processId: 'travels',
            processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
            rootProcessId: null,
            status: 'EXECUTED',
            priority: 0,
            callbackEndpoint:
              'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            repeatInterval: null,
            repeatLimit: null,
            scheduledId: '0',
            retries: 0,
            lastUpdate: '2020-08-27T03:35:50.147Z',
            expirationTime: null,
            endpoint: 'http://localhost:4000',
            nodeInstanceId: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9',
            executionCounter: 6
          },
          {
            id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            processId: 'travels',
            processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
            rootProcessId: null,
            status: 'SCHEDULED',
            priority: 0,
            callbackEndpoint:
              'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            repeatInterval: null,
            repeatLimit: null,
            scheduledId: '0',
            retries: 0,
            lastUpdate: '2020-08-27T03:35:50.147Z',
            expirationTime: null,
            endpoint: 'http://localhost:4000',
            nodeInstanceId: '2f588da5-a323-4111-9017-3093ef9319d1',
            executionCounter: 1
          }
        ]
      }
    }
  }
];

const mocks3 = [
  {
    request: {
      query: GetProcessInstanceByIdDocument,
      variables: {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      },
      fetchPolicy: 'network-only'
    },
    result: {
      data: {
        ProcessInstances: [
          {
            id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
            processId: 'Travels',
            processName: 'travels',
            businessKey: null,
            parentProcessInstanceId: null,
            parentProcessInstance: null,
            roles: [],
            variables:
              '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
            state: ProcessInstanceState.Suspended,
            start: '2019-10-22T03:40:44.089Z',
            lastUpdate: '2019-10-22T03:40:44.089Z',
            end: null,
            endpoint: 'http://localhost:4000',
            addons: ['process-management'],
            serviceUrl: 'http://localhost:4000',
            error: {
              nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
              message: 'Something went wrong'
            },
            childProcessInstances: [],
            nodes: [
              {
                id: '90e5a337-1c26-4fcc-8ee2-d20e6ba2a1a3',
                nodeId: '9',
                name: 'StartProcess',
                enter: '2019-10-22T04:43:01.135Z',
                exit: '2019-10-22T04:43:01.135Z',
                type: 'StartNode',
                definitionId: 'StartEvent_1'
              }
            ],
            milestones: [
              {
                id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
                name: 'Milestone 1: Order placed',
                status: MilestoneStatus['Active'],
                __typename: 'Milestones'
              },
              {
                id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
                name: 'Milestone 2: Order shipped',
                status: MilestoneStatus['Available'],
                __typename: 'Milestones'
              },
              {
                id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
                name: 'Manager decision',
                status: MilestoneStatus['Completed'],
                __typename: 'Milestones'
              }
            ]
          }
        ]
      }
    }
  },
  {
    request: {
      query: GetJobsByProcessInstanceIdDocument,
      variables: {
        processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      },
      fetchPolicy: 'network-only'
    },
    result: {
      loading: false,
      refetch: jest.fn(),
      data: {
        Jobs: [
          {
            id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            processId: 'travels',
            processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
            rootProcessId: null,
            status: 'EXECUTED',
            priority: 0,
            callbackEndpoint:
              'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            repeatInterval: null,
            repeatLimit: null,
            scheduledId: '0',
            retries: 0,
            lastUpdate: '2020-08-27T03:35:50.147Z',
            expirationTime: null,
            endpoint: 'http://localhost:4000',
            nodeInstanceId: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9',
            executionCounter: 4
          },
          {
            id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            processId: 'travels',
            processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
            rootProcessId: null,
            status: 'SCHEDULED',
            priority: 0,
            callbackEndpoint:
              'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
            repeatInterval: null,
            repeatLimit: null,
            scheduledId: '0',
            retries: 0,
            lastUpdate: '2020-08-27T03:35:50.147Z',
            expirationTime: null,
            endpoint: 'http://localhost:4000',
            nodeInstanceId: '2f588da5-a323-4111-9017-3093ef9319d1',
            executionCounter: 7
          }
        ]
      }
    }
  }
];

const res = {
  data: '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="800" height="300" viewBox="0 0 1748 632"></svg>'
};
const svgElement: JSX.Element = (
  <InlineSVG cacheRequests={true} src={res.data} uniquifyIDs={false} />
);
mockedAxios.get.mockResolvedValue(res);
describe('Process Details Page component tests', () => {
  let originalLocalStorage;
  beforeEach(() => {
    originalLocalStorage = Storage.prototype.getItem;
  });

  afterEach(() => {
    Storage.prototype.getItem = originalLocalStorage;
  });
  Date.now = jest.fn(() => 1487076708000);
  Storage.prototype.getItem = jest.fn(() =>
    JSON.stringify({
      prev: '/ProcessInstances/8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
    })
  );
  it('snapshot testing in Active state', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks1} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsPage');
    });
    expect(wrapper).toMatchSnapshot();
  });
  describe('abort button click', () => {
    it('on successfull abort', async () => {
      mockedAxios.delete.mockResolvedValue({});
      let wrapper;
      await act(async () => {
        wrapper = mount(
          <MockedProvider mocks={mocks1} addTypename={false}>
            <BrowserRouter>
              <ProcessDetailsPage {...props} />
            </BrowserRouter>
          </MockedProvider>
        );
        await wait(0);
        wrapper = wrapper.update().find('ProcessDetailsPage');
      });
      const handleAbortSpy = jest.spyOn(Utils, 'handleAbort');
      await act(async () => {
        wrapper.find(Button).find('#abort-button').first().simulate('click');
      });
      wrapper.update();
      expect(handleAbortSpy).toHaveBeenCalled();
    });
    it('on failed abort', async () => {
      mockedAxios.delete.mockRejectedValue({ message: '404 error' });
      let wrapper;
      await act(async () => {
        wrapper = mount(
          <MockedProvider mocks={mocks1} addTypename={false}>
            <BrowserRouter>
              <ProcessDetailsPage {...props} />
            </BrowserRouter>
          </MockedProvider>
        );
        await wait(0);
        wrapper = wrapper.update().find('ProcessDetailsPage');
      });
      const handleAbortSpy = jest.spyOn(Utils, 'handleAbort');
      await act(async () => {
        wrapper.find(Button).find('#abort-button').first().simulate('click');
      });
      wrapper.update();
      expect(handleAbortSpy).toHaveBeenCalled();
    });
  });
  it('snapshot testing in Error state', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks2} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsPage');
    });
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing in Suspended state', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks3} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsPage');
    });
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing for error occurance', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks3} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props1} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsPage');
    });
    expect(wrapper).toMatchSnapshot();
  });
  it('Test refresh and save button', async () => {
    mockedAxios.post.mockResolvedValue({});
    const { location } = window;
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: { reload: jest.fn() }
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks1} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsPage');
    });
    const handleVariableUpdateSpy = jest.spyOn(Utils, 'handleVariableUpdate');
    wrapper.find('#refresh-button').first().simulate('click');
    await act(async () => {
      wrapper.find('#save-button').first().simulate('click');
    });
    act(() => {
      wrapper.find('Modal').at(0).props()['onClose']();
    });
    wrapper.find('Modal').at(1).props()['onClose']();
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: location
    });
    expect(handleVariableUpdateSpy).toHaveBeenCalled();
  });
  it('Test error axios response', async () => {
    mockedAxios.post.mockRejectedValue({ message: '404 error' });
    jest.setTimeout(2000);
    const { location } = window;
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: { reload: jest.fn() }
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks1} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsPage');
    });
    const handleVariableUpdateSpy = jest.spyOn(Utils, 'handleVariableUpdate');
    wrapper.find('#refresh-button').first().simulate('click');
    await act(async () => {
      wrapper.find('#save-button').first().simulate('click');
    });
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: location
    });
    expect(handleVariableUpdateSpy).toHaveBeenCalled();
  });
  it('test node trigger presence', async () => {
    // with active state- node trigger panel present
    let wrapperWithNodeTrigger;
    await act(async () => {
      wrapperWithNodeTrigger = mount(
        <MockedProvider mocks={mocks1} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapperWithNodeTrigger = wrapperWithNodeTrigger
        .update()
        .find('ProcessDetailsPage');
    });
    expect(
      wrapperWithNodeTrigger.find('MockedProcessDetailsNodeTrigger').exists()
    ).toBeTruthy();

    const mockWithCompletedState = _.cloneDeep(mocks1);
    mockWithCompletedState[0].result.data.ProcessInstances[0].state =
      GraphQL.ProcessInstanceState.Completed;
    // with completed state - node trigger panel absent
    let wrapperWithoutNodeTrigger1;
    await act(async () => {
      wrapperWithoutNodeTrigger1 = mount(
        <MockedProvider mocks={mockWithCompletedState} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapperWithoutNodeTrigger1 = wrapperWithoutNodeTrigger1
        .update()
        .find('ProcessDetailsPage');
    });
    expect(
      wrapperWithoutNodeTrigger1
        .find('MockedProcessDetailsNodeTrigger')
        .exists()
    ).toBeFalsy();

    const mockWithAbortedState = _.cloneDeep(mocks1);
    mockWithAbortedState[0].result.data.ProcessInstances[0].state =
      GraphQL.ProcessInstanceState.Aborted;
    // with Aborted state - node trigger panel absent
    let wrapperWithoutNodeTrigger2;
    await act(async () => {
      wrapperWithoutNodeTrigger2 = mount(
        <MockedProvider mocks={mockWithAbortedState} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapperWithoutNodeTrigger2 = wrapperWithoutNodeTrigger2
        .update()
        .find('ProcessDetailsPage');
    });
    expect(
      wrapperWithoutNodeTrigger2
        .find('MockedProcessDetailsNodeTrigger')
        .exists()
    ).toBeFalsy();
  });

  it('test api to get svg', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks1} addTypename={false}>
          <BrowserRouter>
            <ProcessDetailsPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsPage');
    });
    wrapper.update();
    expect(
      wrapper.find('MockedProcessDetailsProcessDiagram').props()['svg']
    ).toEqual(svgElement);
  });
});

import React from 'react';
import JobsManagementPage from '../JobsManagementPage';
import { getWrapperAsync, GraphQL } from '@kogito-apps/common';
import { MockedProvider } from '@apollo/react-testing';
import { BrowserRouter } from 'react-router-dom';
import { act } from 'react-dom/test-utils';

jest.mock('../../../Organisms/JobsManagementTable/JobsManagementTable');
jest.mock('../../../Atoms/JobsRescheduleModal/JobsRescheduleModal');
jest.mock('../../../Atoms/JobsPanelDetailsModal/JobsPanelDetailsModal');
jest.mock('../../../Atoms/JobsCancelModal/JobsCancelModal');
const MockedServerErrors = (): React.ReactElement => {
  return <></>;
};
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  ServerErrors: () => {
    return <MockedServerErrors />;
  }
}));

const MockedBreadcrumb = (): React.ReactElement => {
  return <></>;
};
const MockedIcon = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () => ({
  ...jest.requireActual('@patternfly/react-core'),
  Breadcrumb: () => <MockedBreadcrumb />
}));

jest.mock('@patternfly/react-icons', () => ({
  ...jest.requireActual('@patternfly/react-icons'),
  SyncIcon: () => {
    return <MockedIcon />;
  }
}));
describe('Jobs management page tests', () => {
  const mocks = [
    {
      request: {
        query: GraphQL.GetAllJobsDocument,
        variables: {}
      },
      result: {
        data: {
          Jobs: [
            {
              callbackEndpoint:
                'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
              endpoint: 'http://localhost:4000/jobs',
              expirationTime: null,
              id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
              lastUpdate: '2020-08-27T03:35:50.147Z',
              priority: 0,
              processId: 'travels',
              processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
              repeatInterval: null,
              repeatLimit: null,
              retries: 0,
              rootProcessId: null,
              scheduledId: '0',
              status: GraphQL.JobStatus.Executed,
              __typename: 'Job'
            },
            {
              callbackEndpoint:
                'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
              endpoint: 'http://localhost:4000/jobs',
              expirationTime: '2020-08-27T04:35:54.631Z',
              id: 'dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
              lastUpdate: '2020-08-27T03:35:54.635Z',
              priority: 0,
              processId: 'travels',
              processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
              repeatInterval: null,
              repeatLimit: null,
              retries: 0,
              rootProcessId: '',
              scheduledId: null,
              status: GraphQL.JobStatus.Scheduled,
              __typename: 'Job'
            },
            {
              callbackEndpoint:
                'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
              endpoint: 'http://localhost:4000/jobs',
              expirationTime: '2020-08-27T04:35:54.631Z',
              id: '2234dde-npce1-2908-b3131-6123c675a0fa_0',
              lastUpdate: '2020-08-27T03:35:54.635Z',
              priority: 0,
              processId: 'travels',
              processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
              repeatInterval: null,
              repeatLimit: null,
              retries: 0,
              rootProcessId: '',
              scheduledId: null,
              status: GraphQL.JobStatus.Canceled,
              __typename: 'Job'
            }
          ]
        }
      }
    }
  ];

  const mocks2 = [
    {
      request: {
        query: GraphQL.GetAllJobsDocument,
        variables: {}
      },
      result: {
        data: {
          Jobs: []
        }
      }
    }
  ];

  const mocks3 = [
    {
      request: {
        query: GraphQL.GetAllJobsDocument,
        variables: {}
      },
      result: {
        data: null,
        error: {
          message: 'Expected a value of type JobStatus but received: CANCELLED'
        }
      }
    }
  ];
  const { location } = window;
  beforeEach(() => {
    delete window.location;
    // @ts-ignore
    window.location = { reload: jest.fn() };
  });

  afterAll(() => {
    window.location = location;
  });
  it('snapshot test with mock data', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <BrowserRouter>
          <JobsManagementPage />
        </BrowserRouter>
      </MockedProvider>,
      'JobsManagementPage'
    );
    expect(wrapper).toMatchSnapshot();
    wrapper.update();

    await act(async () => {
      wrapper
        .find('#refresh-button')
        .first()
        .simulate('click');
    });
  });
  it('mock data with empty response', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks2} addTypename={false}>
        <BrowserRouter>
          <JobsManagementPage />
        </BrowserRouter>
      </MockedProvider>,
      'JobsManagementPage'
    );
    expect(wrapper).toMatchSnapshot();
    const redirectObj = {
      pathname: '/NoData',
      state: {
        buttonText: 'Go to process instance',
        description: 'There are no jobs associated with any process instance.',
        prev: '/ProcessInstances',
        title: 'Jobs not found'
      }
    };
    expect(wrapper.find('Redirect').props()['to']).toEqual(redirectObj);
  });

  it('mock data with error response', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks3} addTypename={false}>
        <BrowserRouter>
          <JobsManagementPage />
        </BrowserRouter>
      </MockedProvider>,
      'JobsManagementPage'
    );
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('ServerErrors')).toBeTruthy();
  });
  it('test modal handlers', async () => {
    let wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <BrowserRouter>
          <JobsManagementPage />
        </BrowserRouter>
      </MockedProvider>,
      'JobsManagementPage'
    );
    await act(async () => {
      wrapper
        .find('MockedJobsPanelDetailsModal')
        .props()
        ['handleModalToggle']();
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('MockedJobsPanelDetailsModal').props()['isModalOpen']
    ).toEqual(true);
    await act(async () => {
      wrapper
        .find('JobsRescheduleModal')
        .props()
        ['handleModalToggle']();
    });
    wrapper = wrapper.update();
    expect(wrapper.find('JobsRescheduleModal').props()['isModalOpen']).toEqual(
      true
    );
    await act(async () => {
      wrapper
        .find('JobsCancelModal')
        .props()
        ['handleModalToggle']();
    });
    wrapper = wrapper.update();
    expect(wrapper.find('JobsCancelModal').props()['isModalOpen']).toEqual(
      true
    );
  });
});

import React from 'react';
import ProcessDetailsJobsPanel from '../ProcessDetailsJobsPanel';
import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
import { wait } from '@apollo/react-testing';
jest.mock('../../../Atoms/JobActionsKebab/JobActionsKebab');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    ErrorCircleOIcon: () => {
      return <MockedComponent />;
    },
    BanIcon: () => {
      return <MockedComponent />;
    },
    CheckCircleIcon: () => {
      return <MockedComponent />;
    },
    UndoIcon: () => {
      return <MockedComponent />;
    },
    ClockIcon: () => {
      return <MockedComponent />;
    }
  })
);

const props = {
  jobsResponse: {
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
          endpoint: 'http://localhost:4000'
        }
      ]
    },
    loading: false,
    refetch: jest.fn()
  }
} as any;

const props2 = {
  jobsResponse: {
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
          expirationTime: '2020-08-29T03:35:50.147Z',
          endpoint: 'http://localhost:4000'
        }
      ]
    },
    loading: false,
    refetch: jest.fn()
  },
  ouiaSafe: true
} as any;

Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
describe('Processdetails jobs pannel component tests', () => {
  it('Snapshot testing', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(<ProcessDetailsJobsPanel {...props} />);
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsJobsPanel');
    });
    expect(wrapper).toMatchSnapshot();
  });
  it('test expiration time', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(<ProcessDetailsJobsPanel {...props2} />);
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsJobsPanel');
    });
    expect(wrapper).toMatchSnapshot();
  });
});

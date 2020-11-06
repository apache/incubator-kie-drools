import React from 'react';
import { shallow } from 'enzyme';
import { GraphQL } from '@kogito-apps/common';
import JobsBulkList from '../JobsBulkList';

const props1 = {
  operationResult: {
    messages: {
      successMessage: 'Cancel job',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noJobsMessage: 'No jobs were canceled',
      warningMessage:
        'Note: The job status has been updated. The list may appear inconsistent until you refresh any applied filters.'
    },
    results: {
      successJobs: {
        'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0': {
          id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
          processId: 'travels',
          processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          rootProcessId: '',
          status: GraphQL.JobStatus.Scheduled,
          priority: 0,
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          repeatInterval: null,
          repeatLimit: null,
          scheduledId: null,
          retries: 0,
          lastUpdate: '2020-08-29T03:35:54.635Z',
          expirationTime: '2020-08-29T04:35:54.631Z',
          endpoint: 'http://localhost:4000/jobs'
        }
      },
      failedJobs: {},
      ignoredJobs: {
        '2234dde-npce1-2908-b3131-6123c675a0fa_0': {
          id: '2234dde-npce1-2908-b3131-6123c675a0fa_0',
          processId: 'travels',
          processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          rootProcessId: '',
          status: GraphQL.JobStatus.Canceled,
          priority: 0,
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          repeatInterval: null,
          repeatLimit: null,
          scheduledId: null,
          retries: 0,
          lastUpdate: '2020-08-27T03:35:54.635Z',
          expirationTime: '2020-08-27T04:35:54.631Z',
          endpoint: 'http://localhost:4000/jobs'
        }
      }
    },
    functions: {
      perform: jest.fn()
    }
  }
};

const props2 = {
  operationResult: {
    messages: {
      successMessage: 'Cancel job',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noJobsMessage: 'No jobs were canceled'
    },
    results: {
      successJobs: {},
      failedJobs: {},
      ignoredJobs: {
        '2234dde-npce1-2908-b3131-6123c675a0fa_0': {
          id: '2234dde-npce1-2908-b3131-6123c675a0fa_0',
          processId: 'travels',
          processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          rootProcessId: '',
          status: GraphQL.JobStatus.Canceled,
          priority: 0,
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          repeatInterval: null,
          repeatLimit: null,
          scheduledId: null,
          retries: 0,
          lastUpdate: '2020-08-27T03:35:54.635Z',
          expirationTime: '2020-08-27T04:35:54.631Z',
          endpoint: 'http://localhost:4000/jobs'
        }
      }
    },
    functions: {
      perform: jest.fn()
    }
  }
};

const props3 = {
  operationResult: {
    messages: {
      successMessage: 'Cancel job',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noJobsMessage: 'No jobs were canceled'
    },
    results: {
      successJobs: {
        'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0': {
          id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
          processId: 'travels',
          processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          rootProcessId: '',
          status: GraphQL.JobStatus.Scheduled,
          priority: 0,
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          repeatInterval: null,
          repeatLimit: null,
          scheduledId: null,
          retries: 0,
          lastUpdate: '2020-08-29T03:35:54.635Z',
          expirationTime: '2020-08-29T04:35:54.631Z',
          endpoint: 'http://localhost:4000/jobs'
        }
      },
      failedJobs: {
        'ceb1234-6ae4-deb444-9ec0-neb9809e0b': {
          id: 'T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0',
          processId: 'travels',
          processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          rootProcessId: '',
          status: GraphQL.JobStatus.Error,
          priority: 0,
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
          repeatInterval: 30300,
          repeatLimit: 3,
          scheduledId: null,
          retries: 0,
          lastUpdate: '2020-08-27T03:35:54.635Z',
          expirationTime: '2020-08-27T04:35:54.631Z',
          endpoint: 'http://localhost:4000/jobs'
        }
      },
      ignoredJobs: {}
    },
    functions: {
      perform: jest.fn()
    }
  }
};

describe('JobsBulkLists component tests', () => {
  it('snapshot testing multi-cancel with canceled and skipped jobs ', () => {
    const wrapper = shallow(<JobsBulkList {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing multi-cancel with no canceled jobs and only skipped jobs', () => {
    const wrapper = shallow(<JobsBulkList {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing multi-cancel with no skipped jobs', () => {
    const wrapper = shallow(<JobsBulkList {...props3} />);
    expect(wrapper).toMatchSnapshot();
  });
});

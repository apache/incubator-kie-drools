import React from 'react';
import ProcessDetailsJobsPanel from '../ProcessDetailsJobsPanel';
import { GraphQL, getWrapperAsync } from '@kogito-apps/common';
import { BrowserRouter } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';
jest.mock('../../../Atoms/JobActionsKebab/JobActionsKebab');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons', () => ({
  ...jest.requireActual('@patternfly/react-icons'),
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
}));

const mocks = [
  {
    request: {
      query: GraphQL.GetJobsByProcessInstanceIdDocument,
      variables: {
        processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced'
      }
    },
    result: {
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
            expirationTime: null
          }
        ]
      }
    }
  }
];

const props = {
  processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced'
};

Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
describe('Processdetails jobs pannel component tests', () => {
  it('Snapshot testing', async () => {
    const wrapper = await getWrapperAsync(
      <MockedProvider mocks={mocks} addTypename={false}>
        <BrowserRouter>
          <ProcessDetailsJobsPanel {...props} />
        </BrowserRouter>
      </MockedProvider>,
      'ProcessDetailsJobsPanel'
    );
    expect(wrapper).toMatchSnapshot();
  });
});

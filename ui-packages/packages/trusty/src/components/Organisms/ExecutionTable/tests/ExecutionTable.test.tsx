import React from 'react';
import ExecutionTable from '../ExecutionTable';
import { shallow } from 'enzyme';
import { Executions, RemoteData, RemoteDataStatus } from '../../../../types';
import { TrustyContext } from '../../../Templates/TrustyApp/TrustyApp';

const setupWrapper = (executions: RemoteData<Error, Executions>) => {
  return shallow(
    <TrustyContext.Provider
      value={{
        config: {
          counterfactualEnabled: true,
          explanationEnabled: true,
          basePath: '/',
          useHrefLinks: true
        }
      }}
    >
      <ExecutionTable data={executions} />
    </TrustyContext.Provider>
  );
};

describe('Execution table', () => {
  test('renders loading skeletons when the data is not yet fetching', () => {
    const data = { status: RemoteDataStatus.NOT_ASKED } as RemoteData<
      Error,
      Executions
    >;
    const wrapper = setupWrapper(data);
    expect(wrapper).toMatchSnapshot();
  });

  test('renders loading skeletons when the data is loading', () => {
    const data = { status: RemoteDataStatus.LOADING } as RemoteData<
      Error,
      Executions
    >;
    const wrapper = setupWrapper(data);
    expect(wrapper).toMatchSnapshot();
  });

  test('renders a loading error message when data loading fails', () => {
    const data = {
      status: RemoteDataStatus.FAILURE,
      error: { name: '', message: '' }
    } as RemoteData<Error, Executions>;
    const wrapper = setupWrapper(data);

    expect(wrapper).toMatchSnapshot();
  });

  test('renders a list of executions', () => {
    const data = {
      status: RemoteDataStatus.SUCCESS,
      data: {
        total: 2,
        limit: 10,
        offset: 0,
        headers: [
          {
            executionId: 'b2b0ed8d-c1e2-46b5-ad4f-3ac54ff4beae',
            executionDate: '2020-06-01T12:33:57+0000',
            executionSucceeded: true,
            executorName: 'testUser',
            executedModelName: 'LoanEligibility',
            executionType: 'DECISION'
          },
          {
            executionId: 'b2b0ed8d-c1e2-46b5-ad4f-3hd83kidi4u74',
            executionDate: '2020-06-01T12:33:57+0000',
            executionSucceeded: true,
            executorName: 'testUser',
            executedModelName: 'LoanEligibility',
            executionType: 'DECISION'
          }
        ]
      }
    } as RemoteData<Error, Executions>;
    const wrapper = setupWrapper(data);

    expect(wrapper).toMatchSnapshot();
  });

  test('renders no result message if no executions are found', () => {
    const data = {
      status: RemoteDataStatus.SUCCESS,
      data: {
        total: 0,
        limit: 10,
        offset: 0,
        headers: []
      }
    } as RemoteData<Error, Executions>;
    const wrapper = setupWrapper(data);

    expect(wrapper).toMatchSnapshot();
  });
});

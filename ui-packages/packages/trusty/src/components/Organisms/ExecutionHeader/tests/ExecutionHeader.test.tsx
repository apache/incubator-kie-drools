import React from 'react';
import ExecutionHeader from '../ExecutionHeader';
import { shallow } from 'enzyme';
import { Execution, RemoteData, RemoteDataStatus } from '../../../../types';

describe('ExecutionHeader', () => {
  test('renders a loading animation while fetching data', () => {
    const execution = {
      status: RemoteDataStatus.LOADING
    } as RemoteData<Error, Execution>;

    const wrapper = shallow(<ExecutionHeader execution={execution} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('SkeletonStripe')).toHaveLength(1);
  });

  test('renders the execution info', () => {
    const execution = {
      status: RemoteDataStatus.SUCCESS,
      data: {
        executionId: 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
        executionDate: '2020-08-12T12:54:53.933Z',
        executionType: 'DECISION',
        executedModelName: 'fraud-score',
        executionSucceeded: true,
        executorName: 'Technical User'
      }
    } as RemoteData<Error, Execution>;
    const wrapper = shallow(<ExecutionHeader execution={execution} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('ExecutionId')).toHaveLength(1);
    expect(wrapper.find('Tooltip')).toHaveLength(1);
    expect(wrapper.find('ExecutionStatus')).toHaveLength(1);
    expect(wrapper.find('ExecutionStatus').props().result).toMatch('success');
  });
});

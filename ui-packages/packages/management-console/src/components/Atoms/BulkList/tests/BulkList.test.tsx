import React from 'react';
import { shallow } from 'enzyme';
import BulkList from '../BulkList';

const props1 = {
  operationResult: {
    type: 'job',
    messages: {
      successMessage: 'Cancel job',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noItemsMessage: 'No jobs were canceled',
      warningMessage:
        'Note: The job status has been updated. The list may appear inconsistent until you refresh any applied filters.'
    },
    results: {
      successItems: [
        {
          id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
          name: 'travels',
          description: 'TT111'
        }
      ],
      failedItems: [],
      ignoredItems: [
        {
          id: '2234dde-npce1-2908-b3131-6123c675a0fa_0',
          name: 'travels1',
          description: 'T@123'
        }
      ]
    },
    functions: {
      perform: jest.fn()
    }
  }
};

const props2 = {
  operationResult: {
    type: 'job',
    messages: {
      successMessage: 'Cancel job',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noItemsMessage: 'No jobs were canceled'
    },
    results: {
      successItems: [],
      failedItems: [],
      ignoredItems: [
        {
          id: '2234dde-npce1-2908-b3131-6123c675a0fa_0',
          name: 'travels1',
          description: 'TT111'
        }
      ]
    },
    functions: {
      perform: jest.fn()
    }
  }
};

const props3 = {
  operationResult: {
    type: 'job',
    messages: {
      successMessage: 'Cancel job',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noItemsMessage: 'No jobs were canceled'
    },
    results: {
      successItems: [
        {
          id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
          name: 'travels',
          description: 'TT111'
        }
      ],
      failedItems: [
        {
          id: 'T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0',
          name: 'travels1',
          description: 'T4433',
          errorMessage: '404 error'
        }
      ],
      ignoredItems: []
    },
    functions: {
      perform: jest.fn()
    }
  }
};

describe('JobsBulkLists component tests', () => {
  it('snapshot testing multi-cancel with canceled and skipped jobs ', () => {
    const wrapper = shallow(<BulkList {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing multi-cancel with no canceled jobs and only skipped jobs', () => {
    const wrapper = shallow(<BulkList {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing multi-cancel with no skipped jobs', () => {
    const wrapper = shallow(<BulkList {...props3} />);
    expect(wrapper).toMatchSnapshot();
  });
});

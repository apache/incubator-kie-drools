import React from 'react';
import { setTitle } from '../../../../utils/Utils';
import JobsCancelModal from '../JobsCancelModal';
import { shallow } from 'enzyme';
jest.mock('../../BulkList/BulkList');

const jobOperation = {
  CANCEL: {
    messages: {
      successMessage: 'Cancel Jobs',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noJobsMessage: 'No jobs were canceled'
    },
    functions: {
      perform: jest.fn()
    },
    results: {
      successJobs: {},
      failedJobs: {},
      IgnoredJobs: {}
    }
  }
};
const props = {
  actionType: 'Job Cancel',
  modalContent: 'The job was cancelled successfully',
  modalTitle: setTitle('success', 'Job cancel'),
  isModalOpen: true,
  handleModalToggle: jest.fn()
};

const props1 = {
  actionType: 'Job Cancel',
  modalTitle: setTitle('success', 'Job cancel'),
  modalContent: '',
  isModalOpen: true,
  handleModalToggle: jest.fn(),
  jobOperation
};

describe('job cancel modal tests', () => {
  it('snapshot test - single cancel', () => {
    const wrapper = shallow(<JobsCancelModal {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot test - bulk cancel', () => {
    const wrapper = shallow(<JobsCancelModal {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });
});

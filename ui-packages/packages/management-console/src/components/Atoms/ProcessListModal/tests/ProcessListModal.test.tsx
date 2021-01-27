import React from 'react';
import { shallow } from 'enzyme';
import ProcessListModal from '../ProcessListModal';
import { InfoCircleIcon } from '@patternfly/react-icons';

const props = {
  modalTitle: (
    <>
      <InfoCircleIcon
        className="pf-u-mr-sm"
        color="var(--pf-global--info-color--100)"
      />
      {'Abort operation'}
    </>
  ),
  isModalOpen: true,
  handleModalToggle: jest.fn(),
  processName: 'travels',
  modalContent: 'The process travels was aborted successfully',
  operationResult: {
    type: 'process_instance',
    messages: {
      successMessage: 'Aborted',
      ignoredMessage:
        'These processes were ignored because they were already completed or aborted',
      noItemsMessage: 'No processes were aborted'
    },
    results: {
      successItems: [
        {
          id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          name: 'travels',
          description: 'TT@12'
        }
      ],
      failedItems: [],
      ignoredItems: [
        {
          id: 'e735128t-6tt7-4aa8-9ec0-e18e19809e0b',
          name: 'travels2',
          description: 'TT998'
        }
      ]
    },
    functions: {
      perform: jest.fn()
    }
  },
  resetSelected: jest.fn()
};

describe('ProcessBulkModal component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(<ProcessListModal {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('Ok click test', () => {
    const wrapper = shallow(<ProcessListModal {...props} />);
    wrapper.props()['actions'][0]['props']['onClick']();
    expect(props.resetSelected).toHaveBeenCalled();
    expect(props.handleModalToggle).toHaveBeenCalled();
  });
});

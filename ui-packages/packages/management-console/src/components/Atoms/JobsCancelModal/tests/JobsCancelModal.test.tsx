import React from 'react';
import { setTitle } from '../../../../utils/Utils';
import JobsCancelModal from '../JobsCancelModal';
import { shallow } from 'enzyme';

const props = {
  actionType: 'Job Cancel',
  modalContent: 'The job was cancelled successfully',
  modalTitle: setTitle('success', 'Job cancel'),
  isModalOpen: true,
  handleModalToggle: jest.fn()
};
describe('job cancel modal tests', () => {
  it('snapshot test', () => {
    const wrapper = shallow(<JobsCancelModal {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
});

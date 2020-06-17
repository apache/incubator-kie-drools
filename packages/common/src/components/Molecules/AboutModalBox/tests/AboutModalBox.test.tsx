import React from 'react';
import { shallow } from 'enzyme';
import AboutModal from '../AboutModalBox';

const props = {
  isOpenProp: true,
  handleModalToggleProp: jest.fn()
};
describe('AboutModal component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(<AboutModal {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
});

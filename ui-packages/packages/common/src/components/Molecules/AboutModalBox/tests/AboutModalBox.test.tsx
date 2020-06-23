import React from 'react';
import { shallow } from 'enzyme';
import AboutModal from '../AboutModalBox';

const props = {
  isOpenProp: true,
  handleModalToggleProp: jest.fn()
};
describe('AboutModal component tests', () => {
  it('snapshot testing', () => {
    process.env.KOGITO_APP_VERSION = '1.2.3-MOCKED-VERSION';
    const wrapper = shallow(<AboutModal {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
});

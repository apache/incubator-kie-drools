import React from 'react';
import { shallow } from 'enzyme';
import EmptyStateComponent from '../EmptyStateComponent';

const props1 = {
  iconType: 'warningTriangleIcon',
  title: 'No child process instances',
  body: 'This process has no related sub processes'
};
describe('Emptystate component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(<EmptyStateComponent {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });
});

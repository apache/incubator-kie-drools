import React from 'react';
import { shallow } from 'enzyme';
import LoadMore from '../LoadMore';

describe('LoadMore component tests with isLoading false', () => {
  const props = {
    offset: 0,
    setOffset: jest.fn(),
    getMoreItems: jest.fn(),
    pageSize: 10,
    isLoadingMore: false,
    ouiaId: 'load-more-ouia-id'
  };
  it('snapshot testing', () => {
    const wrapper = shallow(<LoadMore {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('test loadMore button click', () => {
    const wrapper = shallow(<LoadMore {...props} />);
    const button1 = wrapper.find('#load10');
    const button2 = wrapper.find('#load20');
    const button3 = wrapper.find('#load50');
    const button4 = wrapper.find('#load100');
    button1.simulate('click');
    button2.simulate('click');
    button3.simulate('click');
    button4.simulate('click');
    expect(props.getMoreItems).toHaveBeenCalledTimes(4);
    expect(props.getMoreItems.mock.calls).toEqual([
      [10, 10],
      [10, 20],
      [10, 50],
      [10, 100]
    ]);
  });
});

describe('LoadMore component tests with isLoading true', () => {
  const props = {
    offset: 0,
    setOffset: jest.fn(),
    getMoreItems: jest.fn(),
    pageSize: 10,
    isLoadingMore: true,
    ouiaId: 'load-more-ouia-id'
  };
  it('snapshot testing', () => {
    const wrapper = shallow(<LoadMore {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('test Loading button displayed', () => {
    const wrapper = shallow(<LoadMore {...props} />);
    expect(wrapper.find('#loading').exists()).toBeTruthy();
  });
});

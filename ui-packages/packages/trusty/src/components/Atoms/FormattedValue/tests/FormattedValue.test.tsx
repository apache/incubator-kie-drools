import React from 'react';
import FormattedValue from '../FormattedValue';
import { mount, shallow } from 'enzyme';

describe('Formatted Value', () => {
  test('returns "Null" for a null value', () => {
    const wrapper = shallow(<FormattedValue value={null} />);
    expect(wrapper.find('.formatted-value').text()).toBe('Null');
  });

  test('returns a string as is', () => {
    const wrapper = shallow(<FormattedValue value={'standard'} />);
    expect(wrapper.find('.formatted-value').text()).toBe('standard');
  });

  test('returns a number as is', () => {
    const wrapper = shallow(<FormattedValue value={3.14} />);
    expect(wrapper.find('.formatted-value').text()).toBe('3.14');
  });

  test('returns a boolean', () => {
    const wrapper = shallow(<FormattedValue value={true} />);
    expect(wrapper.find('.formatted-value').text()).toBe('true');
  });

  test("returns 'No entries' for an empty list of values", () => {
    const wrapper = mount(<FormattedValue value={[]} />);
    const list = wrapper.find('.formatted-list');
    expect(list).toHaveLength(1);
    expect(list.hasClass('formatted-list--no-entries')).toBe(true);
    expect(list.text()).toBe('No entries');
  });

  test('returns a list of values', () => {
    const valueList = ['alpha', 'beta', 'gamma'];
    const wrapper = mount(<FormattedValue value={valueList} />);
    expect(wrapper.find('.formatted-list')).toHaveLength(1);
    expect(
      wrapper.find('.formatted-list .formatted-value').map(item => item.text())
    ).toStrictEqual(valueList);
  });
});

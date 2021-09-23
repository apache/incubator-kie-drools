import React from 'react';
import { shallow } from 'enzyme';
import FormattedDate from '../FormattedDate';
import { format } from 'date-fns';

describe('FormattedDate', () => {
  test('displays a formatted date', () => {
    const wrapper = shallow(<FormattedDate date="2020-01-01" />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('span').text()).toMatch('Jan 1, 2020');
  });

  test('renders a tooltip with the complete date and time info', () => {
    const initialDate = '2020-01-01';
    const wrapper = shallow(<FormattedDate date={initialDate} />);
    const tooltip = wrapper.find('Tooltip');
    const fullDate = format(new Date(initialDate), 'PPpp');

    expect(wrapper.find('span').text()).toMatch('Jan 1, 2020');
    expect(tooltip.length).toBe(1);
    expect(tooltip.props().content).toMatch(fullDate);
  });

  test('displays the "on" preposition before the date when preposition prop is passed', () => {
    const wrapper = shallow(<FormattedDate date="2020-01-01" preposition />);

    expect(wrapper.find('span').text()).toMatch('on Jan 1, 2020');
  });

  test('displays full date and time when fullDateAndTime prop is passed', () => {
    const wrapper = shallow(
      <FormattedDate date="2020-01-01" fullDateAndTime />
    );

    expect(wrapper.find('Tooltip').length).toBe(0);
    expect(wrapper.find('span').text()).toMatch('Jan 1, 2020, 12:00:00 AM');
  });

  test('displays a relative timestamp if the date is in the last 24h', () => {
    const fixedDate = '2020-01-01T00:00:00.000Z';
    jest
      .spyOn(global.Date, 'now')
      .mockImplementation(() => new Date(fixedDate).getTime());
    const wrapper = shallow(<FormattedDate date={fixedDate} />);

    expect(wrapper.find('span').text()).toMatch('less than a minute ago');
  });
});

import React from 'react';
import SkeletonDataList from '../SkeletonDataList';
import { shallow } from 'enzyme';

describe('SkeletonDatalist', () => {
  test('renders a 2x2 data list', () => {
    const wrapper = shallow(<SkeletonDataList rowsCount={2} colsCount={2} />);

    expect(wrapper).toMatchSnapshot();
  });

  test('renders a 2x2 data list with header styling', () => {
    const wrapper = shallow(
      <SkeletonDataList rowsCount={2} colsCount={2} hasHeader />
    );

    expect(
      wrapper
        .find('DataListItemRow')
        .at(0)
        .hasClass('skeleton-datalist__header')
    ).toBeTruthy();
  });
});

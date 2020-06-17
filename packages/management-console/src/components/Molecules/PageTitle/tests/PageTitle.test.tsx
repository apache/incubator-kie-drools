import React from 'react';
import { shallow } from 'enzyme';
import PageTitle from '../PageTitle';

describe('PageTitle component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(<PageTitle title="Kogito" />);
    expect(wrapper).toMatchSnapshot();
  });
});

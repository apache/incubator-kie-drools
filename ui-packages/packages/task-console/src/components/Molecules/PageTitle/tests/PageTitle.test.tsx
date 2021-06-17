import React from 'react';
import PageTitle from '../PageTitle';
import { mount } from 'enzyme';
import { Label } from '@patternfly/react-core';

describe('PageTitle test', () => {
  it('default snapshot testing', () => {
    const wrapper = mount(<PageTitle title="Title" />).find('PageTitle');

    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing with extra', () => {
    const wrapper = mount(
      <PageTitle title="Title" extra={<Label>Label</Label>} />
    ).find('PageTitle');

    expect(wrapper).toMatchSnapshot();

    const extra = wrapper.find(Label);

    expect(extra.exists()).toBeTruthy();
  });
});

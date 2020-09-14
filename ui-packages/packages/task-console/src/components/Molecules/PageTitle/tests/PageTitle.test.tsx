import React from 'react';
import PageTitle from '../PageTitle';
import { getWrapper } from '@kogito-apps/common';
import { Label } from '@patternfly/react-core';

describe('PageTitle test', () => {
  it('default snapshot testing', () => {
    const wrapper = getWrapper(<PageTitle title="Title" />, 'PageTitle');

    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing with extra', () => {
    const wrapper = getWrapper(
      <PageTitle title="Title" extra={<Label>Label</Label>} />,
      'PageTitle'
    );

    expect(wrapper).toMatchSnapshot();

    const extra = wrapper.find(Label);

    expect(extra.exists()).toBeTruthy();
  });
});

import React from 'react';
import PageLayout from '../PageLayout';
import { getWrapper } from '@kogito-apps/common';
import { MemoryRouter as Router } from 'react-router-dom';

const props: any = {
  location: {
    pathname: '/'
  },
  history: []
};

jest.mock('../../DataListContainerExpandable/DataListContainerExpandable.tsx');

describe('PageLayout tests', () => {
  it('Brand click testing', () => {
    const wrapper = getWrapper(
      <Router keyLength={0}>
        <PageLayout {...props} />
      </Router>,
      'PageLayout'
    );

    wrapper
      .find('KogitoPageLayout')
      .props()
      [
        // tslint:disable-next-line
        'BrandClick'
      ]();
  });
});

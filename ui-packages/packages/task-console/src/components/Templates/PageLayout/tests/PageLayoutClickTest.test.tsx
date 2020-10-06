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

const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoPageLayout: () => {
    return <MockedComponent />;
  }
}));

jest.mock('../../../Organisms/TaskInbox/TaskInbox.tsx');

describe('PageLayout Click test', () => {
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

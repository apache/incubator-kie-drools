import React from 'react';
import DomainExplorerLandingPage from '../DomainExplorerLandingPage';
import { MemoryRouter as Router } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapper } from '@kogito-apps/common';

jest.mock(
  '@kogito-apps/common/src/components/Organisms/DomainExplorerListDomains/DomainExplorerListDomains'
);

const MockedBreadcrumb = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () => ({
  ...jest.requireActual('@patternfly/react-core'),
  Breadcrumb: () => <MockedBreadcrumb />
}));

describe('Domain Explorer Landing Page Component', () => {
  const props = {
    ouiaContext: {
      isOuia: false,
      ouiaId: null
    } as any
  };
  it('Snapshot test with default props', () => {
    const wrapper = getWrapper(
      <MockedProvider mocks={[]} addTypename={false}>
        <Router keyLength={0}>
          <DomainExplorerLandingPage {...props} />
        </Router>
      </MockedProvider>,
      'DomainExplorerLandingPage'
    );
    expect(wrapper).toMatchSnapshot();
  });
});

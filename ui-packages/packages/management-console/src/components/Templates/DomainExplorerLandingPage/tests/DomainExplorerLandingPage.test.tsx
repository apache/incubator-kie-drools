import React from 'react';
import DomainExplorerLandingPage from '../DomainExplorerLandingPage';
import { MemoryRouter as Router } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';
import { mount } from 'enzyme';

const MockedDomainExplorerListDomains = (): React.ReactElement => {
  return <></>;
};
jest.mock('@kogito-apps/common', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/common'), {
    DomainExplorerListDomains: () => {
      return <MockedDomainExplorerListDomains />;
    }
  })
);

const MockedBreadcrumb = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Breadcrumb: () => <MockedBreadcrumb />
  })
);

describe('Domain Explorer Landing Page Component', () => {
  const props = {
    ouiaId: null,
    ouiaSafe: true
  };
  it('Snapshot test with default props', () => {
    const wrapper = mount(
      <MockedProvider mocks={[]} addTypename={false}>
        <Router keyLength={0}>
          <DomainExplorerLandingPage {...props} />
        </Router>
      </MockedProvider>
    ).find('DomainExplorerLandingPage');
    expect(wrapper).toMatchSnapshot();
  });
});

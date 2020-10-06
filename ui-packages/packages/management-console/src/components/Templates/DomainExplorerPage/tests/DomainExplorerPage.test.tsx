import React from 'react';
import { shallow } from 'enzyme';
import { BrowserRouter, match } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';
import DomainExplorerPage from '../DomainExplorerPage';
import { getWrapper } from '@kogito-apps/common';
import * as H from 'history';

const MockedDomainExplorer = (): React.ReactElement => {
  return <></>;
};
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  DomainExplorer: () => {
    return <MockedDomainExplorer />;
  }
}));
const MockedBreadcrumb = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () => ({
  ...jest.requireActual('@patternfly/react-core'),
  Breadcrumb: () => <MockedBreadcrumb />
}));
const props = {
  domains: ['Travels', 'VisaApplications'],
  loadingState: false
};
const path = '/DomainExplorer/:domainName';
const match: match<{ domainName: string }> = {
  isExact: false,
  path,
  url: path.replace(':domainName', 'Travels'),
  params: { domainName: 'Travels' }
};

const routeComponentPropsMock = {
  history: H.createMemoryHistory({ keyLength: 0 }),
  location: {
    pathname: '/DomainExplorer/Travels',
    state: {
      parameters: [{ flight: ['arrival'] }, { flight: ['departure'] }]
    },
    search: '',
    hash: ''
  },
  match
};
const routeComponentPropsMock2 = {
  history: H.createMemoryHistory(),
  location: {
    pathname: '/DomainExplorer/Travels',
    search: '',
    state: {},
    hash: ''
  },
  match
};
const props2 = {
  domains: ['Travels', 'VisaApplications'],
  location: {
    pathname: '/DomainExplorer/Travels',
    search: '',
    state: {},
    hash: ''
  },
  match,
  loadingState: false
};

describe('DomainExplorerPage component', () => {
  it('Snapshot with default props', () => {
    const wrapper = getWrapper(
      <MockedProvider mocks={[]} addTypename={false}>
        <BrowserRouter>
          <DomainExplorerPage {...props} {...routeComponentPropsMock} />
        </BrowserRouter>
      </MockedProvider>,
      'DomainExplorerPage'
    );

    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('Check error response for getQueryFields query', async () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorerPage {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>,
      'DomainExplorerPage'
    );
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('Mock query testing', async () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorerPage {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>,
      'DomainExplorerPage'
    );
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('Check error response for getPicker query', () => {
    const wrapper = shallow(
      <BrowserRouter>
        <DomainExplorerPage {...props} {...routeComponentPropsMock} />
      </BrowserRouter>
    );
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
  it('Check error response for getQueryTypes', () => {
    const wrapper = shallow(
      <BrowserRouter>
        <DomainExplorerPage {...props} {...routeComponentPropsMock} />
      </BrowserRouter>
    );
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
  it('check assertions on rememberedParams', () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorerPage {...props2} {...routeComponentPropsMock2} />
        </MockedProvider>
      </BrowserRouter>,
      'DomainExplorerPage'
    );
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
});

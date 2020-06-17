import React from 'react';
import { shallow, mount } from 'enzyme';
import { BrowserRouter } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';
import DomainExplorerPage from '../DomainExplorerPage';

const props = {
  domains: ['Travels', 'VisaApplications'],
  loadingState: false
};

const routeComponentPropsMock = {
  history: {} as any,
  location: {
    pathname: '/DomainExplorer/Travels',
    state: {
      parameters: [{ flight: ['arrival'] }, { flight: ['departure'] }]
    }
  } as any,
  match: {
    params: {
      domainName: 'Travels'
    }
  } as any
};
const routeComponentPropsMock2 = {
  history: {} as any,
  location: {
    pathname: '/DomainExplorer/Travels',
    state: {}
  } as any,
  match: {
    params: {
      domainName: 'Travels'
    }
  } as any
};
const props2 = {
  domains: ['Travels', 'VisaApplications'],
  location: {
    pathname: '/DomainExplorer/Travels',
    state: {}
  },
  match: {
    params: {
      domainName: 'Travels'
    }
  },
  loadingState: false
};

describe('Domain Explorer Dashboard component', () => {
  it('Snapshot test', () => {
    const wrapper = mount(
      <MockedProvider mocks={[]} addTypename={false}>
        <BrowserRouter>
          <DomainExplorerPage {...props} {...routeComponentPropsMock} />
        </BrowserRouter>
      </MockedProvider>
    );

    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
  it('Check error response for getQueryFields query', async () => {
    const wrapper = mount(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorerPage {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>
    );
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper.find(DomainExplorerPage)).toMatchSnapshot();
  });
  it('Mock query testing', async () => {
    const wrapper = mount(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorerPage {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>
    );
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper.find(DomainExplorerPage)).toMatchSnapshot();
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
    const wrapper = mount(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorerPage {...props2} {...routeComponentPropsMock2} />
        </MockedProvider>
      </BrowserRouter>
    );
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
});

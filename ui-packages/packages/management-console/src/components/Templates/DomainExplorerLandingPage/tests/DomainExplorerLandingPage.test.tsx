import React from 'react';
import { mount } from 'enzyme';
import DomainExplorerLandingPage from '../DomainExplorerLandingPage';
import { MemoryRouter as Router } from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';

describe('Domain Explorer Landing Page Component', () => {
  const props = {
    ouiaContext: {
      isOuia: false,
      ouiaId: null
    } as any
  };
  it('Snapshot test', () => {
    const wrapper = mount(
      <MockedProvider mocks={[]} addTypename={false}>
        <Router keyLength={0}>
          <DomainExplorerLandingPage {...props} />
        </Router>
      </MockedProvider>
    );
    expect(wrapper.find(DomainExplorerLandingPage)).toMatchSnapshot();
  });
});

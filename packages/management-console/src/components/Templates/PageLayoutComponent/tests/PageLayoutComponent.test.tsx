import React from 'react';
import PageLayoutComponent from '../PageLayoutComponent';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapper } from '@kogito-apps/common'
import { MemoryRouter as Router } from 'react-router-dom';


const props: any = {
  location: {
    pathname: '/ProcessInstances'
  }
};

const mocks = [];

jest.mock('../../DataListContainer/DataListContainer.tsx');
 
describe('PageLayoutComponent component tests', () => {
  it('snapshot testing', () => {
    const wrapper = getWrapper(
      // keyLength set to zero to have stable snapshots
      <Router keyLength={0}>
        <MockedProvider mocks={mocks}>
          <PageLayoutComponent {...props} />
        </MockedProvider>
      </Router>
      , 'PageLayoutComponent');
      expect(wrapper).toMatchSnapshot();
    });
  });

import React from 'react';
import PageLayoutComponent from '../PageLayoutComponent';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapper, useGetQueryFieldsQuery } from '@kogito-apps/common';
import { MemoryRouter as Router } from 'react-router-dom';

const props: any = {
  location: {
    pathname: '/ProcessInstances'
  },
  history: []
};

const mocks = [];

jest.mock('../../DataListContainer/DataListContainer.tsx');

jest.mock('@kogito-apps/common/src/graphql/types');
describe('PageLayoutComponent tests', () => {
  // @ts-ignore
  useGetQueryFieldsQuery.mockReturnValue({
    loading: false,
    data: {
      __type: {
        fields: [
          {
            name: 'Travels'
          },
          {
            name: 'visaApplication'
          },
          {
            name: 'Jobs'
          }
        ]
      }
    }
  });
  it('snapshot testing', () => {
    const wrapper = getWrapper(
      // keyLength set to zero to have stable snapshots
      <Router keyLength={0}>
        <MockedProvider mocks={mocks}>
          <PageLayoutComponent {...props} />
        </MockedProvider>
      </Router>,
      'PageLayoutComponent'
    );
    wrapper
      .find('PageLayout')
      .props()
      [
        // tslint:disable-next-line
        'BrandClick'
      ]();
    expect(wrapper).toMatchSnapshot();
  });
});

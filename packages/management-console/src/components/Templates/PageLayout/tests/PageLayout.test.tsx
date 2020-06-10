import React from 'react';
import PageLayout from '../PageLayout';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapper, GraphQL } from '@kogito-apps/common';
import { MemoryRouter as Router } from 'react-router-dom';

const props: any = {
  location: {
    pathname: '/ProcessInstances'
  },
  history: []
};

const mocks = [];

jest.mock('../../ProcessListPage/ProcessListPage.tsx');

jest.mock('@kogito-apps/common/src/graphql/types');
describe('PageLayout tests', () => {
  // @ts-ignore
  GraphQL.useGetQueryFieldsQuery.mockReturnValue({
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
          <PageLayout {...props} />
        </MockedProvider>
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
    expect(wrapper).toMatchSnapshot();
  });
});

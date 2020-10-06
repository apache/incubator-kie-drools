import React from 'react';
import PageLayout from '../PageLayout';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapper, GraphQL } from '@kogito-apps/common';
import { MemoryRouter as Router } from 'react-router-dom';
import * as H from 'history';

const props = {
  location: {
    pathname: '/ProcessInstances',
    search: '',
    state: '',
    hash: ''
  },
  history: H.createMemoryHistory({ keyLength: 0 })
};

const mocks = [
  {
    request: {
      query: GraphQL.GetQueryFieldsDocument
    },
    result: {
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
    }
  }
];

jest.mock('../../ProcessListPage/ProcessListPage.tsx');

jest.mock('@kogito-apps/common/src/graphql/types');
const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoPageLayout: () => {
    return <MockedComponent />;
  }
}));
describe('PageLayout tests', () => {
  it('snapshot testing', () => {
    const wrapper = getWrapper(
      // keyLength set to zero to have stable snapshots
      <Router keyLength={0}>
        <MockedProvider mocks={mocks} addTypename={false}>
          <PageLayout {...props} />
        </MockedProvider>
      </Router>,
      'PageLayout'
    );
    wrapper
      .find('KogitoPageLayout')
      .props()
      ['BrandClick']();
    expect(wrapper).toMatchSnapshot();
  });
});

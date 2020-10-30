import React from 'react';
import PageLayout from '../PageLayout';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapperAsync, GraphQL } from '@kogito-apps/common';
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
      data: {
        __type: {
          fields: [
            {
              name: 'Travels',
              args: [
                {
                  name: 'where',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'TravelsArgument',
                    __typename: '__Type'
                  },
                  __typename: '__InputValue'
                },
                {
                  name: 'orderBy',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'TravelsOrderBy',
                    __typename: '__Type'
                  },
                  __typename: '__InputValue'
                },
                {
                  name: 'pagination',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'Pagination',
                    __typename: '__Type'
                  },
                  __typename: '__InputValue'
                }
              ],
              type: {
                ofType: { name: 'Travels', __typename: '__Type' },
                __typename: '__Type'
              },
              __typename: '__Field'
            },
            {
              name: 'visaApplication',
              args: [
                {
                  name: 'where',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'VisaApplicationsArgument',
                    __typename: '__Type'
                  },
                  __typename: '__InputValue'
                },
                {
                  name: 'orderBy',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'VisaApplicationsOrderBy',
                    __typename: '__Type'
                  },
                  __typename: '__InputValue'
                },
                {
                  name: 'pagination',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'Pagination',
                    __typename: '__Type'
                  },
                  __typename: '__InputValue'
                }
              ],
              type: {
                ofType: { name: 'VisaApplications', __typename: '__Type' },
                __typename: '__Type'
              },
              __typename: '__Field'
            },
            {
              name: 'Jobs',
              args: [
                {
                  name: 'where',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'JobArgument',
                    __typename: '__Type'
                  },
                  __typename: '__InputValue'
                },
                {
                  name: 'orderBy',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'JobOrderBy',
                    __typename: '__Type'
                  },
                  __typename: '__InputValue'
                },
                {
                  name: 'pagination',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'Pagination',
                    __typename: '__Type'
                  },
                  __typename: '__InputValue'
                }
              ],
              type: {
                ofType: { name: 'Job', __typename: '__Type' },
                __typename: '__Type'
              },
              __typename: '__Field'
            }
          ]
        }
      }
    }
  }
];

jest.mock('../../ProcessListPage/ProcessListPage.tsx');

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
  it('snapshot testing', async () => {
    let wrapper = await getWrapperAsync(
      // keyLength set to zero to have stable snapshots
      <MockedProvider mocks={mocks} addTypename={false}>
        <Router keyLength={0}>
          <PageLayout {...props} />
        </Router>
      </MockedProvider>,
      'PageLayout'
    );
    expect(wrapper.find('PageLayout')).toMatchSnapshot();
    wrapper = wrapper.update();
    wrapper
      .find('KogitoPageLayout')
      .props()
      ['BrandClick']();
  });
});

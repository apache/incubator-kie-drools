import React from 'react';
import { mount } from 'enzyme';
import DomainExplorerListDomains from '../DomainExplorerListDomains';
import { GraphQL } from '../../../../graphql/types';
import { MemoryRouter as Router } from 'react-router-dom';
import useGetQueryFieldsQuery = GraphQL.useGetQueryFieldsQuery;

jest.mock('../../../../graphql/types');
jest.mock('@patternfly/react-icons');
describe('DomainExplorerListDomains Component test cases', () => {
  it('Snapshot with mock useGetQueryFieldsQuery', () => {
    // @ts-ignore
    useGetQueryFieldsQuery.mockReturnValue({
      loading: false,
      data: {
        __type: {
          fields: [
            {
              name: 'ProcessInstances',
              args: []
            },
            {
              name: 'UserTaskInstances',
              args: []
            },
            {
              name: 'Travels',
              args: []
            },
            {
              name: 'visaApplication',
              args: []
            },
            {
              name: 'Jobs',
              args: []
            }
          ]
        }
      }
    });
    const wrapper = mount(
      <Router keyLength={0}>
        <DomainExplorerListDomains />
      </Router>
    );
    expect(useGetQueryFieldsQuery).toHaveBeenCalled();
    expect(wrapper.find(DomainExplorerListDomains)).toMatchSnapshot();
  });
  it('Assertions', () => {
    // @ts-ignore
    useGetQueryFieldsQuery.mockReturnValue({
      loading: false,
      data: {
        __type: {
          fields: [
            {
              name: 'UserTaskInstances',
              args: []
            },
            {
              name: 'ProcessInstances',
              args: []
            },
            {
              name: 'Travels',
              args: [
                {
                  name: 'where',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'TravelsArgument'
                  }
                },
                {
                  name: 'orderBy',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'TravelsOrderBy'
                  }
                }
              ],
              type: { ofType: { name: 'Travels' } }
            },
            {
              name: 'visaApplication',
              args: [
                {
                  name: 'where',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'VisaApplicationsArgument'
                  }
                },
                {
                  name: 'orderBy',
                  type: {
                    kind: 'INPUT_OBJECT',
                    name: 'VisaApplicationsOrderBy'
                  }
                }
              ],
              type: { ofType: { name: 'VisaApplications' } }
            }
          ]
        }
      }
    });
    const wrapper = mount(
      <Router keyLength={0}>
        <DomainExplorerListDomains />
      </Router>
    );
    expect(useGetQueryFieldsQuery).toHaveBeenCalled();
    expect(wrapper.find(DomainExplorerListDomains)).toMatchSnapshot();
  });
});

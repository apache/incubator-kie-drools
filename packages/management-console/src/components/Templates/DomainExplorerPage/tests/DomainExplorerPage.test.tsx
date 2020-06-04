import React from 'react';
import { mount } from 'enzyme';
import { BrowserRouter } from 'react-router-dom';
import {
  useGetQueryTypesQuery,
  useGetQueryFieldsQuery,
  useGetColumnPickerAttributesQuery
} from '@kogito-apps/common';
import DomainExplorerPage from '../DomainExplorerPage';
import { MockedProvider } from '@apollo/react-testing';
import gql from 'graphql-tag';

jest.mock('react-apollo');
const GET_QUERY_FIELDS = gql`
  query getQueryFields {
    __type(name: "Query") {
      name
      fields {
        name
        args {
          name
          type {
            kind
            name
          }
        }
        type {
          ofType {
            name
          }
        }
      }
    }
  }
`;

const props = {
  domains: ['Travels', 'VisaApplications']
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
  location: { pathname: '/DomainExplorer/Travels', state: {} } as any,
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
  }
};

jest.mock('@kogito-apps/common/src/graphql/types');
describe('Domain Explorer Dashboard component', () => {
  it('Snapshot test', () => {
    // @ts-ignore
    useGetColumnPickerAttributesQuery.mockReturnValue({
      loading: false,
      data: {
        __type: {
          fields: [
            {
              name: 'flight',
              type: {
                name: 'Flight',
                kind: 'OBJECT',
                fields: [
                  {
                    name: 'arrival',
                    type: {
                      name: 'String',
                      kind: 'SCALAR'
                    }
                  }
                ]
              }
            },
            {
              name: 'id',
              type: {
                name: 'String',
                kind: 'SCALAR',
                fields: null
              }
            }
          ]
        }
      }
    });
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
    // @ts-ignore
    useGetQueryTypesQuery.mockReturnValue({
      loading: false,
      data: {}
    });
    const wrapper = mount(
      <BrowserRouter>
        <DomainExplorerPage {...props} {...routeComponentPropsMock} />
      </BrowserRouter>
    );

    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
  it('Check error response for getQueryFields query', async () => {
    // @ts-ignore
    useGetQueryFieldsQuery.mockReturnValue({
      loading: false,
      data: null,
      error: {}
    });
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
    // @ts-ignore
    useGetColumnPickerAttributesQuery.mockReturnValue({
      loading: false,
      data: {
        __type: {
          fields: [
            {
              name: 'flight',
              type: {
                name: 'Flight',
                kind: 'OBJECT',
                fields: [
                  {
                    name: 'arrival',
                    type: {
                      name: 'String',
                      kind: 'SCALAR'
                    }
                  }
                ]
              }
            },
            {
              name: 'id',
              type: {
                name: 'String',
                kind: 'SCALAR',
                fields: null
              }
            }
          ]
        }
      }
    });
    // @ts-ignore
    useGetQueryTypesQuery.mockReturnValue({
      loading: false,
      data: {}
    });
    const mocks = [
      {
        request: {
          query: GET_QUERY_FIELDS
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
    const wrapper = mount(
      <BrowserRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <DomainExplorerPage {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>
    );
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper.find(DomainExplorerPage)).toMatchSnapshot();
    expect(useGetQueryFieldsQuery).toHaveBeenCalled();
    expect(useGetQueryTypesQuery).toHaveBeenCalled();
    expect(useGetColumnPickerAttributesQuery).toBeCalledWith({
      variables: { columnPickerType: 'Travels' }
    });
  });
  it('Check error response for getPicker query', () => {
    // @ts-ignore
    useGetColumnPickerAttributesQuery.mockReturnValue({
      loading: false,
      error: {}
    });
    const wrapper = mount(
      <BrowserRouter>
        <DomainExplorerPage {...props} {...routeComponentPropsMock} />
      </BrowserRouter>
    );
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
  it('Check error response for getQueryTypes', () => {
    // @ts-ignore
    useGetQueryTypesQuery.mockReturnValue({
      loading: false,
      data: null,
      error: {}
    });
    const wrapper = mount(
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
        <DomainExplorerPage {...props2} {...routeComponentPropsMock2} />
      </BrowserRouter>
    );
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
});

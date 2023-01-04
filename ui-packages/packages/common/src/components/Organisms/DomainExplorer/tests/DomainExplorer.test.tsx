import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import DomainExplorer from '../DomainExplorer';
import { MockedProvider } from '@apollo/react-testing';
import { mount } from 'enzyme';
import { GraphQL } from '../../../../graphql/types';
import useGetQueryTypesQuery = GraphQL.useGetQueryTypesQuery;
import useGetQueryFieldsQuery = GraphQL.useGetQueryFieldsQuery;
import useGetColumnPickerAttributesQuery = GraphQL.useGetColumnPickerAttributesQuery;
import useGetInputFieldsFromTypeQuery = GraphQL.useGetInputFieldsFromTypeQuery;
import useGetInputFieldsFromQueryQuery = GraphQL.useGetInputFieldsFromQueryQuery;
import { act } from 'react-dom/test-utils';
import reactApollo from 'react-apollo';
import wait from 'waait';
jest.mock('apollo-client');
jest.mock('react-apollo', () => {
  const ApolloClient = { query: jest.fn() };
  return { useApolloClient: jest.fn(() => ApolloClient) };
});
jest.mock('../../../../utils/Utils');
jest.mock(
  '../../../Molecules/DomainExplorerManageColumns/DomainExplorerManageColumns'
);
jest.mock('../../../Molecules/DomainExplorerTable/DomainExplorerTable');
jest.mock('../../../Atoms/LoadMore/LoadMore');
jest.mock('../../../Atoms/KogitoSpinner/KogitoSpinner');

const props = {
  domains: ['Travels', 'VisaApplications'],
  loadingState: false,
  rememberedParams: [{ flight: ['arrival'] }, { flight: ['departure'] }],
  rememberedSelections: [],
  rememberedFilters: {
    metadata: {
      processInstances: {
        state: { equal: 'ACTIVE' }
      }
    }
  },
  rememberedChips: ['metadata / processInstances / state: ACTIVE'],
  domainName: 'Travels',
  metaData: {
    metadata: [
      {
        processInstances: [
          'id',
          'processName',
          'state',
          'start',
          'lastUpdate',
          'businessKey',
          'serviceUrl'
        ]
      }
    ]
  },
  defaultChip: ['metadata / processInstances / state: ACTIVE'],
  defaultFilter: {
    metadata: {
      processInstances: {
        state: {
          equal: 'ACTIVE'
        }
      }
    }
  }
};

const routeComponentPropsMock = {
  history: { locations: { key: 'ugubul' } } as any,
  location: {
    pathname: '/DomainExplorer/Travels',
    state: {
      parameters: [{ flight: ['arrival'] }, { flight: ['departure'] }],
      finalFilters: {
        metadata: {
          processInstances: {
            state: {
              equal: 'ACTIVE'
            }
          }
        }
      }
    },
    key: 'ugubul'
  } as any,
  match: {
    params: {
      domainName: 'Travels'
    }
  } as any
};

jest.mock('../../../../graphql/types');

describe('Domain Explorer component', () => {
  const mGraphQLResponse2 = {
    data: {
      Travels: []
    }
  };
  let mGraphQLResponse;
  act(() => {
    mGraphQLResponse = {
      data: {
        Travels: [
          {
            flight: {
              arrival: '2020-07-22T03:30:00.000+05:30',
              departure: '2020-07-07T03:30:00.000+05:30',
              flightNumber: 'MX555',
              gate: null,
              seat: null
            },
            metadata: {
              processInstances: [
                {
                  businessKey: 'LKJD13',
                  id: '37bc93d0-1100-3913-85aa-a8dc253281b0',
                  lastUpdate: '2020-07-06T09:16:09.823Z',
                  processName: 'travels',
                  serviceUrl: 'http://localhost:8080',
                  start: '2020-07-06T09:16:09.58Z',
                  state: 'ACTIVE'
                },
                {
                  businessKey: null,
                  id: '8526d522-24f6-4d12-b975-394a0adeb8f8',
                  lastUpdate: '2020-07-06T09:16:09.824Z',
                  processName: 'HotelBooking',
                  serviceUrl: 'http://localhost:8080',
                  start: '2020-07-06T09:16:09.746Z',
                  state: 'COMPLETED'
                }
              ]
            }
          },
          {
            flight: {
              arrival: '2020-07-23T03:30:00.000+05:30',
              departure: '2020-07-10T03:30:00.000+05:30',
              flightNumber: 'MX555',
              gate: null,
              seat: null
            },
            metadata: {
              processInstances: [
                {
                  businessKey: '4Y0W6E',
                  id: 'd2b4967b-e8b1-3232-a07c-d639e08a11d4',
                  lastUpdate: '2020-07-06T09:16:55.621Z',
                  processName: 'travels',
                  serviceUrl: 'http://localhost:8080',
                  start: '2020-07-06T09:16:55.609Z',
                  state: 'ACTIVE'
                },
                {
                  businessKey: null,
                  id: 'cd5f6cc6-7ef4-4eb1-947b-3f53f201ab15',
                  lastUpdate: '2020-07-06T09:16:55.621Z',
                  processName: 'HotelBooking',
                  serviceUrl: 'http://localhost:8080',
                  start: '2020-07-06T09:16:55.611Z',
                  state: 'COMPLETED'
                }
              ]
            }
          }
        ]
      },
      loading: false,
      networkStatus: 7,
      stale: false
    };
  });
  let client;
  let useApolloClient;
  const mockUseApolloClient = () => {
    act(() => {
      client = useApolloClient();
    });
  };

  beforeEach(() => {
    act(() => {
      useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
      mockUseApolloClient();
    });
  });
  it('Snapshot test with default prop', async () => {
    client.query.mockReturnValueOnce(mGraphQLResponse);
    (useGetColumnPickerAttributesQuery as jest.Mock).mockReturnValue({
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
    (useGetQueryFieldsQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          fields: [
            {
              name: 'Travels',
              args: [
                {
                  name: 'where',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsArgument' }
                },
                {
                  name: 'orderBy',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsOrderBy' }
                },
                {
                  name: 'pagination',
                  type: { kind: 'INPUT_OBJECT', name: 'Pagination' }
                }
              ]
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
    (useGetQueryTypesQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __schema: {
          queryType: [
            {
              name: 'TestArgument',
              inputFields: [
                {
                  name: 'test',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'AddressArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                {
                  name: 'city',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'country',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'street',
                  type: { name: 'TestArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'zipCode',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'IdArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                { name: 'id', type: { name: null, kind: 'LIST' } },
                { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
                { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
              ]
            }
          ]
        }
      }
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={[]} addTypename={false}>
            <DomainExplorer {...props} {...routeComponentPropsMock} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorer');
    });
    await act(async () => {
      wrapper.find('Toolbar').props()['clearAllFilters']();
    });
    expect(wrapper).toMatchSnapshot();
    expect(useGetColumnPickerAttributesQuery).toBeCalledWith({
      variables: { columnPickerType: 'Travels' }
    });
  });
  it('Check error response for getQueryFields query', async () => {
    (useGetQueryFieldsQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: null,
      error: {}
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={[]} addTypename={false}>
            <DomainExplorer {...props} {...routeComponentPropsMock} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorer');
    });
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('h1').text()).toEqual('Error fetching data');
  });
  it('Mock query testing', async () => {
    client.query.mockReturnValueOnce(mGraphQLResponse);
    (useGetQueryFieldsQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          fields: [
            {
              name: 'Travels',
              args: [
                {
                  name: 'where',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsArgument' }
                },
                {
                  name: 'orderBy',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsOrderBy' }
                },
                {
                  name: 'pagination',
                  type: { kind: 'INPUT_OBJECT', name: 'Pagination' }
                }
              ]
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
    (useGetColumnPickerAttributesQuery as jest.Mock).mockReturnValue({
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
    (useGetQueryTypesQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __schema: {
          queryType: [
            {
              name: 'TestArgument',
              inputFields: [
                {
                  name: 'test',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'AddressArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                {
                  name: 'city',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'country',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'street',
                  type: { name: 'TestArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'zipCode',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'IdArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                { name: 'id', type: { name: null, kind: 'LIST' } },
                { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
                { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
              ]
            }
          ]
        }
      }
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={[]} addTypename={false}>
            <DomainExplorer {...props} {...routeComponentPropsMock} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorer');
    });
    wrapper.update();
    expect(wrapper.find(DomainExplorer)).toMatchSnapshot();
    expect(useGetQueryFieldsQuery).toHaveBeenCalled();
    expect(useGetQueryTypesQuery).toHaveBeenCalled();
    expect(useGetColumnPickerAttributesQuery).toBeCalledWith({
      variables: { columnPickerType: 'Travels' }
    });
    act(() => {
      // tslint:disable-next-line: no-string-literal
      wrapper
        .find('Toolbar')
        .props()
        ['clearAllFilters']('Filters', 'hotel/address / country: like s');
    });
  });
  it('Check error response for getPicker query', async () => {
    (useGetColumnPickerAttributesQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: null,
      error: {}
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={[]} addTypename={false}>
            <DomainExplorer {...props} {...routeComponentPropsMock} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorer');
    });
    wrapper.update();
    expect(wrapper.find('h1').text()).toEqual('Error fetching data');
  });
  it('Check error response for getQueryTypes', async () => {
    (useGetQueryTypesQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: null,
      error: {}
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={[]} addTypename={false}>
            <DomainExplorer {...props} {...routeComponentPropsMock} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorer');
    });
    wrapper.update();
    expect(wrapper.find('h1').text()).toEqual('Error fetching data');
  });
  it('check assertions on rememberedParams', async () => {
    client.query.mockReturnValueOnce(mGraphQLResponse);
    (useGetColumnPickerAttributesQuery as jest.Mock).mockReturnValue({
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
    (useGetQueryFieldsQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          fields: [
            {
              name: 'Travels',
              args: [
                {
                  name: 'where',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsArgument' }
                },
                {
                  name: 'orderBy',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsOrderBy' }
                },
                {
                  name: 'pagination',
                  type: { kind: 'INPUT_OBJECT', name: 'Pagination' }
                }
              ]
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
    (useGetQueryTypesQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __schema: {
          queryType: [
            {
              name: 'TestArgument',
              inputFields: [
                {
                  name: 'test',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'AddressArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                {
                  name: 'city',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'country',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'street',
                  type: { name: 'TestArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'zipCode',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'IdArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                { name: 'id', type: { name: null, kind: 'LIST' } },
                { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
                { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
              ]
            }
          ]
        }
      }
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={[]} addTypename={false}>
            <DomainExplorer {...props} {...routeComponentPropsMock} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorer');
    });
    wrapper.update();
    expect(wrapper.find(DomainExplorer)).toMatchSnapshot();
  });
  it('Check generated query', async () => {
    client.query.mockReturnValueOnce(mGraphQLResponse);
    (useGetQueryFieldsQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          fields: [
            {
              name: 'Travels',
              args: [
                {
                  name: 'where',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsArgument' }
                },
                {
                  name: 'orderBy',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsOrderBy' }
                },
                {
                  name: 'pagination',
                  type: { kind: 'INPUT_OBJECT', name: 'Pagination' }
                }
              ]
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
    (useGetColumnPickerAttributesQuery as jest.Mock).mockReturnValue({
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
    (useGetQueryTypesQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __schema: {
          queryType: [
            {
              name: 'TestArgument',
              inputFields: [
                {
                  name: 'test',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'AddressArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                {
                  name: 'city',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'country',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'street',
                  type: { name: 'TestArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'zipCode',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'IdArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                { name: 'id', type: { name: null, kind: 'LIST' } },
                { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
                { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
              ]
            }
          ]
        }
      }
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={[]} addTypename={false}>
            <DomainExplorer {...props} {...routeComponentPropsMock} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorer');
    });
    wrapper.update();
    expect(useGetQueryFieldsQuery).toHaveBeenCalled();
    expect(useGetQueryTypesQuery).toHaveBeenCalled();
    expect(useGetColumnPickerAttributesQuery).toBeCalledWith({
      variables: { columnPickerType: 'Travels' }
    });
    act(() => {
      // tslint:disable-next-line: no-string-literal
      wrapper
        .find('Toolbar')
        .props()
        ['clearAllFilters']('Filters', 'hotel/address / country: like s');
    });
  });
  it('Check null response for generated query', async () => {
    client.query.mockReturnValueOnce(mGraphQLResponse2);
    (useGetQueryFieldsQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          fields: [
            {
              name: 'Travels',
              args: [
                {
                  name: 'where',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsArgument' }
                },
                {
                  name: 'orderBy',
                  type: { kind: 'INPUT_OBJECT', name: 'TravelsOrderBy' }
                },
                {
                  name: 'pagination',
                  type: { kind: 'INPUT_OBJECT', name: 'Pagination' }
                }
              ]
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
    (useGetColumnPickerAttributesQuery as jest.Mock).mockReturnValue({
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
    (useGetQueryTypesQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __schema: {
          queryType: [
            {
              name: 'TestArgument',
              inputFields: [
                {
                  name: 'test',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'AddressArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                {
                  name: 'city',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'country',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'street',
                  type: { name: 'TestArgument', kind: 'INPUT_OBJECT' }
                },
                {
                  name: 'zipCode',
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
                }
              ]
            },
            {
              name: 'IdArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                { name: 'id', type: { name: null, kind: 'LIST' } },
                { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
                { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
              ]
            }
          ]
        }
      }
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={[]} addTypename={false}>
            <DomainExplorer {...props} {...routeComponentPropsMock} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('DomainExplorer');
    });
    wrapper.update();
    expect(useGetQueryFieldsQuery).toHaveBeenCalled();
    expect(useGetQueryTypesQuery).toHaveBeenCalled();
    expect(useGetColumnPickerAttributesQuery).toBeCalledWith({
      variables: { columnPickerType: 'Travels' }
    });
  });
});

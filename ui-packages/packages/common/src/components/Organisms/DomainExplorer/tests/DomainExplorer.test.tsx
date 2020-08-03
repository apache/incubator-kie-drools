import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import DomainExplorer from '../DomainExplorer';
import { MockedProvider } from '@apollo/react-testing';
import { getWrapperAsync } from '../../../../utils/OuiaUtils';
import { GraphQL } from '../../../../graphql/types';
import useGetQueryTypesQuery = GraphQL.useGetQueryTypesQuery;
import useGetQueryFieldsQuery = GraphQL.useGetQueryFieldsQuery;
import useGetColumnPickerAttributesQuery = GraphQL.useGetColumnPickerAttributesQuery;
import { act } from 'react-dom/test-utils';
jest.mock('react-apollo');

jest.mock('../../../../utils/Utils');
jest.mock(
  '../../../Molecules/DomainExplorerFilterOptions/DomainExplorerFilterOptions'
);
jest.mock(
  '../../../Molecules/DomainExplorerManageColumns/DomainExplorerManageColumns'
);
jest.mock('../../../Molecules/DomainExplorerTable/DomainExplorerTable');
jest.mock('../../../Atoms/LoadMore/LoadMore');

const MockedDataToolbar = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () => ({
  ...jest.requireActual('@patternfly/react-core'),
  DataToolbar: () => <MockedDataToolbar />
}));
jest.mock('../../../Atoms/KogitoSpinner/KogitoSpinner');
// tslint:disable: no-string-literal
// tslint:disable: no-unexpected-multiline
const props = {
  domains: ['Travels', 'VisaApplications'],
  loadingState: false,
  rememberedParams: [{ flight: ['arrival'] }, { flight: ['departure'] }],
  rememberedSelections: [],
  rememberedFilters: {},
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
      parameters: [{ flight: ['arrival'] }, { flight: ['departure'] }]
    },
    key: 'ugubul'
  } as any,
  match: {
    params: {
      domainName: 'Travels'
    }
  } as any
};
const routeComponentPropsMock2 = {
  history: { locations: { key: 'ugubul' } } as any,
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
  rememberedParams: [],
  rememberedSelections: [],
  rememberedFilters: {},
  rememberedChips: ['metadata / processInstances / state: ACTIVE'],
  domainName: 'Travels',
  metaData: {},
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

jest.mock('../../../../graphql/types');

describe('Domain Explorer component', () => {
  it('Snapshot test with default prop', async () => {
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
    // @ts-ignore
    useGetQueryTypesQuery.mockReturnValue({
      loading: false,
      data: {}
    });
    const wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorer {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>,
      'DomainExplorer'
    );
    act(() => {
      wrapper
        .find('DataToolbar')
        .props()
        ['clearAllFilters']();
    });
    expect(wrapper).toMatchSnapshot();
  });
  it('Check error response for getQueryFields query', async () => {
    // @ts-ignore
    useGetQueryFieldsQuery.mockReturnValue({
      loading: false,
      data: null,
      error: {}
    });
    const wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorer {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>,
      'DomainExplorer'
    );
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('Mock query testing', async () => {
    // @ts-ignore
    useGetQueryFieldsQuery.mockReturnValue({
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
    const wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorer {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>,
      'DomainExplorer'
    );
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
        .find('DataToolbar')
        .props()
        ['clearAllFilters']('Filters', 'hotel/address / country: like s');
    });
  });
  it('Check error response for getPicker query', async () => {
    // @ts-ignore
    useGetColumnPickerAttributesQuery.mockReturnValue({
      loading: false,
      error: {}
    });
    const wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorer {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>,
      'DomainExplorer'
    );
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('Check error response for getQueryTypes', async () => {
    // @ts-ignore
    useGetQueryTypesQuery.mockReturnValue({
      loading: false,
      data: null,
      error: {}
    });
    const wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorer {...props} {...routeComponentPropsMock} />
        </MockedProvider>
      </BrowserRouter>,
      'DomainExplorer'
    );
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('check assertions on rememberedParams', async () => {
    const wrapper = await getWrapperAsync(
      <BrowserRouter>
        <MockedProvider mocks={[]} addTypename={false}>
          <DomainExplorer {...props2} {...routeComponentPropsMock2} />
        </MockedProvider>
      </BrowserRouter>,
      'DomainExplorer'
    );
    wrapper.update();
    expect(wrapper.find(DomainExplorer)).toMatchSnapshot();
  });
});

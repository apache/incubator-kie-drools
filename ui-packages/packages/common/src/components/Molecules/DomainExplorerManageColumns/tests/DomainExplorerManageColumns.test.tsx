import React from 'react';
import { configure, mount, shallow } from 'enzyme';
import DomainExplorerManageColumns from '../DomainExplorerManageColumns';
import Adapter from '@wojtekmaj/enzyme-adapter-react-17';
import reactApollo from 'react-apollo';
import { act } from 'react-dom/test-utils';

configure({ adapter: new Adapter() });

jest.mock('apollo-client');
jest.mock('react-apollo', () => {
  const ApolloClient = { query: jest.fn() };
  return { useApolloClient: jest.fn(() => ApolloClient) };
});
global.Math.random = () => 0.7336705311965102;

const lib = jest.requireActual('tabbable');

const tabbable = {
  ...lib,
  tabbable: (node, options) =>
    lib.tabbable(node, { ...options, displayCheck: 'none' }),
  focusable: (node, options) =>
    lib.focusable(node, { ...options, displayCheck: 'none' }),
  isFocusable: (node, options) =>
    lib.isFocusable(node, { ...options, displayCheck: 'none' }),
  isTabbable: (node, options) =>
    lib.isTabbable(node, { ...options, displayCheck: 'none' })
};

describe('Domain Explorer Manage columns component', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  let client;
  let useApolloClient;

  const mockUseEffect = () => {
    // eslint-disable-next-line react-hooks/rules-of-hooks
    client = useApolloClient();
  };

  beforeEach(() => {
    useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
    mockUseEffect();
  });
  const props = {
    columnPickerType: 'Travels',
    setTableLoading: jest.fn(),
    getQueryTypes: {
      data: {
        __schema: {
          queryType: [
            {
              name: 'Address',
              kind: 'OBJECT',
              fields: [
                {
                  name: 'city',
                  type: {
                    name: 'String',
                    kind: 'SCALAR'
                  }
                },
                {
                  name: 'country',
                  type: {
                    name: 'String',
                    kind: 'SCALAR'
                  }
                },
                {
                  name: 'location',
                  type: {
                    name: 'Test',
                    kind: 'OBJECT'
                  }
                }
              ]
            },
            {
              name: 'Test',
              kind: 'OBJECT',
              fields: [
                {
                  name: 'city',
                  type: {
                    name: 'String',
                    kind: 'SCALAR'
                  }
                },
                {
                  name: 'country',
                  type: {
                    name: 'String',
                    kind: 'SCALAR'
                  }
                }
              ]
            }
          ]
        }
      },
      loading: false
    },
    setDisplayTable: jest.fn(),
    setParameters: jest.fn(),
    selected: [],
    setSelected: jest.fn(),
    data: [
      {
        name: 'id',
        type: {
          fields: null,
          kind: 'SCALAR',
          name: 'String',
          __typename: '__Type'
        }
      },
      {
        name: 'flight',
        type: {
          name: 'Flight',
          fields: [
            {
              name: 'arrival',
              type: {
                name: 'String',
                kind: 'SCALAR'
              }
            },
            {
              name: 'departure',
              type: {
                name: 'String',
                kind: 'SCALAR'
              }
            }
          ],
          kind: 'OBJECT'
        }
      },
      {
        name: 'hotel',
        type: {
          name: 'Hotel',
          fields: [
            {
              name: 'address',
              type: {
                name: 'Address',
                kind: 'OBJECT',
                fields: [
                  {
                    name: 'city',
                    type: {
                      name: 'String',
                      kind: 'SCALAR'
                    }
                  },
                  {
                    name: 'country',
                    type: {
                      name: 'String',
                      kind: 'SCALAR'
                    }
                  }
                ]
              }
            },
            {
              name: 'bookingNumber',
              type: {
                name: 'String',
                kind: 'SCALAR'
              }
            }
          ],
          kind: 'OBJECT'
        }
      }
    ],
    getPicker: {
      data: {},
      loading: false
    },
    setError: jest.fn(),
    setDisplayEmptyState: jest.fn(),
    rememberedParams: [{ flight: ['arrival'] }, { flight: ['departure'] }],
    enableCache: false,
    setEnableCache: jest.fn(),
    pageSize: 10,
    offsetVal: 0,
    setOffsetVal: jest.fn(),
    setPageSize: jest.fn(),
    setIsLoadingMore: jest.fn(),
    isLoadingMore: true,
    metaData: {
      metadata: [
        {
          processInstances: [
            'id',
            'processName',
            'state',
            'start',
            'lastUpdate',
            'businessKey'
          ]
        }
      ]
    },
    isModalOpen: true,
    setIsModalOpen: jest.fn(),
    finalFilters: {},
    argument: 'TravelsArgument',
    setRunQuery: jest.fn(),
    setEnableRefresh: jest.fn(),
    enableRefresh: false
  };
  it('Snapshot testing with default props', () => {
    const wrapper = mount(<DomainExplorerManageColumns {...props} />);
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
  it('Test toggle selection', () => {
    const event2 = { target: {} } as React.MouseEvent<HTMLInputElement>;
    const wrapper = mount(<DomainExplorerManageColumns {...props} />);
    wrapper.update();
    wrapper.setProps({});
    wrapper.find('#manage-columns-button').first().simulate('click');
    act(() => {
      wrapper.find('DataListToggle').first().props().onClick(event2);
      wrapper.find('DataListToggle').at(2).props().onClick(event2);
    });
  });
  it('Test Apply columns button', async () => {
    const mGraphQLResponse = {
      data: {
        Travels: [
          {
            flight: {
              arrival: 'Hello World',
              __typename: 'Flight',
              departure: 'Hello World',
              flightNumber: 'Hello World',
              gate: 'Hello World',
              seat: 'Hello World'
            },
            metadata: {
              processInstances: [
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                },
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                }
              ]
            }
          },
          {
            flight: {
              arrival: 'Hello World',
              __typename: 'Flight',
              departure: 'Hello World',
              flightNumber: 'Hello World',
              gate: 'Hello World',
              seat: 'Hello World'
            },
            metadata: {
              processInstances: [
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                },
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                }
              ]
            }
          }
        ]
      },
      loading: false,
      errors: [],
      networkStatus: '',
      stale: true
    };

    client.query.mockReturnValueOnce(mGraphQLResponse);
    const enableRefresh = true;
    const wrapper = shallow(
      <DomainExplorerManageColumns {...{ ...props, enableRefresh }} />
    );
    wrapper.find('#refresh-button').simulate('click');
    wrapper.find('#manage-columns-button').simulate('click');
    expect(wrapper.find('DataList')).toBeTruthy();
    expect(wrapper.find('DataListToggle')).toBeTruthy();
    await Promise.resolve();
  });
  it('Simulate save button', () => {
    const wrapper = mount(<DomainExplorerManageColumns {...props} />);
    wrapper.find('#manage-columns-button').first().simulate('click');
    wrapper.find('#save-columns').first().simulate('click');
    expect(wrapper.find('DataList')).toBeTruthy();
    expect(wrapper.find('Dropdown')).toBeTruthy();
  });
  it('Test null response for domain attributes', async () => {
    const mGraphQLResponse = {
      data: {
        Travels: [
          {
            flight: null,
            hotel: null,
            metadata: {
              processInstances: [
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                },
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                }
              ]
            }
          },
          {
            flight: {
              arrival: 'Hello World',
              __typename: 'Flight',
              departure: 'Hello World',
              flightNumber: 'Hello World',
              gate: 'Hello World',
              seat: 'Hello World'
            },
            metadata: {
              processInstances: [
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                },
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                }
              ]
            }
          }
        ]
      },
      loading: false,
      errors: [],
      networkStatus: '',
      stale: true
    };
    const enableRefresh = true;
    client.query.mockReturnValueOnce(mGraphQLResponse);
    const wrapper = shallow(
      <DomainExplorerManageColumns {...{ ...props, enableRefresh }} />
    );
    wrapper.find('#refresh-button').simulate('click');
    await Promise.resolve();
  });
  it('Test nested null response for domain attributes', async () => {
    const mGraphQLResponse = {
      data: {
        Travels: [
          {
            flight: null,
            hotel: null,
            metadata: {
              processInstances: [
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                },
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                }
              ]
            }
          },
          {
            flight: {
              arrival: 'Hello World',
              __typename: 'Flight',
              departure: 'Hello World',
              flightNumber: 'Hello World',
              gate: 'Hello World',
              seat: 'Hello World'
            },
            metadata: {
              processInstances: [
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                },
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                }
              ]
            }
          }
        ]
      },
      loading: false,
      errors: [],
      networkStatus: '',
      stale: true
    };
    client.query.mockReturnValueOnce(mGraphQLResponse);
    const enableRefresh = true;
    const wrapper = shallow(
      <DomainExplorerManageColumns {...{ ...props, enableRefresh }} />
    );
    wrapper.find('#refresh-button').simulate('click');
    await Promise.resolve();
  });
  it('Test dropdown items on the modal', async () => {
    const mGraphQLResponse = {
      data: {
        Travels: [
          {
            flight: null,
            hotel: null,
            metadata: {
              processInstances: [
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                },
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                }
              ]
            }
          },
          {
            flight: {
              arrival: 'Hello World',
              __typename: 'Flight',
              departure: 'Hello World',
              flightNumber: 'Hello World',
              gate: 'Hello World',
              seat: 'Hello World'
            },
            metadata: {
              processInstances: [
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                },
                {
                  businessKey: 'Hello World',
                  id: 'Hello World',
                  lastUpdate: 'Sat, 16 May 2020 14:46:29 GMT',
                  processName: 'Hello World',
                  start: 'Sat, 16 May 2020 14:46:29 GMT',
                  state: 'PENDING',
                  __typename: 'ProcessInstanceMeta'
                }
              ]
            }
          }
        ]
      },
      loading: false,
      errors: [],
      networkStatus: '',
      stale: true
    };
    const event = { target: {} } as React.ChangeEvent<HTMLInputElement>;
    client.query.mockReturnValueOnce(mGraphQLResponse);
    const wrapper = mount(<DomainExplorerManageColumns {...props} />);
    // below function triggers made to test handleSelectClick('all')
    wrapper.find('#manage-columns-button').first().simulate('click');
    act(() => {
      wrapper
        .find('#selectAll-dropdown')
        .first()
        .props()
        ['toggle']['props']['splitButtonItems'][0]['props']['onClick']();
      wrapper
        .find('#selectAll-dropdown')
        .first()
        .props()
        ['toggle']['props'].onToggle();
      wrapper.find('#selectAll-dropdown').first().props()['onSelect'](event);
    });
    expect(wrapper.find('#selectAll-dropdown')).toBeTruthy();
    expect(wrapper.find('DropdownItem')).toBeTruthy();
  });
  it('Test anySelected value and query empty response', async () => {
    const mGraphQLResponse = {
      data: {
        Travels: []
      },
      loading: false,
      errors: [],
      networkStatus: '',
      stale: true
    };
    const event = { target: {} } as React.ChangeEvent<HTMLInputElement>;
    client.query.mockReturnValueOnce(mGraphQLResponse);
    const selected = [
      'flight/arrival',
      'flight/departure',
      'flight/flightNumber',
      'flight/gate',
      'flight/seat'
    ];
    const wrapper = mount(
      <DomainExplorerManageColumns {...{ ...props, selected }} />
    );
    // opens modal on click
    wrapper.find('#manage-columns-button').first().simulate('click');
    // tests checkbox on the select all/none dropdown present on the Modal
    act(() => {
      wrapper
        .find('#selectAll-dropdown')
        .first()
        .props()
        ['toggle']['props']['splitButtonItems'][0]['props']['onClick']();
      // triggers toggle function on select all/none dropdown present on the Modal
      wrapper
        .find('#selectAll-dropdown')
        .first()
        .props()
        ['toggle']['props'].onToggle();
      // triggers select function on select all/none dropdown
      wrapper.find('#selectAll-dropdown').first().props().onSelect(event);
      // triggers click on dropdown's first item
      wrapper
        .find('#selectAll-dropdown')
        .first()
        .props()
        ['dropdownItems'][0].props['onClick']();
      // triggers click on dropdown's second item
      wrapper
        .find('#selectAll-dropdown')
        .first()
        .props()
        ['dropdownItems'][1].props['onClick']();
    });
    expect(wrapper.find('#selectAll-dropdown')).toBeTruthy();
  });
  it('test empty paramFields', () => {
    const mGraphQLResponse = {
      data: {
        Travels: []
      },
      loading: false,
      errors: [],
      networkStatus: '',
      stale: true
    };
    client.query.mockReturnValueOnce(mGraphQLResponse);
    const wrapper = mount(<DomainExplorerManageColumns {...props} />);
    wrapper.find('#manage-columns-button').first().simulate('click');
    expect(wrapper.find('#selectAll-dropdown')).toBeTruthy();
  });
});

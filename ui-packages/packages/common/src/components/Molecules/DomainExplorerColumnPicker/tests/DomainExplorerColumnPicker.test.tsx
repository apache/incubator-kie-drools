// tslint:disable:no-string-literal
import React from 'react';
import { shallow, configure, mount } from 'enzyme';
import DomainExplorerColumnPicker from '../DomainExplorerColumnPicker';
import Adapter from 'enzyme-adapter-react-16';
import reactApollo from 'react-apollo';

configure({ adapter: new Adapter() });

jest.mock('apollo-client');
jest.mock('react-apollo', () => {
  const ApolloClient = { query: jest.fn() };
  return { useApolloClient: jest.fn(() => ApolloClient) };
});
global.Math.random = () => 0.7336705311965102;
describe('Domain Explorer Column picker component', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  let client;
  let useApolloClient;

  const mockUseEffect = () => {
    // tslint:disable-next-line: react-hooks-nesting
    client = useApolloClient();
  };

  beforeEach(() => {
    useApolloClient = jest.spyOn(reactApollo, 'useApolloClient');
    mockUseEffect();
  });
  it('Snapshot testing', () => {
    const props = {
      columnPickerType: 'Travels',
      setColumnFilters: jest.fn(),
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
      parameters: [{ flight: ['arrival'] }, { flight: ['departure'] }],
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
                  kind: 'OBJECT'
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
      isLoadingMore: true
    };

    const wrapper = mount(<DomainExplorerColumnPicker {...props} />);
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });

  it('Test Apply columns button', async () => {
    const props = {
      columnPickerType: 'Travels',
      setColumnFilters: jest.fn(),
      setTableLoading: jest.fn(),
      getQueryTypes: {
        data: {},
        loading: false
      },
      setDisplayTable: jest.fn(),
      parameters: [{ flight: ['arrival'] }, { flight: ['departure'] }],
      setParameters: jest.fn(),
      selected: [],
      setSelected: jest.fn(),
      data: [],
      getPicker: {
        data: {},
        loading: false
      },
      setError: jest.fn(),
      setDisplayEmptyState: jest.fn(),
      rememberedParams: [],
      enableCache: false,
      setEnableCache: jest.fn(),
      pageSize: 2,
      offsetVal: 10,
      setOffsetVal: jest.fn(),
      setPageSize: jest.fn(),
      setIsLoadingMore: jest.fn(),
      isLoadingMore: true
    };
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
    const wrapper = shallow(<DomainExplorerColumnPicker {...props} />);
    wrapper.find('#refresh-button').simulate('click');
    wrapper.find('#apply-columns').simulate('click');
    await Promise.resolve();
  });
  it('Test empty response for query', async () => {
    const props = {
      columnPickerType: 'Travels',
      setColumnFilters: jest.fn(),
      setTableLoading: jest.fn(),
      getQueryTypes: {
        data: {},
        loading: false
      },
      setDisplayTable: jest.fn(),
      parameters: [{ flight: ['arrival'] }, { flight: ['departure'] }],
      setParameters: jest.fn(),
      selected: [],
      setSelected: jest.fn(),
      data: [],
      getPicker: {
        data: {},
        loading: false
      },
      setError: jest.fn(),
      setDisplayEmptyState: jest.fn(),
      rememberedParams: [],
      enableCache: false,
      setEnableCache: jest.fn(),
      pageSize: 2,
      offsetVal: 10,
      setOffsetVal: jest.fn(),
      setPageSize: jest.fn(),
      setIsLoadingMore: jest.fn(),
      isLoadingMore: true
    };
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
    const wrapper = shallow(<DomainExplorerColumnPicker {...props} />);
    wrapper.find('#refresh-button').simulate('click');
    wrapper.find('#apply-columns').simulate('click');
    await Promise.resolve();
  });
  it('Test null response for domain attributes', async () => {
    const props = {
      columnPickerType: 'Travels',
      setColumnFilters: jest.fn(),
      setTableLoading: jest.fn(),
      getQueryTypes: {
        data: {},
        loading: false
      },
      setDisplayTable: jest.fn(),
      parameters: [
        { flight: ['arrival'] },
        { flight: ['departure'] },
        { hotel: [{ address: ['city'] }] }
      ],
      setParameters: jest.fn(),
      selected: [],
      setSelected: jest.fn(),
      data: [],
      getPicker: {
        data: {},
        loading: false
      },
      setError: jest.fn(),
      setDisplayEmptyState: jest.fn(),
      rememberedParams: [],
      enableCache: false,
      setEnableCache: jest.fn(),
      pageSize: 2,
      offsetVal: 10,
      setOffsetVal: jest.fn(),
      setPageSize: jest.fn(),
      setIsLoadingMore: jest.fn(),
      isLoadingMore: true
    };
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
    const wrapper = shallow(<DomainExplorerColumnPicker {...props} />);
    wrapper.find('#refresh-button').simulate('click');
    wrapper.find('#apply-columns').simulate('click');
    await Promise.resolve();
  });
  it('Test nested null response for domain attributes', async () => {
    const props = {
      columnPickerType: 'Travels',
      setColumnFilters: jest.fn(),
      setTableLoading: jest.fn(),
      getQueryTypes: {
        data: {},
        loading: false
      },
      setDisplayTable: jest.fn(),
      parameters: [
        { flight: ['arrival'] },
        { flight: ['departure'] },
        { hotel: [{ address: [{ city: [{ test: ['random'] }] }] }] },
        {
          hotel: [{ address: [{ city: [{ test: [{ test2: ['random'] }] }] }] }]
        },
        { hotel: [{ address: [{ country: [{ test: ['random'] }] }] }] }
      ],
      setParameters: jest.fn(),
      selected: [],
      setSelected: jest.fn(),
      data: [],
      getPicker: {
        data: {},
        loading: false
      },
      setError: jest.fn(),
      setDisplayEmptyState: jest.fn(),
      rememberedParams: [],
      enableCache: false,
      setEnableCache: jest.fn(),
      pageSize: 2,
      offsetVal: 10,
      setOffsetVal: jest.fn(),
      setPageSize: jest.fn(),
      setIsLoadingMore: jest.fn(),
      isLoadingMore: true
    };
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
    const wrapper = shallow(<DomainExplorerColumnPicker {...props} />);
    wrapper.find('#refresh-button').simulate('click');
    wrapper.find('#apply-columns').simulate('click');
    await Promise.resolve();
  });
  it('Test refresh button', async () => {
    const props = {
      columnPickerType: 'Travels',
      setColumnFilters: jest.fn(),
      setTableLoading: jest.fn(),
      getQueryTypes: {
        data: {},
        loading: false
      },
      setDisplayTable: jest.fn(),
      parameters: [{ flight: ['arrival'] }, { flight: ['departure'] }],
      setParameters: jest.fn(),
      selected: ['cityhotelAddress'],
      setSelected: jest.fn(),
      data: [],
      getPicker: {
        data: {},
        loading: false
      },
      setError: jest.fn(),
      setDisplayEmptyState: jest.fn(),
      rememberedParams: [],
      enableCache: true,
      setEnableCache: jest.fn(),
      pageSize: 10,
      offsetVal: 0,
      setOffsetVal: jest.fn(),
      setPageSize: jest.fn(),
      setIsLoadingMore: jest.fn(),
      isLoadingMore: true
    };
    const obj = {
      target: { id: 'cityhotelAddress' },
      nativeEvent: {
        target: {
          nextSibling: {
            innerText: ''
          },
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => 'cityhotelAddress')
            }
          }
        }
      }
    };
    const mGraphQLResponse = {
      response: {
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
        }
      },
      loading: false,
      errors: []
    };
    client.query.mockReturnValueOnce(mGraphQLResponse);
    const wrapper = shallow(<DomainExplorerColumnPicker {...props} />);
    wrapper.find('#refresh-button').simulate('click');
    wrapper.find('#apply-columns').simulate('click');
    await Promise.resolve();
    wrapper.find('#columnPicker-dropdown').simulate('select', obj);
    wrapper
      .find('#columnPicker-dropdown')
      .props()
      [
        // tslint:disable-next-line
        'onToggle'
      ]();
  });
  it('check invalid column picker', () => {
    const props = {
      columnPickerType: ' ',
      setColumnFilters: jest.fn(),
      setTableLoading: jest.fn(),
      getQueryTypes: {
        data: {},
        loading: false
      },
      setDisplayTable: jest.fn(),
      parameters: [],
      setParameters: jest.fn(),
      selected: [],
      setSelected: jest.fn(),
      data: [],
      getPicker: {
        data: {},
        loading: false
      },
      setError: jest.fn(),
      setDisplayEmptyState: jest.fn(),
      rememberedParams: [],
      enableCache: false,
      setEnableCache: jest.fn(),
      pageSize: 10,
      offsetVal: 0,
      setOffsetVal: jest.fn(),
      setPageSize: jest.fn(),
      setIsLoadingMore: jest.fn(),
      isLoadingMore: true
    };
    const obj = {
      target: { id: 'id' },
      nativeEvent: {
        target: {
          nextSibling: {
            innerText: ''
          },
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => 'id')
            }
          }
        }
      }
    };
    const wrapper = shallow(<DomainExplorerColumnPicker {...props} />);
    wrapper.update();

    wrapper.find('#columnPicker-dropdown').simulate('select', obj);
    expect(wrapper).toMatchSnapshot();
  });
  it('check condition remembered params equal to zero', () => {
    const props = {
      columnPickerType: ' ',
      setColumnFilters: jest.fn(),
      setTableLoading: jest.fn(),
      getQueryTypes: {
        data: {},
        loading: false
      },
      setDisplayTable: jest.fn(),
      parameters: [{ flight: ['arrival'] }, { flight: ['departure'] }],
      setParameters: jest.fn(),
      selected: [],
      setSelected: jest.fn(),
      data: [],
      getPicker: {
        data: {},
        loading: false
      },
      setError: jest.fn(),
      setDisplayEmptyState: jest.fn(),
      rememberedParams: [],
      enableCache: false,
      setEnableCache: jest.fn(),
      pageSize: 2,
      offsetVal: 10,
      setOffsetVal: jest.fn(),
      setPageSize: jest.fn(),
      setIsLoadingMore: jest.fn(),
      isLoadingMore: true
    };

    const wrapper = mount(<DomainExplorerColumnPicker {...props} />);
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
});

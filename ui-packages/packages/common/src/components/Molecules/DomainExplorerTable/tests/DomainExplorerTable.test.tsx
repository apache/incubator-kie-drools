import React from 'react';
import { shallow, mount } from 'enzyme';

import DomainExplorerTable from '../DomainExplorerTable';

global.Math.random = () => 0.7218415351930461;

describe('Domain Explorer Table Component', () => {
  let useEffect;
  const mockUseEffect = () => {
    useEffect.mockImplementationOnce(f => f());
  };
  beforeEach(() => {
    useEffect = jest.spyOn(React, 'useEffect');
    mockUseEffect();
    mockUseEffect();
  });
  it('Snapshot test', () => {
    const props = {
      columnFilters: [
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        }
      ],
      tableLoading: false,
      displayTable: true /*  */,
      displayEmptyState: false,
      parameters: [],
      selected: [],
      isLoadingMore: true,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: {}
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 10
    };
    const wrapper = shallow(<DomainExplorerTable {...props} />);
    wrapper.update();
    wrapper.setProps({});
    expect(wrapper).toMatchSnapshot();
  });
  it('Boolean assertions-false', () => {
    const props = {
      columnFilters: [
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'COMPLETED'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        }
      ],
      tableLoading: false,
      displayTable: true,
      displayEmptyState: false,
      parameters: [],
      selected: [],
      isLoadingMore: true,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: {}
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 10
    };

    const wrapper = shallow(<DomainExplorerTable {...props} />);

    wrapper.update();
    wrapper.setProps({});

    expect(wrapper).toMatchSnapshot();
  });
  it('Boolean assertions-true', () => {
    const props = {
      columnFilters: [
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'PENDING'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        }
      ],
      tableLoading: false,
      displayTable: true,
      displayEmptyState: false,
      parameters: [],
      selected: [],
      isLoadingMore: true,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: {}
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 10
    };
    const wrapper = shallow(<DomainExplorerTable {...props} />);
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('check zero offset', () => {
    const props = {
      columnFilters: [
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ACTIVE'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'COMPLETED'
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'PENDING'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'SUSPENDED'
              }
            ]
          }
        }
      ],
      tableLoading: false,
      displayTable: true,
      displayEmptyState: true,
      parameters: [],
      selected: [],
      isLoadingMore: true,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: {}
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 0
    };
    const wrapper = mount(<DomainExplorerTable {...props} />);
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('check false value of isLoadingMore', () => {
    const props = {
      columnFilters: [
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ABORTED'
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        }
      ],
      tableLoading: true,
      displayTable: true,
      displayEmptyState: true,
      parameters: [],
      selected: [],
      isLoadingMore: false,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: {}
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 0
    };
    const wrapper = mount(<DomainExplorerTable {...props} />);
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
  it('check null value for process instance attributes', () => {
    const props = {
      columnFilters: [
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
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ABORTED'
              }
            ]
          }
        },
        {
          flight: {
            arrival: null,
            __typename: 'Flight',
            departure: 'Hello World',
            flightNumber: 'Hello World',
            gate: 'Hello World',
            seat: 'Hello World'
          },
          metadata: {
            processInstances: [
              {
                businessKey: null,
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              },
              {
                businessKey: 'Hello World',
                id: 'Hello World',
                lastUpdate: 'Tue, 12 May 2020 12:33:58 GMT',
                processName: 'Hello World',
                start: 'Tue, 12 May 2020 12:33:58 GMT',
                state: 'ERROR'
              }
            ]
          }
        }
      ],
      tableLoading: true,
      displayTable: true,
      displayEmptyState: true,
      parameters: [],
      selected: [],
      isLoadingMore: false,
      rows: [
        {
          cells: [
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World',
            'Hello World'
          ],
          isOpen: false,
          rowKey: '0.008857835601127073'
        },
        {
          parent: 0,
          rowKey: '0.6632979792309541',
          cells: [
            {
              title: {}
            }
          ]
        }
      ],
      setRows: jest.fn(),
      offset: 0
    };
    const wrapper = mount(<DomainExplorerTable {...props} />);
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });
});

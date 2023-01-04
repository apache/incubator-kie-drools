import React from 'react';
import DomainExplorerFilterOptions from '../DomainExplorerFilterOptions';
import { GraphQL } from '../../../../graphql/types';
import useGetInputFieldsFromQueryQuery = GraphQL.useGetInputFieldsFromQueryQuery;
import useGetInputFieldsFromTypeQuery = GraphQL.useGetInputFieldsFromTypeQuery;
import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';

jest.mock('../../../../graphql/types');
// tslint:disable: no-string-literal
// tslint:disable: no-unexpected-multiline
describe('Domain explorer filter options component tests', () => {
  const defaultProps = {
    filterArgument: 'TravelsArgument',
    generateFilterQuery: jest.fn(),
    setOffset: jest.fn(),
    filterChips: ['metadata / processInstances / state: ACTIVE'],
    setFilterChips: jest.fn(),
    runQuery: true,
    setRunQuery: jest.fn(),
    finalFilters: {
      metadata: {
        processInstances: { state: { equal: 'ACTIVE' } }
      },
      trip: {
        country: {
          equal: 'Australia'
        }
      }
    },
    setFinalFilters: jest.fn(),
    getSchema: {
      data: {
        __type: {
          name: 'TravelsArgument',
          inputFields: [
            { name: 'and', type: { name: null, kind: 'LIST' } },
            { name: 'or', type: { name: null, kind: 'LIST' } },
            {
              name: 'flight',
              type: {
                name: 'FlightArgument',
                kind: 'INPUT_OBJECT',
                inputFields: [
                  {
                    name: 'arrival',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'departure',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'flightNumber',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'gate',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'seat',
                    type: {
                      name: 'StringArgument'
                    }
                  }
                ]
              }
            }
          ]
        }
      }
    },
    reset: false,
    setReset: jest.fn()
  };
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('Snapshot test with default props', async () => {
    const props = {
      filterArgument: 'TravelsArgument',
      generateFilterQuery: jest.fn(),
      reset: false,
      setReset: jest.fn(),
      setOffset: jest.fn(),
      getQueryTypes: {
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
      },
      filterChips: [],
      setFilterChips: jest.fn(),
      runQuery: true,
      setRunQuery: jest.fn(),
      finalFilters: {
        metadata: {
          processInstances: { state: { equal: 'ACTIVE' } }
        },
        trip: {
          country: {
            equal: 'Australia'
          }
        }
      },
      setFinalFilters: jest.fn(),
      getSchema: {
        data: {
          __type: {
            name: 'TravelsArgument',
            inputFields: [
              { name: 'and', type: { name: null, kind: 'LIST' } },
              { name: 'or', type: { name: null, kind: 'LIST' } },
              {
                name: 'id',
                type: {
                  inputFields: [
                    { name: 'in', type: { name: null, __typename: '__Type' } },
                    {
                      name: 'equal',
                      type: { name: 'String', __typename: '__Type' }
                    },
                    {
                      name: 'isNull',
                      type: { name: 'Boolean', __typename: '__Type' }
                    }
                  ],
                  kind: 'INPUT_OBJECT',
                  name: 'IdArgument'
                }
              },
              {
                name: 'flight',
                type: {
                  name: 'FlightArgument',
                  kind: 'INPUT_OBJECT',
                  inputFields: [
                    {
                      name: 'arrival',
                      type: {
                        name: 'StringArgument'
                      }
                    },
                    {
                      name: 'departure',
                      type: {
                        name: 'StringArgument'
                      }
                    },
                    {
                      name: 'flightNumber',
                      type: {
                        name: 'StringArgument'
                      }
                    },
                    {
                      name: 'gate',
                      type: {
                        name: 'StringArgument'
                      }
                    },
                    {
                      name: 'seat',
                      type: {
                        name: 'StringArgument'
                      }
                    }
                  ]
                }
              },
              {
                name: 'hotel',
                type: {
                  name: 'HotelArgument',
                  kind: 'INPUT_OBJECT',
                  inputFields: [
                    {
                      name: 'address',
                      type: { name: 'AddressArgument', __typename: '__Type' }
                    },
                    {
                      name: 'bookingNumber',
                      type: { name: 'StringArgument', __typename: '__Type' }
                    }
                  ]
                }
              }
            ]
          }
        }
      }
    };
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'IdArgument',
          inputFields: [
            { name: 'in', type: { name: null, kind: 'LIST' } },
            { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
            { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
          ],
          kind: 'INPUT_OBJECT'
        }
      }
    });
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'TravelsArgument',
          inputFields: [
            {
              name: 'and',
              type: {
                name: null,
                kind: 'LIST',
                inputFields: null
              }
            },
            {
              name: 'or',
              type: {
                name: null,
                kind: 'LIST',
                inputFields: null
              }
            },
            {
              name: 'flight',
              type: {
                name: 'FlightArgument',
                kind: 'INPUT_OBJECT',
                inputFields: [
                  {
                    name: 'arrival',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'departure',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'flightNumber',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'gate',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'seat',
                    type: {
                      name: 'StringArgument'
                    }
                  }
                ]
              }
            },
            {
              name: 'hotel',
              type: {
                name: 'HotelArgument',
                kind: 'INPUT_OBJECT',
                inputFields: [
                  {
                    name: 'address',
                    type: {
                      name: 'AddressArgument'
                    }
                  },
                  {
                    name: 'bookingNumber',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'name',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'phone',
                    type: {
                      name: 'StringArgument'
                    }
                  },
                  {
                    name: 'room',
                    type: {
                      name: 'StringArgument'
                    }
                  }
                ]
              }
            }
          ]
        }
      }
    });
    const wrapper = mount(<DomainExplorerFilterOptions {...props} />);
    wrapper.update();
    wrapper.setProps({});
    await Promise.resolve();
    expect(wrapper).toMatchSnapshot();
  });
  it('Trigger onselect function on field select', async () => {
    const getQueryTypes = {
      loading: false,
      data: {
        __schema: {
          queryType: [
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
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
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
    };
    const wrapper = mount(
      <DomainExplorerFilterOptions {...{ ...defaultProps, getQueryTypes }} />
    );
    wrapper.update();
    wrapper.setProps({});

    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'IdArgument',
          inputFields: [
            { name: 'in', type: { name: null, kind: 'LIST' } },
            { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
            { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
          ],
          kind: 'INPUT_OBJECT'
        }
      }
    });
    const obj = {
      nativeEvent: {
        target: {
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => ' ')
            }
          }
        }
      },
      target: { innerText: 'id' }
    } as any;
    const obj2 = {
      target: {
        innerText: 'equal'
      }
    } as any;
    // simulate on select prop on fields dropdown to make a selection
    act(() => {
      wrapper.find('#select-field').first().props()['onSelect'](obj);
    });
    // simulate dropdown to select an operator
    act(() => {
      wrapper.find('#select-operator').first().props()['onSelect'](obj2);
    });
    expect(wrapper.find('input')).toBeTruthy();
    // check input textbox when the operator is either "equal" or "like"
    wrapper.update().find('input').at(0).simulate('change', 'Hello');
    expect(wrapper.find('#button-with-string')).toBeTruthy();
    // trigger button click after setting isDisable false on button
    act(() => {
      wrapper.find('#button-with-string').at(0).props()['isDisabled'] = false;
      wrapper.find('#button-with-string').at(0).props()['disabled'] = false;
      const event = {} as React.MouseEvent<HTMLButtonElement, MouseEvent>;
      wrapper.find('#button-with-string').at(0).props()['onClick'](event);
    });
  });
  it('check "in" operator', async () => {
    const getQueryTypes = {
      loading: false,
      data: {
        __schema: {
          queryType: [
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
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
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
    };
    const wrapper = mount(
      <DomainExplorerFilterOptions {...{ ...defaultProps, getQueryTypes }} />
    );
    wrapper.update();
    wrapper.setProps({});
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'IdArgument',
          inputFields: [
            { name: 'in', type: { name: null, kind: 'LIST' } },
            { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
            { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
          ],
          kind: 'INPUT_OBJECT'
        }
      }
    });
    const obj = {
      nativeEvent: {
        target: {
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => 'id')
            }
          }
        }
      },
      target: {
        innerText: 'id'
      }
    } as any;
    const obj2 = {
      target: {
        innerText: 'in'
      }
    } as any;
    // simulate on select prop on fields dropdown to make a selection
    act(() => {
      wrapper.find('#select-field').first().props()['onSelect'](obj);
    });
    // trigger on select prop to make a selection on operator dropdown
    act(() => {
      wrapper.find('#select-operator').first().props()['onSelect'](obj2);
    });
    expect(wrapper.find('input')).toBeTruthy();
    // check input text box group when selected operator is "in"
    wrapper
      .update()
      .find('#filterArrayOfInputs')
      .at(0)
      .simulate('change', 'test1,test2');

    expect(wrapper.find('#button-with-arrayInput')).toBeTruthy();
    // trigger button click after setting isDisable false on button
    wrapper.find('#button-with-arrayInput').at(0).props()['isDisabled'] = false;
    wrapper.find('#button-with-arrayInput').at(0).props()['disabled'] = false;
    const event = {} as React.MouseEvent<HTMLButtonElement, MouseEvent>;
    act(() => {
      wrapper.find('#button-with-arrayInput').at(0).props()['onClick'](event);
    });
  });
  it('check isNull operator', async () => {
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'IdArgument',
          inputFields: [
            { name: 'in', type: { name: null, kind: 'LIST' } },
            { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
            { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
          ],
          kind: 'INPUT_OBJECT'
        }
      }
    });
    const getQueryTypes = {
      loading: false,
      data: {
        __schema: {
          queryType: [
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
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
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
    };
    const wrapper = mount(
      <DomainExplorerFilterOptions {...{ ...defaultProps, getQueryTypes }} />
    );
    wrapper.update();
    wrapper.setProps({});
    const obj = {
      nativeEvent: {
        target: {
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => 'id')
            }
          }
        }
      },
      target: {
        innerText: 'id'
      }
    } as any;
    const obj2 = {
      target: {
        innerText: 'isNull'
      }
    } as any;
    // simulate on select prop on fields dropdown to make a selection
    act(() => {
      wrapper.find('#select-field').first().props()['onSelect'](obj);
    });
    // simulate on toggle prop on fields dropdown
    act(() => {
      wrapper.find('#select-field').first().props()['onToggle']();
    });
    // simulate on select on operatore dropdown
    act(() => {
      wrapper.find('#select-operator').first().props()['onSelect'](obj2);
    });
    act(() => {
      wrapper.find('#select-operator').first().props()['onToggle']();
    });
    const obj3 = {
      target: {
        innerText: ''
      }
    } as any;
    // check if third value input is a dropdown when selected operator is "isNull"
    act(() => {
      wrapper.update().find('Dropdown').props()['onSelect'](obj3);
    });
    expect(wrapper.find('dropdown')).toBeTruthy();
    // stimulate on toggle props on boolean value dropdown
    act(() => {
      wrapper
        .update()
        .find('Dropdown')
        .props()
        ['toggle']['props']['onToggle']();
    });
    expect(wrapper.find('#button-with-boolean')).toBeTruthy();
    wrapper.update().find('#button-with-boolean').first().simulate('click');
  });
  it('check equal operator on enumSingleSelection', async () => {
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'ProcessInstanceStateArgument',
          inputFields: [
            {
              name: 'equal',
              type: {
                name: 'ProcessInstanceState',
                kind: 'ENUM',
                enumValues: [
                  {
                    name: 'PENDING'
                  },
                  {
                    name: 'ACTIVE'
                  },
                  {
                    name: 'COMPLETED'
                  },
                  {
                    name: 'ABORTED'
                  },
                  {
                    name: 'SUSPENDED'
                  },
                  {
                    name: 'ERROR'
                  }
                ],
                __typename: '__Type'
              }
            },
            {
              name: 'in',
              type: { name: null, kind: 'LIST', __typename: '__Type' }
            }
          ],
          kind: 'INPUT_OBJECT'
        }
      }
    });
    const getQueryTypes = {
      loading: false,
      data: {
        __schema: {
          queryType: [
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
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
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
            },
            {
              name: 'ProcessInstanceMetaArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                {
                  name: 'state',
                  type: {
                    name: 'ProcessInstanceStateArgument',
                    kind: 'INPUT_OBJECT'
                  }
                }
              ]
            }
          ]
        }
      }
    };
    const wrapper = mount(
      <DomainExplorerFilterOptions {...{ ...defaultProps, getQueryTypes }} />
    );
    wrapper.update();
    wrapper.setProps({});
    const obj = {
      nativeEvent: {
        target: {
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => 'metadata / processInstances')
            }
          }
        }
      },
      target: { innerText: 'state' }
    } as any;
    const obj2 = {
      target: {
        innerText: 'equal'
      }
    } as any;
    // simulate field dropdown to select "state" field
    act(() => {
      wrapper.update().find('#select-field').first().props()['onSelect'](obj);
    });
    // simulate field dropdown to select "equal" operator
    act(() => {
      wrapper
        .update()
        .find('#select-operator')
        .first()
        .props()
        ['onSelect'](obj2);
    });
    const obj3 = {
      target: {
        innerText: 'ACTIVE'
      }
    } as any;
    // simulate value dropdown to select "ACTIVE" state
    act(() => {
      wrapper
        .update()
        .find('#enumSingleSelection')
        .at(0)
        .props()
        ['onSelect'](obj3);
    });
    act(() => {
      wrapper.update().find('#enumSingleSelection').at(0).props()['onToggle']();
    });
    expect(wrapper.find('#enumSingleSelection')).toBeTruthy();
    wrapper.update().find('#enumSingleSelection').at(0).props()['selections'] =
      'ACTIVE';
    expect(wrapper.find('#button-with-enumSingleSelection')).toBeTruthy();
    wrapper
      .update()
      .find('#button-with-enumSingleSelection')
      .first()
      .simulate('click');
  });
  it('check in operator on enumSingleSelection', async () => {
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'ProcessInstanceStateArgument',
          inputFields: [
            {
              name: 'equal',
              type: {
                name: 'ProcessInstanceState',
                kind: 'ENUM',
                enumValues: null,
                __typename: '__Type'
              }
            },
            {
              name: 'in',
              type: {
                name: null,
                kind: 'LIST',
                ofType: {
                  kind: 'ENUM',
                  name: 'ProcessInstanceState',
                  enumValues: [
                    {
                      name: 'PENDING'
                    },
                    {
                      name: 'ACTIVE'
                    },
                    {
                      name: 'COMPLETED'
                    },
                    {
                      name: 'ABORTED'
                    },
                    {
                      name: 'SUSPENDED'
                    },
                    {
                      name: 'ERROR'
                    }
                  ]
                },
                __typename: '__Type'
              }
            }
          ],
          kind: 'INPUT_OBJECT'
        }
      }
    });
    const obj = {
      nativeEvent: {
        target: {
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => 'metadata / processInstances')
            }
          }
        }
      },
      target: { innerText: 'state' }
    } as any;
    const obj2 = {
      target: {
        innerText: 'in'
      }
    } as any;
    const getQueryTypes = {
      loading: false,
      data: {
        __schema: {
          queryType: [
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
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
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
            },
            {
              name: 'ProcessInstanceMetaArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                {
                  name: 'state',
                  type: {
                    name: 'ProcessInstanceStateArgument',
                    kind: 'INPUT_OBJECT'
                  }
                }
              ]
            }
          ]
        }
      }
    };
    const wrapper = mount(
      <DomainExplorerFilterOptions {...{ ...defaultProps, getQueryTypes }} />
    );
    wrapper.update();
    wrapper.setProps({});
    // simulate field dropdown to select "state" field
    act(() => {
      wrapper.update().find('#select-field').first().props()['onSelect'](obj);
    });
    // simulate operator dropdown to select "in" operator
    act(() => {
      wrapper
        .update()
        .find('#select-operator')
        .first()
        .props()
        ['onSelect'](obj2);
    });
    const obj3 = {
      target: {
        innerText: ''
      }
    } as any;
    expect(wrapper.find('#enumMultiSelection')).toBeTruthy();
    // simulate value dropdown to make multiple state values
    act(() => {
      wrapper
        .update()
        .find('#enumMultiSelection')
        .at(0)
        .props()
        ['onSelect'](obj3);
    });
    act(() => {
      wrapper.update().find('#enumMultiSelection').at(0).props()['onToggle']();
    });
    expect(wrapper.find('#button-with-enumMultiSelection')).toBeTruthy();
    wrapper
      .update()
      .find('#button-with-enumMultiSelection')
      .first()
      .simulate('click');
  });
  it('check equal operator on user task enumSingleSelection', async () => {
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'ProcessInstanceStateArgument',
          inputFields: [
            {
              name: 'equal',
              type: {
                name: 'ProcessInstanceState',
                kind: 'ENUM',
                enumValues: [
                  {
                    name: 'PENDING'
                  },
                  {
                    name: 'ACTIVE'
                  },
                  {
                    name: 'COMPLETED'
                  },
                  {
                    name: 'ABORTED'
                  },
                  {
                    name: 'SUSPENDED'
                  },
                  {
                    name: 'ERROR'
                  }
                ],
                __typename: '__Type'
              }
            },
            {
              name: 'in',
              type: { name: null, kind: 'LIST', __typename: '__Type' }
            }
          ],
          kind: 'INPUT_OBJECT'
        }
      }
    });
    const obj = {
      nativeEvent: {
        target: {
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => 'metadata / userTasks')
            }
          }
        }
      },
      target: { innerText: 'state' }
    } as any;
    const obj2 = {
      target: {
        innerText: 'in'
      }
    } as any;
    const getQueryTypes = {
      loading: false,
      data: {
        __schema: {
          queryType: [
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
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
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
            },
            {
              name: 'ProcessInstanceMetaArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                {
                  name: 'state',
                  type: {
                    name: 'ProcessInstanceStateArgument',
                    kind: 'INPUT_OBJECT'
                  }
                }
              ]
            },
            {
              name: 'UserTaskInstanceMetaArgument',
              kind: 'INPUT_OBJECT',
              inputFields: [
                {
                  name: 'state',
                  type: {
                    name: 'StringArgument',
                    kind: 'INPUT_OBJECT',
                    __typename: '__Type'
                  }
                }
              ]
            }
          ]
        }
      }
    };
    const wrapper = mount(
      <DomainExplorerFilterOptions {...{ ...defaultProps, getQueryTypes }} />
    );
    wrapper.update();
    wrapper.setProps({});
    // simulate fields dropdown to select "state" from userTasks
    act(() => {
      wrapper.update().find('#select-field').first().props()['onSelect'](obj);
    });
    // simulate operator dropdown to select "in" operator
    act(() => {
      wrapper
        .update()
        .find('#select-operator')
        .first()
        .props()
        ['onSelect'](obj2);
    });
    expect(wrapper.find('#enumMultiSelection')).toBeTruthy();
    expect(wrapper.find('#button-with-enumMultiSelection')).toBeTruthy();
  });
  it('test empty parent string', () => {
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'IdArgument',
          inputFields: [
            { name: 'in', type: { name: null, kind: 'LIST' } },
            { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
            { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
          ],
          kind: 'INPUT_OBJECT'
        }
      }
    });
    const getQueryTypes = {
      loading: false,
      data: {
        __schema: {
          queryType: [
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
                  type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
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
    };
    const wrapper = mount(
      <DomainExplorerFilterOptions {...{ ...defaultProps, getQueryTypes }} />
    );
    wrapper.update();
    wrapper.setProps({});
    const obj = {
      nativeEvent: {
        target: {
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => ' ')
            }
          }
        }
      },
      target: { innerText: 'id' }
    } as any;
    // simulate field operator to test root options which has no parent
    act(() => {
      wrapper.find('#select-field').first().props()['onSelect'](obj);
    });
    const obj2 = {
      target: {
        innerText: 'equal'
      }
    } as any;
    act(() => {
      wrapper.find('#select-operator').first().props()['onSelect'](obj2);
    });
    wrapper.update().find('input').at(0).simulate('change', 'Hello');
    act(() => {
      wrapper.find('#button-with-string').at(0).props()['isDisabled'] = false;
      wrapper.find('#button-with-string').at(0).props()['disabled'] = false;
      const event = {} as React.MouseEvent<HTMLButtonElement, MouseEvent>;
      wrapper.find('#button-with-string').at(0).props()['onClick'](event);
    });
    expect(wrapper.find('input')).toBeTruthy();
    expect(wrapper.find('#button-with-string')).toBeTruthy();
  });
  it('test reset to default', () => {
    const props = {
      filterArgument: 'TravelsArgument',
      generateFilterQuery: jest.fn(),
      setReset: jest.fn(),
      setOffset: jest.fn(),
      setFilterError: jest.fn(),
      getQueryTypes: {
        loading: false,
        data: {
          __schema: {
            queryType: [
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
                    type: { name: 'StringArgument', kind: 'INPUT_OBJECT' }
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
      },
      filterChips: ['metadata / processInstances / state: ACTIVE'],
      setFilterChips: jest.fn(),
      runQuery: true,
      setRunQuery: jest.fn(),
      finalFilters: {
        metadata: {
          processInstances: { state: { equal: 'ACTIVE' } }
        },
        trip: {
          country: {
            equal: 'Australia'
          }
        }
      },
      setFinalFilters: jest.fn(),
      getSchema: {
        data: {
          __type: {
            name: 'TravelsArgument',
            inputFields: [
              { name: 'and', type: { name: null, kind: 'LIST' } },
              { name: 'or', type: { name: null, kind: 'LIST' } },
              {
                name: 'flight',
                type: {
                  name: 'FlightArgument',
                  kind: 'INPUT_OBJECT',
                  inputFields: [
                    {
                      name: 'arrival',
                      type: {
                        name: 'StringArgument'
                      }
                    },
                    {
                      name: 'departure',
                      type: {
                        name: 'StringArgument'
                      }
                    },
                    {
                      name: 'flightNumber',
                      type: {
                        name: 'StringArgument'
                      }
                    },
                    {
                      name: 'gate',
                      type: {
                        name: 'StringArgument'
                      }
                    },
                    {
                      name: 'seat',
                      type: {
                        name: 'StringArgument'
                      }
                    }
                  ]
                }
              }
            ]
          }
        }
      },
      reset: true
    };
    (useGetInputFieldsFromQueryQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {}
    });
    (useGetInputFieldsFromTypeQuery as jest.Mock).mockReturnValue({
      loading: false,
      data: {
        __type: {
          name: 'IdArgument',
          inputFields: [
            { name: 'in', type: { name: null, kind: 'LIST' } },
            { name: 'equal', type: { name: 'String', kind: 'SCALAR' } },
            { name: 'isNull', type: { name: 'Boolean', kind: 'SCALAR' } }
          ],
          kind: 'INPUT_OBJECT'
        }
      }
    });
    const wrapper = mount(<DomainExplorerFilterOptions {...props} />);
    wrapper.update();
    wrapper.setProps({});
    const obj = {
      nativeEvent: {
        target: {
          parentElement: {
            parentElement: {
              getAttribute: jest.fn(() => ' ')
            }
          }
        }
      },
      target: { innerText: 'id' }
    } as any;
    // check reset to default sets "id" field on dropdown
    act(() => {
      wrapper.find('#select-field').first().props()['onSelect'](obj);
    });
    const obj2 = {
      target: {
        innerText: 'equal'
      }
    } as any;
    // check reset to default sets "equal" operator on dropdown
    act(() => {
      wrapper.find('#select-operator').first().props()['onSelect'](obj2);
    });
    wrapper.update().find('input').at(0).simulate('change', 'Hello');
    act(() => {
      wrapper.find('#button-with-string').at(0).props()['isDisabled'] = false;
      wrapper.find('#button-with-string').at(0).props()['disabled'] = false;
      const event = {} as React.MouseEvent<HTMLButtonElement, MouseEvent>;
      wrapper.find('#button-with-string').at(0).props()['onClick'](event);
    });
    expect(wrapper.find('input')).toBeTruthy();
  });
});

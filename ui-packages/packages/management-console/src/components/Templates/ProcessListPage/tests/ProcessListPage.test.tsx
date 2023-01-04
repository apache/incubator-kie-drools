import React from 'react';
import ProcessListPage from '../ProcessListPage';
import { GraphQL, LoadMore } from '@kogito-apps/common';
import { mount } from 'enzyme';
import { MockedProvider } from '@apollo/react-testing';
import { BrowserRouter } from 'react-router-dom';
import { Button, EmptyState, EmptyStateBody } from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';
import * as H from 'history';
import { match } from 'react-router';
jest.mock('../../../Organisms/ProcessListTable/ProcessListTable');
jest.mock('../../../Atoms/ProcessListModal/ProcessListModal');
import { mockProcessData } from './mocks/LoadMoreMockData';
import wait from 'waait';
const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('@kogito-apps/common', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/common'), {
    LoadMore: () => {
      return <MockedComponent />;
    }
  })
);
jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    ExclamationTriangleIcon: () => {
      return <MockedComponent />;
    },
    ExclamationCircleIcon: () => {
      return <MockedComponent />;
    }
  })
);

const match: match<{ domainName: string }> = {
  isExact: false,
  path: '/ProcessInstances',
  url: '/ProcessInstances',
  params: { domainName: 'domain-name' }
};

const routeComponentPropsMock1 = {
  history: H.createMemoryHistory(),
  location: {
    pathname: '/ProcessInstances',
    state: {
      filters: {
        status: [GraphQL.ProcessInstanceState.Active],
        businessKey: []
      }
    },
    hash: '',
    search: ''
  },
  match
};

const routeComponentPropsMock2 = {
  history: H.createMemoryHistory(),
  location: {
    pathname: '/ProcessInstances',
    state: {
      filters: {
        status: [GraphQL.ProcessInstanceState.Active],
        businessKey: ['TRAVELS']
      }
    },
    hash: '',
    search: ''
  },
  match
};

const routeComponentPropsMock3 = {
  history: H.createMemoryHistory(),
  location: {
    pathname: '/ProcessInstances',
    state: {
      filters: {
        status: [],
        businessKey: []
      }
    },
    hash: '',
    search: ''
  },
  match
};

const mocks1 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc },
        offset: 0,
        limit: 10
      }
    },
    result: {
      data: {
        ProcessInstances: [mockProcessData[0]]
      }
    }
  },
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10,
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc }
      }
    },
    result: {
      data: {
        ProcessInstances: [mockProcessData[0]]
      }
    }
  }
];

const mocks2 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true },
          or: [{ businessKey: { like: 'TRAVELS' } }]
        },
        offset: 0,
        limit: 10,
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc }
      }
    },
    result: {
      data: {
        ProcessInstances: [mockProcessData[1]]
      }
    }
  },
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true },
          or: [{ businessKey: { like: 'TRAVELS' } }]
        },
        offset: 0,
        limit: 10,
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc }
      }
    },
    result: {
      data: {
        ProcessInstances: [mockProcessData[1]]
      }
    }
  }
];

const mocks3 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10,
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc }
      }
    },
    error: new Error('something went wrong')
  }
];

const mocks5 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10,
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc }
      }
    },
    result: {
      data: {
        ProcessInstances: []
      }
    }
  },
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10,
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc }
      }
    },
    result: {
      data: {
        ProcessInstances: [mockProcessData[0]]
      }
    }
  }
];

const mocks6 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10,
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc }
      }
    },
    result: {
      data: {
        ProcessInstances: mockProcessData.slice(0, 10)
      }
    }
  },
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 20,
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc }
      }
    },
    result: {
      data: {
        ProcessInstances: mockProcessData.slice(0, 20)
      }
    }
  }
];

const mocks7 = [
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10,
        orderBy: { lastUpdate: GraphQL.OrderBy.Asc }
      }
    },
    result: {
      data: {
        ProcessInstances: mockProcessData.slice(0, 10)
      }
    }
  },
  {
    request: {
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: {
          state: { in: [GraphQL.ProcessInstanceState.Active] },
          parentProcessInstanceId: { isNull: true }
        },
        offset: 0,
        limit: 10,
        orderBy: { processName: GraphQL.OrderBy.Asc }
      }
    },
    result: {
      data: {
        ProcessInstances: mockProcessData.slice(0, 10)
      }
    }
  }
];

describe('ProcessListPage component tests', () => {
  it('initial render snapshot', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={mocks1} addTypename={false}>
            <ProcessListPage {...routeComponentPropsMock1} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessListPage');
    });
    expect(wrapper.find('MockedProcessListTable').exists()).toBeTruthy();
  });
  it('on FilterClick tests- without businesskey', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={mocks1} addTypename={false}>
            <ProcessListPage {...routeComponentPropsMock1} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessListPage');
    });
    await act(async () => {
      wrapper
        .find('#apply-filter-button')
        .find(Button)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('MockedProcessListTable').props()['initData'][
        'ProcessInstances'
      ][0]['id']
    ).toEqual(mocks1[0].result.data.ProcessInstances[0].id);
  });

  it('on FilterClick tests- with businesskey', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={mocks2} addTypename={false}>
            <ProcessListPage {...routeComponentPropsMock2} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessListPage');
    });
    await act(async () => {
      wrapper
        .find('#apply-filter-button')
        .find(Button)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('MockedProcessListTable').props()['initData'][
        'ProcessInstances'
      ][0]['businessKey']
    ).toEqual(mocks2[0].result.data.ProcessInstances[0].businessKey);
  });

  it('error in query - without businesskey', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={mocks3} addTypename={false}>
            <ProcessListPage {...routeComponentPropsMock1} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessListPage');
    });
    wrapper = wrapper.update();
    wrapper = wrapper.find(EmptyState);
    expect(
      wrapper
        .find(EmptyStateBody)
        .children()
        .html()
        .includes('An error occurred while accessing data.')
    ).toBeTruthy();
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot tests for no status selected', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={mocks5} addTypename={false}>
            <ProcessListPage {...routeComponentPropsMock3} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessListPage');
    });
    wrapper = wrapper.find(EmptyState);
    expect(wrapper).toMatchSnapshot();
  });

  it('reset click in no status found test', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={mocks5} addTypename={false}>
            <ProcessListPage {...routeComponentPropsMock3} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessListPage');
    });
    await act(async () => {
      wrapper.find(EmptyState).find(Button).find('button').simulate('click');
    });
    expect(
      wrapper
        .find(EmptyStateBody)
        .children()
        .html()
        .includes('Try applying at least one filter to see results')
    ).toBeTruthy();
    const emptyState = wrapper.find(EmptyState);
    expect(emptyState).toMatchSnapshot();
    wrapper = wrapper.update();
    expect(wrapper.find('MockedProcessListTable').exists()).toBeTruthy();
  });

  it('Loadmore tests', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={mocks6} addTypename={false}>
            <ProcessListPage {...routeComponentPropsMock1} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
    });
    wrapper = wrapper.update().find('ProcessListPage');
    expect(
      wrapper.find('MockedProcessListTable').props()['initData'][
        'ProcessInstances'
      ].length
    ).toEqual(10);
    const loadmore = wrapper.find(LoadMore);
    expect(loadmore.exists()).toBeTruthy();
    expect(loadmore).toMatchSnapshot();

    await act(async () => {
      wrapper.find(LoadMore).props()['getMoreItems'](0, 20);
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('MockedProcessListTable').props()['initData'][
        'ProcessInstances'
      ].length
    ).toEqual(16);
  });

  it('sorting tests', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <MockedProvider mocks={mocks7} addTypename={false}>
            <ProcessListPage {...routeComponentPropsMock1} />
          </MockedProvider>
        </BrowserRouter>
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessListPage');
    });
    await act(async () => {
      wrapper
        .find('MockedProcessListTable')
        .props()
        ['onSort']({ target: { innerText: 'id' } }, 0, GraphQL.OrderBy.Asc);
    });
    wrapper = wrapper.update();
    expect(wrapper.find('MockedProcessListTable').props()['sortBy']).toEqual({
      index: 0,
      direction: 'ASC'
    });
  });
});

import React from 'react';
import JobsManagementPage from '../JobsManagementPage';
import { GraphQL } from '@kogito-apps/common';
import { mount } from 'enzyme';
import { MockedProvider, wait } from '@apollo/react-testing';
import { BrowserRouter } from 'react-router-dom';
import { act } from 'react-dom/test-utils';
import { JobsData } from '../mockData/JobsMockData';
import { Button } from '@patternfly/react-core';

jest.mock('../../../Organisms/JobsManagementTable/JobsManagementTable');
jest.mock('../../../Organisms/JobsManagementFilters/JobsManagementFilters');
jest.mock('../../../Atoms/JobsRescheduleModal/JobsRescheduleModal');
jest.mock('../../../Atoms/JobsPanelDetailsModal/JobsPanelDetailsModal');
jest.mock('../../../Atoms/JobsCancelModal/JobsCancelModal');
const MockedServerErrors = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/common', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/common'), {
    ServerErrors: () => {
      return <MockedServerErrors />;
    }
  })
);

const MockedBreadcrumb = (): React.ReactElement => {
  return <></>;
};
const MockedIcon = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Breadcrumb: () => <MockedBreadcrumb />
  })
);

jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    SyncIcon: () => {
      return <MockedIcon />;
    }
  })
);
describe('Jobs management page tests', () => {
  const mockOffset1: number = 0;
  const mockLimit1: number = 10;
  const mockOffset2: number = 10;
  const mockLimit2: number = 10;

  const props = {
    ouiaId: null,
    ouiaSafe: true
  };

  const mocks = [
    {
      request: {
        query: GraphQL.GetJobsWithFiltersDocument,
        variables: {
          values: ['SCHEDULED'],
          orderBy: {
            lastUpdate: GraphQL.OrderBy.Asc
          },
          offset: 0,
          limit: 10
        }
      },
      result: {
        data: {
          Jobs: JobsData
        }
      }
    },
    {
      request: {
        query: GraphQL.GetJobsWithFiltersDocument,
        variables: {
          values: ['SCHEDULED'],
          orderBy: {
            lastUpdate: GraphQL.OrderBy.Asc
          },
          offset: 0,
          limit: 10
        }
      },
      result: {
        data: {
          Jobs: JobsData
        }
      }
    }
  ];

  const mocks3 = [
    {
      request: {
        query: GraphQL.GetJobsWithFiltersDocument,
        variables: {
          values: ['SCHEDULED'],
          orderBy: {
            lastUpdate: GraphQL.OrderBy.Asc
          },
          offset: 0,
          limit: 10
        }
      },
      result: {
        data: null,
        error: {
          message: 'Expected a value of type JobStatus but received: CANCELLED'
        }
      }
    }
  ];
  const mockData = [...JobsData];
  const mocks4 = [
    {
      request: {
        query: GraphQL.GetJobsWithFiltersDocument,
        variables: {
          offset: 0,
          limit: 10,
          values: ['SCHEDULED'],
          orderBy: {
            lastUpdate: GraphQL.OrderBy.Asc
          }
        }
      },
      result: {
        data: {
          Jobs: mockData.splice(mockOffset1 - mockLimit1, mockLimit1)
        }
      }
    },
    {
      request: {
        query: GraphQL.GetJobsWithFiltersDocument,
        variables: {
          offset: 0,
          limit: 10,
          values: ['SCHEDULED'],
          orderBy: {
            lastUpdate: GraphQL.OrderBy.Asc
          }
        }
      },
      result: {
        data: {
          Jobs: mockData.slice(0, 20)
        }
      }
    }
  ];
  const mockData2 = [...JobsData];
  const mocks5: any = [
    {
      request: {
        query: GraphQL.GetJobsWithFiltersDocument,
        variables: {
          values: ['SCHEDULED'],
          limit: 10,
          offset: 0,
          orderBy: {
            lastUpdate: GraphQL.OrderBy.Asc
          }
        }
      },
      result: {
        data: {
          Jobs: mockData2.splice(mockOffset2 - mockLimit2, mockLimit2)
        }
      }
    },
    {
      request: {
        query: GraphQL.GetJobsWithFiltersDocument,
        variables: {
          values: ['SCHEDULED'],
          limit: 10,
          offset: 0,
          orderBy: {
            lastUpdate: GraphQL.OrderBy.Asc
          }
        }
      },
      result: {
        data: {
          Jobs: mockData2.splice(mockOffset2 - mockLimit2, mockLimit2)
        }
      }
    }
  ];
  const { location } = window;
  beforeEach(() => {
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: { reload: jest.fn() }
    });
  });

  afterAll(() => {
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: location
    });
  });
  it('snapshot test with mock data', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks} addTypename={false}>
          <BrowserRouter>
            <JobsManagementPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('JobsManagementPage');
    });
    expect(wrapper).toMatchSnapshot();
    wrapper.update();
    await act(async () => {
      wrapper.find('#refresh-button').first().simulate('click');
    });
  });

  it('mock data with error response', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks3} addTypename={false}>
          <BrowserRouter>
            <JobsManagementPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('JobsManagementPage');
    });
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('ServerErrors')).toBeTruthy();
  });
  it('test modal handlers', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks} addTypename={false}>
          <BrowserRouter>
            <JobsManagementPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('JobsManagementPage');
    });
    await act(async () => {
      wrapper
        .find('MockedJobsPanelDetailsModal')
        .props()
        ['handleModalToggle']();
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('MockedJobsPanelDetailsModal').props()['isModalOpen']
    ).toEqual(true);
    await act(async () => {
      wrapper.find('JobsRescheduleModal').props()['handleModalToggle']();
    });
    wrapper = wrapper.update();
    expect(wrapper.find('JobsRescheduleModal').props()['isModalOpen']).toEqual(
      true
    );
    await act(async () => {
      wrapper.find('JobsCancelModal').props()['handleModalToggle']();
    });
    wrapper = wrapper.update();
    expect(wrapper.find('JobsCancelModal').props()['isModalOpen']).toEqual(
      true
    );
  });
  it('toggle kebab', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks} addTypename={false}>
          <BrowserRouter>
            <JobsManagementPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('JobsManagementPage');
    });
    await act(async () => {
      wrapper
        .find('#jobs-management-buttons')
        .at(0)
        .find('Dropdown')
        .find('KebabToggle')
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(
      wrapper
        .find('Dropdown')
        .find('DropdownItem')
        .find('a')
        .children()
        .contains('Cancel selected')
    ).toBeTruthy();
  });
  it('test click handler on empty state & empty state snapshot', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks5} addTypename={false}>
          <BrowserRouter>
            <JobsManagementPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('JobsManagementPage');
    });
    await act(async () => {
      wrapper.find('JobsManagementFilters').props()['setChips']([]);
    });
    wrapper = wrapper.update();
    const emptyState = wrapper.find('EmptyState');
    expect(emptyState.exists()).toBeTruthy();
    expect(emptyState).toMatchSnapshot();
    await act(async () => {
      emptyState.find(Button).simulate('click');
    });
    wrapper = wrapper.update();
    const defaultChip: string[] = ['SCHEDULED'];
    expect(wrapper.find('JobsManagementFilters').props()['chips']).toEqual(
      defaultChip
    );
    expect(
      wrapper.find('JobsManagementFilters').props()['selectedStatus']
    ).toEqual(defaultChip);
  });
  it('Test pagination with mock responses with offset 0', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks4} addTypename={false}>
          <BrowserRouter>
            <JobsManagementPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('JobsManagementPage');
    });
    const tempJobs = wrapper.find('JobsManagementTable').props()['data'];
    expect(tempJobs[Object.keys(tempJobs)[0]].length).toEqual(10);
    await act(async () => {
      wrapper.find('LoadMore').props()['getMoreItems'](0, 10);
    });
    wrapper = wrapper.update();
    expect(wrapper.find('LoadMore').props()['isLoadingMore']).toEqual(true);
  });
  it('Test pagination with mock responses with offset 10', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks5} addTypename={false}>
          <BrowserRouter>
            <JobsManagementPage />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('JobsManagementPage');
    });
    const tempJobs = wrapper.find('JobsManagementTable').props()['data'];
    expect(tempJobs[Object.keys(tempJobs)[0]].length).toEqual(10);
    await act(async () => {
      wrapper.find('LoadMore').props()['getMoreItems'](0, 10);
    });
    wrapper = wrapper.update();
    expect(wrapper.find('LoadMore').props()['isLoadingMore']).toEqual(true);
    await act(async () => {
      wrapper.find('LoadMore').find('button').first().simulate('click');
    });
    wrapper = wrapper.update();
    expect(mocks5[0].request.variables.offset).toEqual(0);
  });
  it('test clearAllFilters on toolbar', async () => {
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <MockedProvider mocks={mocks} addTypename={false}>
          <BrowserRouter>
            <JobsManagementPage {...props} />
          </BrowserRouter>
        </MockedProvider>
      );
      await wait(0);
      wrapper = wrapper.update().find('JobsManagementPage');
    });
    await act(async () => {
      wrapper.find('Toolbar').props()['clearAllFilters']();
    });
    wrapper = wrapper.update();
    const defaultChip: string[] = ['SCHEDULED'];
    expect(wrapper.find('JobsManagementFilters').props()['chips']).toEqual(
      defaultChip
    );
    expect(
      wrapper.find('JobsManagementFilters').props()['selectedStatus']
    ).toEqual(defaultChip);
  });
});

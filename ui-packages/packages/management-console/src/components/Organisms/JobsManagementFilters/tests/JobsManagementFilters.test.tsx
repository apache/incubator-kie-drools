import { GraphQL } from '@kogito-apps/common';
import { Toolbar, ToolbarContent } from '@patternfly/react-core';
import { mount } from 'enzyme';
import React from 'react';
import { act } from 'react-dom/test-utils';
import JobsManagementFilters from '../JobsManagementFilters';

const TestWrapper = () => {
  const [mockState, setMockState] = React.useState([
    GraphQL.JobStatus.Scheduled
  ]);
  const [mockChips, setMockChips] = React.useState([
    GraphQL.JobStatus.Scheduled
  ]);
  const [, setMockValues] = React.useState([GraphQL.JobStatus.Scheduled]);
  const [, setMockSelectedJobInstances] = React.useState([]);

  return (
    <Toolbar
      id="data-toolbar-with-chip-groups"
      className="pf-m-toggle-group-container"
      collapseListedFiltersBreakpoint="md"
      clearAllFilters={jest.fn()}
      clearFiltersButtonText="Reset to default"
    >
      <ToolbarContent>
        <JobsManagementFilters
          selectedStatus={mockState}
          setSelectedStatus={setMockState}
          chips={mockChips}
          setChips={setMockChips}
          setDisplayTable={jest.fn()}
          setValues={setMockValues}
          setOffset={jest.fn()}
          setSelectedJobInstances={setMockSelectedJobInstances}
        />
      </ToolbarContent>
    </Toolbar>
  );
};
describe('Jobs management filters component tests', () => {
  const props = {
    selectedStatus: [GraphQL.JobStatus.Scheduled],
    setSelectedStatus: jest.fn(),
    chips: [GraphQL.JobStatus.Scheduled],
    setChips: jest.fn(),
    setDisplayTable: jest.fn(),
    setValues: jest.fn(),
    setOffset: jest.fn(),
    setSelectedJobInstances: jest.fn()
  };
  it('Snapshot with default props', () => {
    const wrapper = mount(
      <Toolbar
        id="data-toolbar-with-chip-groups"
        className="pf-m-toggle-group-container"
        collapseListedFiltersBreakpoint="md"
        clearAllFilters={jest.fn()}
        clearFiltersButtonText="Reset to default"
      >
        <ToolbarContent>
          <JobsManagementFilters {...props} />
        </ToolbarContent>
      </Toolbar>
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('test toolbarFilter props', async () => {
    let wrapper = mount(<TestWrapper />);
    const type = 'Status';
    const id = GraphQL.JobStatus.Scheduled;
    await act(async () => {
      wrapper.find('ToolbarFilter').props()['deleteChip'](type, id);
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('JobsManagementFilters').props()['selectedStatus'].length
    ).toEqual(0);
  });
  it('test apply filter button', () => {
    let wrapper = mount(
      <Toolbar
        id="data-toolbar-with-chip-groups"
        className="pf-m-toggle-group-container"
        collapseListedFiltersBreakpoint="md"
        clearAllFilters={jest.fn()}
        clearFiltersButtonText="Reset to default"
      >
        <ToolbarContent>
          <JobsManagementFilters {...props} />
        </ToolbarContent>
      </Toolbar>
    );
    wrapper.find('#apply-filter').first().simulate('click');
    wrapper = wrapper.update();
    expect(props.setChips).toHaveBeenCalled();
    expect(props.setValues).toHaveBeenCalled();
    expect(wrapper.find('JobsManagementFilters').props()['chips']).toEqual([
      'SCHEDULED'
    ]);
  });
  it('test select component props', async () => {
    let wrapper = mount(<TestWrapper />);
    await act(async () => {
      wrapper.find('#status-select').first().props()['onToggle']();
    });
    wrapper = wrapper.update();
    expect(wrapper.find('#status-select').first().props()['isOpen']).toEqual(
      true
    );
    const event: any = { target: { id: 'pf-random-id-2-SCHEDULED' } };
    await act(async () => {
      wrapper.find('#status-select').first().props()['onSelect'](event);
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('JobsManagementFilters').props()['selectedStatus'].length
    ).toEqual(0);
  });
  it('test select component props with selection', async () => {
    let wrapper = mount(<TestWrapper />);
    const event2: any = { target: { id: 'pf-random-id-2-EXECUTED' } };
    await act(async () => {
      wrapper.find('#status-select').first().props()['onSelect'](event2);
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('JobsManagementFilters').props()['selectedStatus'].length
    ).toEqual(2);
    expect(
      wrapper.find('JobsManagementFilters').props()['selectedStatus']
    ).toContain('EXECUTED');
  });
  it('test select component props with De-selection', async () => {
    let wrapper = mount(<TestWrapper />);
    const event2: any = { target: { id: 'pf-random-id-2-SCHEDULED' } };
    await act(async () => {
      wrapper.find('#status-select').first().props()['onSelect'](event2);
    });
    wrapper = wrapper.update();
    expect(
      wrapper.find('JobsManagementFilters').props()['selectedStatus'].length
    ).toEqual(0);
  });
});

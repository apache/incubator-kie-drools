import React, { FormEvent } from 'react';
import TaskInboxToolbar from '../TaskInboxToolbar';
import { getWrapper } from '@kogito-apps/common';
import {
  Select,
  SelectOption,
  Chip,
  TextInputBase
} from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';
import wait from 'waait';
import TaskConsoleFilterContext, {
  TaskConsoleFilterContextImpl
} from '../../../../context/TaskConsoleFilterContext/TaskConsoleFilterContext';
const applyFilter = jest.fn();
const resetFilter = jest.fn();

const getTaskInboxWrapper = context => {
  return getWrapper(
    <TaskConsoleFilterContext.Provider value={context}>
      <TaskInboxToolbar applyFilter={applyFilter} resetFilter={resetFilter} />
    </TaskConsoleFilterContext.Provider>,
    'TaskInboxToolbar'
  );
};

describe('TaskInbox toolbar tests', () => {
  it('toolbar initial snapshot', () => {
    const context = new TaskConsoleFilterContextImpl();
    expect(getTaskInboxWrapper(context)).toMatchSnapshot();
  });

  it('select status from dropdown', () => {
    const context = new TaskConsoleFilterContextImpl();
    let wrapper = getTaskInboxWrapper(context);
    wrapper
      .find(Select)
      .find('button')
      .simulate('click');
    wrapper = wrapper.update();
    // check length to be 5
    expect(wrapper.find(Select).find(SelectOption).length).toBe(5);
    wrapper
      .find(Select)
      .find(SelectOption)
      .findWhere(node => node.props().value === 'Completed')
      .find('input')
      .simulate('change');
    // add completed to context
    expect(context.getActiveFilters().selectedStatus).toEqual([
      'Ready',
      'Reserved',
      'Completed'
    ]);
    wrapper
      .find(Select)
      .find(SelectOption)
      .findWhere(node => node.props().value === 'Ready')
      .find('input')
      .simulate('change');
    // remove ready from context
    expect(context.getActiveFilters().selectedStatus).toEqual([
      'Reserved',
      'Completed'
    ]);
    wrapper
      .find('#apply-filter')
      .find('button')
      .simulate('click');
    // update filters
    expect(context.getActiveFilters().filters.status).toEqual([
      'Reserved',
      'Completed'
    ]);
  });
  it('delete a chip of status', () => {
    const context = new TaskConsoleFilterContextImpl();
    const wrapper = getTaskInboxWrapper(context);
    // deletes 'Reserved' chip from the filter
    wrapper
      .find(Chip)
      .at(1)
      .find('button')
      .simulate('click');
    expect(context.getActiveFilters().filters.status).toEqual(['Ready']);
    expect(context.getActiveFilters().selectedStatus).toEqual(['Ready']);
  });

  it('enter a text in search box , click apply filter and delete the chip', async () => {
    const context = new TaskConsoleFilterContextImpl();
    let wrapper = getTaskInboxWrapper(context);
    const event = {
      target: {}
    } as FormEvent<HTMLInputElement>;
    await act(async () => {
      wrapper
        .find(TextInputBase)
        .props()
        ['onChange']('', event);
      // enter a text 'App' in text box
      wrapper
        .find(TextInputBase)
        .props()
        ['onChange']('App', event);
      await wait(0);
      // apply the filter
      wrapper
        .find('#apply-filter')
        .find('button')
        .simulate('click');
    });

    wrapper = wrapper.update();
    // adds taskNames filters
    expect(context.getActiveFilters().filters.taskNames).toEqual(['App']);
    wrapper
      .find(Chip)
      .at(2)
      .find('button')
      .simulate('click');
    // deletes taskNames filters using chips
    expect(context.getActiveFilters().filters.taskNames).toEqual([]);
  });

  it('refresh clicked ', () => {
    const context = new TaskConsoleFilterContextImpl();
    const wrapper = getTaskInboxWrapper(context);
    wrapper
      .find('#refresh')
      .find('button')
      .simulate('click');
    expect(applyFilter).toHaveBeenCalled();
  });

  it('disabled filter check', () => {
    const context = new TaskConsoleFilterContextImpl();
    let wrapper = getTaskInboxWrapper(context);
    wrapper
      .find(Select)
      .find('button')
      .simulate('click');
    wrapper = wrapper.update();
    wrapper
      .find(Select)
      .find(SelectOption)
      .findWhere(node => node.props().value === 'Ready')
      .find('input')
      .simulate('change');
    wrapper = wrapper.update();
    wrapper
      .find(Select)
      .find(SelectOption)
      .findWhere(node => node.props().value === 'Reserved')
      .find('input')
      .simulate('change');
    wrapper = wrapper.update();
    expect(
      wrapper
        .find('#apply-filter')
        .at(0)
        .props()['isDisabled']
    ).toBeTruthy();
  });
});

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import ProcessDefinitionListToolbar from '../ProcessDefinitionListToolbar';
import { act } from 'react-dom/test-utils';

describe('ProcessDefinition list toolbar tests', () => {
  it('render toolbar', () => {
    const container = render(
      <ProcessDefinitionListToolbar
        applyFilter={jest.fn()}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
      />
    ).container;
    expect(container).toMatchSnapshot();
    const checkToolbar = container.querySelector(
      '[class="pf-c-toolbar__content"]'
    );
    expect(checkToolbar).toBeTruthy();
    expect(container.querySelector('Trigger Cloud Event')).toBeFalsy();
  });

  it('render toolbar - with trigger cloud event', () => {
    const container = render(
      <ProcessDefinitionListToolbar
        applyFilter={jest.fn()}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
        onOpenTriggerCloudEvent={jest.fn()}
      />
    ).container;
    expect(container).toMatchSnapshot();

    const checkToolbar = container.querySelector(
      '[class="pf-c-toolbar__content"]'
    );
    expect(checkToolbar).toBeTruthy();
    const checkTriggerCloudEventButton = screen.getByText(
      'Trigger Cloud Event'
    );
    expect(checkTriggerCloudEventButton).toBeTruthy();
  });

  it('apply filter click', () => {
    const applyFilter = jest.fn();
    const container = render(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
      />
    ).container;
    fireEvent.click(screen.getByTestId('apply-filter'));
    expect(applyFilter).toHaveBeenCalled();
  });

  it('reset click', () => {
    const applyFilter = jest.fn();
    const container = render(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={['process1']}
        singularProcessLabel={'Workflow'}
      />
    ).container;

    act(() => {
      fireEvent.click(screen.getAllByText('Reset to default')[0]);
    });
    expect(applyFilter).toHaveBeenCalled();
  });

  it('refresh click', () => {
    const applyFilter = jest.fn();
    render(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
      />
    );
    act(() => {
      fireEvent.click(screen.getByTestId('refresh'));
    });
    expect(applyFilter).toHaveBeenCalled();
  });

  it('enter clicked', () => {
    const applyFilter = jest.fn();
    const container = render(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
      />
    ).container;

    const input = screen.getByTestId('apply-filter');
    fireEvent.change(input, { target: { value: 'process1' } });
    fireEvent.keyDown(input, { key: 'enter', keyCode: 13 });
    expect(container).toMatchSnapshot();
  });

  it('on delete chip', () => {
    const applyFilter = jest.fn();
    const container = render(
      <ProcessDefinitionListToolbar
        applyFilter={applyFilter}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={['process1']}
        singularProcessLabel={'Workflow'}
      />
    ).container;

    fireEvent.click(screen.getByLabelText('close'));
    expect(applyFilter).toHaveBeenCalled();
  });

  it('on open trigger cloud event form', () => {
    const triggerCloudEvenMock = jest.fn();
    const container = render(
      <ProcessDefinitionListToolbar
        applyFilter={jest.fn()}
        setFilterProcessNames={jest.fn()}
        filterProcessNames={[]}
        singularProcessLabel={'Workflow'}
        onOpenTriggerCloudEvent={triggerCloudEvenMock}
      />
    ).container;

    const triggerCloudEventButton = screen.getByText('Trigger Cloud Event');

    expect(triggerCloudEventButton).toBeTruthy();

    act(() => {
      fireEvent.click(triggerCloudEventButton);
    });

    expect(triggerCloudEvenMock).toHaveBeenCalled();
  });
});

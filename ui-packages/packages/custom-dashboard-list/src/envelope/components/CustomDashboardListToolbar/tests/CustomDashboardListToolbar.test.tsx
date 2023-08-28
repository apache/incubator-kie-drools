/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import CustomDashboardListToolbar from '../CustomDashboardListToolbar';

describe('CustomDashboard list toolbar tests', () => {
  it('render toolbar', () => {
    const { container } = render(
      <CustomDashboardListToolbar
        applyFilter={jest.fn()}
        setFilterDashboardNames={jest.fn()}
        filterDashboardNames={[]}
      />
    );
    expect(container).toMatchSnapshot();
  });

  it('apply filter click', () => {
    const applyFilter = jest.fn();
    render(
      <CustomDashboardListToolbar
        applyFilter={applyFilter}
        setFilterDashboardNames={jest.fn()}
        filterDashboardNames={[]}
      />
    );

    const button = screen.getByTestId('apply-filter');
    fireEvent.click(button);
    expect(applyFilter).toHaveBeenCalled();
  });

  it('reset click', () => {
    const applyFilter = jest.fn();
    render(
      <CustomDashboardListToolbar
        applyFilter={applyFilter}
        setFilterDashboardNames={jest.fn()}
        filterDashboardNames={['dashboard']}
      />
    );
    const resetButton = screen.getAllByText('Reset to default')[1];
    fireEvent.click(resetButton);
    expect(applyFilter).toHaveBeenCalled();
  });
  it('refresh click', () => {
    const applyFilter = jest.fn();
    render(
      <CustomDashboardListToolbar
        applyFilter={applyFilter}
        setFilterDashboardNames={jest.fn()}
        filterDashboardNames={[]}
      />
    );

    const refreshButton = screen.getByTestId('refresh');
    fireEvent.click(refreshButton);
    expect(applyFilter).toHaveBeenCalled();
  });

  it('enter clicked', () => {
    const applyFilter = jest.fn();
    const { container } = render(
      <CustomDashboardListToolbar
        applyFilter={applyFilter}
        setFilterDashboardNames={jest.fn()}
        filterDashboardNames={[]}
      />
    );
    const searchInput = screen.getByTestId('search-input');
    fireEvent.keyPress(searchInput, { key: 'Enter', code: 13 });

    const applyFilterButton = screen.getByTestId('apply-filter');

    fireEvent.click(applyFilterButton);

    expect(applyFilter).toHaveBeenCalled();
  });

  it('on delete chip', () => {
    const applyFilter = jest.fn();
    const { container } = render(
      <CustomDashboardListToolbar
        applyFilter={applyFilter}
        setFilterDashboardNames={jest.fn()}
        filterDashboardNames={['dashboard']}
      />
    );

    const closeButton = screen.getByLabelText('close');
    fireEvent.click(closeButton);

    expect(applyFilter).toHaveBeenCalled();
  });
});

/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { render, screen, fireEvent } from '@testing-library/react';
import React from 'react';
import { KeycloakUnavailablePage } from '../KeycloakUnavailablePage';

describe('KeycloakUnavailablePage test', () => {
  it('render the page', () => {
    const { container } = render(<KeycloakUnavailablePage />);
    expect(container).toMatchSnapshot();
  });
  it('reload button is clicked', () => {
    const location: Location = window.location;
    delete window.location;
    window.location = {
      ...location,
      reload: jest.fn()
    };
    render(<KeycloakUnavailablePage />);
    const button = screen.getByText('click here to retry');
    fireEvent.click(button);
    expect(window.location.reload).toHaveBeenCalled();
    jest.restoreAllMocks();
    window.location = location;
  });
});

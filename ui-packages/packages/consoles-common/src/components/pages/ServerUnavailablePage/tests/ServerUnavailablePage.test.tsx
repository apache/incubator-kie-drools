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
import { render, screen, fireEvent } from '@testing-library/react';
import { ServerUnavailablePage } from '../ServerUnavailablePage';

const reload = jest.fn();
process.env.KOGITO_APP_NAME = 'Sample console';

describe('ServerUnavailablePage tests', () => {
  beforeEach(() => {
    reload.mockClear();
  });
  it('Snapshot with default name', () => {
    const { container } = render(<ServerUnavailablePage reload={reload} />);

    expect(container).toMatchSnapshot();

    const emptystates = screen.getAllByTestId('empty-state-body');

    expect(emptystates).toHaveLength(2);
    expect(emptystates[0].textContent).toContain(
      `The ${process.env.KOGITO_APP_NAME} could not access the server to display content.`
    );

    const reset = screen.getByTestId('refresh-button');
    fireEvent.click(reset);

    expect(reload).toHaveBeenCalled();
  });

  it('Snapshot with custom name', () => {
    const customDisplayName: string = 'My custom display Name';

    const { container } = render(
      <ServerUnavailablePage displayName={customDisplayName} reload={reload} />
    );

    expect(container).toMatchSnapshot();

    const emptystates = screen.getAllByTestId('empty-state-body');

    expect(emptystates).toHaveLength(2);
    expect(emptystates[0].textContent).toContain(
      `The ${customDisplayName} could not access the server to display content.`
    );

    const reset = screen.getByTestId('refresh-button');
    fireEvent.click(reset);

    expect(reload).toHaveBeenCalled();
  });
});

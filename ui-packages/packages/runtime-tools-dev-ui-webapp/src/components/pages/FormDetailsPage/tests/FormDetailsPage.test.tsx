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
import FormDetailsPage from '../FormDetailsPage';
import { BrowserRouter } from 'react-router-dom';

jest.mock('../../../containers/FormDetailsContainer/FormDetailsContainer');

Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useHistory: () => ({
    location: {
      state: {
        formData: {
          name: 'form1',
          type: 'html',
          lastModified: new Date('2021-08-23T13:26:02.13Z')
        }
      }
    }
  })
}));

describe('FormDetailsPage tests', () => {
  it('Snapshot', () => {
    const { container } = render(
      <BrowserRouter>
        <FormDetailsPage />
      </BrowserRouter>
    );

    expect(container).toMatchSnapshot();

    expect(
      document.querySelector('body[data-ouia-page-type="form-detail"]')
    ).toBeTruthy();
  });
  it('Test close action in Alert component', () => {
    render(
      <BrowserRouter>
        <FormDetailsPage />
      </BrowserRouter>
    );

    const button = screen.getByTestId('close-button');
    fireEvent.click(button);
    expect(() => screen.getByLabelText('Danger Alert')).toThrow(
      'Unable to find a label with the text of: Danger Alert'
    );
  });
});

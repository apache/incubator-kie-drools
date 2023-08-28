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
import { act, fireEvent, render, screen } from '@testing-library/react';
import PageToolbar from '../PageToolbar';
import {
  resetTestKogitoAppContext,
  testHandleLogoutMock
} from '../../../../environment/auth/tests/utils/KogitoAppContextTestingUtils';
import { BrandContext } from '../../BrandContext/BrandContext';

describe('PageToolbar component tests', () => {
  beforeEach(() => {
    testHandleLogoutMock.mockClear();
    resetTestKogitoAppContext(false);
  });

  it('Snapshot testing - auth disabled', () => {
    const { container } = render(
      <BrandContext.Provider
        value={{
          imageSrc: 'kogito-image-src',
          altText: 'kogito image alt text'
        }}
      >
        <PageToolbar />
      </BrandContext.Provider>
    );

    expect(container).toMatchSnapshot();
  });

  it('Snapshot testing - auth enabled', () => {
    resetTestKogitoAppContext(true);
    const { container } = render(
      <BrandContext.Provider
        value={{
          imageSrc: 'kogito-image-src',
          altText: 'kogito image alt text'
        }}
      >
        <PageToolbar />
      </BrandContext.Provider>
    );

    expect(container).toMatchSnapshot();
  });

  it('Testing dropdown items - auth enabled', () => {
    resetTestKogitoAppContext(true);

    const { container } = render(
      <BrandContext.Provider
        value={{
          imageSrc: 'kogito-image-src',
          altText: 'kogito image alt text'
        }}
      >
        <PageToolbar />
      </BrandContext.Provider>
    );

    expect(container).toMatchSnapshot();

    const dropdown = screen.getByTestId('pageToolbar-dropdown');

    const dropdownItems = container.querySelectorAll('span');
    const userProfile = screen.getByText('jdoe');

    expect(userProfile).toBeTruthy();
    fireEvent.click(container.querySelector('button')!);

    const testAbout = container.querySelectorAll('li')[0];
    fireEvent.click(testAbout!);

    const testAboutModalBox = screen.getByText('Version:');
    act(() => {
      expect(testAboutModalBox).toBeTruthy();
    });

    expect(dropdownItems.length).toStrictEqual(3);
  });

  it('Testing logout - auth enabled', () => {
    resetTestKogitoAppContext(true);

    const { container } = render(
      <BrandContext.Provider
        value={{
          imageSrc: 'kogito-image-src',
          altText: 'kogito image alt text'
        }}
      >
        <PageToolbar />
      </BrandContext.Provider>
    );

    const dropdown = screen.getByTestId('pageToolbar-dropdown');

    const dropdownItems = container.querySelectorAll('span');

    const userProfile = screen.getByText('jdoe');

    expect(userProfile).toBeTruthy();
    fireEvent.click(container.querySelector('button')!);

    const testLogoutButton = container.querySelectorAll('li')[2];
    fireEvent.click(testLogoutButton!);

    fireEvent.click(
      container.querySelector(
        '[data-ouia-component-id="OUIA-Generated-Toolbar-4"]'
      )!
    );
    expect(dropdownItems.length).toStrictEqual(3);

    expect(container).toMatchSnapshot();
  });

  it('Testing dropdown items - auth disabled', () => {
    const { container } = render(
      <BrandContext.Provider
        value={{
          imageSrc: 'kogito-image-src',
          altText: 'kogito image alt text'
        }}
      >
        <PageToolbar />
      </BrandContext.Provider>
    );

    expect(container).toMatchSnapshot();

    const anonymousProfile = screen.getByText('Anonymous');

    expect(anonymousProfile).toBeTruthy();
  });
});

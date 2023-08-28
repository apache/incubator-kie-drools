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
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { KogitoEmptyState, KogitoEmptyStateType } from '../KogitoEmptyState';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons/dist/js/icons/info-circle-icon', () =>
  Object.assign(
    { __esModule: true },
    jest.requireActual('@patternfly/react-icons'),
    {
      InfoCircleIcon: () => {
        return <MockedComponent />;
      }
    }
  )
);

jest.mock('@patternfly/react-icons/dist/js/icons/search-icon', () =>
  Object.assign(
    { __esModule: true },
    jest.requireActual('@patternfly/react-icons'),
    {
      SearchIcon: () => {
        return <MockedComponent />;
      }
    }
  )
);

jest.mock(
  '@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon',
  () =>
    Object.assign(
      { __esModule: true },
      jest.requireActual('@patternfly/react-icons'),
      {
        ExclamationTriangleIcon: () => {
          return <MockedComponent />;
        }
      }
    )
);

const props = {
  title: 'No child process instances',
  body: 'This process has no related sub processes',
  ouiaId: 'empty-state-ouia-id'
};

describe('KogitoEmptyState component tests', () => {
  it('Search test', () => {
    const { container } = render(
      <KogitoEmptyState type={KogitoEmptyStateType.Search} {...props} />
    );
    expect(container).toMatchSnapshot();
  });
  it('Reset test', () => {
    const click = jest.fn();
    const { container } = render(
      <KogitoEmptyState
        type={KogitoEmptyStateType.Reset}
        onClick={click}
        {...props}
      />
    );
    expect(container).toMatchSnapshot();
    fireEvent.click(screen.getByText('Reset to default'));
    expect(click).toHaveBeenCalledTimes(1);
  });
  it('Info test', () => {
    const { container } = render(
      <KogitoEmptyState type={KogitoEmptyStateType.Info} {...props} />
    );
    expect(container).toMatchSnapshot();
  });
  it('Refresh test', () => {
    const click = jest.fn();
    const { container } = render(
      <KogitoEmptyState
        type={KogitoEmptyStateType.Refresh}
        onClick={click}
        {...props}
      />
    );
    expect(container).toMatchSnapshot();
    fireEvent.click(screen.getByText('Refresh'));
    expect(click).toHaveBeenCalledTimes(1);
  });
});

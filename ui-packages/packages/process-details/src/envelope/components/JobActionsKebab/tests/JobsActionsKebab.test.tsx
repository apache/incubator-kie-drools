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
import JobActionsKebab from '../JobActionsKebab';
import { fireEvent, render, waitFor, screen } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import { MockedProcessDetailsDriver } from '../../../../embedded/tests/mocks/Mocks';
import { JobStatus } from '@kogito-apps/management-console-shared/dist/types';

const MockedIcon = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    InfoCircleIcon: () => {
      return <MockedIcon />;
    },
    TimesIcon: () => {
      return <MockedIcon />;
    }
  })
);

const props = {
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: JobStatus.Canceled,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: 1,
    repeatLimit: 3,
    scheduledId: '0',
    retries: 0,
    lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
    expirationTime: new Date('2020-08-27T03:35:50.147Z')
  },
  driver: MockedProcessDetailsDriver()
};
const prop2 = {
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: JobStatus.Scheduled,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: 1,
    repeatLimit: 3,
    scheduledId: '0',
    retries: 0,
    lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
    expirationTime: new Date('2020-08-27T03:35:50.147Z')
  },
  driver: MockedProcessDetailsDriver()
};
describe('job actions kebab tests', () => {
  it('dropdown open/close tests and details click', async () => {
    const container = render(<JobActionsKebab {...props} />).container;
    await act(async () => {
      fireEvent.click(container.querySelector('#kebab-toggle')!);
    });
    await waitFor(() => screen.getAllByText('Details'));
    expect(screen.getAllByText('Details')).toBeTruthy();
    await act(async () => {
      fireEvent.click(screen.getByTestId('job-details'));
    });
    expect(container).toMatchSnapshot();
  });

  it('test reschedule option', async () => {
    const modalTitle = 'success';
    const modalContent =
      'The job: 6e74a570-31c8-4020-bd70-19be2cb625f3_0 is rescheduled successfully';
    (prop2.driver.rescheduleJob as jest.Mock).mockImplementationOnce(() =>
      Promise.resolve({ modalTitle, modalContent })
    );
    const container = render(<JobActionsKebab {...prop2} />).container;
    await act(async () => {
      fireEvent.click(container.querySelector('#kebab-toggle')!);
    });
    await waitFor(() => screen.getAllByText('Reschedule'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('job-reschedule'));
    });
    expect(container).toMatchSnapshot();
  });

  it('test job cancel option', async () => {
    const modalTitle = 'success';
    const modalContent =
      'The job: 6e74a570-31c8-4020-bd70-19be2cb625f3_0 is canceled successfully';
    (prop2.driver.cancelJob as jest.Mock).mockImplementationOnce(() =>
      Promise.resolve({ modalTitle, modalContent })
    );
    const container = render(<JobActionsKebab {...prop2} />).container;
    await act(async () => {
      fireEvent.click(container.querySelector('#kebab-toggle')!);
    });
    await waitFor(() => screen.getAllByText('Cancel'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('job-cancel'));
    });
    expect(container).toMatchSnapshot();
  });
});

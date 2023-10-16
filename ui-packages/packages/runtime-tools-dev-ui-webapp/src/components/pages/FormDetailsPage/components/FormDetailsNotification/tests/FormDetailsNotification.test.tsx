/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import FormDetailsNotification, {
  Notification
} from '../FormDetailsNotification';

describe('FormDetailsNotification test', () => {
  it('Simple notification', () => {
    const notificationProps: Notification = {
      type: 'success',
      message: 'The form has been stored',
      close: jest.fn()
    };

    const { container } = render(
      <FormDetailsNotification notification={notificationProps} />
    );

    expect(container).toMatchSnapshot();

    const successAlert = screen.getByLabelText('Success Alert');

    expect(successAlert).toBeTruthy();

    const message = container.querySelector('h4')?.textContent;
    expect(message).toBe('Success alert:The form has been stored');

    const button = screen.getByLabelText(
      'Close Success alert: alert: The form has been stored'
    );
    fireEvent.click(button);

    expect(notificationProps.close).toBeCalled();
  });

  it('Notification with details', async () => {
    const notificationProps: Notification = {
      type: 'error',
      message: 'The form has been stored',
      close: jest.fn(),
      details: 'The details here!'
    };

    const { container } = render(
      <FormDetailsNotification notification={notificationProps} />
    );
    expect(container).toMatchSnapshot();

    const dangerAlert = screen.getByLabelText('Danger Alert');

    expect(dangerAlert).toBeTruthy();

    const alertActions = container.querySelector(
      '[class="pf-c-alert__action"]'
    );

    expect(alertActions).toBeTruthy();

    const detailsButton = screen.getByText('View details');

    fireEvent.click(detailsButton);

    expect(detailsButton).toMatchSnapshot();
  });
});

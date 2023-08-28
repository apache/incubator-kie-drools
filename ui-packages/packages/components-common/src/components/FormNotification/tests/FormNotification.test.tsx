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
import { FormNotification, Notification } from '../FormNotification';
import { render, screen, fireEvent } from '@testing-library/react';

describe('FormNotification test', () => {
  it('Simple notification', () => {
    const notificationProps: Notification = {
      type: 'success',
      message: 'The form has been submitted',
      close: jest.fn()
    };

    const { container } = render(
      <FormNotification notification={notificationProps} />
    );

    expect(container).toMatchSnapshot();

    const alert = screen.getByTestId('alert-box');

    expect(alert).toBeTruthy();

    const successVariant = screen.getByLabelText('Success Alert');

    expect(successVariant).toBeTruthy();

    const alertMessage = container.querySelector('h4')?.textContent;

    expect(alertMessage).toEqual('Success alert:The form has been submitted');

    const closeButton = screen.getByTestId('close-button');

    fireEvent.click(closeButton);

    expect(notificationProps.close).toBeCalled();
  });

  it('Notification with details', async () => {
    const notificationProps: Notification = {
      type: 'error',
      message: 'The form has been submitted',
      close: jest.fn(),
      details: 'The details here!'
    };

    const { container } = render(
      <FormNotification notification={notificationProps} />
    );
    expect(container).toMatchSnapshot();

    const alertMessage = container.querySelector('h4')?.textContent;

    expect(alertMessage).toEqual('Danger alert:The form has been submitted');

    const alert = screen.getByTestId('alert-box');

    expect(alert).toBeTruthy();

    const dangerVariant = screen.getByLabelText('Danger Alert');

    expect(dangerVariant).toBeTruthy();

    const button = screen.getByTestId('view-details');

    expect(button).toBeTruthy();

    expect(button.textContent).toEqual('View details');

    fireEvent.click(button);

    expect(container).toMatchSnapshot();

    const detail = container.querySelector('p')?.textContent;

    expect(detail).toEqual(notificationProps.details);
  });

  it('Notification with custom action', async () => {
    const notificationProps: Notification = {
      type: 'success',
      message: 'The form has been submitted',
      close: jest.fn(),
      customActions: [
        {
          label: 'Custom action',
          onClick: jest.fn()
        }
      ]
    };

    const { container } = render(
      <FormNotification notification={notificationProps} />
    );
    expect(container).toMatchSnapshot();

    const alertMessage = container.querySelector('h4')?.textContent;

    expect(alertMessage).toEqual('Success alert:The form has been submitted');

    const customActionButton = screen.getByTestId('custom-action');

    expect(customActionButton).toBeTruthy();

    expect(customActionButton.textContent).toEqual('Custom action');

    fireEvent.click(customActionButton);

    expect(notificationProps?.customActions[0]?.onClick).toBeCalled();
  });
});

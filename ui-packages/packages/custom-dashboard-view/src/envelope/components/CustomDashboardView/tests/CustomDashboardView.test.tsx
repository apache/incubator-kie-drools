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
import { render, fireEvent } from '@testing-library/react';
import React from 'react';
import CustomDashboardView, {
  CustomDashboardViewProps
} from '../CustomDashboardView';
import { act } from 'react-dom/test-utils';
import { BrowserRouter } from 'react-router-dom';
import { MockedCustomDashboardViewDriver } from '../../../../embedded/tests/utils/Mocks';

const props: CustomDashboardViewProps = {
  isEnvelopeConnectedToChannel: true,
  driver: MockedCustomDashboardViewDriver(),
  customDashboardName: 'name',
  targetOrigin: 'targetOrigin'
};

describe('Custom Dashboard View tests', () => {
  it('Snapshot tests with data', async () => {
    (props.driver.getCustomDashboardContent as jest.Mock).mockResolvedValue(
      'its a yml file'
    );
    await act(async () => {
      fireEvent(
        window,
        new MessageEvent('message', { data: 'ready', origin: 'targetOrigin' })
      );
    });
    let container;
    await act(async () => {
      container = render(<CustomDashboardView {...props} />).container;
    });
    expect(container).toMatchSnapshot();

    const iframeWrapper = container.querySelector('iframe');

    expect(iframeWrapper?.getAttribute('src')).toEqual(
      'resources/webapp/custom-dashboard-view/dashbuilder/index.html'
    );
    expect(props.driver.getCustomDashboardContent).toHaveBeenCalled();
  });

  it('Snapshot tests with error', async () => {
    (props.driver.getCustomDashboardContent as jest.Mock).mockRejectedValue({
      message: 'network issue'
    });
    await act(async () => {
      fireEvent(
        window,
        new MessageEvent('message', { data: 'ready', origin: 'targetOrigin' })
      );
    });
    let container;
    await act(async () => {
      container = render(
        <BrowserRouter>
          <CustomDashboardView {...props} />
        </BrowserRouter>
      ).container;
    });
    expect(container).toMatchSnapshot();

    const cardWrapper = container.querySelector('article');
    expect(cardWrapper).toMatchSnapshot();

    expect(cardWrapper.getAttribute('class')).toEqual(
      'pf-c-card kogito-custom-dashboard-view-__card-size'
    );

    expect(props.driver.getCustomDashboardContent).toHaveBeenCalled();

    expect(container.querySelector('h1').textContent).toEqual(
      'Error fetching data'
    );
  });

  it('With wrong target origin', async () => {
    (props.driver.getCustomDashboardContent as jest.Mock).mockRejectedValue({
      message: 'network issue'
    });
    await act(async () => {
      fireEvent(
        window,
        new MessageEvent('message', { data: 'ready', origin: 'whatever' })
      );
    });
    let container;
    await act(async () => {
      container = render(
        <BrowserRouter>
          <CustomDashboardView {...props} />
        </BrowserRouter>
      ).container;
    });

    expect(container).toMatchSnapshot();
  });
});

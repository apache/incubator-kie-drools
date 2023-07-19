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

import { mount } from 'enzyme';
import { ServerErrors } from '@kogito-apps/components-common';
import React from 'react';
import CustomDashboardView, {
  CustomDashboardViewProps
} from '../CustomDashboardView';
import { act } from 'react-dom/test-utils';
import { Card } from '@patternfly/react-core/dist/js/components/Card';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import { BrowserRouter } from 'react-router-dom';
import { MockedCustomDashboardViewDriver } from '../../../../embedded/tests/utils/Mocks';
import wait from 'waait';

const props: CustomDashboardViewProps = {
  isEnvelopeConnectedToChannel: true,
  driver: MockedCustomDashboardViewDriver(),
  customDashboardName: 'name',
  targetOrigin: 'targetOrigin'
};
const MockedComponent = (): React.ReactElement => {
  return <></>;
};

describe('Custom Dashboard View tests', () => {
  it('Snapshot tests with data', async () => {
    (props.driver.getCustomDashboardContent as jest.Mock).mockResolvedValue(
      'its a yml file'
    );
    let wrapper;
    await act(async () => {
      wrapper = mount(<CustomDashboardView {...props} />);
      wrapper = wrapper.update().find('CustomDashboardView');
      wait();
    });
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(CustomDashboardView)).toMatchSnapshot();
    expect(wrapper.find(CustomDashboardView)).toBeTruthy();
    const iframeWrapper = wrapper.find('iframe');
    expect(iframeWrapper.find('iframe').props()['src']).toEqual(
      'resources/webapp/custom-dashboard-view/dashbuilder/index.html'
    );
    expect(props.driver.getCustomDashboardContent).toHaveBeenCalled();
  });

  it('Snapshot tests with error', async () => {
    (props.driver.getCustomDashboardContent as jest.Mock).mockRejectedValue({
      message: 'network issue'
    });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <BrowserRouter>
          <CustomDashboardView {...props} />
        </BrowserRouter>
      );
      wrapper = wrapper.update().find('CustomDashboardView');
    });
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(CustomDashboardView)).toMatchSnapshot();
    expect(wrapper.find(CustomDashboardView)).toBeTruthy();
    const cardWrapper = wrapper.update().find(Card);
    expect(cardWrapper.find(Card)).toMatchSnapshot();
    expect(cardWrapper.find(Card).props()['className']).toEqual(
      'kogito-custom-dashboard-view-__card-size'
    );

    const bullseyeWrapper = wrapper.update().find(Card);
    expect(bullseyeWrapper.find(Bullseye)).toMatchSnapshot();
    const serverErrorsWrapper = wrapper.update().find(ServerErrors);
    expect(serverErrorsWrapper.find(ServerErrors)).toMatchSnapshot();
    expect(props.driver.getCustomDashboardContent).toHaveBeenCalled();

    expect(serverErrorsWrapper.find(ServerErrors).props()['error']).toEqual(
      'network issue'
    );
  });
});

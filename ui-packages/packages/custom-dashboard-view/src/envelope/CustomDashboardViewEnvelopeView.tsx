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

import React, { useImperativeHandle, useState } from 'react';
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { CustomDashboardViewChannelApi } from '../api';
import CustomDashboardView from './components/CustomDashboardView/CustomDashboardView';
import CustomDashboardViewEnvelopeViewDriver from './CustomDashboardViewEnvelopeViewDriver';
import '@patternfly/patternfly/patternfly.css';

export interface CustomDashboardViewEnvelopeViewApi {
  initialize: (dashboardName: string, targetOrigin: string) => void;
}

interface Props {
  channelApi: MessageBusClientApi<CustomDashboardViewChannelApi>;
}

export const CustomDashboardViewEnvelopeView = React.forwardRef<
  CustomDashboardViewEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] =
    useState<boolean>(false);

  const [customDashboardName, setCustomDashboardName] = useState<string>();
  const [targetOrigin, setTargetOrigin] = useState<string>();

  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (dashboardName: string, targetOrigin: string) => {
        setEnvelopeConnectedToChannel(true);
        setCustomDashboardName(dashboardName);
        setTargetOrigin(targetOrigin);
      }
    }),
    []
  );
  return (
    <>
      {customDashboardName && customDashboardName != 'undefined' && (
        <CustomDashboardView
          isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
          driver={new CustomDashboardViewEnvelopeViewDriver(props.channelApi)}
          customDashboardName={customDashboardName}
          targetOrigin={targetOrigin}
        />
      )}
    </>
  );
});

export default CustomDashboardViewEnvelopeView;

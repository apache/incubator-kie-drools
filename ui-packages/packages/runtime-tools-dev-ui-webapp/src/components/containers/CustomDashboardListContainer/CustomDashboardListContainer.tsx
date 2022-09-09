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

import React, { useEffect } from 'react';
import { OUIAProps } from '@kogito-apps/ouia-tools';
import {
  EmbeddedCustomDashboardList,
  CustomDashboardInfo
} from '@kogito-apps/custom-dashboard-list';
import { CustomDashboardListGatewayApi } from '../../../channel/CustomDashboardList';
import { useCustomDashboardListGatewayApi } from '../../../channel/CustomDashboardList/CustomDashboardListContext';
import { useHistory } from 'react-router-dom';

const CustomDashboardListContainer: React.FC<OUIAProps> = () => {
  const history = useHistory();
  const gatewayApi: CustomDashboardListGatewayApi = useCustomDashboardListGatewayApi();

  useEffect(() => {
    const unsubscriber = gatewayApi.onOpenCustomDashboardListen({
      onOpen(customDashboardInfo: CustomDashboardInfo) {
        history.push({
          pathname: `/customDashboard/${customDashboardInfo.name}`,
          state: {
            filter: gatewayApi.getCustomDashboardFilter(),
            data: customDashboardInfo
          }
        });
      }
    });
    return () => {
      unsubscriber.unSubscribe();
    };
  }, []);

  return <EmbeddedCustomDashboardList driver={gatewayApi} targetOrigin={'*'} />;
};

export default CustomDashboardListContainer;

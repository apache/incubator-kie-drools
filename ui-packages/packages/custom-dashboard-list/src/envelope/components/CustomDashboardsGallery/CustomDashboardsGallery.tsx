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

import React from 'react';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import {
  Gallery,
  GalleryItem
} from '@patternfly/react-core/dist/js/layouts/Gallery';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import { CustomDashboardInfo } from '../../../api/CustomDashboardListEnvelopeApi';
import CustomDashboardCard from '../CustomDashboardCard/CustomDashboardCard';
import { CustomDashboardListDriver } from '../../../api/CustomDashboardListDriver';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import {
  KogitoEmptyState,
  KogitoEmptyStateType
} from '@kogito-apps/components-common/dist/components/KogitoEmptyState';

export interface CustomDashboardGalleryProps {
  driver: CustomDashboardListDriver;
  customDashboardsDatas: CustomDashboardInfo[];
  isLoading: boolean;
}

const CustomDashboardsGallery: React.FC<
  CustomDashboardGalleryProps & OUIAProps
> = ({ driver, customDashboardsDatas, isLoading, ouiaId, ouiaSafe }) => {
  if (isLoading) {
    return (
      <Bullseye>
        <KogitoSpinner
          spinnerText="Loading customDashboard..."
          ouiaId="custom-dashboard-list-loading-custom-dashboard"
        />
      </Bullseye>
    );
  }

  if (
    !isLoading &&
    customDashboardsDatas &&
    customDashboardsDatas.length === 0
  ) {
    return (
      <KogitoEmptyState
        type={KogitoEmptyStateType.Search}
        title="No results found"
        body="Try using different filters"
      />
    );
  }

  return (
    <Gallery
      hasGutter
      style={{ margin: '25px' }}
      {...componentOuiaProps(ouiaId, 'customDashboard-gallery', ouiaSafe)}
    >
      {customDashboardsDatas &&
        customDashboardsDatas.map((customDashboardData, index) => (
          <GalleryItem key={index}>
            <CustomDashboardCard
              customDashboardData={customDashboardData}
              key={index}
              driver={driver}
            ></CustomDashboardCard>
          </GalleryItem>
        ))}
    </Gallery>
  );
};

export default CustomDashboardsGallery;

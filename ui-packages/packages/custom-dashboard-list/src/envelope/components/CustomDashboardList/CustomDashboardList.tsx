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

import React, { useEffect, useState } from 'react';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { CustomDashboardListDriver } from '../../../api/CustomDashboardListDriver';
import CustomDashboardListToolbar from '../CustomDashboardListToolbar/CustomDashboardListToolbar';
import {
  Divider,
  Split,
  SplitItem,
  ToggleGroup,
  ToggleGroupItem
} from '@patternfly/react-core';
import {
  CustomDashboardInfo,
  CustomDashboardFilter
} from '../../../api/CustomDashboardListEnvelopeApi';
import CustomDashboardsTable from '../CustomDashboardsTable/CustomDashboardsTable';
import CustomDashboardsGallery from '../CustomDashboardsGallery/CustomDashboardsGallery';
import { BarsIcon, ThIcon } from '@patternfly/react-icons';
import { ServerErrors } from '@kogito-apps/components-common';
export interface CustomDashboardListProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: CustomDashboardListDriver;
}

const CustomDashboardList: React.FC<CustomDashboardListProps & OUIAProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  ouiaId,
  ouiaSafe
}) => {
  const [filterNames, setFilterNames] = useState<string[]>([]);
  const [dashboardData, setDashboardData] = useState<CustomDashboardInfo[]>([]);
  const [isSelected, setIsSelected] = useState<{
    tableView: boolean;
    cardsView: boolean;
  }>({
    tableView: true,
    cardsView: false
  });
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<any>(null);

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsLoading(true);
      return;
    }
    init();
  }, [isEnvelopeConnectedToChannel]);

  const init = async (): Promise<void> => {
    try {
      const namesFilter = await driver.getCustomDashboardFilter();
      setFilterNames(namesFilter.customDashboardNames);
      setIsLoading(true);
      const response = await driver.getCustomDashboardsQuery();
      setDashboardData(response);
      setIsLoading(false);
    } catch (errorResponse) {
      setError(errorResponse);
    }
  };

  const applyFilter = (filters: CustomDashboardFilter): void => {
    driver.applyFilter(filters);
    init();
  };

  const handleItemClick = (isChosen, event): void => {
    const toggleButtonId = event.currentTarget.id;
    if (toggleButtonId === 'tableView') {
      setIsSelected({
        tableView: true,
        cardsView: false
      });
    } else if (toggleButtonId === 'cardsView') {
      setIsSelected({
        tableView: false,
        cardsView: true
      });
    }
  };
  if (error) {
    return <ServerErrors error={error.message} variant={'large'} />;
  }

  return (
    <div {...componentOuiaProps(ouiaId, 'custom-dashboard-list', ouiaSafe)}>
      <Split hasGutter>
        <SplitItem>
          <CustomDashboardListToolbar
            applyFilter={applyFilter}
            setFilterDashboardNames={setFilterNames}
            filterDashboardNames={filterNames}
          />
        </SplitItem>
        <SplitItem isFilled></SplitItem>
        <SplitItem style={{ padding: '20px' }}>
          <ToggleGroup aria-label="switch view toggle group">
            <ToggleGroupItem
              icon={<BarsIcon />}
              aria-label="table view icon button"
              buttonId="tableView"
              isSelected={isSelected.tableView}
              onChange={handleItemClick}
            />
            <ToggleGroupItem
              icon={<ThIcon />}
              aria-label="card view icon button"
              buttonId="cardsView"
              isSelected={isSelected.cardsView}
              onChange={handleItemClick}
            />
          </ToggleGroup>
        </SplitItem>
      </Split>
      <Divider />
      {isSelected.tableView ? (
        <CustomDashboardsTable
          driver={driver}
          customDashboardData={dashboardData}
          setDashboardsData={setDashboardData}
          isLoading={isLoading}
        />
      ) : (
        <CustomDashboardsGallery
          customDashboardsDatas={dashboardData}
          driver={driver}
          isLoading={isLoading}
        />
      )}
    </div>
  );
};

export default CustomDashboardList;

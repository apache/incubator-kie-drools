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

import React, { useEffect, useState } from 'react';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { FormsListDriver } from '../../../api/FormsListDriver';
import FormsListToolbar from '../FormsListToolbar/FormsListToolbar';
import {
  Divider,
  Split,
  SplitItem,
  ToggleGroup,
  ToggleGroupItem
} from '@patternfly/react-core';
import { FormInfo, FormFilter } from '../../../api/FormsListEnvelopeApi';
import FormsTable from '../FormsTable/FormsTable';
import FormsGallery from '../FormsGallery/FormsGallery';
import { BarsIcon, ThIcon } from '@patternfly/react-icons';
import { ServerErrors } from '@kogito-apps/components-common/dist/components/ServerErrors';
export interface FormsListProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: FormsListDriver;
}

const FormsList: React.FC<FormsListProps & OUIAProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  ouiaId,
  ouiaSafe
}) => {
  const [filterFormNames, setFilterFormNames] = useState<string[]>([]);
  const [formsData, setFormsData] = useState<FormInfo[]>([]);
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
      const namesFilter = await driver.getFormFilter();
      setFilterFormNames(namesFilter.formNames);
      setIsLoading(true);
      const response = await driver.getFormsQuery();
      setFormsData(response);
      setIsLoading(false);
    } catch (errorResponse) {
      setError(errorResponse);
    }
  };

  const applyFilter = (filters: FormFilter): void => {
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
    return <ServerErrors error={error} variant={'large'} />;
  }

  return (
    <div {...componentOuiaProps(ouiaId, 'forms-list', ouiaSafe)}>
      <Split hasGutter>
        <SplitItem>
          <FormsListToolbar
            applyFilter={applyFilter}
            setFilterFormNames={setFilterFormNames}
            filterFormNames={filterFormNames}
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
        <FormsTable
          driver={driver}
          formsData={formsData}
          setFormsData={setFormsData}
          isLoading={isLoading}
        />
      ) : (
        <FormsGallery
          formsData={formsData}
          driver={driver}
          isLoading={isLoading}
        />
      )}
    </div>
  );
};

export default FormsList;

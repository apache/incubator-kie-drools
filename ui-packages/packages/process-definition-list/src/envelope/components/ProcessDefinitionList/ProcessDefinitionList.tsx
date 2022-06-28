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
  DataTable,
  DataTableColumn,
  KogitoSpinner,
  ServerErrors
} from '@kogito-apps/components-common';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import {
  getActionColumn,
  getColumn
} from '../utils/ProcessDefinitionListUtils';
import { ProcessDefinitionListDriver } from '../../../api/ProcessDefinitionListDriver';
import { ProcessDefinition } from '../../../api/ProcessDefinitionListEnvelopeApi';
import { Bullseye, Divider } from '@patternfly/react-core';
import ProcessDefinitionListToolbar from '../ProcessDefinitionListToolbar/ProcessDefinitionListToolbar';

export interface ProcessDefinitionListProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: ProcessDefinitionListDriver;
  singularProcessLabel: string;
}

const ProcessDefinitionList: React.FC<ProcessDefinitionListProps &
  OUIAProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  singularProcessLabel,
  ouiaId,
  ouiaSafe
}) => {
  const [processDefinitionList, setProcessDefinitionList] = useState<
    ProcessDefinition[]
  >([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [filterProcessNames, setFilterProcessNames] = useState<string[]>([]);
  const [error, setError] = useState<string>(null);

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsLoading(true);
      return;
    }
    init();
    return () => {
      setFilterProcessNames([]);
    };
  }, [isEnvelopeConnectedToChannel]);

  const init = async (): Promise<void> => {
    try {
      const response = await driver.getProcessDefinitionsQuery();
      const pdFilter = await driver.getProcessDefinitionFilter();
      setFilterProcessNames(pdFilter);
      setProcessDefinitionList(response);
      setIsLoading(false);
    } catch (errorContent) {
      setError(errorContent);
    }
  };
  const columns: DataTableColumn[] = [
    getColumn('processName', `${singularProcessLabel} Name`),
    getColumn('endpoint', 'Endpoint'),
    getActionColumn(processDefinition => {
      driver.openProcessForm(processDefinition);
    }, singularProcessLabel)
  ];

  const applyFilter = async (): Promise<void> => {
    await driver.setProcessDefinitionFilter(filterProcessNames);
  };

  const filterProcessDefinition = (): ProcessDefinition[] => {
    if (filterProcessNames.length === 0) {
      return processDefinitionList;
    }
    return processDefinitionList.filter(pd =>
      filterProcessNames.includes(pd.processName)
    );
  };

  const processDefinitionLoadingComponent: JSX.Element = (
    <Bullseye>
      <KogitoSpinner
        spinnerText={`Loading ${singularProcessLabel.toLowerCase()} definitions...`}
        ouiaId="forms-list-loading-process-definitions"
      />
    </Bullseye>
  );

  if (error) {
    return <ServerErrors error={error} variant={'large'} />;
  }

  return (
    <div
      {...componentOuiaProps(
        ouiaId,
        'process-definition-list',
        ouiaSafe ? ouiaSafe : !isLoading
      )}
    >
      <ProcessDefinitionListToolbar
        filterProcessNames={filterProcessNames}
        setFilterProcessNames={setFilterProcessNames}
        applyFilter={applyFilter}
        singularProcessLabel={singularProcessLabel}
      />
      <Divider />
      <DataTable
        data={filterProcessDefinition()}
        isLoading={isLoading}
        columns={columns}
        error={false}
        LoadingComponent={processDefinitionLoadingComponent}
      />
    </div>
  );
};

export default ProcessDefinitionList;

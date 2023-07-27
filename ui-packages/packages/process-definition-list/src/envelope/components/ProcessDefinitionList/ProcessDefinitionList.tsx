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

import React, { useCallback, useEffect, useState } from 'react';
import { ServerErrors } from '@kogito-apps/components-common/dist/components/ServerErrors';
import {
  DataTable,
  DataTableColumn
} from '@kogito-apps/components-common/dist/components/DataTable';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import {
  getActionColumn,
  getColumn
} from '../utils/ProcessDefinitionListUtils';
import { ProcessDefinitionListDriver } from '../../../api/ProcessDefinitionListDriver';
import { ProcessDefinition } from '../../../api/ProcessDefinitionListEnvelopeApi';
import { Divider } from '@patternfly/react-core/dist/js/components/Divider';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import ProcessDefinitionListToolbar from '../ProcessDefinitionListToolbar/ProcessDefinitionListToolbar';

export interface ProcessDefinitionListProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: ProcessDefinitionListDriver;
  singularProcessLabel: string;
  isTriggerCloudEventEnabled?: boolean;
}

const ProcessDefinitionList: React.FC<
  ProcessDefinitionListProps & OUIAProps
> = ({
  isEnvelopeConnectedToChannel,
  driver,
  singularProcessLabel,
  isTriggerCloudEventEnabled = false,
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
    getActionColumn((processDefinition) => {
      driver.openProcessForm(processDefinition);
    }, singularProcessLabel)
  ];

  const applyFilter = async (): Promise<void> => {
    await driver.setProcessDefinitionFilter(filterProcessNames);
  };

  const onOpenTriggerCloudEvent = useCallback(() => {
    if (isTriggerCloudEventEnabled) {
      driver.openTriggerCloudEvent();
    }
  }, [isTriggerCloudEventEnabled]);

  const filterProcessDefinition = (): ProcessDefinition[] => {
    if (filterProcessNames.length === 0) {
      return processDefinitionList;
    }
    return processDefinitionList.filter((pd) =>
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
        onOpenTriggerCloudEvent={
          isTriggerCloudEventEnabled ? onOpenTriggerCloudEvent : undefined
        }
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

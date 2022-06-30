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

import React, { ReactText, useCallback, useEffect, useState } from 'react';
import {
  Card,
  PageSection,
  Tab,
  Tabs,
  TabTitleText,
  Select,
  SelectOption,
  SelectVariant,
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem
} from '@patternfly/react-core';
import {
  OUIAProps,
  ouiaPageTypeAndObjectId,
  componentOuiaProps
} from '@kogito-apps/ouia-tools';
import { PageSectionHeader } from '@kogito-apps/consoles-common';
import { ProcessListGatewayApi } from '../../../channel/ProcessList';
import { useProcessListGatewayApi } from '../../../channel/ProcessList/ProcessListContext';
import MonitoringContainer from '../../containers/MonitoringContainer/MonitoringContainer';
import {
  KogitoEmptyState,
  KogitoEmptyStateType
} from '@kogito-apps/components-common';
import '../../styles.css';
import {
  ProcessInstance,
  ProcessInstanceState,
  OrderBy
} from '@kogito-apps/management-console-shared';
import { Dashboard } from '@kogito-apps/monitoring';

interface Props {
  dataIndexUrl?: string;
}
const MonitoringPage: React.FC<OUIAProps & Props> = ({
  ouiaId,
  ouiaSafe,
  dataIndexUrl
}) => {
  const gatewayApi: ProcessListGatewayApi = useProcessListGatewayApi();
  const [hasWorkflow, setHasWorkflow] = useState(false);
  const [loading, setLoading] = useState(true);
  const [openProcessSelect, setOpenProcessSelect] = useState(false);
  const [dashboard, setDashboard] = useState(Dashboard.MONITORING);
  const [workflowList, setWorkflowList] = useState<ProcessInstance[]>([]);
  const [selectedWorkflow, setSelectedWorkflow] = useState<ProcessInstance>();
  const [activeTabKey, setActiveTabKey] = useState<ReactText>(0);

  const initialLoad = () =>
    gatewayApi.initialLoad(
      {
        status: [
          ProcessInstanceState.Aborted,
          ProcessInstanceState.Active,
          ProcessInstanceState.Completed,
          ProcessInstanceState.Error,
          ProcessInstanceState.Suspended
        ],
        businessKey: []
      },
      { start: OrderBy.DESC }
    );

  const loadWorkflowList = useCallback(() => {
    gatewayApi.query(0, 1000).then(list => {
      setSelectedWorkflow(list[0]);
      setWorkflowList(list);
    });
  }, [workflowList, selectedWorkflow]);

  useEffect(() => {
    const intervaId = setInterval(() => {
      if (!hasWorkflow) {
        initialLoad();
        gatewayApi.query(0, 1).then(list => {
          if (list.length > 0) {
            setHasWorkflow(true);
            loadWorkflowList();
          }
          setLoading(false);
        });
      }
    }, 500);
    return () => clearInterval(intervaId);
  }, [hasWorkflow, loading]);

  useEffect(() => {
    if (dashboard === Dashboard.DETAILS) {
      loadWorkflowList();
    }
  }, [dashboard]);

  useEffect(() => {
    return ouiaPageTypeAndObjectId('monitoring');
  });

  return (
    <React.Fragment>
      <PageSectionHeader titleText="Monitoring" ouiaId={ouiaId} />
      {hasWorkflow ? (
        <>
          <Tabs
            activeKey={activeTabKey}
            onSelect={(event, tabIndex) => {
              setActiveTabKey(tabIndex);
              const dashboard =
                tabIndex === 0 ? Dashboard.MONITORING : Dashboard.DETAILS;
              setDashboard(dashboard);
              loadWorkflowList();
            }}
            isBox
            variant="light300"
            style={{
              background: 'white'
            }}
          >
            <Tab
              id="monitoring-report-tab"
              eventKey={0}
              title={<TabTitleText>Summary</TabTitleText>}
            ></Tab>
            <Tab
              id="monitoring-workflow-tab"
              eventKey={1}
              title={<TabTitleText>Workflows</TabTitleText>}
            ></Tab>
          </Tabs>
          <PageSection
            {...componentOuiaProps(ouiaId, 'monitoring-page-section', ouiaSafe)}
          >
            <Card className="Dev-ui__card-size">
              {dashboard === Dashboard.DETAILS && (
                <Toolbar>
                  <ToolbarContent>
                    <ToolbarGroup>
                      <ToolbarItem>
                        <Select
                          aria-labelledby={'workfflow-id-select'}
                          variant={SelectVariant.single}
                          onSelect={(event, v) => {
                            setSelectedWorkflow(
                              workflowList.find(p => p.id === v)
                            );
                            setOpenProcessSelect(false);
                          }}
                          onToggle={() =>
                            setOpenProcessSelect(!openProcessSelect)
                          }
                          isOpen={openProcessSelect}
                          placeholderText="Select Workflow"
                          hasInlineFilter
                          maxHeight={'300px'}
                        >
                          {workflowList.map((p, i) => (
                            <SelectOption
                              key={i}
                              value={p.id}
                              description={p.processId}
                            />
                          ))}
                        </Select>
                      </ToolbarItem>
                    </ToolbarGroup>
                  </ToolbarContent>
                </Toolbar>
              )}
              <MonitoringContainer
                dataIndexUrl={dataIndexUrl}
                workflow={selectedWorkflow ? selectedWorkflow.id : undefined}
                dashboard={dashboard}
              />
            </Card>
          </PageSection>
        </>
      ) : (
        <KogitoEmptyState
          title={loading ? 'Loading' : 'No Data'}
          body={loading ? 'Loading Data' : 'No workflows were started'}
          type={KogitoEmptyStateType.Info}
        />
      )}
    </React.Fragment>
  );
};

export default MonitoringPage;

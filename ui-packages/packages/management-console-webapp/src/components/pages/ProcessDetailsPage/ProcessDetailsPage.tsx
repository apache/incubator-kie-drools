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
import React, { useState, useEffect } from 'react';
import { Card } from '@patternfly/react-core/dist/js/components/Card';
import { PageSection } from '@patternfly/react-core/dist/js/components/Page';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import { ServerErrors } from '@kogito-apps/components-common/dist/components/ServerErrors';
import {
  OUIAProps,
  ouiaPageTypeAndObjectId
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { RouteComponentProps } from 'react-router-dom';
import { PageSectionHeader } from '@kogito-apps/consoles-common/dist/components/layout/PageSectionHeader';
import ProcessDetailsContainer from '../../containers/ProcessDetailsContainer/ProcessDetailsContainer';
import {
  ProcessDetailsGatewayApi,
  useProcessDetailsGatewayApi
} from '../../../channel/ProcessDetails';
import { StaticContext, useHistory } from 'react-router';
import * as H from 'history';
import '../../styles.css';
import { ProcessInstance } from 'packages/management-console-shared/dist/types';

interface MatchProps {
  instanceID: string;
}

const ProcessDetailsPage: React.FC<
  RouteComponentProps<MatchProps, StaticContext, H.LocationState> & OUIAProps
> = ({ ...props }) => {
  const processId = props.match.params.instanceID;
  useEffect(() => {
    return ouiaPageTypeAndObjectId('process-instances', processId);
  });

  const gatewayApi: ProcessDetailsGatewayApi = useProcessDetailsGatewayApi();

  const history = useHistory();
  const [processInstance, setProcessInstance] = useState<ProcessInstance>(
    {} as ProcessInstance
  );
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  let currentPage = JSON.parse(window.localStorage.getItem('state'));

  useEffect(() => {
    window.onpopstate = () => {
      props.history.push({ state: Object.assign({}, props.location.state) });
    };
  });

  async function fetchDetails() {
    let response: ProcessInstance = {} as ProcessInstance;
    try {
      setIsLoading(true);
      response = await gatewayApi.processDetailsQuery(processId);
      setProcessInstance(response);
    } catch (error) {
      setError(error);
    } finally {
      setIsLoading(false);
      /* istanbul ignore else */
      if (error.length === 0 && Object.keys(response).length === 0) {
        let prevPath;
        /* istanbul ignore else */
        if (currentPage) {
          currentPage = Object.assign({}, currentPage, props.location.state);
          const tempPath = currentPage.prev.split('/');
          prevPath = tempPath.filter((item) => item);
        }
        history.push({
          pathname: '/NoData',
          state: {
            prev: currentPage ? currentPage.prev : '/ProcessInstances',
            title: 'Process not found',
            description: `Process instance with the id ${processId} not found`,
            buttonText: currentPage
              ? `Go to ${prevPath[0]
                  .replace(/([A-Z])/g, ' $1')
                  .trim()
                  .toLowerCase()}`
              : 'Go to process instances',
            rememberedData: Object.assign({}, props.location.state)
          }
        });
      }
    }
  }

  useEffect(() => {
    /* istanbul ignore else */
    if (processId) {
      fetchDetails();
    }
  }, [processId]);

  const processName = processInstance ? processInstance.processName : '';

  const renderItems = () => {
    if (!isLoading) {
      return (
        <>
          {processInstance &&
          Object.keys(processInstance).length > 0 &&
          !error ? (
            <ProcessDetailsContainer processInstance={processInstance} />
          ) : (
            <>
              {error.length > 0 && (
                <Card className="kogito-management-console__card-size">
                  <Bullseye>
                    <ServerErrors error={error} variant="large" />
                  </Bullseye>
                </Card>
              )}
            </>
          )}
        </>
      );
    } else {
      return (
        <Card>
          <KogitoSpinner spinnerText="Loading process details..." />
        </Card>
      );
    }
  };

  return (
    <React.Fragment>
      <PageSectionHeader
        titleText="Process Details"
        breadcrumbText={['Home', 'Processes', processName]}
        breadcrumbPath={[
          '/',
          {
            pathname: '/ProcessInstances',
            state: Object.assign({}, props.location.state)
          }
        ]}
      />
      <PageSection>{renderItems()}</PageSection>
    </React.Fragment>
  );
};

export default ProcessDetailsPage;

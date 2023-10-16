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
import React, { useCallback } from 'react';
import { useHistory } from 'react-router-dom';
import { PageSection, Stack, StackItem, Title } from '@patternfly/react-core';
import Outcomes from '../../Organisms/Outcomes/Outcomes';
import SkeletonCards from '../../Molecules/SkeletonCards/SkeletonCards';
import { Outcome, RemoteData, RemoteDataStatus } from '../../../types';
import './ExecutionDetail.scss';

type ExecutionDetailProps = {
  outcomes: RemoteData<Error, Outcome[]>;
};

const ExecutionDetail = (props: ExecutionDetailProps) => {
  const { outcomes } = props;
  const history = useHistory();
  const goToExplanation = useCallback(
    (outcomeId: string) => {
      history.push({
        pathname: 'outcomes-details',
        search: `?outcomeId=${outcomeId}`
      });
    },
    [history]
  );

  return (
    <section className="execution-detail">
      <PageSection variant="default">
        <Stack hasGutter>
          <StackItem>
            <Title headingLevel="h3" size="2xl">
              Outcomes
            </Title>
          </StackItem>
          <StackItem>
            {outcomes.status === RemoteDataStatus.LOADING && (
              <SkeletonCards quantity={2} />
            )}
            {outcomes.status === RemoteDataStatus.SUCCESS && (
              <Outcomes
                outcomes={outcomes.data}
                onExplanationClick={goToExplanation}
                listView
                ouiaId="outcomes-gallery"
              />
            )}
          </StackItem>
        </Stack>
      </PageSection>
    </section>
  );
};

export default ExecutionDetail;

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
import React, { useCallback, useContext, useEffect, useState } from 'react';
import { useParams, useHistory, useLocation } from 'react-router-dom';
import queryString from 'query-string';
import {
  Card,
  CardBody,
  Divider,
  PageSection,
  Stack,
  StackItem,
  Title,
  Tooltip
} from '@patternfly/react-core';
import { HelpIcon } from '@patternfly/react-icons';
import Outcomes from '../../Organisms/Outcomes/Outcomes';
import InputDataBrowser from '../../Organisms/InputDataBrowser/InputDataBrowser';
import SkeletonGrid from '../../Molecules/SkeletonGrid/SkeletonGrid';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import useOutcomeDetail from './useOutcomeDetail';
import EvaluationStatus from '../../Atoms/EvaluationStatus/EvaluationStatus';
import OutcomeSwitch from '../../Organisms/OutcomeSwitch/OutcomeSwitch';
import Explanation from '../../Organisms/Explanation/Explanation';
import { TrustyContext } from '../TrustyApp/TrustyApp';
import {
  ExecutionRouteParams,
  Outcome,
  RemoteData,
  RemoteDataStatus
} from '../../../types';
import './OutcomeDetails.scss';

type OutcomeDetailsProps = {
  outcomes: RemoteData<Error, Outcome[]>;
};

const OutcomeDetails = ({ outcomes }: OutcomeDetailsProps) => {
  const { executionId } = useParams<ExecutionRouteParams>();
  const { config } = useContext(TrustyContext);
  const [outcomeData, setOutcomeData] = useState<Outcome | null>(null);
  const [outcomesList, setOutcomesList] = useState<Outcome[] | null>(null);
  const [outcomeId, setOutcomeId] = useState<string | null>(null);
  const outcomeDetail = useOutcomeDetail(executionId, outcomeId);

  const history = useHistory();
  const location = useLocation();

  const switchExplanation = useCallback(
    (selectedOutcomeId: string) => {
      if (selectedOutcomeId !== outcomeId) {
        history.push({
          search: `outcomeId=${selectedOutcomeId}`
        });
      }
    },
    [history, outcomeId]
  );

  useEffect(() => {
    if (outcomes.status === RemoteDataStatus.SUCCESS) {
      setOutcomesList(outcomes.data);
    }
  }, [outcomes]);

  useEffect(() => {
    const query = queryString.parse(location.search);
    if (query.outcomeId && query.outcomeId.length) {
      setOutcomeId(query.outcomeId as string);
      if (outcomesList) {
        const outcome = outcomesList.find(
          (item) => item.outcomeId === query.outcomeId
        );
        setOutcomeData(outcome);
      }
    }
  }, [location.search, outcomesList]);

  useEffect(() => {
    if (!outcomeId && outcomesList) {
      const defaultOutcome = outcomesList[0];
      history.replace({
        search: `outcomeId=${defaultOutcome.outcomeId}`
      });
    }
  }, [outcomeId, history, outcomesList]);

  return (
    <section className="outcome-details">
      {outcomesList == null && (
        <PageSection
          variant="light"
          className="outcome-details__section--outcome-selector"
        >
          <Divider className="outcome-details__section--outcome-selector__divider" />
          <SkeletonStripe customStyle={{ width: 400 }} />
        </PageSection>
      )}
      {outcomeId !== null &&
        outcomesList !== null &&
        outcomesList.length > 1 && (
          <PageSection
            variant="light"
            className="outcome-details__section--outcome-selector"
          >
            <Divider className="outcome-details__section--outcome-selector__divider" />
            <OutcomeSwitch
              currentExplanationId={outcomeId}
              onDecisionSelection={switchExplanation}
              outcomesList={outcomesList}
            />
          </PageSection>
        )}
      <PageSection
        variant="default"
        className="outcome-details__section outcome-details__outcome"
      >
        <div className="container">
          <Stack hasGutter>
            <StackItem>
              <Title headingLevel="h3" size="2xl">
                Outcome details
              </Title>
            </StackItem>
            <StackItem>
              {outcomeData === null && (
                <Card>
                  <CardBody>
                    <SkeletonGrid rowsCount={2} colsDefinition={2} />
                  </CardBody>
                </Card>
              )}
              {outcomeData !== null && (
                <>
                  {outcomeData.evaluationStatus === 'SUCCEEDED' ? (
                    <Outcomes outcomes={[outcomeData]} />
                  ) : (
                    <Card>
                      <CardBody>
                        <span className="outcome-details__outcome-not-successful">
                          Evaluation status
                        </span>
                        <EvaluationStatus
                          status={outcomeData.evaluationStatus}
                        />
                      </CardBody>
                    </Card>
                  )}
                </>
              )}
            </StackItem>
          </Stack>
        </div>
      </PageSection>
      {config.explanationEnabled && (
        <PageSection className="outcome-details__section">
          <div className="container">
            <Stack hasGutter>
              <StackItem>
                <Title headingLevel="h3" size="2xl">
                  Explanation
                </Title>
              </StackItem>
              <StackItem>
                <Explanation executionId={executionId} outcomeId={outcomeId} />
              </StackItem>
            </Stack>
          </div>
        </PageSection>
      )}
      <PageSection className="outcome-details__section">
        <div className="container">
          <Stack hasGutter>
            <StackItem>
              <Title headingLevel="h3" size="2xl">
                <span>Outcome influencing inputs</span>
                <Tooltip
                  position="auto"
                  content={
                    <div>
                      This section displays all the input that contributed to
                      this specific decision outcome. They can include model
                      inputs (or a subset) or other sub-decisions.
                    </div>
                  }
                >
                  <HelpIcon className="outcome-details__input-help" />
                </Tooltip>
              </Title>
            </StackItem>
            <StackItem>
              <InputDataBrowser inputData={outcomeDetail} />
            </StackItem>
          </Stack>
        </div>
      </PageSection>
    </section>
  );
};

export default OutcomeDetails;

import React, { useCallback, useEffect, useState } from 'react';
import { useParams, useHistory, useLocation } from 'react-router-dom';
import queryString from 'query-string';
import {
  Button,
  Card,
  CardBody,
  CardHeader,
  Divider,
  Grid,
  GridItem,
  Modal,
  PageSection,
  Stack,
  StackItem,
  Title,
  Tooltip
} from '@patternfly/react-core';
import { HelpIcon } from '@patternfly/react-icons';
import Outcomes from '../../Organisms/Outcomes/Outcomes';
import InputDataBrowser from '../../Organisms/InputDataBrowser/InputDataBrowser';
import FeaturesScoreTable from '../../Organisms/FeaturesScoreTable/FeaturesScoreTable';
import ExplanationSwitch from '../../Organisms/ExplanationSwitch/ExplanationSwitch';
import SkeletonGrid from '../../Molecules/SkeletonGrid/SkeletonGrid';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import useFeaturesScores from './useFeaturesScores';
import useOutcomeDetail from './useOutcomeDetail';
import useSaliencies from './useSaliencies';
import ExplanationUnavailable from '../../Molecules/ExplanationUnavailable/ExplanationUnavailable';
import ExplanationError from '../../Molecules/ExplanationError/ExplanationError';
import EvaluationStatus from '../../Atoms/EvaluationStatus/EvaluationStatus';
import SkeletonDoubleBarChart from '../../Molecules/SkeletonDoubleBarChart/SkeletonDoubleBarChart';
import FeaturesScoreChartBySign from '../../Organisms/FeaturesScoreChartBySign/FeaturesScoreChartBySign';
import { ExecutionRouteParams, Outcome, RemoteData } from '../../../types';
import './Explanation.scss';

type ExplanationProps = {
  outcomes: RemoteData<Error, Outcome[]>;
};

const Explanation = ({ outcomes }: ExplanationProps) => {
  const { executionId } = useParams<ExecutionRouteParams>();
  const [outcomeData, setOutcomeData] = useState<Outcome | null>(null);
  const [outcomesList, setOutcomesList] = useState<Outcome[] | null>(null);
  const [outcomeId, setOutcomeId] = useState<string | null>(null);
  const outcomeDetail = useOutcomeDetail(executionId, outcomeId);
  const saliencies = useSaliencies(executionId);
  const {
    featuresScores,
    topFeaturesScores,
    topFeaturesScoresBySign
  } = useFeaturesScores(saliencies, outcomeId);
  const [displayChart, setDisplayChart] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const history = useHistory();
  const location = useLocation();

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const switchExplanation = useCallback(
    (selectedOutcomeId: string) => {
      if (selectedOutcomeId !== outcomeId) {
        history.push({
          search: `outcomeId=${selectedOutcomeId}`
        });
        setDisplayChart(false);
      }
    },
    [history, outcomeId]
  );

  useEffect(() => {
    if (featuresScores.length) {
      setDisplayChart(true);
    }
  }, [featuresScores]);
  useEffect(() => {
    if (outcomes.status === 'SUCCESS') {
      setOutcomesList(outcomes.data);
    }
  }, [outcomes]);

  useEffect(() => {
    const query = queryString.parse(location.search);
    if (query.outcomeId && query.outcomeId.length) {
      setOutcomeId(query.outcomeId as string);
      if (outcomesList) {
        const outcome = outcomesList.find(
          item => item.outcomeId === query.outcomeId
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
    <section className="explanation-view">
      {outcomesList == null && (
        <PageSection
          variant="light"
          className="explanation-view__section--outcome-selector"
        >
          <Divider className="explanation-view__section--outcome-selector__divider" />
          <SkeletonStripe customStyle={{ width: 400 }} />
        </PageSection>
      )}
      {outcomeId !== null && outcomesList !== null && outcomesList.length > 1 && (
        <PageSection
          variant="light"
          className="explanation-view__section--outcome-selector"
        >
          <Divider className="explanation-view__section--outcome-selector__divider" />
          <ExplanationSwitch
            currentExplanationId={outcomeId}
            onDecisionSelection={switchExplanation}
            outcomesList={outcomesList}
          />
        </PageSection>
      )}
      <PageSection
        variant="default"
        className="explanation-view__section explanation-view__outcome"
      >
        <div className="container">
          <Stack hasGutter>
            <StackItem>
              <Title headingLevel="h3" size="2xl">
                Outcome Details
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
                        <span className="explanation-view__outcome-not-successful">
                          Evaluation Status
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
      <PageSection className="explanation-view__section">
        <div className="container">
          <Stack hasGutter>
            <StackItem>
              <Title headingLevel="h3" size="2xl">
                Explanation
              </Title>
            </StackItem>
            <StackItem>
              {(saliencies.status === 'LOADING' ||
                (saliencies.status === 'SUCCESS' &&
                  featuresScores.length > 0)) && (
                <Grid hasGutter>
                  <GridItem span={8}>
                    <Card>
                      <CardHeader>
                        {topFeaturesScoresBySign.length ? (
                          <Title headingLevel="h4" size="xl">
                            Top Features Score Chart
                          </Title>
                        ) : (
                          <Title headingLevel="h4" size="xl">
                            Features Score Chart
                          </Title>
                        )}
                      </CardHeader>
                      <CardBody>
                        {saliencies.status === 'LOADING' && (
                          <SkeletonDoubleBarChart
                            valuesCount={5}
                            height={400}
                          />
                        )}
                        {saliencies.status === 'SUCCESS' && (
                          <>
                            {topFeaturesScoresBySign.length === 0 && (
                              <div className="explanation-view__chart">
                                {displayChart && (
                                  <FeaturesScoreChartBySign
                                    featuresScore={featuresScores}
                                  />
                                )}
                              </div>
                            )}
                            {topFeaturesScoresBySign.length > 0 && (
                              <>
                                <div className="explanation-view__chart">
                                  {displayChart && (
                                    <FeaturesScoreChartBySign
                                      featuresScore={topFeaturesScoresBySign}
                                    />
                                  )}
                                </div>
                                <Button
                                  variant="secondary"
                                  type="button"
                                  className="all-features-opener"
                                  onClick={handleModalToggle}
                                >
                                  View Complete Chart
                                </Button>
                                <Modal
                                  width={'80%'}
                                  title="All Features Score Chart"
                                  isOpen={isModalOpen}
                                  onClose={handleModalToggle}
                                  actions={[
                                    <Button
                                      key="close"
                                      onClick={handleModalToggle}
                                    >
                                      Close
                                    </Button>
                                  ]}
                                >
                                  <FeaturesScoreChartBySign
                                    featuresScore={featuresScores}
                                    large={true}
                                  />
                                </Modal>
                              </>
                            )}
                          </>
                        )}
                      </CardBody>
                    </Card>
                  </GridItem>
                  <GridItem span={4}>
                    <Card className="explanation-view__score-table">
                      <CardHeader>
                        <Title headingLevel={'h4'} size={'lg'}>
                          Features Weight
                        </Title>
                      </CardHeader>
                      <CardBody>
                        {saliencies.status === 'LOADING' && (
                          <SkeletonGrid rowsCount={4} colsDefinition={2} />
                        )}
                        {saliencies.status === 'SUCCESS' && (
                          <FeaturesScoreTable
                            featuresScore={
                              topFeaturesScores.length > 0
                                ? topFeaturesScores
                                : featuresScores
                            }
                          />
                        )}
                      </CardBody>
                    </Card>
                  </GridItem>
                </Grid>
              )}
              {saliencies.status === 'SUCCESS' && (
                <>
                  {saliencies.data.status === 'SUCCEEDED' &&
                    featuresScores.length === 0 && <ExplanationUnavailable />}
                  {saliencies.data.status === 'FAILED' && (
                    <ExplanationError
                      statusDetail={saliencies.data.statusDetail}
                    />
                  )}
                </>
              )}
            </StackItem>
          </Stack>
        </div>
      </PageSection>
      <PageSection className="explanation-view__section">
        <div className="container">
          <Stack hasGutter>
            <StackItem>
              <Title headingLevel="h3" size="2xl">
                <span>Outcome Influencing Inputs</span>
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
                  <HelpIcon className="explanation-view__input-help" />
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

export default Explanation;

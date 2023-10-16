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
import React, { useEffect, useState } from 'react';
import { RemoteDataStatus, SaliencyStatus } from '../../../types';
import {
  Button,
  Card,
  CardBody,
  CardHeader,
  Grid,
  GridItem,
  Modal,
  Title
} from '@patternfly/react-core';
import SkeletonDoubleBarChart from '../../Molecules/SkeletonDoubleBarChart/SkeletonDoubleBarChart';
import FeaturesScoreChartBySign from '../FeaturesScoreChartBySign/FeaturesScoreChartBySign';
import SkeletonGrid from '../../Molecules/SkeletonGrid/SkeletonGrid';
import FeaturesScoreTable from '../FeaturesScoreTable/FeaturesScoreTable';
import ExplanationUnavailable from '../../Molecules/ExplanationUnavailable/ExplanationUnavailable';
import ExplanationError from '../../Molecules/ExplanationError/ExplanationError';
import useSaliencies from '../../Templates/OutcomeDetails/useSaliencies';
import useFeaturesScores from '../../Templates/OutcomeDetails/useFeaturesScores';
import './Explanation.scss';

type ExplanationProps = {
  executionId: string;
  outcomeId: string;
};

const Explanation = ({ executionId, outcomeId }: ExplanationProps) => {
  const saliencies = useSaliencies(executionId);
  const { featuresScores, topFeaturesScoresBySign } = useFeaturesScores(
    saliencies,
    outcomeId
  );
  const [displayChart, setDisplayChart] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    if (featuresScores.length) {
      setDisplayChart(true);
    }
  }, [featuresScores]);

  useEffect(() => {
    setDisplayChart(false);
  }, [outcomeId]);

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };
  return (
    <>
      {(saliencies.status === RemoteDataStatus.LOADING ||
        (saliencies.status === RemoteDataStatus.SUCCESS &&
          featuresScores.length > 0)) && (
        <Grid hasGutter>
          <GridItem xl2={8} xl={12}>
            <Card className="explanation__chart-card">
              <CardHeader>
                {topFeaturesScoresBySign.length ? (
                  <Title headingLevel="h4" size="xl">
                    Top features score chart
                  </Title>
                ) : (
                  <Title headingLevel="h4" size="xl">
                    Features score chart
                  </Title>
                )}
              </CardHeader>
              <CardBody>
                {saliencies.status === RemoteDataStatus.LOADING && (
                  <SkeletonDoubleBarChart valuesCount={5} height={400} />
                )}
                {saliencies.status === RemoteDataStatus.SUCCESS && (
                  <>
                    {topFeaturesScoresBySign.length === 0 && (
                      <div className="explanation__chart">
                        {displayChart && (
                          <FeaturesScoreChartBySign
                            featuresScore={featuresScores}
                          />
                        )}
                      </div>
                    )}
                    {topFeaturesScoresBySign.length > 0 && (
                      <>
                        <div className="explanation__chart">
                          {displayChart && (
                            <FeaturesScoreChartBySign
                              featuresScore={topFeaturesScoresBySign}
                            />
                          )}
                        </div>
                        <Button
                          variant="secondary"
                          type="button"
                          className="explanation__all-features-opener"
                          onClick={handleModalToggle}
                        >
                          View complete chart
                        </Button>
                        <Modal
                          width={'80%'}
                          title="All features score chart"
                          isOpen={isModalOpen}
                          onClose={handleModalToggle}
                          actions={[
                            <Button key="close" onClick={handleModalToggle}>
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
          <GridItem xl2={4} xl={12}>
            <Card className="explanation__score-table">
              <CardHeader>
                <Title headingLevel={'h4'} size={'lg'}>
                  Features weight
                </Title>
              </CardHeader>
              <CardBody>
                {saliencies.status === RemoteDataStatus.LOADING && (
                  <SkeletonGrid rowsCount={4} colsDefinition={2} />
                )}
                {saliencies.status === RemoteDataStatus.SUCCESS && (
                  <FeaturesScoreTable
                    featuresScore={
                      topFeaturesScoresBySign.length > 0
                        ? topFeaturesScoresBySign
                        : featuresScores
                    }
                  />
                )}
              </CardBody>
            </Card>
          </GridItem>
        </Grid>
      )}
      {saliencies.status === RemoteDataStatus.SUCCESS && (
        <>
          {saliencies.data.status === SaliencyStatus.SUCCEEDED &&
            featuresScores.length === 0 && <ExplanationUnavailable />}
          {saliencies.data.status === SaliencyStatus.FAILED && (
            <ExplanationError statusDetail={saliencies.data.statusDetail} />
          )}
        </>
      )}
    </>
  );
};

export default Explanation;

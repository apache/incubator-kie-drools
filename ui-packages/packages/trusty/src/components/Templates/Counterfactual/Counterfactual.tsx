import React, { useRef } from 'react';
import {
  Divider,
  PageSection,
  Stack,
  StackItem,
  Title
} from '@patternfly/react-core';
import { useParams } from 'react-router-dom';

import { ExecutionRouteParams, RemoteDataStatus } from '../../../types';
import CounterfactualAnalysis from '../../Organisms/CounterfactualAnalysis/CounterfactualAnalysis';
import useInputData from '../InputData/useInputData';
import useDecisionOutcomes from '../AuditDetail/useDecisionOutcomes';
import SkeletonDataList from '../../Molecules/SkeletonDataList/SkeletonDataList';
import SkeletonFlexStripes from '../../Molecules/SkeletonFlexStripes/SkeletonFlexStripes';
import useCFSizes from './useCFSizes';
import './Counterfactual.scss';

const Counterfactual = () => {
  const { executionId } = useParams<ExecutionRouteParams>();
  const inputData = useInputData(executionId);
  const outcomesData = useDecisionOutcomes(executionId);
  const containerRef = useRef<HTMLDivElement>(null);
  const { containerWidth, containerHeight } = useCFSizes(containerRef.current);

  return (
    <>
      <Divider className="counterfactual__divider" />
      <div className="counterfactual__wrapper">
        <div className="counterfactual__wrapper__container" ref={containerRef}>
          {inputData.status === RemoteDataStatus.SUCCESS &&
          outcomesData.status === RemoteDataStatus.SUCCESS ? (
            <CounterfactualAnalysis
              inputs={inputData.data}
              outcomes={outcomesData.data}
              executionId={executionId}
              containerWidth={containerWidth}
              containerHeight={containerHeight}
            />
          ) : (
            <PageSection variant={'light'} isFilled={true}>
              <Stack hasGutter={true}>
                <StackItem>
                  <Title headingLevel="h3" size="2xl">
                    Counterfactual Analysis
                  </Title>
                </StackItem>
                <StackItem>
                  <SkeletonFlexStripes
                    stripesNumber={3}
                    stripesWidth={'100px'}
                    stripesHeight={'1.5em'}
                  />
                </StackItem>
                <StackItem>
                  <SkeletonDataList rowsCount={5} colsCount={5} />
                </StackItem>
              </Stack>
            </PageSection>
          )}
        </div>
      </div>
    </>
  );
};

export default Counterfactual;

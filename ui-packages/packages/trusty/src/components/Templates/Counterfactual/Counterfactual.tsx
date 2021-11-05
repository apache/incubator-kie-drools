import React, { useMemo, useRef } from 'react';
import {
  Divider,
  PageSection,
  Stack,
  StackItem,
  Title
} from '@patternfly/react-core';
import { useParams } from 'react-router-dom';

import {
  CFSupportMessage,
  ExecutionRouteParams,
  RemoteDataStatus
} from '../../../types';
import CounterfactualAnalysis from '../../Organisms/CounterfactualAnalysis/CounterfactualAnalysis';
import useInputData from '../InputData/useInputData';
import useDecisionOutcomes from '../AuditDetail/useDecisionOutcomes';
import SkeletonDataList from '../../Molecules/SkeletonDataList/SkeletonDataList';
import SkeletonFlexStripes from '../../Molecules/SkeletonFlexStripes/SkeletonFlexStripes';
import useCFSizes from './useCFSizes';
import './Counterfactual.scss';
import CounterfactualUnsupported from '../../Atoms/CounterfactualUnsupported/CounterfactualUnsupported';

const Counterfactual = () => {
  const { executionId } = useParams<ExecutionRouteParams>();
  const inputData = useInputData(executionId);
  const outcomesData = useDecisionOutcomes(executionId);
  const containerRef = useRef<HTMLDivElement>(null);
  const { containerWidth, containerHeight } = useCFSizes(containerRef.current);

  const noSupportedInputs = useMemo(() => {
    if (inputData.status !== RemoteDataStatus.SUCCESS) {
      return false;
    }
    return (
      inputData.data.find(input => input.value.kind === 'UNIT') === undefined
    );
  }, [inputData]);

  const noSupportedSearchDomain = useMemo(() => {
    if (inputData.status !== RemoteDataStatus.SUCCESS) {
      return false;
    }
    const units = inputData.data.filter(input => input.value.kind === 'UNIT');
    if (units.length === 0) {
      return false;
    }
    return units.every(input => typeof input.value.value === 'string');
  }, [inputData]);

  const noSupportedOutcomes = useMemo(() => {
    if (outcomesData.status !== RemoteDataStatus.SUCCESS) {
      return false;
    }
    return (
      outcomesData.data.find(
        outcome => outcome.outcomeResult.kind === 'UNIT'
      ) === undefined
    );
  }, [outcomesData]);

  const isSupported = useMemo(
    () =>
      !(noSupportedInputs || noSupportedOutcomes || noSupportedSearchDomain),
    [noSupportedInputs, noSupportedOutcomes, noSupportedSearchDomain]
  );

  const messages = useMemo(() => {
    const messages: CFSupportMessage[] = [];
    if (noSupportedSearchDomain) {
      messages.push({
        id: 'message-outcomes',
        message:
          'All of the model inputs are strings and cannot have search domains defined, which is unsupported.'
      });
    } else if (noSupportedInputs && noSupportedOutcomes) {
      messages.push({
        id: 'message-inputs-and-outcomes',
        message:
          'All model inputs and outcomes are structured data types, which are unsupported.'
      });
    } else if (noSupportedInputs) {
      messages.push({
        id: 'message-inputs',
        message:
          'All model inputs are structured data types, which are unsupported.'
      });
    } else if (noSupportedOutcomes) {
      messages.push({
        id: 'message-outcomes',
        message:
          'All model outcomes are structured data types, which are unsupported.'
      });
    }
    return messages;
  }, [noSupportedInputs, noSupportedOutcomes, noSupportedSearchDomain]);

  return (
    <>
      <Divider className="counterfactual__divider" />
      <div className="counterfactual__wrapper">
        <div className="counterfactual__wrapper__container" ref={containerRef}>
          {inputData.status === RemoteDataStatus.SUCCESS &&
          outcomesData.status === RemoteDataStatus.SUCCESS ? (
            isSupported ? (
              <CounterfactualAnalysis
                inputs={inputData.data}
                outcomes={outcomesData.data}
                executionId={executionId}
                containerWidth={containerWidth}
                containerHeight={containerHeight}
              />
            ) : (
              <CounterfactualUnsupported messages={messages} />
            )
          ) : (
            <PageSection variant={'light'} isFilled={true}>
              <Stack hasGutter={true}>
                <StackItem>
                  <Title headingLevel="h3" size="2xl">
                    Counterfactual analysis
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

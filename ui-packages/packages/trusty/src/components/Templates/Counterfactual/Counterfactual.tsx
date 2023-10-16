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

  const hasAnyUnsupportedInput = useMemo(() => {
    if (inputData.status !== RemoteDataStatus.SUCCESS) {
      return false;
    }
    return (
      inputData.data.find((input) => input.value.kind !== 'UNIT') !== undefined
    );
  }, [inputData]);

  const hasOnlyStringInputs = useMemo(() => {
    if (inputData.status !== RemoteDataStatus.SUCCESS) {
      return false;
    }
    const units = inputData.data.filter((input) => input.value.kind === 'UNIT');
    if (units.length === 0) {
      return false;
    }
    return units.every((input) => typeof input.value.value === 'string');
  }, [inputData]);

  const hasOnlyUnsupportedOutcomes = useMemo(() => {
    if (outcomesData.status !== RemoteDataStatus.SUCCESS) {
      return false;
    }
    return (
      outcomesData.data.find(
        (outcome) => outcome.outcomeResult.kind === 'UNIT'
      ) === undefined
    );
  }, [outcomesData]);

  const isSupported = useMemo(
    () =>
      !(
        hasAnyUnsupportedInput ||
        hasOnlyUnsupportedOutcomes ||
        hasOnlyStringInputs
      ),
    [hasAnyUnsupportedInput, hasOnlyUnsupportedOutcomes, hasOnlyStringInputs]
  );

  const messages = useMemo(() => {
    const messages: CFSupportMessage[] = [];
    if (hasOnlyStringInputs) {
      messages.push({
        id: 'message-inputs-string',
        message:
          'All of the model inputs are strings and cannot have search domains defined, which is unsupported.'
      });
    } else if (hasAnyUnsupportedInput && hasOnlyUnsupportedOutcomes) {
      messages.push({
        id: 'message-inputs-and-outcomes',
        message:
          'At least one model input and all outcomes are either a structure or collection, which are unsupported.'
      });
    } else if (hasAnyUnsupportedInput) {
      messages.push({
        id: 'message-inputs',
        message:
          'At least one model input is either a structure or collection, which are unsupported.'
      });
    } else if (hasOnlyUnsupportedOutcomes) {
      messages.push({
        id: 'message-outcomes',
        message:
          'All model outcomes are either a structure or collection, which are unsupported.'
      });
    }
    return messages;
  }, [hasAnyUnsupportedInput, hasOnlyUnsupportedOutcomes, hasOnlyStringInputs]);

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

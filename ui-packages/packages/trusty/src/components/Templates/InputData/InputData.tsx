import React from 'react';
import { useParams } from 'react-router-dom';
import { PageSection, Stack, StackItem, Title } from '@patternfly/react-core';
import InputDataBrowser from '../../Organisms/InputDataBrowser/InputDataBrowser';
import useInputData from './useInputData';
import { ExecutionRouteParams } from '../../../types';

const InputData = () => {
  const { executionId } = useParams<ExecutionRouteParams>();
  const inputData = useInputData(executionId);

  return (
    <PageSection>
      <Stack hasGutter>
        <StackItem>
          <Title headingLevel="h3" size="2xl">
            Input data
          </Title>
        </StackItem>
        <StackItem>
          <InputDataBrowser inputData={inputData} />
        </StackItem>
      </Stack>
    </PageSection>
  );
};

export default InputData;

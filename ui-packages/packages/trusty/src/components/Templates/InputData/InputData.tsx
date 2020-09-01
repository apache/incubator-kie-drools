import React from 'react';
import { useParams } from 'react-router-dom';
import { PageSection } from '@patternfly/react-core';
import InputDataBrowser from '../../Organisms/InputDataBrowser/InputDataBrowser';
import useInputData from './useInputData';
import { ExecutionRouteParams } from '../../../types';

const InputData = () => {
  const { executionId } = useParams<ExecutionRouteParams>();
  const inputData = useInputData(executionId);

  return (
    <PageSection>
      <InputDataBrowser inputData={inputData} />
    </PageSection>
  );
};

export default InputData;

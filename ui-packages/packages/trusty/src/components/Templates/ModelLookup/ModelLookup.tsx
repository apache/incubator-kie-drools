import React from 'react';
import { Divider, PageSection } from '@patternfly/react-core';
import useModelData from './useModelData';
import { useParams } from 'react-router-dom';
import { ExecutionRouteParams, RemoteDataStatus } from '../../../types';
import ModelDiagram from '../../Organisms/ModelDiagram/ModelDiagram';

const ModelLookup = () => {
  const { executionId } = useParams<ExecutionRouteParams>();
  const modelData = useModelData(executionId);
  return (
    <>
      <PageSection
        variant={'light'}
        style={{ paddingTop: 0, paddingBottom: 0 }}
      >
        <Divider />
      </PageSection>
      <PageSection variant={'light'}>
        {modelData.status === RemoteDataStatus.SUCCESS && (
          <ModelDiagram model={modelData.data} />
        )}
      </PageSection>
    </>
  );
};

export default ModelLookup;

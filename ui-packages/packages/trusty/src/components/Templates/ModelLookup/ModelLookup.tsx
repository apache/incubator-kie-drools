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

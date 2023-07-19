/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useMemo } from 'react';
import {
  DevUIAppContext,
  useDevUIAppContext
} from '../../components/contexts/DevUIAppContext';
import ProcessDefinitionListContext from './ProcessDefinitionListContext';
import { ProcessDefinitionListGatewayApiImpl } from './ProcessDefinitionListGatewayApi';

interface ProcessDefinitionListContextProviderProps {
  children;
}

const ProcessDefinitionListContextProvider: React.FC<
  ProcessDefinitionListContextProviderProps
> = ({ children }) => {
  const runtimeToolsApi: DevUIAppContext = useDevUIAppContext();

  const gatewayApiImpl = useMemo(() => {
    return new ProcessDefinitionListGatewayApiImpl(
      runtimeToolsApi.getDevUIUrl(),
      runtimeToolsApi.getOpenApiPath()
    );
  }, []);

  return (
    <ProcessDefinitionListContext.Provider value={gatewayApiImpl}>
      {children}
    </ProcessDefinitionListContext.Provider>
  );
};

export default ProcessDefinitionListContextProvider;

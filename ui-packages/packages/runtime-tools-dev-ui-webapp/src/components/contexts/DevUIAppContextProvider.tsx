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

import React from 'react';
import { User } from '@kogito-apps/consoles-common';
import RuntimeToolsDevUIAppContext, {
  DevUIAppContextImpl
} from './DevUIAppContext';
import { CustomLabels } from '../../api/CustomLabels';
import { DiagramPreviewSize } from '@kogito-apps/process-details/dist/api';

interface IOwnProps {
  users: User[];
  devUIUrl: string;
  openApiPath: string;
  isProcessEnabled: boolean;
  isTracingEnabled: boolean;
  availablePages: string[];
  customLabels: CustomLabels;
  omittedProcessTimelineEvents: string[];
  diagramPreviewSize: DiagramPreviewSize;
  isStunnerEnabled: boolean;
}

const DevUIAppContextProvider: React.FC<IOwnProps> = ({
  users,
  devUIUrl,
  openApiPath,
  isProcessEnabled,
  isTracingEnabled,
  availablePages,
  customLabels,
  omittedProcessTimelineEvents,
  diagramPreviewSize,
  isStunnerEnabled,
  children
}) => {
  return (
    <RuntimeToolsDevUIAppContext.Provider
      value={
        new DevUIAppContextImpl({
          users,
          devUIUrl,
          openApiPath,
          isProcessEnabled,
          isTracingEnabled,
          availablePages,
          customLabels,
          omittedProcessTimelineEvents,
          diagramPreviewSize,
          isStunnerEnabled
        })
      }
    >
      {children}
    </RuntimeToolsDevUIAppContext.Provider>
  );
};

export default DevUIAppContextProvider;

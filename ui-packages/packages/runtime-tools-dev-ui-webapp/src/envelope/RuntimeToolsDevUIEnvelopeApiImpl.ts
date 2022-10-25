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

import { EnvelopeApiFactoryArgs } from '@kogito-tooling/envelope';
import {
  Association,
  RuntimeToolsDevUIChannelApi,
  RuntimeToolsDevUIEnvelopeApi,
  RuntimeToolsDevUIInitArgs
} from '../api';
import { RuntimeToolsDevUIEnvelopeContextType } from './RuntimeToolsDevUIEnvelopeContext';
import { RuntimeToolsDevUIEnvelopeViewApi } from './RuntimeToolsDevUIEnvelopeViewApi';

export class RuntimeToolsDevUIEnvelopeApiImpl
  implements RuntimeToolsDevUIEnvelopeApi {
  private capturedInitRequestYet = false;

  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      RuntimeToolsDevUIEnvelopeApi,
      RuntimeToolsDevUIChannelApi,
      RuntimeToolsDevUIEnvelopeViewApi,
      RuntimeToolsDevUIEnvelopeContextType
    >
  ) { }

  private hasCapturedInitRequestYet() {
    return this.capturedInitRequestYet;
  }

  private ackCapturedInitRequest() {
    this.capturedInitRequestYet = true;
  }

  public runtimeToolsDevUI_initRequest = async (
    association: Association,
    initArgs: RuntimeToolsDevUIInitArgs
  ): Promise<void> => {
    this.args.envelopeBusController.associate(
      association.origin,
      association.envelopeServerId
    );

    if (this.hasCapturedInitRequestYet()) {
      return;
    }

    this.ackCapturedInitRequest();

    this.args.view().setDataIndexUrl(initArgs.dataIndexUrl);
    this.args.view().setTrustyServiceUrl(initArgs.trustyServiceUrl);
    this.args.view().setUsers(initArgs.users);
    this.args.view().navigateTo(initArgs.page);
    this.args.view().setDevUIUrl &&
      this.args.view().setDevUIUrl(initArgs.devUIUrl);
    this.args.view().setOpenApiPath &&
      this.args.view().setOpenApiPath(initArgs.openApiPath);
    this.args.view().setAvailablePages &&
      this.args.view().setAvailablePages(initArgs.availablePages);
    this.args.view().setCustomLabels &&
      this.args.view().setCustomLabels(initArgs.customLabels);
    this.args.view().setOmittedProcessTimelineEvents &&
      this.args
        .view()
        .setOmittedProcessTimelineEvents(initArgs.omittedProcessTimelineEvents);
    this.args.view().setDiagramPreviewSize &&
      this.args.view().setDiagramPreviewSize(initArgs.diagramPreviewSize);
    this.args.view().setIsStunnerEnabled && this.args.view().setIsStunnerEnabled(initArgs.isStunnerEnabled)
    // Ensure these are set last. This is a workaround to ensure views are corrected configured with other properties
    // from the DevUIAppContext before they are rendered. i.e. use of DevUIAppContext is not responsive to updates.
    this.args.view().setProcessEnabled(initArgs.isDataIndexAvailable);
    this.args.view().setTracingEnabled(initArgs.isTracingEnabled);
  };
}

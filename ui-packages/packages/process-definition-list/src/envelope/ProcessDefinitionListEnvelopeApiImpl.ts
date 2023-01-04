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
import { ProcessDefinitionListEnvelopeViewApi } from './ProcessDefinitionListEnvelopeView';
import {
  Association,
  ProcessDefinitionListChannelApi,
  ProcessDefinitionListEnvelopeApi,
  ProcessDefinitionListInitArgs
} from '../api';
import { ProcessDefinitionListEnvelopeContext } from './ProcessDefinitionListEnvelopeContext';

/**
 * Implementation of the ProcessDefinitionListEnvelopeApi
 */
export class ProcessDefinitionListEnvelopeApiImpl
  implements ProcessDefinitionListEnvelopeApi
{
  private capturedInitRequestYet = false;
  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      ProcessDefinitionListEnvelopeApi,
      ProcessDefinitionListChannelApi,
      ProcessDefinitionListEnvelopeViewApi,
      ProcessDefinitionListEnvelopeContext
    >
  ) {}

  private hasCapturedInitRequestYet() {
    return this.capturedInitRequestYet;
  }

  private ackCapturedInitRequest() {
    this.capturedInitRequestYet = true;
  }

  processDefinitionList__init = async (
    association: Association,
    initArgs: ProcessDefinitionListInitArgs
  ): Promise<void> => {
    this.args.envelopeBusController.associate(
      association.origin,
      association.envelopeServerId
    );

    if (this.hasCapturedInitRequestYet()) {
      return;
    }

    this.ackCapturedInitRequest();
    this.args.view().initialize(initArgs);
  };
}

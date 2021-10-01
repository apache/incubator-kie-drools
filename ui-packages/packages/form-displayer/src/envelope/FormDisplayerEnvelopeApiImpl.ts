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
  FormArgs,
  FormDisplayerChannelApi,
  FormDisplayerEnvelopeApi,
  FormDisplayerInitArgs
} from '../api';
import { FormDisplayerEnvelopeViewApi } from './FormDisplayerEnvelopeView';
import { FormDisplayerEnvelopeContext } from './FormDisplayerEnvelopeContext';
import isEqual from 'lodash/isEqual';
export class FormDisplayerEnvelopeApiImpl implements FormDisplayerEnvelopeApi {
  private capturedInitRequestYet = false;
  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      FormDisplayerEnvelopeApi,
      FormDisplayerChannelApi,
      FormDisplayerEnvelopeViewApi,
      FormDisplayerEnvelopeContext
    >
  ) {}

  private hasCapturedInitRequestYet() {
    return this.capturedInitRequestYet;
  }

  private ackCapturedInitRequest() {
    this.capturedInitRequestYet = true;
  }

  public async formDisplayer__init(
    association: Association,
    initArgs: FormDisplayerInitArgs
  ) {
    this.args.envelopeBusController.associate(
      association.origin,
      association.envelopeServerId
    );
    let tempContent = {};

    if (this.hasCapturedInitRequestYet()) {
      return;
    }

    this.ackCapturedInitRequest();

    if (!isEqual(tempContent, initArgs.formContent)) {
      tempContent = initArgs.formContent;
      this.args.view().setFormContent(initArgs.formContent, initArgs.formData);
    }
  }
  formDisplayer__notify = (formContent: FormArgs): Promise<void> => {
    this.args.view().notify(formContent);
    return Promise.resolve();
  };
}

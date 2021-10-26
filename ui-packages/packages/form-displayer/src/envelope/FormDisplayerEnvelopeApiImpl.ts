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
  FormDisplayerChannelApi,
  FormDisplayerEnvelopeApi,
  FormDisplayerInitArgs,
  FormSubmitContext,
  FormSubmitResponse
} from '../api';
import { FormDisplayerEnvelopeViewApi } from './FormDisplayerEnvelopeView';
import { FormDisplayerEnvelopeContext } from './FormDisplayerEnvelopeContext';
import isEmpty from 'lodash/isEmpty';

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

    if (this.hasCapturedInitRequestYet()) {
      return;
    }

    this.ackCapturedInitRequest();

    if (!isEmpty(initArgs.form)) {
      this.args.view().initForm(initArgs);
    }
  }
  formDisplayer__notifyInit = (initArgs: FormDisplayerInitArgs): void => {
    this.args.view().initForm(initArgs);
  };

  formDisplayer__startSubmit(context: FormSubmitContext): Promise<any> {
    return this.args.view().startSubmit(context);
  }

  formDisplayer__notifySubmitResponse(response: FormSubmitResponse) {
    this.args.view().notifySubmitResponse(response);
  }
}

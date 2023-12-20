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
import { EnvelopeApiFactoryArgs } from '@kie-tools-core/envelope';
import { FormDisplayerChannelApi, FormDisplayerEnvelopeApi } from '../api';
import {
  Association,
  FormDisplayerInitArgs,
  FormSubmitContext,
  FormSubmitResponse
} from '@kogito-apps/components-common/dist/types';
import { FormDisplayerEnvelopeViewApi } from './FormDisplayerEnvelopeView';
import { FormDisplayerEnvelopeContext } from './FormDisplayerEnvelopeContext';
import isEmpty from 'lodash/isEmpty';

export class FormDisplayerEnvelopeApiImpl implements FormDisplayerEnvelopeApi {
  private view: () => FormDisplayerEnvelopeViewApi;
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
    this.args.envelopeClient.associate(
      association.origin,
      association.envelopeServerId
    );

    if (this.hasCapturedInitRequestYet()) {
      return;
    }

    this.ackCapturedInitRequest();
    this.view = await this.args.viewDelegate();

    if (!isEmpty(initArgs.form)) {
      this.view().initForm(initArgs);
    }
  }
  formDisplayer__notifyInit = (initArgs: FormDisplayerInitArgs): void => {
    this.view().initForm(initArgs);
  };

  formDisplayer__startSubmit(context: FormSubmitContext): Promise<any> {
    return this.view().startSubmit(context);
  }

  formDisplayer__notifySubmitResponse(response: FormSubmitResponse) {
    this.view().notifySubmitResponse(response);
  }
}

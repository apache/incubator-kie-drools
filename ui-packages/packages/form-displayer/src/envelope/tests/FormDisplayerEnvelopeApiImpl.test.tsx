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
import {
  MockedEnvelopeClient,
  MockedFormDisplayerEnvelopeViewApi
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kie-tools-core/envelope';
import { FormDisplayerChannelApi, FormDisplayerEnvelopeApi } from '../../api';
import { FormType } from '@kogito-apps/components-common/dist';
import { FormDisplayerEnvelopeApiImpl } from '../FormDisplayerEnvelopeApiImpl';
import { FormDisplayerEnvelopeViewApi } from '../FormDisplayerEnvelopeView';
import { FormDisplayerEnvelopeContext } from '../FormDisplayerEnvelopeContext';

describe('FormDisplayerEnvelopeApiImpl tests', () => {
  it('initialize', async () => {
    const envelopeClient = MockedEnvelopeClient;
    const view = new MockedFormDisplayerEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      FormDisplayerEnvelopeApi,
      FormDisplayerChannelApi,
      FormDisplayerEnvelopeViewApi,
      FormDisplayerEnvelopeContext
    > = {
      envelopeClient,
      envelopeContext: {},
      viewDelegate: () => Promise.resolve(() => view)
    };

    const envelopeApi = new FormDisplayerEnvelopeApiImpl(args);

    envelopeApi.formDisplayer__init(
      {
        envelopeServerId: 'envelopeServerId',
        origin: 'origin'
      },
      {
        form: {
          formInfo: {
            lastModified: new Date('2021-08-23T13:26:02.130Z'),
            name: 'react_hiring_HRInterview',
            type: FormType.TSX
          },
          configuration: {
            resources: {
              scripts: {},
              styles: {}
            },
            schema: 'json schema'
          },
          source: 'react source code'
        }
      }
    );

    expect(envelopeClient.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
    const calledView = await view.initForm;
    expect(calledView).toHaveBeenCalled();
  });
});

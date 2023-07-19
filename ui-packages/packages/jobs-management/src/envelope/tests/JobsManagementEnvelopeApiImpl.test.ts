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

import {
  MockedEnvelopeClient,
  MockedJobsManagementEnvelopeViewApi
} from './mocks/Mocks';
import { EnvelopeApiFactoryArgs } from '@kie-tools-core/envelope';
import { JobsManagementChannelApi, JobsManagementEnvelopeApi } from '../../api';
import { JobsManagementEnvelopeApiImpl } from '../JobsManagementEnvelopeApiImpl';
import { JobsManagementEnvelopeViewApi } from '../JobsManagementEnvelopeView';
import { JobsManagementEnvelopeContext } from '../JobsManagementEnvelopeContext';

describe('JobsManagementEnvelopeApiImpl tests', () => {
  it('initialize', async () => {
    const envelopeClient = MockedEnvelopeClient;
    const view = new MockedJobsManagementEnvelopeViewApi();
    const args: EnvelopeApiFactoryArgs<
      JobsManagementEnvelopeApi,
      JobsManagementChannelApi,
      JobsManagementEnvelopeViewApi,
      JobsManagementEnvelopeContext
    > = {
      envelopeClient,
      envelopeContext: {},
      viewDelegate: () => Promise.resolve(() => view)
    };

    const envelopeApi = new JobsManagementEnvelopeApiImpl(args);

    envelopeApi.jobsManagement__init({
      envelopeServerId: 'envelopeServerId',
      origin: 'origin'
    });

    expect(envelopeClient.associate).toHaveBeenCalledWith(
      'origin',
      'envelopeServerId'
    );
    const calledView = await view.initialize;
    expect(calledView).toHaveBeenCalled();
  });
});

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
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import {
  FormFilter,
  FormsListChannelApi,
  FormsListDriver,
  FormInfo
} from '../api';

/**
 * Implementation of FormsListDriver that delegates calls to the channel Api
 */
export default class FormsListEnvelopeViewDriver implements FormsListDriver {
  constructor(
    private readonly channelApi: MessageBusClientApi<FormsListChannelApi>
  ) {}

  getFormFilter(): Promise<FormFilter> {
    return this.channelApi.requests.formsList__getFormFilter();
  }

  applyFilter(formFilter: FormFilter): Promise<void> {
    return this.channelApi.requests.formsList__applyFilter(formFilter);
  }

  getFormsQuery(): Promise<FormInfo[]> {
    return this.channelApi.requests.formsList__getFormsQuery();
  }

  openForm(formData: FormInfo): Promise<void> {
    return this.channelApi.requests.formsList__openForm(formData);
  }
}

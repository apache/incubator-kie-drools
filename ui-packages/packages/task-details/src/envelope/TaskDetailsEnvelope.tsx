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
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { EnvelopeBus } from '@kie-tools-core/envelope-bus/dist/api';
import { Envelope, EnvelopeDivConfig } from '@kie-tools-core/envelope';
import { TaskDetailsChannelApi, TaskDetailsEnvelopeApi } from '../api';
import { TaskDetailsEnvelopeContext } from './TaskDetailsEnvelopeContext';
import {
  TaskDetailsEnvelopeView,
  TaskDetailsEnvelopeViewApi
} from './TaskDetailsEnvelopeView';
import { TaskDetailsEnvelopeApiImpl } from './TaskDetailsEnvelopeApiImpl';

export function init(args: {
  config: EnvelopeDivConfig;
  container: HTMLDivElement;
  bus: EnvelopeBus;
}) {
  const envelope = new Envelope<
    TaskDetailsEnvelopeApi,
    TaskDetailsChannelApi,
    TaskDetailsEnvelopeViewApi,
    TaskDetailsEnvelopeContext
  >(args.bus, args.config);

  const envelopeViewDelegate = async () => {
    const ref = React.createRef<TaskDetailsEnvelopeViewApi>();
    return new Promise<() => TaskDetailsEnvelopeViewApi>((res) => {
      ReactDOM.render(
        <TaskDetailsEnvelopeView ref={ref} channelApi={envelope.channelApi} />,
        args.container,
        () => res(() => ref.current)
      );
    });
  };

  const context: TaskDetailsEnvelopeContext = {};
  return envelope.start(envelopeViewDelegate, context, {
    create: (apiFactoryArgs) => {
      return new TaskDetailsEnvelopeApiImpl(apiFactoryArgs);
    }
  });
}

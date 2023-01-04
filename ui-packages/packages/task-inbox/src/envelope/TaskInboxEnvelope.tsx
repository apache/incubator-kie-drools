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

import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { EnvelopeBus } from '@kogito-tooling/envelope-bus/dist/api';
import { TaskInboxChannelApi, TaskInboxEnvelopeApi } from '../api';
import { TaskInboxEnvelopeContext } from './TaskInboxEnvelopeContext';
import {
  TaskInboxEnvelopeView,
  TaskInboxEnvelopeViewApi
} from './TaskInboxEnvelopeView';
import { TaskInboxEnvelopeApiImpl } from './TaskInboxEnvelopeApiImpl';
import { Envelope, EnvelopeDivConfig } from '@kogito-tooling/envelope';

/**
 * Function that starts an Envelope application.
 *
 * @param args.container: The HTML element in which the TaskInbox will render
 * @param args.bus: The implementation of a `bus` that knows how to send messages to the Channel.
 *
 */
export function init(args: {
  config: EnvelopeDivConfig;
  container: HTMLDivElement;
  bus: EnvelopeBus;
}) {
  /**
   * Creates a new generic Envelope, typed with the right interfaces.
   */
  const envelope = new Envelope<
    TaskInboxEnvelopeApi,
    TaskInboxChannelApi,
    TaskInboxEnvelopeViewApi,
    TaskInboxEnvelopeContext
  >(args.bus, args.config);

  /**
   * Function that knows how to render a TaskInbox.
   * In this case, it's a React application, but any other framework can be used.
   *
   * Returns a Promise<() => TaskInboxEnvelopeViewApi> that can be used in TaskInboxEnvelopeApiImpl.
   */
  const envelopeViewDelegate = async () => {
    const ref = React.createRef<TaskInboxEnvelopeViewApi>();
    return new Promise<() => TaskInboxEnvelopeViewApi>((res) => {
      ReactDOM.render(
        <TaskInboxEnvelopeView ref={ref} channelApi={envelope.channelApi} />,
        args.container,
        () => res(() => ref.current)
      );
    });
  };

  const context: TaskInboxEnvelopeContext = {};
  return envelope.start(envelopeViewDelegate, context, {
    create: (apiFactoryArgs) => new TaskInboxEnvelopeApiImpl(apiFactoryArgs)
  });
}

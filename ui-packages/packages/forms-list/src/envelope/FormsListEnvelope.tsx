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
import { EnvelopeBus } from '@kie-tools-core/envelope-bus/dist/api';
import { FormsListChannelApi, FormsListEnvelopeApi } from '../api';
import { FormsListEnvelopeContext } from './FormsListEnvelopeContext';
import {
  FormsListEnvelopeView,
  FormsListEnvelopeViewApi
} from './FormsListEnvelopeView';
import { FormsListEnvelopeApiImpl } from './FormsListEnvelopeApiImpl';
import { Envelope, EnvelopeDivConfig } from '@kie-tools-core/envelope';

/**
 * Function that starts an Envelope application.
 *
 * @param args.container: The HTML element in which the FormsList will render
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
    FormsListEnvelopeApi,
    FormsListChannelApi,
    FormsListEnvelopeViewApi,
    FormsListEnvelopeContext
  >(args.bus, args.config);

  /**
   * Function that knows how to render a FormsList.
   * In this case, it's a React application, but any other framework can be used.
   *
   * Returns a Promise<() => FormsListEnvelopeViewApi> that can be used in FormsListEnvelopeApiImpl.
   */
  const envelopeViewDelegate = async () => {
    const ref = React.createRef<FormsListEnvelopeViewApi>();
    return new Promise<() => FormsListEnvelopeViewApi>((res) => {
      ReactDOM.render(
        <FormsListEnvelopeView ref={ref} channelApi={envelope.channelApi} />,
        args.container,
        () => res(() => ref.current)
      );
    });
  };

  const context: FormsListEnvelopeContext = {};
  return envelope.start(envelopeViewDelegate, context, {
    create: (apiFactoryArgs) => new FormsListEnvelopeApiImpl(apiFactoryArgs)
  });
}

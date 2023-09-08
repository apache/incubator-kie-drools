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
import { FormDetailsChannelApi, FormDetailsEnvelopeApi } from '../api';
import { FormDetailsEnvelopeContext } from './FormDetailsEnvelopeContext';
import {
  FormDetailsEnvelopeView,
  FormDetailsEnvelopeViewApi
} from './FormDetailsEnvelopeView';
import { FormDetailsEnvelopeApiImpl } from './FormDetailsEnvelopeApiImpl';
import { Envelope, EnvelopeDivConfig } from '@kie-tools-core/envelope';

/**
 * Function that starts an Envelope application.
 *
 * @param args.container: The HTML element in which the FormDetails will render
 * @param args.bus: The implementation of a `bus` that knows how to send messages to the Channel.
 *
 */
export function init(args: {
  config: EnvelopeDivConfig;
  container: HTMLDivElement;
  bus: EnvelopeBus;
  targetOrigin: string;
}): Promise<any> {
  /**
   * Creates a new generic Envelope, typed with the right interfaces.
   */
  const envelope = new Envelope<
    FormDetailsEnvelopeApi,
    FormDetailsChannelApi,
    FormDetailsEnvelopeViewApi,
    FormDetailsEnvelopeContext
  >(args.bus, args.config);

  /**
   * Function that knows how to render a FormDetails.
   * In this case, it's a React application, but any other framework can be used.
   *
   * Returns a Promise<() => FormDetailsEnvelopeViewApi> that can be used in FormDetailsEnvelopeApiImpl.
   */
  const envelopeViewDelegate = async () => {
    const ref = React.createRef<FormDetailsEnvelopeViewApi>();
    return new Promise<() => FormDetailsEnvelopeViewApi>((res) => {
      args.container.className = 'kogito-form-details-container';
      ReactDOM.render(
        <FormDetailsEnvelopeView
          ref={ref}
          channelApi={envelope.channelApi}
          targetOrigin={args.targetOrigin}
        />,
        args.container,
        () => res(() => ref.current)
      );
    });
  };

  const context: FormDetailsEnvelopeContext = {};
  return envelope.start(envelopeViewDelegate, context, {
    create: (apiFactoryArgs) => new FormDetailsEnvelopeApiImpl(apiFactoryArgs)
  });
}

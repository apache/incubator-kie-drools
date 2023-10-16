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
import { Envelope, EnvelopeIFrameConfig } from '@kie-tools-core/envelope';
import { FormDisplayerChannelApi, FormDisplayerEnvelopeApi } from '../api';
import { FormDisplayerEnvelopeContext } from './FormDisplayerEnvelopeContext';
import {
  FormDisplayerEnvelopeView,
  FormDisplayerEnvelopeViewApi
} from './FormDisplayerEnvelopeView';
import { FormDisplayerEnvelopeApiImpl } from './FormDisplayerEnvelopeApiImpl';

export function init(args: {
  container: HTMLElement;
  bus: EnvelopeBus;
  config: EnvelopeIFrameConfig;
}) {
  const envelope = new Envelope<
    FormDisplayerEnvelopeApi,
    FormDisplayerChannelApi,
    FormDisplayerEnvelopeViewApi,
    FormDisplayerEnvelopeContext
  >(args.bus, args.config);

  const envelopeViewDelegate = async () => {
    const ref = React.createRef<FormDisplayerEnvelopeViewApi>();
    return new Promise<() => FormDisplayerEnvelopeViewApi>((res) => {
      ReactDOM.render(
        <FormDisplayerEnvelopeView
          ref={ref}
          channelApi={envelope.channelApi}
        />,
        args.container,
        () => res(() => ref.current!)
      );
    });
  };

  const context: FormDisplayerEnvelopeContext = {};
  return envelope.start(envelopeViewDelegate, context, {
    create: (apiFactoryArgs) => {
      return new FormDisplayerEnvelopeApiImpl(apiFactoryArgs);
    }
  });
}

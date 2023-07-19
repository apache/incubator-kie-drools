/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import {
  CustomDashboardViewChannelApi,
  CustomDashboardViewEnvelopeApi
} from '../api';
import { CustomDashboardViewEnvelopeContext } from './CustomDashboardViewEnvelopeContext';
import {
  CustomDashboardViewEnvelopeView,
  CustomDashboardViewEnvelopeViewApi
} from './CustomDashboardViewEnvelopeView';
import { CustomDashboardViewEnvelopeApiImpl } from './CustomDashboardViewEnvelopeApiImpl';
import { Envelope, EnvelopeDivConfig } from '@kie-tools-core/envelope';

/**
 * Function that starts an Envelope application.
 *
 * @param args.container: The HTML element in which the CustomDashboardView will render
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
    CustomDashboardViewEnvelopeApi,
    CustomDashboardViewChannelApi,
    CustomDashboardViewEnvelopeViewApi,
    CustomDashboardViewEnvelopeContext
  >(args.bus, args.config);

  /**
   * Function that knows how to render a CustomDashboardView.
   * In this case, it's a React application, but any other framework can be used.
   *
   * Returns a Promise<() => CustomDashboardViewEnvelopeViewApi> that can be used in CustomDashboardViewEnvelopeApiImpl.
   */
  const envelopeViewDelegate = async () => {
    const ref = React.createRef<CustomDashboardViewEnvelopeViewApi>();
    return new Promise<() => CustomDashboardViewEnvelopeViewApi>((res) => {
      ReactDOM.render(
        <CustomDashboardViewEnvelopeView
          ref={ref}
          channelApi={envelope.channelApi}
        />,
        args.container,
        () => res(() => ref.current)
      );
    });
  };

  const context: CustomDashboardViewEnvelopeContext = {};
  return envelope.start(envelopeViewDelegate, context, {
    create: (apiFactoryArgs) =>
      new CustomDashboardViewEnvelopeApiImpl(apiFactoryArgs)
  });
}

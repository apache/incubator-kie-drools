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

import { Envelope, EnvelopeApiFactory } from '@kogito-tooling/envelope';
import {
  RuntimeToolsDevUIChannelApi,
  RuntimeToolsDevUIEnvelopeApi
} from '../api';
import { RuntimeToolsDevUIEnvelopeViewApi } from './RuntimeToolsDevUIEnvelopeViewApi';
import { RuntimeToolsDevUIEnvelopeView } from './RuntimeToolsDevUIEnvelopeView';
import {
  RuntimeToolsDevUIEnvelopeContext,
  RuntimeToolsDevUIEnvelopeContextType
} from './RuntimeToolsDevUIEnvelopeContext';
import ReactDOM from 'react-dom';
import React from 'react';

export class RuntimeToolsDevUIEnvelope {
  constructor(
    private readonly envelope: Envelope<
      RuntimeToolsDevUIEnvelopeApi,
      RuntimeToolsDevUIChannelApi,
      RuntimeToolsDevUIEnvelopeViewApi,
      RuntimeToolsDevUIEnvelopeContextType
    >,
    private readonly envelopeApiFactory: EnvelopeApiFactory<
      RuntimeToolsDevUIEnvelopeApi,
      RuntimeToolsDevUIChannelApi,
      RuntimeToolsDevUIEnvelopeViewApi,
      RuntimeToolsDevUIEnvelopeContextType
    >,
    private readonly context: RuntimeToolsDevUIEnvelopeContextType = {
      channelApi: envelope.channelApi
    }
  ) {}

  public start(container: HTMLElement) {
    return this.envelope.start(
      () => this.renderView(container),
      this.context,
      this.envelopeApiFactory
    );
  }

  private renderView(container: HTMLElement) {
    const runtimeToolsDevUIEnvelopeViewRef = React.createRef<
      RuntimeToolsDevUIEnvelopeViewApi
    >();

    const app = () => {
      return (
        <RuntimeToolsDevUIEnvelopeContext.Provider value={this.context}>
          <RuntimeToolsDevUIEnvelopeView
            ref={runtimeToolsDevUIEnvelopeViewRef}
          />
        </RuntimeToolsDevUIEnvelopeContext.Provider>
      );
    };

    return new Promise<() => RuntimeToolsDevUIEnvelopeViewApi>(res => {
      setTimeout(() => {
        ReactDOM.render(app(), container, () => {
          res(() => runtimeToolsDevUIEnvelopeViewRef.current!);
        });
      }, 0);
    });
  }
}

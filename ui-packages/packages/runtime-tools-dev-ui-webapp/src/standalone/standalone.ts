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

import devUIEnvelopeIndex from '!!raw-loader!../../resources/iframe.html';
import { EnvelopeServer } from '@kogito-tooling/envelope-bus/dist/channel';
import {
  RuntimeToolsDevUIChannelApi,
  RuntimeToolsDevUIEnvelopeApi,
  User
} from '../api';
import { RuntimeToolsDevUIChannelApiImpl } from '../standalone/RuntimeToolsDevUIChannelApiImpl';

export interface StandaloneDevUIApi {
  close: () => void;
}

export interface Consoles {
  open: (args: {
    container: Element;
    users: User[];
    dataIndexUrl: string;
    page: string;
    origin?: string;
  }) => StandaloneDevUIApi;
}

declare global {
  interface Window {
    RuntimeToolsDevUI: Consoles;
  }
}

export const createDevUI = (
  envelopeServer: EnvelopeServer<
    RuntimeToolsDevUIChannelApi,
    RuntimeToolsDevUIEnvelopeApi
  >,
  listener: (message: MessageEvent) => void,
  iframe: HTMLIFrameElement
) => {
  return {
    envelopeApi: envelopeServer.envelopeApi,
    close: () => {
      window.removeEventListener('message', listener);
      iframe.remove();
    }
  };
};

const createEnvelopeServer = (
  iframe: HTMLIFrameElement,
  users: User[],
  dataIndexUrl: string,
  page: string,
  origin?: string
) => {
  const defaultOrigin =
    window.location.protocol === 'file:' ? '*' : window.location.origin;
  return new EnvelopeServer<
    RuntimeToolsDevUIChannelApi,
    RuntimeToolsDevUIEnvelopeApi
  >(
    { postMessage: message => iframe.contentWindow?.postMessage(message, '*') },
    origin ?? defaultOrigin,
    self => {
      return self.envelopeApi.requests.runtimeToolsDevUI_initRequest(
        {
          origin: self.origin,
          envelopeServerId: self.id
        },
        {
          users,
          dataIndexUrl,
          page
        }
      );
    }
  );
};

export function open(args: {
  container: Element;
  users: User[];
  dataIndexUrl: string;
  page: string;
  origin?: string;
}): StandaloneDevUIApi {
  const iframe = document.createElement('iframe');
  iframe.srcdoc = devUIEnvelopeIndex; // index coming from webapp
  iframe.id = 'iframe';
  iframe.style.width = '100%';
  iframe.style.height = '100%';
  iframe.style.border = 'none';

  const envelopeServer = createEnvelopeServer(
    iframe,
    args.users,
    args.dataIndexUrl,
    args.page,
    args.origin
  );

  const listener = (message: MessageEvent) => {
    envelopeServer.receive(message.data, new RuntimeToolsDevUIChannelApiImpl());
  };
  window.addEventListener('message', listener);

  args.container.appendChild(iframe);
  envelopeServer.startInitPolling();

  return createDevUI(envelopeServer, listener, iframe);
}

window.RuntimeToolsDevUI = { open };

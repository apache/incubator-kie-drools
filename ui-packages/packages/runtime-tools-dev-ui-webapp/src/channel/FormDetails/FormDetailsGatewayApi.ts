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
export interface OnOpenFormDetailsListener {
  onOpen(name: string): void;
}

export interface FormDetailsUnSubscribeHandler {
  unSubscribe: () => void;
}

export interface FormDetailsGatewayApi {
  openFormDetails(name: string): Promise<void>;
  onOpenFormDetailsListener: (
    listener: OnOpenFormDetailsListener
  ) => FormDetailsUnSubscribeHandler;
}

export class FormDetailsGatewayApiImpl implements FormDetailsGatewayApi {
  //@ts-ignore
  private readonly queries: FormDetailsQueries;
  private readonly listeners: OnOpenFormDetailsListener[] = [];

  openFormDetails(name: string): Promise<void> {
    this.listeners.forEach(listener => listener.onOpen(name));
    return Promise.resolve();
  }

  onOpenFormDetailsListener(
    listener: OnOpenFormDetailsListener
  ): FormDetailsUnSubscribeHandler {
    this.listeners.push(listener);

    const unSubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };

    return {
      unSubscribe
    };
  }
}

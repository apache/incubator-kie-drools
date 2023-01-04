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

import React, { useCallback, useMemo } from 'react';
import {
  FormDisplayerApi,
  FormDisplayerChannelApi,
  FormDisplayerEnvelopeApi,
  Form,
  FormDisplayerInitArgs,
  FormOpened
} from '../api';
import { ContainerType } from '@kogito-tooling/envelope/dist/api';
import { EnvelopeServer } from '@kogito-tooling/envelope-bus/dist/channel';
import { EmbeddedEnvelopeFactory } from '@kogito-tooling/envelope/dist/embedded';

export type Props = {
  targetOrigin: string;
  formContent: Form;
  data?: any;
  context?: Record<string, any>;
  envelopePath: string;
  onOpenForm?: (opened: FormOpened) => void;
};

export const EmbeddedFormDisplayer = React.forwardRef<FormDisplayerApi, Props>(
  (props, forwardedRef) => {
    const pollInit = useCallback(
      (
        // eslint-disable-next-line
        envelopeServer: EnvelopeServer<
          FormDisplayerChannelApi,
          FormDisplayerEnvelopeApi
        >
      ) => {
        return envelopeServer.envelopeApi.requests.formDisplayer__init(
          {
            origin: envelopeServer.origin,
            envelopeServerId: envelopeServer.id
          },
          {
            form: props.formContent,
            data: props.data ?? {},
            context: props.context ?? {}
          }
        );
      },
      []
    );

    const refDelegate = useCallback(
      (
        envelopeServer: EnvelopeServer<
          FormDisplayerChannelApi,
          FormDisplayerEnvelopeApi
        >
      ): FormDisplayerApi => ({
        startSubmit: (context) => {
          return envelopeServer.envelopeApi.requests.formDisplayer__startSubmit(
            context
          );
        },
        notifySubmitResult: (response) => {
          envelopeServer.envelopeApi.notifications.formDisplayer__notifySubmitResponse(
            response
          );
        },
        init: (args: FormDisplayerInitArgs) => {
          envelopeServer.envelopeApi.notifications.formDisplayer__notifyInit(
            args
          );
        }
      }),
      []
    );

    const EmbeddedEnvelope = useMemo(() => {
      return EmbeddedEnvelopeFactory({
        api: {
          notifyOnOpenForm: (opened) => {
            if (props.onOpenForm) {
              props.onOpenForm(opened);
            }
          }
        },
        origin: props.targetOrigin,
        refDelegate,
        pollInit,
        config: {
          containerType: ContainerType.IFRAME,
          envelopePath: props.envelopePath
        }
      });
    }, []);

    return <EmbeddedEnvelope ref={forwardedRef} />;
  }
);

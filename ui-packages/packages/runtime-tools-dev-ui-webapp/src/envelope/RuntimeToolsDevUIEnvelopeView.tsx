/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useImperativeHandle } from 'react';
import '@patternfly/patternfly/patternfly.css';
import { RuntimeToolsDevUIEnvelopeViewApi } from './RuntimeToolsDevUIEnvelopeViewApi';
import RuntimeTools from '../components/DevUI/RuntimeTools/RuntimeTools';
import { User } from '@kogito-apps/consoles-common';

export const RuntimeToolsDevUIEnvelopeView = React.forwardRef<
  RuntimeToolsDevUIEnvelopeViewApi
>((props, forwardingRef) => {
  const [dataIndex, setDataIndex] = React.useState('');
  const [DevUiUsers, setDevUiUsers] = React.useState<User[]>([]);
  const [navigate, setNavigate] = React.useState<string>('');
  const [devUIUrl, setDevUIUrl] = React.useState<string>('');
  const [openApiPath, setOpenApiPath] = React.useState<string>('');

  useImperativeHandle(
    forwardingRef,
    () => {
      return {
        setDataIndexUrl: dataIndexUrl => {
          setDataIndex(dataIndexUrl);
        },
        setUsers: users => {
          setDevUiUsers(users);
        },
        navigateTo: page => {
          setNavigate(page);
        },
        setDevUIUrl: url => {
          setDevUIUrl(url);
        },
        setOpenApiPath: path => {
          setOpenApiPath(path);
        }
      };
    },
    []
  );
  return (
    <>
      {dataIndex.length > 0 &&
        navigate.length > 0 &&
        devUIUrl.length > 0 &&
        openApiPath.length > 0 && (
          <RuntimeTools
            users={DevUiUsers}
            dataIndex={dataIndex}
            navigate={navigate}
            openApiPath={openApiPath}
            devUIUrl={devUIUrl}
          />
        )}
    </>
  );
});

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

import { ApolloClient } from 'apollo-client';

jest.mock('apollo-link-http');

const renderMock = jest.fn();
jest.mock('react-dom', () => ({ render: renderMock }));

const rootDiv = document.createElement('div');
global.document.getElementById = id => id === 'root' && rootDiv;
process.env.KOGITO_DATAINDEX_HTTP_URL = 'http://localhost:8180';

describe('Index test', () => {
  it('regular rendering', () => {
    require('../index.tsx');

    expect(renderMock).toBeCalled();
    expect(renderMock.mock.calls.length).toBe(1);

    const callArguments = renderMock.mock.calls[0];

    const app = callArguments[0];

    expect(app).not.toBeNull();
    expect(app.props).not.toBeNull();
    expect(app.props.apolloClient).toBeInstanceOf(ApolloClient);
    expect(app.props.userContext).not.toBeNull();

    expect(callArguments[1]).toBe(rootDiv);
  });
});

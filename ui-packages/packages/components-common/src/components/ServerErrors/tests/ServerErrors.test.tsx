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
import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { ServerErrors } from '../ServerErrors';

const errorMessage1 =
  '{\n' +
  '  "graphQLErrors": [],\n' +
  '  "networkError": {\n' +
  '    "name": "ServerError",\n' +
  '    "response": {},\n' +
  '    "statusCode": 500,\n' +
  '    "result": {\n' +
  '      "details": "Error id 51742367-8b20-48c0-9fd6-29774e8256f6-2, java.lang.RuntimeException: network error",\n' +
  '      "stack": "java.lang.RuntimeException: network error\\n\\tat org.kie.kogito.index.vertx.BlockingGraphqlRouterProducer.apolloWSHandler(BlockingGraphqlRouterProducer.java:51)\\n\\tat org.kie.kogito.index.vertx.BlockingGraphqlRouterProducer_RouteHandler_apolloWSHandler_08a09272f869f08e6baa194b6ae486ffb063912a.invoke(Unknown Source)\\n\\tat io.quarkus.vertx.web.runtime.RouteHandler.handle(RouteHandler.java:97)\\n\\tat io.quarkus.vertx.web.runtime.RouteHandler.handle(RouteHandler.java:22)\\n\\tat io.vertx.ext.web.impl.BlockingHandlerDecorator.lambda$handle$0(BlockingHandlerDecorator.java:48)\\n\\tat io.vertx.core.impl.ContextBase.lambda$null$0(ContextBase.java:137)\\n\\tat io.vertx.core.impl.ContextInternal.dispatch(ContextInternal.java:264)\\n\\tat io.vertx.core.impl.ContextBase.lambda$executeBlocking$1(ContextBase.java:135)\\n\\tat org.jboss.threads.ContextHandler$1.runWith(ContextHandler.java:18)\\n\\tat org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2449)\\n\\tat org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1478)\\n\\tat org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:29)\\n\\tat org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:29)\\n\\tat io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)\\n\\tat java.base/java.lang.Thread.run(Thread.java:829)"\n' +
  '    }\n' +
  '  },\n' +
  '  "message": "Network error: Response not successful: Received status code 500"\n' +
  '}';

const errorMessage2 =
  '{\n' +
  '  "graphQLErrors": [],\n' +
  '  "networkError": {\n' +
  '    "name": "ServerError" \n' +
  '  },\n' +
  '  "message": "Network error: Response not successful: Received status code 500"\n' +
  '}';

const errorMessage3 =
  '{\n' +
  '  "graphQLErrors": [],\n' +
  ' "networkError": {},\n' +
  ' "message": "Network error: Failed to fetch" \n' +
  '}';

const props = {
  error: errorMessage1,
  variant: 'large'
};

const props2 = {
  error: errorMessage2,
  variant: 'small'
};

const props3 = {
  error: 'error occured',
  variant: 'small'
};

const props4 = {
  error: errorMessage3,
  variant: 'small'
};

describe('ServerErrors component tests', () => {
  it('snapshot testing ', () => {
    const { container } = render(<ServerErrors {...props} />);
    expect(screen.queryByTestId('empty-state-body')?.textContent).toContain(
      'An error occurred while accessing data. It is possible the data index is still being loaded, please try again in a few moments. See more details'
    );

    expect(container).toMatchSnapshot();
  });

  it('snapshot with children ', () => {
    const onClickMock = jest.fn();

    const { container } = render(
      <ServerErrors {...props}>
        <Button onClick={onClickMock}>Go back</Button>
      </ServerErrors>
    );

    expect(container).toMatchSnapshot();
    expect(screen.getByText('Go back')).toBeTruthy();

    fireEvent.click(screen.getByText('Go back'));
    expect(onClickMock).toHaveBeenCalled();
  });

  it('display error button click ', async () => {
    const { container } = render(<ServerErrors {...props} />);

    fireEvent.click(screen.getByTestId('display-error'));
    const result = await container.querySelector('pre')?.textContent;

    expect(JSON.parse(result!).name).toEqual('ServerError');
    expect(JSON.parse(result!).statusCode).toEqual(500);
  });

  it('snapshot testing with small variant ', () => {
    const { container } = render(<ServerErrors {...props2} />);

    expect(container).toMatchSnapshot();
  });

  it('display error button click with small variant ', () => {
    const { container } = render(<ServerErrors {...props2} />);

    fireEvent.click(screen.getByTestId('display-error'));
    const result = container.querySelector('pre')?.textContent;

    expect(JSON.parse(result!).name).toEqual('ServerError');
  });

  it('display error button click with small variant and not full error message ', () => {
    const { container } = render(<ServerErrors {...props3} />);
    expect(screen.getByTestId('empty-state-body').textContent).toEqual(
      'An error occurred while accessing data. See more details'
    );

    fireEvent.click(screen.getByTestId('display-error'));
    const result = container.querySelector('pre')?.textContent;

    expect(result).toEqual('error occured');
  });

  it('display error title ', () => {
    const { container } = render(<ServerErrors {...props4} />);
    expect(screen.getByTestId('empty-state-body').textContent).toEqual(
      'An error occurred while accessing data. It is possible the data index is still being loaded, please try again in a few moments. See more details'
    );

    fireEvent.click(screen.getByTestId('display-error'));
    expect(container.querySelector('pre')?.textContent).toContain(
      'Network error: Failed to fetch'
    );
  });
});

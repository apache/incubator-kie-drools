/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import CloudEventCustomHeadersEditor, {
  CloudEventCustomHeadersEditorApi
} from '../CloudEventCustomHeadersEditor';

jest.mock('uuid', () => {
  let count = 0;
  return () => count++;
});

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons/dist/esm/icons/plus-circle-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    PlusCircleIcon: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@patternfly/react-icons/dist/esm/icons/trash-icon', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    TrashIcon: () => {
      return <MockedComponent />;
    }
  })
);

function addHeader(
  container,
  index: number,
  header?: string,
  headerValue?: string
) {
  const addButton = screen.getByText('Add Header');

  expect(addButton).toBeTruthy();

  act(() => {
    fireEvent.click(addButton);
  });

  const headerField = container.querySelector(
    `[id = header-key-${index}-input]`
  );
  expect(headerField).toBeTruthy();
  const valueField = container.querySelector(
    `[id = header-value-${index}-input]`
  );
  expect(valueField).toBeTruthy();
  const deleteButton = screen.getAllByLabelText('delete')[index];
  expect(deleteButton).toBeTruthy();

  fireEvent.change(screen.getAllByTestId('update-key')[index], {
    target: { value: header }
  });
  fireEvent.change(screen.getAllByTestId('update-value')[index], {
    target: { value: headerValue }
  });
  return container;
}

function deleteHeader(container, index: number) {
  const deleteButton = screen.getAllByLabelText('delete')[index];
  expect(deleteButton).toBeTruthy();

  act(() => {
    fireEvent.click(deleteButton);
  });

  return container;
}

describe('CloudEventCustomHeadersEditor tests', () => {
  it('Snapshot - empty', () => {
    let container;
    act(() => {
      container = render(<CloudEventCustomHeadersEditor />).container;
    });

    expect(container).toMatchSnapshot();

    expect(screen.getByText('Add Header')).toBeTruthy();
  });

  it('Add headers', () => {
    const editorApiRef = React.createRef<CloudEventCustomHeadersEditorApi>();
    let { container } = render(
      <CloudEventCustomHeadersEditor ref={editorApiRef} />
    );

    expect(screen.getByText('Add Header')).toBeTruthy();

    container = addHeader(container, 0, 'key', 'value');

    expect(container).toMatchSnapshot();

    expect(container.querySelectorAll('input')).toHaveLength(2);

    container = addHeader(container, 1, 'key2', 'value2');
    addHeader(container, 2);

    const result = editorApiRef.current.getCustomHeaders();

    expect(Object.keys(result)).toHaveLength(2);
    expect(result).toHaveProperty('key', 'value');
    expect(result).toHaveProperty('key2', 'value2');
  });

  it('Remove headers', () => {
    const editorApiRef = React.createRef<CloudEventCustomHeadersEditorApi>();
    let { container } = render(
      <CloudEventCustomHeadersEditor ref={editorApiRef} />
    );

    container = addHeader(container, 0, 'key', 'value');
    container = addHeader(container, 1, 'key2', 'value2');
    container = addHeader(container, 2, 'key3', 'value3');

    let result = editorApiRef.current.getCustomHeaders();

    expect(Object.keys(result)).toHaveLength(3);

    expect(result).toHaveProperty('key', 'value');
    expect(result).toHaveProperty('key2', 'value2');
    expect(result).toHaveProperty('key3', 'value3');

    container = deleteHeader(container, 1);

    result = editorApiRef.current.getCustomHeaders();

    expect(Object.keys(result)).toHaveLength(2);

    expect(result).toHaveProperty('key', 'value');
    expect(result).not.toHaveProperty('key2', 'value2');
    expect(result).toHaveProperty('key3', 'value3');

    container = deleteHeader(container, 0);

    result = editorApiRef.current.getCustomHeaders();

    expect(Object.keys(result)).toHaveLength(1);
    expect(result).not.toHaveProperty('key', 'value');
    expect(result).not.toHaveProperty('key2', 'value2');
    expect(result).toHaveProperty('key3', 'value3');

    result = editorApiRef.current.getCustomHeaders();

    expect(result).not.toHaveProperty('key', 'value');
    expect(result).not.toHaveProperty('key2', 'value2');
  });
});

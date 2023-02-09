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
import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
import CloudEventCustomHeadersEditor, {
  CloudEventCustomHeadersEditorApi
} from '../CloudEventCustomHeadersEditor';
import { Grid, TextInput } from '@patternfly/react-core';

jest.mock('uuid', () => {
  let count = 0;
  return () => count++;
});

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Button: () => {
      return <MockedComponent />;
    },
    TextInput: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    TrashIcon: () => {
      return <MockedComponent />;
    },
    PlusCircleIcon: () => {
      return <MockedComponent />;
    }
  })
);

function addHeader(
  wrapper,
  index: number,
  header?: string,
  headerValue?: string
) {
  const addButton = wrapper.findWhere(
    (child) => child.key() === 'add-header-button'
  );

  expect(addButton.exists()).toBeTruthy();

  act(() => {
    addButton.prop('onClick')(undefined);
  });

  wrapper = wrapper.update();

  const headerField = wrapper.findWhere(
    (child) => child.props().id === `header-key-${index}-input`
  );
  expect(headerField.exists()).toBeTruthy();
  const valueField = wrapper.findWhere(
    (child) => child.props().id === `header-value-${index}-input`
  );
  expect(valueField.exists()).toBeTruthy();
  const deleteButton = wrapper.findWhere(
    (child) => child.key() === `header-delete-${index}-button`
  );
  expect(deleteButton.exists()).toBeTruthy();

  if (header && valueField) {
    act(() => {
      headerField.props().onChange(header);
      valueField.props().onChange(headerValue);
    });
  }

  return wrapper.update();
}

function deleteHeader(wrapper, index: number) {
  const deleteButton = wrapper.findWhere(
    (child) => child.key() === `header-delete-${index}-button`
  );
  expect(deleteButton.exists()).toBeTruthy();

  act(() => {
    deleteButton.prop('onClick')(undefined);
  });

  return wrapper.update();
}

describe('CloudEventCustomHeadersEditor tests', () => {
  it('Snapshot - empty', () => {
    const wrapper = mount(<CloudEventCustomHeadersEditor />);

    expect(wrapper).toMatchSnapshot();

    expect(
      wrapper.findWhere((child) => child.key() === 'add-header-button').exists()
    ).toBeTruthy();
    expect(wrapper.find(Grid).exists()).toBeFalsy();
  });

  it('Add headers', () => {
    const editorApiRef = React.createRef<CloudEventCustomHeadersEditorApi>();
    let wrapper = mount(<CloudEventCustomHeadersEditor ref={editorApiRef} />);

    expect(
      wrapper.findWhere((child) => child.key() === 'add-header-button').exists()
    ).toBeTruthy();
    expect(wrapper.find(Grid).exists()).toBeFalsy();

    wrapper = addHeader(wrapper, 0, 'key', 'value');

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(Grid).exists()).toBeTruthy();
    expect(wrapper.find(TextInput)).toHaveLength(2);

    wrapper = addHeader(wrapper, 1, 'key2', 'value2');
    addHeader(wrapper, 2);

    const result = editorApiRef.current.getCustomHeaders();

    expect(Object.keys(result)).toHaveLength(2);
    expect(result).toHaveProperty('key', 'value');
    expect(result).toHaveProperty('key2', 'value2');
  });

  it('Remove headers', () => {
    const editorApiRef = React.createRef<CloudEventCustomHeadersEditorApi>();
    let wrapper = mount(<CloudEventCustomHeadersEditor ref={editorApiRef} />);

    wrapper = addHeader(wrapper, 0, 'key', 'value');
    wrapper = addHeader(wrapper, 1, 'key2', 'value2');
    wrapper = addHeader(wrapper, 2, 'key3', 'value3');

    let result = editorApiRef.current.getCustomHeaders();

    expect(Object.keys(result)).toHaveLength(3);
    expect(result).toHaveProperty('key', 'value');
    expect(result).toHaveProperty('key2', 'value2');
    expect(result).toHaveProperty('key3', 'value3');

    wrapper = deleteHeader(wrapper, 1);

    result = editorApiRef.current.getCustomHeaders();

    expect(Object.keys(result)).toHaveLength(2);
    expect(result).toHaveProperty('key', 'value');
    expect(result).not.toHaveProperty('key2', 'value2');
    expect(result).toHaveProperty('key3', 'value3');

    wrapper = deleteHeader(wrapper, 0);

    result = editorApiRef.current.getCustomHeaders();

    expect(Object.keys(result)).toHaveLength(1);
    expect(result).not.toHaveProperty('key', 'value');
    expect(result).not.toHaveProperty('key2', 'value2');
    expect(result).toHaveProperty('key3', 'value3');

    wrapper = deleteHeader(wrapper, 0);

    result = editorApiRef.current.getCustomHeaders();

    expect(Object.keys(result)).toHaveLength(0);
    expect(result).not.toHaveProperty('key', 'value');
    expect(result).not.toHaveProperty('key2', 'value2');
    expect(result).not.toHaveProperty('key3', 'value3');

    expect(wrapper.find(Grid).exists()).toBeFalsy();
  });
});

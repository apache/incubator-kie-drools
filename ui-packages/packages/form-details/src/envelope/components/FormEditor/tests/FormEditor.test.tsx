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
import { mount } from 'enzyme';
import FormEditor from '../FormEditor';
import RuntimeToolsFormDetailsContext, {
  FormDetailsContextImpl
} from '../../contexts/FormDetailsContext';
import { act } from 'react-dom/test-utils';
import FormDetailsContextProvider from '../../contexts/FormDetailsContextProvider';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('@patternfly/react-code-editor/dist/js/components/CodeEditor', () =>
  Object.assign(jest.requireActual('@patternfly/react-code-editor'), {
    CodeEditor: () => {
      return <MockedComponent />;
    },
    Language: {
      json: 'json'
    }
  })
);

const formContent = {
  formInfo: {
    name: 'form1',
    type: 'HTML' as any,
    lastModified: new Date('2020-07-11T18:30:00.000Z')
  },
  source: '<div><span>1</span></div>',
  configuration: {
    resources: {
      styles: {},
      scripts: {}
    },
    schema: 'json schema'
  }
};
const changedValue = '<React.FC><div><span>2</span></div></React.FC>';
const monaco = {
  languages: {
    typescript: {
      typescriptDefaults: {
        setCompilerOptions: jest.fn(),
        setDiagnosticsOptions: jest.fn()
      }
    }
  },
  KeyMod: {
    CtrlCmd: 2048
  },
  KeyCode: {
    49: 'KEY_S'
  }
};

const editor = {
  addCommand: jest.fn(),
  getValue: () => changedValue,
  focus: jest.fn(),
  trigger: jest.fn()
};

describe('FormEditor test', () => {
  it('render source - html', () => {
    const props = {
      code: '<div><span>1</span></div>',
      isSource: true,
      formType: 'html',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn()
    };

    const wrapper = mount(<FormEditor {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('render source - tsx', async () => {
    const props = {
      code: '<React.FC><div><span>1</span></div></React.FC>',
      isSource: true,
      formType: 'tsx',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn()
    };
    const wrapper = mount(
      <FormDetailsContextProvider>
        <FormEditor {...props} />
      </FormDetailsContextProvider>
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('Test execute option - source content', async () => {
    const props = {
      code: '<React.FC><div><span>1</span></div></React.FC>',
      isSource: true,
      formType: 'tsx',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn()
    };
    const wrapper = mount(
      <FormDetailsContextProvider>
        <FormEditor {...props} />
      </FormDetailsContextProvider>
    );
    await act(async () => {
      wrapper.find('CodeEditor').props()['onEditorDidMount'](editor, monaco);
    });
    wrapper.update();
    const childs = wrapper.find('CodeEditor').props()['customControls']
      .props.children;
    childs[0].props.onClick();
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });

  it('Test execute option - configuration', async () => {
    const props = {
      code: '<React.FC><div><span>1</span></div></React.FC>',
      isSource: false,
      formType: 'tsx',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn()
    };
    const changedValue = `{
      "styles": {
        "bootstrap.min.css": " https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
      },
      "scripts": {
        "bootstrap.min.js": "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js",
        "jquery.js": "https://code.jquery.com/jquery-3.2.1.slim.min.js",
        "popper.js": "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
      }
    }`;

    const editorData = {
      addCommand: jest.fn(),
      getValue: () => changedValue,
      focus: jest.fn(),
      trigger: jest.fn()
    };

    const wrapper = mount(
      <FormDetailsContextProvider>
        <FormEditor {...props} />
      </FormDetailsContextProvider>
    );
    await act(async () => {
      wrapper
        .find('CodeEditor')
        .props()
        ['onEditorDidMount'](editorData, monaco);
    });
    wrapper.update();
    const childs = wrapper.find('CodeEditor').props()['customControls']
      .props.children;
    childs[0].props.onClick();
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });

  it('Test save option', async () => {
    const props = {
      code: '<React.FC><div><span>1</span></div></React.FC>',
      isSource: true,
      formType: 'tsx',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn()
    };
    const wrapper = mount(
      <FormDetailsContextProvider>
        <FormEditor {...props} />
      </FormDetailsContextProvider>
    );
    await act(async () => {
      wrapper.find('CodeEditor').props()['onEditorDidMount'](editor, monaco);
    });
    wrapper.update();
    const childs = wrapper.find('CodeEditor').props()['customControls']
      .props.children;
    childs[3].props.onClick();
    wrapper.update();
    expect(wrapper).toMatchSnapshot();
  });

  it('Test undo option', async () => {
    const props = {
      code: '<React.FC><div><span>1</span></div></React.FC>',
      isSource: true,
      formType: 'tsx',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn()
    };
    const wrapper = mount(
      <FormDetailsContextProvider>
        <FormEditor {...props} />
      </FormDetailsContextProvider>
    );
    await act(async () => {
      wrapper.find('CodeEditor').props()['onEditorDidMount'](editor, monaco);
    });
    wrapper.update();
    const childs = wrapper.find('CodeEditor').props()['customControls']
      .props.children;
    childs[1].props.onClick();
    wrapper.update();
    expect(wrapper.find('CodeEditor').props()['code']).toEqual(
      '<React.FC><div><span>1</span></div></React.FC>'
    );
  });

  it('Test redo option', async () => {
    const props = {
      code: '<React.FC><div><span>1</span></div></React.FC>',
      isSource: true,
      formType: 'tsx',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn()
    };
    const wrapper = mount(
      <FormDetailsContextProvider>
        <FormEditor {...props} />
      </FormDetailsContextProvider>
    );

    await act(async () => {
      wrapper.find('CodeEditor').props()['onEditorDidMount'](editor, monaco);
    });
    wrapper.update();
    const childs = wrapper.find('CodeEditor').props()['customControls']
      .props.children;
    childs[2].props.onClick();
    wrapper.update();
    expect(wrapper.find('CodeEditor').props()['code']).toEqual(
      '<React.FC><div><span>1</span></div></React.FC>'
    );
  });

  it('render config', () => {
    const props = {
      code: JSON.stringify({
        1: '1',
        2: '2'
      }),
      isConfig: true,
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn()
    };
    const wrapper = mount(<FormEditor {...props} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('call refresh', () => {
    const props = {
      code: JSON.stringify({
        1: '1',
        2: '2'
      }),
      isConfig: true,
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn()
    };
    const wrapper = mount(
      <RuntimeToolsFormDetailsContext.Provider
        value={new FormDetailsContextImpl()}
      >
        <FormEditor {...props} />
      </RuntimeToolsFormDetailsContext.Provider>
    );
    expect(wrapper).toMatchSnapshot();
  });
});

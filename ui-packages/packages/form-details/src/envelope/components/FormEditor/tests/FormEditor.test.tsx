import React from 'react';
import { mount } from 'enzyme';
import FormEditor from '../FormEditor';
import RuntimeToolsFormDetailsContext, {
  FormDetailsContextImpl
} from '../../contexts/FormDetailsContext';
import { act } from 'react-dom/test-utils';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('@patternfly/react-code-editor', () => ({
  ...jest.requireActual('@patternfly/react-code-editor'),
  CodeEditor: () => {
    return <MockedComponent />;
  }
}));

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

const contentChange = {
  formInfo: {
    name: 'form1',
    type: 'HTML' as any,
    lastModified: new Date('2020-07-11T18:30:00.000Z')
  },
  source: '<div><span>1</span><span>2</span></div>',
  configuration: {
    resources: {
      styles: {},
      scripts: {}
    },
    schema: 'json schema'
  }
};
describe('FormEditor test', () => {
  it('render source - html', () => {
    const props = {
      code: '<div><span>1</span></div>',
      isSource: true,
      formType: 'html',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn(),
      contentChange: contentChange,
      setContentChange: jest.fn()
    };

    const wrapper = mount(<FormEditor {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('render source - tsx', () => {
    const props = {
      code: '<React.FC><div><span>1</span></div></React.FC>',
      isSource: true,
      formType: 'tsx',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn(),
      contentChange: contentChange,
      setContentChange: jest.fn()
    };
    const wrapper = mount(<FormEditor {...props} />);
    expect(wrapper).toMatchSnapshot();
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
      saveFormContent: jest.fn(),
      contentChange: contentChange,
      setContentChange: jest.fn()
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
      saveFormContent: jest.fn(),
      contentChange: contentChange,
      setContentChange: jest.fn()
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

  it('call handleChange - source', async () => {
    const props = {
      code: '<React.FC><div><span>1</span></div></React.FC>',
      isSource: true,
      formType: 'tsx',
      formContent: formContent,
      setFormContent: jest.fn(),
      saveFormContent: jest.fn(),
      contentChange: contentChange,
      setContentChange: jest.fn()
    };
    const wrapperWithSource = mount(<FormEditor {...props} />);
    await act(async () => {
      wrapperWithSource
        .find('CodeEditor')
        .props()
        ['onChange']({} as any);
    });
    expect(props.formContent).toBeDefined();
  });
});

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

import React, { useImperativeHandle, useState } from 'react';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import {
  CodeEditor,
  CodeEditorControl,
  Language
} from '@patternfly/react-code-editor';
import { PlayIcon, RedoIcon, UndoIcon } from '@patternfly/react-icons';
import { Form } from '../../../api';
import { useFormDetailsContext } from '../contexts/FormDetailsContext';
import { ResizableContent } from '../FormDetails/FormDetails';
import '../styles.css';
export interface FormEditorProps {
  formType?: string;
  isSource?: boolean;
  isConfig?: boolean;
  formContent: Form;
  contentChange: Form;
  code: string;
  setFormContent: (formContent: Form) => void;
  setContentChange: (contentChange: Form) => void;
}

export const FormEditor = React.forwardRef<
  ResizableContent,
  FormEditorProps & OUIAProps
>(
  (
    {
      code,
      formType,
      formContent,
      setFormContent,
      contentChange,
      setContentChange,
      isSource = false,
      isConfig = false,
      ouiaId,
      ouiaSafe
    },
    forwardedRef
  ) => {
    const appContext = useFormDetailsContext();

    const [monacoEditor, setMonacoEditor] = useState<any>();

    useImperativeHandle(
      forwardedRef,
      () => {
        return {
          doResize() {
            monacoEditor.layout();
          }
        };
      },
      [monacoEditor]
    );

    const getFormLanguage = (): Language => {
      if (isSource && formType) {
        switch (formType.toLowerCase()) {
          case 'tsx':
            return Language.typescript;
          case 'html':
            return Language.html;
        }
        /* istanbul ignore else */
      } else if (isConfig) {
        return Language.json;
      }
    };

    const editorDidMount = (editor, monaco): void => {
      if (isSource && formType.toLowerCase() === 'tsx') {
        monaco.languages.typescript.typescriptDefaults.setCompilerOptions({
          jsx: 'react'
        });

        monaco.languages.typescript.typescriptDefaults.setDiagnosticsOptions({
          noSemanticValidation: false,
          noSyntaxValidation: false
        });
      }
      setMonacoEditor(editor);
    };

    const handleChange = (value): void => {
      if (Object.keys(formContent)[0].length > 0 && isSource) {
        const temp: Form = formContent;
        temp.source['source-content'] = value;
        setContentChange({ ...formContent, ...temp });
      } else {
        const temp: Form = formContent;
        temp.formConfiguration['resources'] = JSON.parse(value);
        setContentChange({ ...formContent, ...temp });
      }
    };

    const onExecuteCode = (): void => {
      appContext.updateContent(contentChange);
      setFormContent(contentChange);
    };

    const onUndoChanges = (): void => {
      if (monacoEditor !== null) {
        monacoEditor.focus();
        document.execCommand('undo');
      }
    };

    const onRedoChanges = (): void => {
      if (monacoEditor !== null) {
        monacoEditor.focus();
        document.execCommand('redo');
      }
    };

    const customControl = (
      <>
        <CodeEditorControl
          icon={<PlayIcon className="pf-global--primary-color--100" />}
          aria-label="Execute code"
          toolTipText="Execute code"
          onClick={onExecuteCode}
          isVisible={contentChange && Object.keys(contentChange)[0].length > 0}
        />
        <CodeEditorControl
          icon={<UndoIcon className="pf-global--primary-color--100" />}
          aria-label="Undo changes"
          toolTipText="Undo changes"
          onClick={onUndoChanges}
          isVisible={contentChange && Object.keys(contentChange)[0].length > 0}
        />
        <CodeEditorControl
          icon={<RedoIcon className="pf-global--primary-color--100" />}
          aria-label="Redo changes"
          toolTipText="Redo changes"
          onClick={onRedoChanges}
          isVisible={contentChange && Object.keys(contentChange)[0].length > 0}
        />
      </>
    );

    return (
      <div {...componentOuiaProps(ouiaId, 'form-view', ouiaSafe)}>
        <CodeEditor
          isCopyEnabled
          isDarkTheme={false}
          isLineNumbersVisible={true}
          isReadOnly={false}
          isMinimapVisible={false}
          isLanguageLabelVisible
          customControls={customControl}
          code={code}
          language={getFormLanguage()}
          height="800px"
          onEditorDidMount={editorDidMount}
          onChange={handleChange}
        />
      </div>
    );
  }
);

export default FormEditor;

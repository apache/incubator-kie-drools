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
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import {
  CodeEditor,
  CodeEditorControl,
  Language
} from '@patternfly/react-code-editor/dist/js/components/CodeEditor';
import { UndoIcon } from '@patternfly/react-icons/dist/js/icons/undo-icon';
import { SaveIcon } from '@patternfly/react-icons/dist/js/icons/save-icon';
import { RedoIcon } from '@patternfly/react-icons/dist/js/icons/redo-icon';
import { PlayIcon } from '@patternfly/react-icons/dist/js/icons/play-icon';
import { Form } from '../../../api';
import { useFormDetailsContext } from '../contexts/FormDetailsContext';
import { ResizableContent } from '../FormDetails/FormDetails';
import cloneDeep from 'lodash/cloneDeep';
import '../styles.css';

export interface FormEditorProps {
  formType?: string;
  isSource?: boolean;
  isConfig?: boolean;
  formContent: Form;
  code: string;
  setFormContent: (formContent: Form) => void;
  saveFormContent: (formContent: Form) => void;
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
      saveFormContent,
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
      /* istanbul ignore else */
      if (isSource && formType.toLowerCase() === 'tsx') {
        monaco.languages.typescript.typescriptDefaults.setCompilerOptions({
          jsx: 'react'
        });

        monaco.languages.typescript.typescriptDefaults.setDiagnosticsOptions({
          noSemanticValidation: false,
          noSyntaxValidation: false
        });
      }
      editor.addCommand(
        monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_S,
        function () {
          onSaveForm();
        }
      );
      setMonacoEditor(editor);
    };

    const onExecuteCode = (): void => {
      const tempContent: Form = cloneDeep(formContent);
      const value = monacoEditor.getValue();
      if (Object.keys(formContent)[0].length > 0 && isSource) {
        tempContent.source = value;
      } else {
        tempContent.configuration['resources'] = JSON.parse(value);
      }
      const content = { ...formContent, ...tempContent };
      appContext.updateContent(content);
      setFormContent(content);
    };

    const onSaveForm = (): void => {
      saveFormContent(formContent);
    };

    const onUndoChanges = (): void => {
      /* istanbul ignore else */
      if (monacoEditor !== null) {
        monacoEditor.focus();
        monacoEditor.trigger('whatever...', 'undo');
      }
    };

    const onRedoChanges = (): void => {
      /* istanbul ignore else */
      if (monacoEditor !== null) {
        monacoEditor.focus();
        monacoEditor.trigger('whatever...', 'redo');
      }
    };

    const customControl = (
      <>
        <CodeEditorControl
          icon={<PlayIcon />}
          aria-label="Execute form"
          toolTipText="Execute form"
          onClick={onExecuteCode}
        />
        <CodeEditorControl
          icon={<UndoIcon />}
          aria-label="Undo changes"
          toolTipText="Undo changes"
          onClick={onUndoChanges}
        />
        <CodeEditorControl
          icon={<RedoIcon />}
          aria-label="Redo changes"
          toolTipText="Redo changes"
          onClick={onRedoChanges}
        />
        <CodeEditorControl
          icon={<SaveIcon />}
          aria-label="Save form"
          toolTipText="Save form"
          onClick={() => onSaveForm()}
        />
      </>
    );

    return (
      <div {...componentOuiaProps(ouiaId, 'form-view', ouiaSafe)}>
        <CodeEditor
          isDarkTheme={false}
          isLineNumbersVisible={true}
          isReadOnly={false}
          isCopyEnabled={true}
          isMinimapVisible={false}
          isLanguageLabelVisible
          customControls={customControl}
          code={code}
          language={getFormLanguage()}
          height="700px"
          onEditorDidMount={editorDidMount}
        />
      </div>
    );
  }
);

export default FormEditor;

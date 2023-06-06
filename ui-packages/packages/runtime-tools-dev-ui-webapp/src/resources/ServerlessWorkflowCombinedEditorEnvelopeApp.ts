/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import * as EditorEnvelope from '@kie-tools-core/editor/dist/envelope';
import { NoOpKeyboardShortcutsService } from '@kie-tools-core/keyboard-shortcuts/dist/envelope';
import { ServerlessWorkflowCombinedEditorChannelApi } from '@kie-tools/serverless-workflow-combined-editor/dist/api';
import { ServerlessWorkflowCombinedEditorEnvelopeApi } from '@kie-tools/serverless-workflow-combined-editor/dist/api/ServerlessWorkflowCombinedEditorEnvelopeApi';
import {
  ServerlessWorkflowCombinedEditorApi,
  ServerlessWorkflowCombinedEditorFactory
} from '@kie-tools/serverless-workflow-combined-editor/dist/editor';
import { ServerlessWorkflowCombinedEditorEnvelopeApiImpl } from '@kie-tools/serverless-workflow-combined-editor/dist/impl';

EditorEnvelope.initCustom<
  ServerlessWorkflowCombinedEditorApi,
  ServerlessWorkflowCombinedEditorEnvelopeApi,
  ServerlessWorkflowCombinedEditorChannelApi
>({
  container: document.getElementById('swf-combined-editor-envelope-app')!,
  bus: {
    postMessage: (message, _targetOrigin, _) =>
      window.parent.postMessage(message, window.location.origin, _)
  },
  apiImplFactory: {
    create: (args) =>
      new ServerlessWorkflowCombinedEditorEnvelopeApiImpl(
        args,
        new ServerlessWorkflowCombinedEditorFactory()
      )
  },
  keyboardShortcutsService: new NoOpKeyboardShortcutsService()
});

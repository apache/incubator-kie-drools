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

import React, { useEffect } from 'react';
import { FormResources } from '../../../api';
import { renderResources } from '../../../utils';

interface HtmlFormRendererProps {
  source: any;
  resources: FormResources;
}

const HtmlFormRenderer: React.FC<HtmlFormRendererProps> = ({
  source,
  resources
}) => {
  useEffect(() => {
    if (source && resources) {
      renderResources('formContainer', resources);
    }
  }, [resources]);

  return (
    <div id="formContainer">
      <div dangerouslySetInnerHTML={{ __html: source }} />
    </div>
  );
};

export default HtmlFormRenderer;

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
import {
  EmbeddedFormDisplayer,
  FormDisplayerApi
} from '@kogito-apps/form-displayer';
import { Form } from '../../../api';
import { FormInfo } from '@kogito-apps/forms-list';
import { useFormDetailsContext } from '../../components/contexts/FormDetailsContext';

interface FormDisplayerContainerProps {
  formContent: Form;
  formData: FormInfo;
}

const FormDisplayerContainer: React.FC<FormDisplayerContainerProps> = ({
  formContent,
  formData
}) => {
  const appContext = useFormDetailsContext();
  const formDisplayerApiRef = React.useRef<FormDisplayerApi>();

  useEffect(() => {
    const unsubscribeUserChange = appContext.onUpdateContent({
      onUpdateContent(formContent) {
        formDisplayerApiRef.current.formDisplayer__notify(formContent);
      }
    });
    return () => {
      unsubscribeUserChange.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedFormDisplayer
      targetOrigin={window.location.origin}
      envelopePath={'resources/form-displayer.html'}
      formContent={formContent}
      formData={formData}
      ref={formDisplayerApiRef}
    />
  );
};

export default FormDisplayerContainer;

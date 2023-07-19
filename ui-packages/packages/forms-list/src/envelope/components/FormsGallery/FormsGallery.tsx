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

import React from 'react';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import {
  Gallery,
  GalleryItem
} from '@patternfly/react-core/dist/js/layouts/Gallery';
import { FormInfo } from '../../../api/FormsListEnvelopeApi';
import FormCard from '../FormCard/FormCard';
import { FormsListDriver } from '../../../api/FormsListDriver';
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner
} from '@kogito-apps/components-common';

export interface FormsGalleryProps {
  driver: FormsListDriver;
  formsData: FormInfo[];
  isLoading: boolean;
}

const FormsGallery: React.FC<FormsGalleryProps & OUIAProps> = ({
  driver,
  formsData,
  isLoading,
  ouiaId,
  ouiaSafe
}) => {
  if (isLoading) {
    return (
      <Bullseye>
        <KogitoSpinner
          spinnerText="Loading forms..."
          ouiaId="forms-list-loading-forms"
        />
      </Bullseye>
    );
  }

  if (!isLoading && formsData && formsData.length === 0) {
    return (
      <KogitoEmptyState
        type={KogitoEmptyStateType.Search}
        title="No results found"
        body="Try using different filters"
      />
    );
  }

  return (
    <Gallery
      hasGutter
      style={{ margin: '25px' }}
      {...componentOuiaProps(ouiaId, 'forms-gallery', ouiaSafe)}
    >
      {formsData &&
        formsData.map((formData, index) => (
          <GalleryItem key={index}>
            <FormCard
              formData={formData}
              key={index}
              driver={driver}
            ></FormCard>
          </GalleryItem>
        ))}
    </Gallery>
  );
};

export default FormsGallery;

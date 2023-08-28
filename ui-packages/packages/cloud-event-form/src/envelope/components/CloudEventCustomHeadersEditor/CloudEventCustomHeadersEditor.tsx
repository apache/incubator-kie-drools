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

import React, {
  useCallback,
  useEffect,
  useImperativeHandle,
  useState
} from 'react';
import uuidv4 from 'uuid';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { Grid, GridItem } from '@patternfly/react-core/dist/js/layouts/Grid';
import { Stack, StackItem } from '@patternfly/react-core/dist/js/layouts/Stack';
import { TextInput } from '@patternfly/react-core/dist/js/components/TextInput';
import { PlusCircleIcon } from '@patternfly/react-icons/dist/esm/icons/plus-circle-icon';
import { TrashIcon } from '@patternfly/react-icons/dist/esm/icons/trash-icon';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

export interface CloudEventCustomHeadersEditorApi {
  reset(): void;
  getCustomHeaders(): Record<string, string>;
}

type CustomHeaderEntry = {
  uuid: string;
  key: string;
  value: string;
};

const CloudEventCustomHeadersEditor = React.forwardRef<
  CloudEventCustomHeadersEditorApi,
  OUIAProps
>(({ ouiaId, ouiaSafe }, forwardedRef) => {
  const [headers, setHeaders] = useState<CustomHeaderEntry[]>([]);
  const [isNewHeader, setIsNewHeader] = useState<boolean>(false);

  useEffect(() => {
    setIsNewHeader(false);
  }, [isNewHeader]);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        reset(): void {
          setHeaders([]);
          setIsNewHeader(false);
        },
        getCustomHeaders(): Record<string, string> {
          const result = {};
          headers
            .filter((entry) => entry.key && entry.value)
            .forEach((entry) => {
              result[entry.key] = entry.value;
            });
          return result;
        }
      };
    },
    [headers]
  );

  const addNewHeader = useCallback(() => {
    const headersCopy = [...headers];
    headersCopy.push({
      uuid: uuidv4(),
      key: '',
      value: ''
    });
    setHeaders(headersCopy);
    setIsNewHeader(true);
  }, [headers]);

  const deleteHeader = useCallback(
    (index: number) => {
      const headersCopy = [...headers];
      headersCopy.splice(index, 1);
      setHeaders(headersCopy);
    },
    [headers]
  );

  const updateHeaderKey = useCallback(
    (index: number, value: string) => {
      const headersCopy = [...headers];
      headersCopy[index].key = value;
      setHeaders(headersCopy);
    },
    [headers]
  );

  const updateHeaderValue = useCallback(
    (index: number, value: string) => {
      const headersCopy = [...headers];
      headersCopy[index].value = value;
      setHeaders(headersCopy);
    },
    [headers]
  );

  return (
    <div {...componentOuiaProps(ouiaId, 'custom-headers-editor', ouiaSafe)}>
      <Stack hasGutter>
        <StackItem>
          <Button
            key={'add-header-button'}
            variant="link"
            isInline
            icon={<PlusCircleIcon />}
            onClick={() => addNewHeader()}
          >
            Add Header
          </Button>
        </StackItem>
        {headers.length > 0 && (
          <StackItem>
            <Grid
              {...componentOuiaProps(
                /* istanbul ignore next */
                (ouiaId ? ouiaId : 'custom-headers-editor') + '-grid',
                'custom-headers-editor',
                true
              )}
            >
              <GridItem span={4} key={'headers-grid-col-header'}>
                <h4>Header Name</h4>
              </GridItem>
              <GridItem span={8} key={'headers-grid-col-value'}>
                <h4>Value</h4>
              </GridItem>
              {headers.map((header, index) => {
                return (
                  <React.Fragment key={`headers-grid-row-${header.uuid}`}>
                    <GridItem span={4} key={`header-key-${header.uuid}`}>
                      <TextInput
                        id={`header-key-${index}-input`}
                        value={header.key}
                        onChange={(value) => updateHeaderKey(index, value)}
                        autoFocus={isNewHeader && index === headers.length - 1}
                        data-testid="update-key"
                      />
                    </GridItem>
                    <GridItem span={7} key={`header-value-${header.uuid}`}>
                      <TextInput
                        id={`header-value-${index}-input`}
                        value={header.value}
                        onChange={(value) => updateHeaderValue(index, value)}
                        data-testid="update-value"
                      />
                    </GridItem>
                    <GridItem span={1} key={`header-delete-${header.uuid}`}>
                      <Button
                        variant="plain"
                        aria-label="delete"
                        key={`header-delete-${index}-button`}
                        onClick={() => deleteHeader(index)}
                      >
                        <TrashIcon />
                      </Button>
                    </GridItem>
                  </React.Fragment>
                );
              })}
            </Grid>
          </StackItem>
        )}
      </Stack>
    </div>
  );
});

export default CloudEventCustomHeadersEditor;

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
import React, { useEffect, useState } from 'react';
import {
  Button,
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  Split,
  SplitItem,
  Title
} from '@patternfly/react-core';
import { v4 as uuid } from 'uuid';
import SkeletonDataList from '../../Molecules/SkeletonDataList/SkeletonDataList';
import FormattedValue from '../../Atoms/FormattedValue/FormattedValue';
import SkeletonFlexStripes from '../../Molecules/SkeletonFlexStripes/SkeletonFlexStripes';
import { OutlinedMehIcon } from '@patternfly/react-icons';
import {
  InputRow,
  ItemObject,
  ItemObjectValue,
  RemoteData,
  RemoteDataStatus
} from '../../../types';
import './InputDataBrowser.scss';

type InputDataBrowserProps = {
  inputData: RemoteData<Error, ItemObject[]>;
};

const InputDataBrowser = ({ inputData }: InputDataBrowserProps) => {
  const [inputs, setInputs] = useState<ItemObject[] | null>(null);
  const [categories, setCategories] = useState<string[]>([]);
  const [viewSection, setViewSection] = useState<number>(0);

  const handleSectionSwitch = (index: number) => {
    setViewSection(index);
  };

  useEffect(() => {
    if (
      inputData.status === RemoteDataStatus.SUCCESS &&
      inputData.data.length > 0
    ) {
      const items: ItemObject[] = [];
      const categoryList = [];
      const rootSection: ItemObject = {
        name: 'Root',
        value: { kind: 'STRUCTURE', type: 'root', value: {} }
      };
      for (const item of inputData.data) {
        if (item.value.kind === 'UNIT') {
          // collecting inputs with values at root level (not containing components)
          rootSection.value.value[item.name] = item.value;
        } else {
          items.push(item);
          categoryList.push(item.name);
        }
      }
      if (Object.entries(rootSection.value.value).length !== 0) {
        // if the root section is not empty, than add the root section as first one
        items.unshift(rootSection);
        categoryList.unshift('Root');
      }
      setInputs(items);
      setCategories(categoryList);
      // open the fist section as default
      setViewSection(0);
    }
  }, [inputData]);

  return (
    <div className="input-browser">
      {inputData.status === RemoteDataStatus.LOADING && (
        <>
          <div className="input-browser__section-list">
            <SkeletonFlexStripes
              stripesNumber={6}
              stripesWidth={'100px'}
              stripesHeight={'1.5em'}
            />
          </div>
          <SkeletonDataList rowsCount={4} colsCount={6} hasHeader={true} />
        </>
      )}
      {inputData.status === RemoteDataStatus.SUCCESS &&
        inputData.data.length > 0 && (
          <>
            <div className="input-browser__section-list">
              <Split>
                <SplitItem>
                  <span className="input-browser__section-list__label">
                    Browse sections
                  </span>
                </SplitItem>
                <SplitItem>
                  {categories.map((item, index) => (
                    <Button
                      type={'button'}
                      variant={index === viewSection ? 'primary' : 'control'}
                      isActive={index === viewSection}
                      key={`section-${index}`}
                      onClick={() => handleSectionSwitch(index)}
                    >
                      {item}
                    </Button>
                  ))}
                </SplitItem>
              </Split>
            </div>
            <DataList
              aria-label="Input Data"
              className="input-browser__data-list"
            >
              <DataListItem
                aria-labelledby="header"
                key="header"
                className="input-browser__header"
              >
                <DataListItemRow>
                  <DataListItemCells
                    dataListCells={[
                      <DataListCell width={3} key="Input Data">
                        <span>Input data</span>
                      </DataListCell>,
                      <DataListCell width={3} key="Value">
                        <span>Value</span>
                      </DataListCell>,
                      <DataListCell width={3} key="Distribution">
                        <></>
                      </DataListCell>
                    ]}
                  />
                </DataListItemRow>
              </DataListItem>
              {inputs &&
                renderItem(inputs[viewSection].name, inputs[viewSection].value)}
            </DataList>
          </>
        )}
      {inputData.status === RemoteDataStatus.SUCCESS &&
        inputData.data.length === 0 && (
          <EmptyState variant={EmptyStateVariant.full}>
            <EmptyStateIcon icon={OutlinedMehIcon} />
            <Title headingLevel="h5" size="lg">
              No input data
            </Title>
            <EmptyStateBody>No inputs available to display.</EmptyStateBody>
          </EmptyState>
        )}
    </div>
  );
};

const ItemsSubList = (props: {
  name: string;
  value: ItemObjectValue;
  listCategory: string;
}) => {
  const { name, value, listCategory } = props;

  return (
    <DataListItem aria-labelledby="" className={'category__sublist'}>
      <DataList aria-label="" className={'category__sublist__item'}>
        {value.kind === 'UNIT' && (
          <InputValue
            inputLabel={name}
            inputValue={value}
            key={name}
            category={listCategory}
          />
        )}
        {value.kind === 'STRUCTURE' &&
          Object.entries(value.value).map(([key, value]) => {
            return (
              <InputValue
                key={key}
                inputLabel={key}
                inputValue={value}
                category={listCategory}
              />
            );
          })}
      </DataList>
    </DataListItem>
  );
};

const CategoryLine = (props: { categoryLabel: string }) => {
  const { categoryLabel } = props;
  const categoryKey = categoryLabel.replace(' ', '').toLocaleLowerCase();
  return (
    <DataListItem
      aria-labelledby={categoryLabel}
      key={'category-' + categoryKey}
      className="category__heading"
    >
      <DataListItemRow>
        <DataListItemCells
          dataListCells={[
            <DataListCell key={categoryLabel}>
              <span>{categoryLabel}</span>
            </DataListCell>
          ]}
        />
      </DataListItemRow>
    </DataListItem>
  );
};

const InputValue = (props: InputRow) => {
  const { inputValue, inputLabel, category } = props;
  const effectItemClass = 'input-data--ignored';
  const dataListCells = [];
  dataListCells.push(
    <DataListCell width={3} key="primary content" className="input-data__wrap">
      <span>{inputLabel}</span>
      <span className="input-data__wrap__desc">{category}</span>
    </DataListCell>
  );
  dataListCells.push(
    <DataListCell width={3} key="secondary content">
      <span>
        <FormattedValue value={inputValue.value} />
      </span>
    </DataListCell>
  );
  dataListCells.push(
    <DataListCell width={3} key="distribution">
      <span />
    </DataListCell>
  );

  return (
    <DataListItem
      aria-labelledby={'Input columns'}
      key={`input-item-heading`}
      className={`input-data__item ${effectItemClass}`}
    >
      <DataListItemRow>
        <DataListItemCells dataListCells={dataListCells} />
      </DataListItemRow>
    </DataListItem>
  );
};

let itemCategory = '';

const renderItem = (
  name: string,
  value: ItemObjectValue,
  categoryName?: string
): JSX.Element => {
  const renderedItems: JSX.Element[] = [];

  const elaborateRender = (
    name: string,
    value: ItemObjectValue,
    category?: string
  ): JSX.Element => {
    if (value.kind === 'UNIT') {
      return (
        <InputValue
          inputLabel={name}
          inputValue={value}
          category={itemCategory}
          key={name}
        />
      );
    }

    if (value.kind === 'STRUCTURE' || value.kind === 'COLLECTION') {
      itemCategory = category ? `${itemCategory} / ${category}` : name;
      const categoryLabel = itemCategory.length > 0 ? `${itemCategory}` : name;

      if (value.kind === 'STRUCTURE') {
        Object.entries(value.value).forEach(([key, value]) => {
          renderedItems.push(renderItem(key, value, key));
        });
      } else if (value.kind === 'COLLECTION') {
        value.value.forEach((value) => {
          renderedItems.push(
            <ItemsSubList
              name={name}
              value={value}
              key={uuid()}
              listCategory={categoryLabel}
            />
          );
        });
      }

      return (
        <React.Fragment key={categoryLabel}>
          <div className="category">
            <CategoryLine
              categoryLabel={categoryLabel}
              key={`category-${categoryLabel}`}
            />
          </div>
          {renderedItems}
        </React.Fragment>
      );
    }
    return <></>;
  };

  return elaborateRender(name, value, categoryName);
};

export default InputDataBrowser;

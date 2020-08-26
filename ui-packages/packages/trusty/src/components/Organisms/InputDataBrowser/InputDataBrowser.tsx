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
  isItemObjectArray,
  isItemObjectMultiArray,
  RemoteData
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
    if (inputData.status === 'SUCCESS' && inputData.data.length > 0) {
      const items: ItemObject[] = [];
      const categoryList = [];
      const rootSection: ItemObject = {
        name: 'Root',
        typeRef: 'root',
        value: null,
        components: []
      };
      for (const item of inputData.data) {
        if (item.value) {
          // collecting inputs with values at root level (not containing components)
          rootSection.components!.push(item);
        } else {
          items.push(item);
          categoryList.push(item.name);
        }
      }
      if (rootSection.components!.length) {
        // if the root section as something inside it, than add the root section as first one
        items.unshift(rootSection);
        categoryList.unshift('Root');
      }
      setInputs(items);
      setCategories(categoryList);
      // open the fist section as default
      setViewSection(0);
    }
  }, [inputData.status]);

  return (
    <div className="input-browser">
      {inputData.status === 'LOADING' && (
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
      {inputData.status === 'SUCCESS' && inputData.data.length > 0 && (
        <>
          <div className="input-browser__section-list">
            <Split>
              <SplitItem>
                <span className="input-browser__section-list__label">
                  Browse Sections
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
                      <span>Input Data</span>
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
            {inputs && renderItem(inputs[viewSection])}
          </DataList>
        </>
      )}
      {inputData.status === 'SUCCESS' && inputData.data.length === 0 && (
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
  itemsList: ItemObject[];
  listCategory: string;
}) => {
  const { itemsList, listCategory } = props;

  return (
    <DataListItem aria-labelledby="" className={'category__sublist'}>
      <DataList aria-label="" className={'category__sublist__item'}>
        {itemsList.map(item => (
          <InputValue
            inputLabel={item.name}
            inputValue={item.value}
            hasEffect={item.impact}
            score={item.score}
            key={item.name}
            category={listCategory}
          />
        ))}
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
  const { inputValue, inputLabel, category, hasEffect } = props;
  const effectItemClass = hasEffect
    ? 'input-data--affecting'
    : 'input-data--ignored';
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
        <FormattedValue value={inputValue} />
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
  singleItem: ItemObject,
  categoryName?: string
): JSX.Element => {
  const renderedItems: JSX.Element[] = [];

  const elaborateRender = (
    item: ItemObject,
    category?: string
  ): JSX.Element => {
    if (item.value !== null) {
      return (
        <InputValue
          inputLabel={item.name}
          inputValue={item.value}
          hasEffect={item.impact}
          score={item.score}
          category={itemCategory}
          key={item.name}
        />
      );
    }

    if (item.components.length) {
      itemCategory = category ? `${itemCategory} / ${category}` : item.name;
      const categoryLabel =
        itemCategory.length > 0 ? `${itemCategory}` : item.name;

      if (item.components) {
        if (isItemObjectArray(item.components)) {
          for (const subItem of item.components) {
            renderedItems.push(renderItem(subItem, subItem.name));
          }
        } else if (isItemObjectMultiArray(item.components)) {
          for (const subItem of item.components) {
            renderedItems.push(
              <ItemsSubList
                itemsList={subItem}
                key={uuid()}
                listCategory={categoryLabel}
              />
            );
          }
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
    }
    return <></>;
  };

  return elaborateRender(singleItem, categoryName);
};

export default InputDataBrowser;

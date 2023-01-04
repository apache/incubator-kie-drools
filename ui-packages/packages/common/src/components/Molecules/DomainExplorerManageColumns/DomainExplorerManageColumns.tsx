import React, { useState } from 'react';
import {
  Button,
  DataList,
  DataListCheck,
  DataListItem,
  DataListItemRow,
  DataListCell,
  DataListItemCells,
  DataListToggle,
  DataListContent,
  Dropdown,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
  DropdownToggleCheckbox,
  Modal,
  Text,
  TextContent,
  TextVariants
} from '@patternfly/react-core';
import { SyncIcon } from '@patternfly/react-icons';
import '../../styles.css';
import _ from 'lodash';
import { filterColumnSelection, removeDuplicates } from '../../../utils/Utils';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';

interface ResponseType {
  loading: boolean;
  data: any;
}
export interface IOwnProps {
  columnPickerType: string;
  getQueryTypes: ResponseType;
  setParameters: (
    parameters:
      | ((parameter: Record<string, unknown>[]) => Record<string, unknown>[])
      | Record<string, unknown>[]
  ) => void;
  selected: string[];
  setSelected: (selected: ((selected: string[]) => string[]) | []) => void;
  data: Record<string, unknown>[];
  getPicker: ResponseType;
  setOffsetVal: (offsetVal: number) => void;
  setPageSize: (pageSize: number) => void;
  metaData: Record<string, unknown>;
  setIsModalOpen: (isModalOpen: boolean) => void;
  isModalOpen: boolean;
  setRunQuery: (runQuery: boolean) => void;
  setEnableRefresh: (enableRefresh: boolean) => void;
  enableRefresh: boolean;
}

const DomainExplorerManageColumns: React.FC<IOwnProps & OUIAProps> = ({
  columnPickerType,
  data,
  enableRefresh,
  getPicker,
  getQueryTypes,
  isModalOpen,
  metaData,
  selected,
  setEnableRefresh,
  setIsModalOpen,
  setOffsetVal,
  setParameters,
  setPageSize,
  setRunQuery,
  setSelected,
  ouiaId,
  ouiaSafe
}) => {
  // tslint:disable: forin
  // tslint:disable: no-floating-promises
  const [expanded, setExpanded] = useState([]);
  const [isDropDownOpen, setIsDropDownOpen] = useState(false);
  const allSelections: any = [];

  const nullTypes = [null, 'String', 'Boolean', 'Int', 'DateTime'];

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const handleChange = (checked, event) => {
    const target = event.target;
    const selection = target.name;
    const selectionArray = target.name
      .split('/')
      .map((item) => item.charAt(0).toLowerCase() + item.slice(1));
    setEnableRefresh(false);
    if (selected.includes(selection)) {
      setSelected((prevState) =>
        prevState.filter((item) => item !== selection)
      );
      const objValue = selectionArray.pop();
      const rest = filterColumnSelection(selectionArray, objValue);
      setParameters((prevState) =>
        prevState.filter((obj) => {
          if (!_.isEqual(obj, rest)) {
            return obj;
          }
        })
      );
    } else {
      setSelected((prevState) => [...prevState, selection]);
      const objValue = selectionArray.pop();
      const rest = filterColumnSelection(selectionArray, objValue);
      setParameters((prevState) => [...prevState, rest]);
    }
  };

  const tempExpanded = [];
  const toggle = (id) => {
    const index = expanded.indexOf(id);
    const newExpanded =
      index >= 0
        ? [
            ...expanded.slice(0, index),
            ...expanded.slice(index + 1, expanded.length)
          ]
        : [...expanded, id];
    tempExpanded.push(newExpanded);
    setExpanded(newExpanded);
  };

  const fetchSchema = (option) => {
    return (
      !getQueryTypes.loading &&
      getQueryTypes.data.__schema &&
      getQueryTypes.data.__schema.queryType.find((item) => {
        if (item.name === option.type.name) {
          return item;
        }
      })
    );
  };
  const rootElements = [];
  let finalResult: any = [];
  let childItems;

  const childSelectionItems = (_data, title, ...attr) => {
    let nestedTitles = '';
    childItems =
      !getQueryTypes.loading &&
      _data.map((group, index) => {
        const label = title + '/' + attr.join();
        const childEle = (
          <DataListItem
            aria-labelledby={'kie-datalist-item-' + label.replace(/,/g, '')}
            isExpanded={expanded.includes(label.replace(/,/g, ''))}
            key={'kie-datalist-item-' + label.replace(/,/g, '')}
          >
            <DataListItemRow>
              <DataListToggle
                onClick={() => toggle(label.replace(/,/g, ''))}
                isExpanded={expanded.includes(label.replace(/,/g, ''))}
                id={'kie-datalist-toggle-' + label.replace(/,/g, '')}
                aria-controls={'kie-datalist-toggle-' + label.replace(/,/g, '')}
              />
              <DataListItemCells
                dataListCells={[
                  <DataListCell
                    id={'kie-datalist-item-' + label.replace(/,/g, '')}
                    key={index}
                  >
                    {(title + ' / ' + attr.join()).replace(/,/g, '')}
                  </DataListCell>
                ]}
              />
            </DataListItemRow>
            {group.fields
              .filter((item) => {
                if (!nullTypes.includes(item.type.name)) {
                  const tempData = [];
                  const n = fetchSchema(item);
                  tempData.push(n);
                  nestedTitles = nestedTitles + ' / ' + item.name;
                  childSelectionItems(tempData, title, attr, nestedTitles);
                } else {
                  return item;
                }
              })
              .map((item, _index) => {
                const itemName = title + '/' + group.name + '/' + item.name;
                allSelections.push(itemName);
                return (
                  <DataListContent
                    aria-label={itemName}
                    id={'kie-datalist-content-' + itemName}
                    isHidden={!expanded.includes(label.replace(/,/g, ''))}
                    className="kogito-common--manage-columns__data-list-content"
                    key={itemName + _index}
                  >
                    <DataListItemRow>
                      <DataListCheck
                        aria-labelledby={'kie-datalist-item-' + itemName}
                        name={itemName}
                        checked={selected.includes(itemName)}
                        onChange={handleChange}
                      />
                      <DataListItemCells
                        dataListCells={[
                          <DataListCell
                            id={'kie-datalist-item-' + itemName}
                            key={_index}
                          >
                            {item.name}
                          </DataListCell>
                        ]}
                      />
                    </DataListItemRow>
                  </DataListContent>
                );
              })}
          </DataListItem>
        );
        return childEle;
      });
    finalResult.push(childItems);
  };

  const selectionItems = (_data) => {
    !getPicker.loading &&
      _data
        .filter((group, index) => {
          if (group.type.kind !== 'SCALAR') {
            return group;
          } else {
            allSelections.push(group.name);
            const rootEle = (
              <DataListItem
                aria-labelledby=""
                key={'kie-datalist-item-' + group.name}
              >
                <DataListItemRow>
                  <DataListCheck
                    aria-labelledby={'kie-datalist-item-' + group.name}
                    name={group.name}
                    checked={selected.includes(group.name)}
                    onChange={handleChange}
                  />
                  <DataListItemCells
                    dataListCells={[
                      <DataListCell
                        id={'kie-datalist-item-' + group.name}
                        key={index}
                      >
                        {group.name}
                      </DataListCell>
                    ]}
                  />
                </DataListItemRow>
              </DataListItem>
            );
            rootElements.push(rootEle);
          }
        })
        .map((group, index) => {
          const nestedEle = (
            <DataListItem
              aria-labelledby={'kie-datalist-item-' + group.name}
              isExpanded={expanded.includes(group.name)}
              id={'kie-datalist-item-' + group.name}
              key={'kie-datalist-item-' + group.name}
            >
              <DataListItemRow>
                <DataListToggle
                  onClick={() => toggle(group.name)}
                  isExpanded={expanded.includes(group.name)}
                  id={'kie-datalist-toggle-' + group.name}
                  aria-controls={'kie-datalist-toggle-' + group.name}
                />
                <DataListItemCells
                  dataListCells={[
                    <DataListCell
                      id={'kie-datalist-item-cell-' + group.name}
                      key={index}
                    >
                      {group.name}
                    </DataListCell>
                  ]}
                />
              </DataListItemRow>
              {group.type.fields &&
                group.type.fields
                  .filter((item) => {
                    if (!nullTypes.includes(item.type.name)) {
                      const tempData = [];
                      const _v = fetchSchema(item);
                      tempData.push(_v);
                      childSelectionItems(tempData, group.name, item.name);
                    } else {
                      /* istanbul ignore else */
                      if (item.type.kind !== 'LIST') {
                        return item;
                      }
                    }
                  })
                  .map((item, _index) => {
                    const itemName = group.name + '/' + item.name;
                    allSelections.push(itemName);
                    return (
                      <DataListContent
                        aria-label={itemName}
                        id={'kie-datalist-content-' + itemName}
                        isHidden={!expanded.includes(group.name)}
                        key={_index}
                        className="kogito-common--manage-columns__data-list-content"
                      >
                        <DataListItemRow>
                          <DataListCheck
                            aria-labelledby={'kie-datalist-item-' + itemName}
                            name={itemName}
                            checked={selected.includes(itemName)}
                            onChange={handleChange}
                          />
                          <DataListItemCells
                            dataListCells={[
                              <DataListCell
                                id={'kie-datalist-item-' + itemName}
                                key={_index}
                              >
                                {item.name}
                              </DataListCell>
                            ]}
                          />
                        </DataListItemRow>
                      </DataListContent>
                    );
                  })}
            </DataListItem>
          );
          !finalResult.includes(nestedEle) && finalResult.push(nestedEle);
        });
  };
  columnPickerType && selectionItems(data);

  finalResult = finalResult.flat();
  finalResult.unshift(rootElements);

  const renderModal = () => {
    const numSelected = selected.length;
    const allSelected = numSelected === allSelections.length;
    const anySelected = numSelected > 0;
    const someChecked = anySelected ? null : false;
    const isChecked = allSelected ? true : someChecked;

    const onDropDownToggle = (isOpen) => {
      setIsDropDownOpen(isOpen);
    };

    const onDropDownSelect = () => {
      setIsDropDownOpen(!isDropDownOpen);
    };

    const handleSelectClickNone = () => {
      setSelected([]);
      setParameters([metaData]);
    };

    const handleSelectClickAll = () => {
      setSelected(allSelections);
      const selectionArray = allSelections.map((ele) =>
        ele
          .split('/')
          .map((item) => item.charAt(0).toLowerCase() + item.slice(1))
      );
      const finalObj = [];
      selectionArray.forEach((arr) => {
        const objValue = arr.pop();
        const rest = filterColumnSelection(arr, objValue);
        finalObj.push(rest);
      });
      finalObj.push(metaData);
      setParameters(finalObj);
    };

    const items = [
      <DropdownItem key={0} onClick={() => handleSelectClickNone()}>
        Select none
      </DropdownItem>,
      <DropdownItem key={1} onClick={() => handleSelectClickAll()}>
        Select all
      </DropdownItem>
    ];
    const bulkSelection = (
      <Dropdown
        onSelect={onDropDownSelect}
        position={DropdownPosition.left}
        id="selectAll-dropdown"
        toggle={
          <DropdownToggle
            splitButtonItems={[
              <DropdownToggleCheckbox
                id="selectAll-dropdown-checkbox"
                key={1}
                aria-label={anySelected ? 'Deselect all' : 'Select all'}
                isChecked={isChecked}
                onClick={() => {
                  anySelected
                    ? handleSelectClickNone()
                    : handleSelectClickAll();
                }}
              />
            ]}
            onToggle={onDropDownToggle}
          >
            {numSelected !== 0 && (
              <React.Fragment>{numSelected} selected</React.Fragment>
            )}
          </DropdownToggle>
        }
        isOpen={isDropDownOpen}
        dropdownItems={items}
      />
    );

    return (
      <Modal
        title="Manage columns"
        isOpen={isModalOpen}
        variant="small"
        description={
          <TextContent>
            <Text component={TextVariants.p}>
              Selected categories will be displayed in the table.
            </Text>
          </TextContent>
        }
        onClose={handleModalToggle}
        className="kogito-common--manage-columns__modal"
        actions={[
          <Button
            key="save"
            variant="primary"
            onClick={() => {
              onRunQuery();
            }}
            id="save-columns"
          >
            Save
          </Button>,
          <Button key="cancel" variant="secondary" onClick={handleModalToggle}>
            Cancel
          </Button>
        ]}
        {...componentOuiaProps(ouiaId, 'manage-columns-modal', ouiaSafe)}
      >
        {bulkSelection}
        <DataList
          aria-label="Table column management"
          id="table-column-management"
          key={1}
          className="kogito-common--manage-columns__data-list"
          isCompact
        >
          {removeDuplicates(finalResult, 'props')}
        </DataList>
      </Modal>
    );
  };

  const onRunQuery = () => {
    setOffsetVal(0);
    setPageSize(10);
    setRunQuery(true);
    setIsModalOpen(!isModalOpen);
  };

  const onRefresh = () => {
    /* istanbul ignore else */
    if (enableRefresh) {
      setOffsetVal(0);
      setPageSize(10);
      setRunQuery(true);
    }
  };
  return (
    <>
      <Button
        variant="link"
        onClick={handleModalToggle}
        id="manage-columns-button"
        ouiaId="manage-columns-button"
      >
        Manage columns
      </Button>
      {renderModal()}
      <Button
        variant="plain"
        onClick={() => {
          onRefresh();
        }}
        id="refresh-button"
        ouiaId="refresh-button"
        aria-label={'Refresh list'}
      >
        <SyncIcon />
      </Button>
    </>
  );
};

export default DomainExplorerManageColumns;

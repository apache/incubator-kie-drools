import React, { useState, useEffect } from 'react';
import {
  DataToolbar,
  DataToolbarContent,
  DataToolbarToggleGroup,
  DataToolbarGroup,
  DataToolbarFilter
} from '@patternfly/react-core/dist/esm/experimental';
import {
  Dropdown,
  DropdownToggle,
  DropdownItem,
  PageSection,
  Breadcrumb,
  BreadcrumbItem
} from '@patternfly/react-core';
import { FilterIcon } from '@patternfly/react-icons';
import { Link } from 'react-router-dom';
import { Redirect } from 'react-router';
import './DomainExplorerDashboard.css';
import DomainExplorerColumnPicker from '../../Organisms/DomainExplorerColumnPicker/DomainExplorerColumnPicker';
import DomainExplorerTable from '../../Organisms/DomainExplorerTable/DomainExplorerTable';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';

import {
  useGetQueryTypesQuery,
  useGetQueryFieldsQuery,
  useGetInputFieldsFromQueryQuery
} from '../../../graphql/types';

export interface IOwnProps {
  domains: any;
}

const DomainExplorerDashboard = props => {
  const domainName = props.match.params.domainName;
  let BreadCrumb = props.location.pathname.split('/');
  BreadCrumb = BreadCrumb.filter(item => {
    if (item !== '') {
      return item;
    }
  });
  const [initData2, setInitData2] = useState<any>({
    __schema: { queryType: [] }
  });
  const [schemaDropdown, setSchemaDropDown] = useState(false);
  const [currentCategory, setCurrentCategory] = useState('Domains');
  const [currentSchema, setCurrentSchema] = useState([]);
  const [currentQuery, setCurrentQuery] = useState('');
  const [columnPickerType, setColumnPickerType] = useState('');
  const [isCategoryDropdownOpen, setIsCategoryDropdownOpen] = useState(false);
  const [columnFilters, setColumnFilters] = useState({});
  const [tableLoading, setTableLoading] = useState(true);
  const [displayTable, setDisplayTable] = useState(false);
  const [selected, setSelected] = useState([]);
  const [parameters, setParameters] = useState([]);

  const temp = [];

  const getQuery = useGetQueryFieldsQuery();

  const getQueryTypes = useGetQueryTypesQuery();

  useEffect(() => {
    setInitData2(getQueryTypes.data);
  }, [getQueryTypes.data]);

  useEffect(() => {
    setParameters([]);
    setSelected([]);
  }, [columnPickerType]);

  useEffect(() => {
    setColumnPickerType(domainName);
    if (getQuery.data) {
      const _a =
        !getQuery.loading &&
        getQuery.data.__type.fields.find(item => {
          if (item.name === domainName) {
            return item;
          }
        });

      setCurrentQuery(_a.args[0].type.name);
    }
  }, []);

  useEffect(() => {
    setDisplayTable(false);
  }, [columnPickerType]);

  const getSchema: any = useGetInputFieldsFromQueryQuery({
    variables: { currentQuery }
  });

  useEffect(() => {
    setCurrentSchema(temp);
  }, [getSchema.data]);

  const renderToolbar = () => {
    return (
      <DataToolbar
        id="data-toolbar-with-chip-groups"
        className="pf-m-toggle-group-container"
        collapseListedFiltersBreakpoint="xl"
      >
        <DataToolbarContent>
          <DataToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
            <DataToolbarGroup>
              {!getSchema.loading && (
                <DomainExplorerColumnPicker
                  columnPickerType={columnPickerType}
                  setColumnFilters={setColumnFilters}
                  setTableLoading={setTableLoading}
                  getQueryTypes={getQueryTypes}
                  setDisplayTable={setDisplayTable}
                  parameters={parameters}
                  setParameters={setParameters}
                  selected={selected}
                  setSelected={setSelected}
                />
              )}
            </DataToolbarGroup>
          </DataToolbarToggleGroup>
        </DataToolbarContent>
      </DataToolbar>
    );
  };

  return (
    <>
      {!props.domains.includes(domainName) && <Redirect to="/ErrorComponent" />}
      <PageSection variant="light">
        <PageTitleComponent title="Domain Explorer" />
        <Breadcrumb>
          <BreadcrumbItem to="/">
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          {BreadCrumb.map((item, index) => {
            if (index === BreadCrumb.length - 1) {
              return <BreadcrumbItem isActive>{item}</BreadcrumbItem>;
            } else {
              return (
                <BreadcrumbItem>
                  <Link to="/DomainExplorer">{item}</Link>{' '}
                </BreadcrumbItem>
              );
            }
          })}
        </Breadcrumb>
      </PageSection>
      <PageSection>
        {renderToolbar()}

        <div className="kogito-management-console--domain-explorer__table-OverFlow">
          <DomainExplorerTable
            columnFilters={columnFilters}
            tableLoading={tableLoading}
            displayTable={displayTable}
          />
        </div>
      </PageSection>
    </>
  );
};

export default DomainExplorerDashboard;

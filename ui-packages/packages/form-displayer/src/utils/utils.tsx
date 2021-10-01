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

const renderTags = (container, resources): void => {
  for (const key in resources.scripts) {
    const script: HTMLScriptElement = document.createElement('script');

    script.src = resources.scripts[key];
    container.appendChild(script);
  }

  for (const key in resources.styles) {
    const link: HTMLLinkElement = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = resources.styles[key];
    container.appendChild(link);
  }
};

export const renderResources = (containerId, resources): void => {
  const container: HTMLElement = document.getElementById(containerId);
  const scripts: HTMLCollectionOf<HTMLScriptElement> = container.getElementsByTagName(
    'script'
  );
  const styles: HTMLCollectionOf<HTMLLinkElement> = container.getElementsByTagName(
    'link'
  );
  if (scripts.length > 0 || styles.length > 0) {
    let scriptIndex: number = scripts.length;
    let styleIndex: number = styles.length;
    while (scriptIndex--) {
      container.removeChild(scripts[scriptIndex]);
    }
    while (styleIndex--) {
      container.removeChild(styles[styleIndex]);
    }
    renderTags(container, resources);
  } else {
    renderTags(container, resources);
  }
};

export const sourceHandler = source => {
  const reactReg = /import React, {[^}]*}.*(?='react').*/gim;
  const patternflyReg = /import {[^}]*}.*(?='@patternfly\/react-core').*/gim;
  const regexvalueReact = new RegExp(reactReg);
  const reactImport = regexvalueReact.exec(source);
  const reg = /\{([^)]+)\}/;
  const reactElements = reg.exec(reactImport[0])[1];
  const regexvaluePat = new RegExp(patternflyReg);
  const patternflyImport = regexvaluePat.exec(source);
  const patternflyElements = reg.exec(patternflyImport[0])[1];
  const trimmedSource = source
    .split(reactReg)
    .join('')
    .trim()
    .split(patternflyReg)
    .join('')
    .trim();
  const tempSource = trimmedSource;
  const formName = tempSource.split(':')[0].split('const ')[1];

  return { reactElements, patternflyElements, formName, trimmedSource };
};

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.
 */

(function () {
  'use strict';

  const API = '/api';
  let allPackages = [];
  let currentView = null;

  async function fetchJson(url, options) {
    const res = await fetch(API + url, options);
    if (!res.ok) {
      const err = await res.json().catch(() => ({ error: res.statusText }));
      throw new Error(err.error || res.statusText);
    }
    return res.json();
  }

  // === Initialization ===

  async function init() {
    try {
      const [summary, packages] = await Promise.all([
        fetchJson('/summary'),
        fetchJson('/packages')
      ]);
      allPackages = packages;
      renderSummaryBar(summary);
      renderStatsGrid(summary);
      renderSidebar(packages);
      setupSearch();
    } catch (e) {
      document.getElementById('mainContent').innerHTML =
        '<div class="welcome"><h1>Connection Error</h1>' +
        '<p>Could not connect to the Rule Swagger API. Make sure the server is running.</p>' +
        '<p style="color:var(--red)">' + escapeHtml(e.message) + '</p></div>';
    }
  }

  // === Top bar summary ===

  function renderSummaryBar(summary) {
    document.getElementById('summary').innerHTML =
      stat('Packages', summary.totalPackages) +
      stat('Rules', summary.totalRules) +
      stat('Fact Types', summary.totalFactTypes);
  }

  function stat(label, value) {
    return '<div class="topbar-stat"><span class="topbar-stat-value">' + value + '</span> ' + label + '</div>';
  }

  // === Stats grid (welcome page) ===

  function renderStatsGrid(summary) {
    document.getElementById('statsGrid').innerHTML =
      statCard(summary.totalPackages, 'Packages') +
      statCard(summary.totalRules, 'Rules') +
      statCard(summary.totalFactTypes, 'Fact Types');
  }

  function statCard(value, label) {
    return '<div class="stat-card"><div class="stat-value">' + value +
      '</div><div class="stat-label">' + label + '</div></div>';
  }

  // === Sidebar ===

  function renderSidebar(packages) {
    const container = document.getElementById('sidebarContent');
    container.innerHTML = packages.map(function (pkg) {
      const ruleCount = pkg.rules ? pkg.rules.length : 0;
      const items = (pkg.rules || []).map(function (r) {
        return '<div class="rule-item" data-pkg="' + escapeAttr(pkg.name) +
          '" data-rule="' + escapeAttr(r.name) + '">' + escapeHtml(r.name) + '</div>';
      }).join('');
      return '<div class="pkg-group" data-pkg="' + escapeAttr(pkg.name) + '">' +
        '<div class="pkg-header" data-pkg="' + escapeAttr(pkg.name) + '">' +
        '<span class="pkg-arrow">&#9654;</span>' +
        '<span>' + escapeHtml(shortPkgName(pkg.name)) + '</span>' +
        '<span class="pkg-count">' + ruleCount + '</span>' +
        '</div>' +
        '<div class="pkg-items">' + items + '</div>' +
        '</div>';
    }).join('');

    container.addEventListener('click', function (e) {
      var header = e.target.closest('.pkg-header');
      if (header) {
        togglePackage(header);
        return;
      }
      var ruleItem = e.target.closest('.rule-item');
      if (ruleItem) {
        selectRule(ruleItem.dataset.pkg, ruleItem.dataset.rule);
      }
    });
  }

  function togglePackage(header) {
    var group = header.closest('.pkg-group');
    var items = group.querySelector('.pkg-items');
    var arrow = header.querySelector('.pkg-arrow');
    var isOpen = items.classList.contains('open');

    items.classList.toggle('open');
    arrow.classList.toggle('open');
    header.classList.toggle('active');

    if (!isOpen) {
      showPackageView(header.dataset.pkg);
    }
  }

  function selectRule(pkgName, ruleName) {
    document.querySelectorAll('.rule-item').forEach(function (el) {
      el.classList.remove('active');
    });
    var sel = document.querySelector('.rule-item[data-pkg="' + CSS.escape(pkgName) +
      '"][data-rule="' + CSS.escape(ruleName) + '"]');
    if (sel) sel.classList.add('active');
  }

  function shortPkgName(name) {
    if (!name) return '(default)';
    var parts = name.split('.');
    if (parts.length <= 2) return name;
    return parts.slice(-2).join('.');
  }

  // === Search ===

  function setupSearch() {
    document.getElementById('searchInput').addEventListener('input', function (e) {
      var q = e.target.value.toLowerCase();
      document.querySelectorAll('.pkg-group').forEach(function (group) {
        var pkgMatch = group.dataset.pkg.toLowerCase().indexOf(q) >= 0;
        var anyRuleMatch = false;
        group.querySelectorAll('.rule-item').forEach(function (item) {
          var match = item.dataset.rule.toLowerCase().indexOf(q) >= 0 || pkgMatch;
          item.style.display = match ? '' : 'none';
          if (match) anyRuleMatch = true;
        });
        group.style.display = (pkgMatch || anyRuleMatch) ? '' : 'none';
        if (q && anyRuleMatch) {
          group.querySelector('.pkg-items').classList.add('open');
          group.querySelector('.pkg-arrow').classList.add('open');
        }
      });
    });
  }

  // === Package View ===

  function showPackageView(pkgName) {
    var pkg = allPackages.find(function (p) { return p.name === pkgName; });
    if (!pkg) return;
    currentView = { type: 'package', name: pkgName };

    var html = '<div class="package-view">';
    html += '<div class="package-header"><h2>' + escapeHtml(pkgName || '(default)') + '</h2>';
    html += '<div class="breadcrumb">Package &middot; ' +
      (pkg.rules ? pkg.rules.length : 0) + ' rules &middot; ' +
      (pkg.factTypes ? pkg.factTypes.length : 0) + ' fact types</div></div>';

    // Rules section
    if (pkg.rules && pkg.rules.length > 0) {
      html += '<div class="section"><div class="section-title">Rules</div>';
      pkg.rules.forEach(function (rule, i) {
        html += renderRuleCard(rule, pkgName, i);
      });
      html += '</div>';
    }

    // Fact Types section
    if (pkg.factTypes && pkg.factTypes.length > 0) {
      html += '<div class="section"><div class="section-title">Fact Types</div>';
      pkg.factTypes.forEach(function (ft, i) {
        html += renderFactTypeCard(ft, pkgName, i);
      });
      html += '</div>';
    }

    // Globals section
    if (pkg.globals && pkg.globals.length > 0) {
      html += '<div class="section"><div class="section-title">Globals</div>';
      pkg.globals.forEach(function (g) {
        html += '<div class="rule-card"><div class="rule-card-header">' +
          '<span class="method-badge global">GLB</span>' +
          '<span class="rule-card-name">' + escapeHtml(g.name) + '</span>' +
          '<span class="meta-tag">' + escapeHtml(g.type) + '</span>' +
          '</div></div>';
      });
      html += '</div>';
    }

    // Try It section
    html += renderTryItPanel(pkgName, pkg);

    html += '</div>';
    document.getElementById('mainContent').innerHTML = html;
    attachCardListeners();
    attachTryItListeners(pkgName, pkg);
  }

  // === Rule Card ===

  function renderRuleCard(rule, pkgName, index) {
    var id = 'rule-' + index;
    var tags = [];
    if (rule.salience && rule.salience !== 0) tags.push('salience: ' + rule.salience);
    if (rule.agendaGroup && rule.agendaGroup !== 'MAIN') tags.push(rule.agendaGroup);
    if (rule.noLoop) tags.push('no-loop');

    var html = '<div class="rule-card" data-card="' + id + '">';
    html += '<div class="rule-card-header" data-toggle="' + id + '">';
    html += '<span class="method-badge rule">RULE</span>';
    html += '<span class="rule-card-name">' + escapeHtml(rule.name) + '</span>';
    html += '<span class="rule-card-meta">';
    tags.forEach(function (t) { html += '<span class="meta-tag">' + escapeHtml(t) + '</span>'; });
    html += '</span>';
    html += '<span class="rule-card-chevron" data-chev="' + id + '">&#9660;</span>';
    html += '</div>';

    html += '<div class="rule-card-body" data-body="' + id + '">';
    html += renderRuleDetail(rule);
    html += '</div></div>';
    return html;
  }

  function renderRuleDetail(rule) {
    var html = '<div class="attr-grid">';
    html += attrItem('Package', rule.packageName);
    html += attrItem('Load Order', rule.loadOrder);
    if (rule.salience !== null && rule.salience !== undefined) html += attrItem('Salience', rule.salience);
    if (rule.agendaGroup) html += attrItem('Agenda Group', rule.agendaGroup);
    if (rule.activationGroup) html += attrItem('Activation Group', rule.activationGroup);
    if (rule.ruleFlowGroup) html += attrItem('Ruleflow Group', rule.ruleFlowGroup);
    if (rule.noLoop !== null && rule.noLoop !== undefined) html += attrItem('No-Loop', rule.noLoop);
    if (rule.lockOnActive !== null && rule.lockOnActive !== undefined) html += attrItem('Lock-on-Active', rule.lockOnActive);
    html += '</div>';

    if (rule.metadata && Object.keys(rule.metadata).length > 0) {
      html += '<div><strong style="font-size:12px;color:var(--text-secondary)">METADATA</strong>';
      html += '<div class="metadata-list">';
      Object.entries(rule.metadata).forEach(function (entry) {
        html += '<span class="metadata-chip">' + escapeHtml(entry[0]) + '=' + escapeHtml(String(entry[1])) + '</span>';
      });
      html += '</div></div>';
    }
    return html;
  }

  function attrItem(label, value) {
    return '<div class="attr-item"><div class="attr-label">' + escapeHtml(label) +
      '</div><div class="attr-value">' + escapeHtml(String(value)) + '</div></div>';
  }

  // === Fact Type Card ===

  function renderFactTypeCard(ft, pkgName, index) {
    var id = 'fact-' + index;
    var html = '<div class="rule-card" data-card="' + id + '">';
    html += '<div class="rule-card-header" data-toggle="' + id + '">';
    html += '<span class="method-badge fact">TYPE</span>';
    html += '<span class="rule-card-name">' + escapeHtml(ft.simpleName || ft.name) + '</span>';
    if (ft.superClass) {
      html += '<span class="meta-tag">extends ' + escapeHtml(ft.superClass) + '</span>';
    }
    html += '<span class="meta-tag">' + (ft.fields ? ft.fields.length : 0) + ' fields</span>';
    html += '<span class="rule-card-chevron" data-chev="' + id + '">&#9660;</span>';
    html += '</div>';

    html += '<div class="rule-card-body" data-body="' + id + '">';
    html += renderFactTypeDetail(ft);
    html += '</div></div>';
    return html;
  }

  function renderFactTypeDetail(ft) {
    var html = '';
    if (ft.fields && ft.fields.length > 0) {
      html += '<table class="field-table"><thead><tr>' +
        '<th>Field</th><th>Type</th><th>Key</th><th>Index</th>' +
        '</tr></thead><tbody>';
      ft.fields.forEach(function (f) {
        html += '<tr><td>' + escapeHtml(f.name) + '</td>' +
          '<td><span class="type-badge">' + escapeHtml(f.type) + '</span></td>' +
          '<td>' + (f.key ? '<span class="key-badge">KEY</span>' : '') + '</td>' +
          '<td>' + f.index + '</td></tr>';
      });
      html += '</tbody></table>';
    }

    if (ft.sampleJson && Object.keys(ft.sampleJson).length > 0) {
      html += '<div style="margin-top:16px"><strong style="font-size:12px;color:var(--text-secondary)">SAMPLE JSON</strong>';
      html += '<pre style="margin-top:8px;padding:12px;background:var(--surface);border:1px solid var(--border);border-radius:6px;font-size:12px;font-family:var(--mono)">' +
        escapeHtml(JSON.stringify(ft.sampleJson, null, 2)) + '</pre></div>';
    }
    return html;
  }

  // === Try It Panel ===

  function renderTryItPanel(pkgName, pkg) {
    var sampleRequest = buildSampleRequest(pkg);
    var html = '<div class="section"><div class="section-title">Try It</div>';
    html += '<div class="try-it-panel">';
    html += '<div class="try-it-title">Execute Rules</div>';
    html += '<p style="font-size:13px;color:var(--text-secondary);margin-bottom:12px">' +
      'Insert facts and fire rules against this package. Provide facts as JSON objects with a <code>type</code> (fully qualified class name) and <code>data</code> (field values).</p>';
    html += '<textarea class="json-editor" id="executionInput">' + escapeHtml(JSON.stringify(sampleRequest, null, 2)) + '</textarea>';
    html += '<div class="btn-row">';
    html += '<button class="btn btn-primary" id="executeBtn">Execute Rules</button>';
    html += '<button class="btn btn-secondary" id="resetBtn">Reset</button>';
    html += '</div>';
    html += '<div id="executionResult"></div>';
    html += '</div></div>';
    return html;
  }

  function buildSampleRequest(pkg) {
    var request = { facts: [], globals: {} };

    if (pkg.factTypes && pkg.factTypes.length > 0) {
      pkg.factTypes.forEach(function (ft) {
        if (ft.sampleJson && Object.keys(ft.sampleJson).length > 0) {
          request.facts.push({
            type: ft.name,
            data: ft.sampleJson
          });
        }
      });
    }

    if (request.facts.length === 0) {
      request.facts.push({
        type: 'com.example.YourFactClass',
        data: { field1: 'value1', field2: 42 }
      });
    }

    return request;
  }

  // === Card Toggle Logic ===

  function attachCardListeners() {
    document.querySelectorAll('[data-toggle]').forEach(function (header) {
      header.addEventListener('click', function () {
        var id = header.dataset.toggle;
        var body = document.querySelector('[data-body="' + id + '"]');
        var chev = document.querySelector('[data-chev="' + id + '"]');
        body.classList.toggle('open');
        chev.classList.toggle('open');
      });
    });
  }

  // === Try It Execution Logic ===

  function attachTryItListeners(pkgName, pkg) {
    var executeBtn = document.getElementById('executeBtn');
    var resetBtn = document.getElementById('resetBtn');
    var input = document.getElementById('executionInput');

    if (!executeBtn) return;

    executeBtn.addEventListener('click', async function () {
      executeBtn.disabled = true;
      executeBtn.innerHTML = '<span class="spinner"></span>Executing...';
      document.getElementById('executionResult').innerHTML = '';

      try {
        var request = JSON.parse(input.value);
        var result = await fetchJson('/execute', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(request)
        });
        renderExecutionResult(result);
      } catch (e) {
        renderExecutionError(e.message);
      } finally {
        executeBtn.disabled = false;
        executeBtn.textContent = 'Execute Rules';
      }
    });

    resetBtn.addEventListener('click', function () {
      input.value = JSON.stringify(buildSampleRequest(pkg), null, 2);
      document.getElementById('executionResult').innerHTML = '';
    });
  }

  function renderExecutionResult(result) {
    var container = document.getElementById('executionResult');
    var isError = !!result.error;

    var html = '<div class="result-panel" style="margin-top:16px">';
    html += '<div class="result-header ' + (isError ? 'error' : 'success') + '">';
    html += isError ? 'Execution Failed' : 'Execution Successful';
    html += '<span>' + result.executionTimeMs + 'ms</span>';
    html += '</div>';
    html += '<div class="result-body">';

    if (isError) {
      html += '<pre style="color:var(--red)">' + escapeHtml(result.error) + '</pre>';
    } else {
      html += '<div class="result-stats">';
      html += '<div class="result-stat"><strong>' + result.rulesFired + '</strong> rules fired</div>';
      html += '<div class="result-stat"><strong>' + (result.resultFacts ? result.resultFacts.length : 0) + '</strong> resulting facts</div>';
      html += '</div>';

      if (result.firedRuleNames && result.firedRuleNames.length > 0) {
        html += '<div style="margin-bottom:12px"><strong style="font-size:12px;color:var(--text-secondary)">FIRED RULES</strong>';
        html += '<div class="fired-rules">';
        result.firedRuleNames.forEach(function (name) {
          html += '<span class="fired-rule-tag">' + escapeHtml(name) + '</span>';
        });
        html += '</div></div>';
      }

      if (result.resultFacts && result.resultFacts.length > 0) {
        html += '<div><strong style="font-size:12px;color:var(--text-secondary)">RESULTING FACTS</strong>';
        html += '<pre style="margin-top:8px">' + escapeHtml(JSON.stringify(result.resultFacts, null, 2)) + '</pre></div>';
      }
    }

    html += '</div></div>';
    container.innerHTML = html;
  }

  function renderExecutionError(message) {
    var container = document.getElementById('executionResult');
    container.innerHTML = '<div class="result-panel" style="margin-top:16px">' +
      '<div class="result-header error">Error</div>' +
      '<div class="result-body"><pre style="color:var(--red)">' + escapeHtml(message) + '</pre></div></div>';
  }

  // === Utils ===

  function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;')
      .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
  }

  function escapeAttr(str) {
    return escapeHtml(str);
  }

  // === Start ===
  document.addEventListener('DOMContentLoaded', init);
})();

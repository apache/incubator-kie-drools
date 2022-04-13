/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/* Taken from https://gitlab.com/antora/antora-lunr-extension. */

/* global CustomEvent */
;(function (globalScope) {
  /* eslint-disable no-var */
  var config = document.getElementById('search-ui-script').dataset
  var snippetLength = parseInt(config.snippetLength || 100, 10)
  var siteRootPath = config.siteRootPath || ''
  appendStylesheet(config.stylesheet)
  var searchInput = document.getElementById('search-input')
  var searchResult = document.createElement('div')
  searchResult.classList.add('search-result-dropdown-menu')
  searchInput.parentNode.appendChild(searchResult)

  function appendStylesheet (href) {
    if (!href) return
    document.head.appendChild(Object.assign(document.createElement('link'), { rel: 'stylesheet', href: href }))
  }

  function highlightText (doc, position) {
    var hits = []
    var start = position[0]
    var length = position[1]

    var text = doc.text
    var highlightSpan = document.createElement('span')
    highlightSpan.classList.add('search-result-highlight')
    highlightSpan.innerText = text.substr(start, length)

    var end = start + length
    var textEnd = text.length - 1
    var contextAfter = end + snippetLength > textEnd ? textEnd : end + snippetLength
    var contextBefore = start - snippetLength < 0 ? 0 : start - snippetLength
    if (start === 0 && end === textEnd) {
      hits.push(highlightSpan)
    } else if (start === 0) {
      hits.push(highlightSpan)
      hits.push(document.createTextNode(text.substr(end, contextAfter)))
    } else if (end === textEnd) {
      hits.push(document.createTextNode(text.substr(0, start)))
      hits.push(highlightSpan)
    } else {
      hits.push(document.createTextNode('...' + text.substr(contextBefore, start - contextBefore)))
      hits.push(highlightSpan)
      hits.push(document.createTextNode(text.substr(end, contextAfter - end) + '...'))
    }
    return hits
  }

  function highlightTitle (sectionTitle, doc, position) {
    var hits = []
    var start = position[0]
    var length = position[1]

    var highlightSpan = document.createElement('span')
    highlightSpan.classList.add('search-result-highlight')
    var title
    if (sectionTitle) {
      title = sectionTitle.text
    } else {
      title = doc.title
    }
    highlightSpan.innerText = title.substr(start, length)

    var end = start + length
    var titleEnd = title.length - 1
    if (start === 0 && end === titleEnd) {
      hits.push(highlightSpan)
    } else if (start === 0) {
      hits.push(highlightSpan)
      hits.push(document.createTextNode(title.substr(length, titleEnd)))
    } else if (end === titleEnd) {
      hits.push(document.createTextNode(title.substr(0, start)))
      hits.push(highlightSpan)
    } else {
      hits.push(document.createTextNode(title.substr(0, start)))
      hits.push(highlightSpan)
      hits.push(document.createTextNode(title.substr(end, titleEnd)))
    }
    return hits
  }

  function highlightHit (metadata, sectionTitle, doc) {
    var hits = []
    for (var token in metadata) {
      var fields = metadata[token]
      for (var field in fields) {
        var positions = fields[field]
        if (positions.position) {
          var position = positions.position[0] // only higlight the first match
          if (field === 'title') {
            hits = highlightTitle(sectionTitle, doc, position)
          } else if (field === 'text') {
            hits = highlightText(doc, position)
          }
        }
      }
    }
    return hits
  }

  function createSearchResult (result, store, searchResultDataset) {
    result.forEach(function (item) {
      var ids = item.ref.split('-')
      var docId = ids[0]
      var doc = store[docId]
      var sectionTitle
      if (ids.length > 1) {
        var titleId = ids[1]
        sectionTitle = doc.titles.filter(function (item) {
          return String(item.id) === titleId
        })[0]
      }
      var metadata = item.matchData.metadata
      var hits = highlightHit(metadata, sectionTitle, doc)
      searchResultDataset.appendChild(createSearchResultItem(doc, sectionTitle, item, hits))
    })
  }

  function createSearchResultItem (doc, sectionTitle, item, hits) {
    var documentTitle = document.createElement('div')
    documentTitle.classList.add('search-result-document-title')
    documentTitle.innerText = doc.title
    var documentHit = document.createElement('div')
    documentHit.classList.add('search-result-document-hit')
    var documentHitLink = document.createElement('a')
    documentHitLink.href = siteRootPath + doc.url + (sectionTitle ? '#' + sectionTitle.hash : '')
    documentHit.appendChild(documentHitLink)
    hits.forEach(function (hit) {
      documentHitLink.appendChild(hit)
    })
    var searchResultItem = document.createElement('div')
    searchResultItem.classList.add('search-result-item')
    searchResultItem.appendChild(documentTitle)
    searchResultItem.appendChild(documentHit)
    searchResultItem.addEventListener('mousedown', function (e) {
      e.preventDefault()
    })
    return searchResultItem
  }

  function createNoResult (text) {
    var searchResultItem = document.createElement('div')
    searchResultItem.classList.add('search-result-item')
    var documentHit = document.createElement('div')
    documentHit.classList.add('search-result-document-hit')
    var message = document.createElement('strong')
    message.innerText = 'No results found for query "' + text + '"'
    documentHit.appendChild(message)
    searchResultItem.appendChild(documentHit)
    return searchResultItem
  }

  function clearSearchResults (reset) {
    if (reset === true) searchInput.value = ''
    searchResult.innerHTML = ''
  }

  function search (index, text) {
    // execute an exact match search
    var result = index.search(text)
    if (result.length > 0) {
      return result
    }
    // no result, use a begins with search
    result = index.search(text + '*')
    if (result.length > 0) {
      return result
    }
    // no result, use a contains search
    result = index.search('*' + text + '*')
    return result
  }

  function searchIndex (index, store, text) {
    clearSearchResults(false)
    if (text.trim() === '') {
      return
    }
    var result = search(index, text)
    var searchResultDataset = document.createElement('div')
    searchResultDataset.classList.add('search-result-dataset')
    searchResult.appendChild(searchResultDataset)
    if (result.length > 0) {
      createSearchResult(result, store, searchResultDataset)
    } else {
      searchResultDataset.appendChild(createNoResult(text))
    }
  }

  function confineEvent (e) {
    e.stopPropagation()
  }

  function debounce (func, wait, immediate) {
    var timeout
    return function () {
      var context = this
      var args = arguments
      var later = function () {
        timeout = null
        if (!immediate) func.apply(context, args)
      }
      var callNow = immediate && !timeout
      clearTimeout(timeout)
      timeout = setTimeout(later, wait)
      if (callNow) func.apply(context, args)
    }
  }

  function enableSearchInput (enabled) {
    searchInput.disabled = !enabled
    searchInput.title = enabled ? '' : 'Loading index...'
  }

  function initSearch (lunr, data) {
    var start = performance.now()
    var index = Object.assign({ index: lunr.Index.load(data.index), store: data.store })
    enableSearchInput(true)
    searchInput.dispatchEvent(
            new CustomEvent('loadedindex', {
              detail: {
                took: performance.now() - start,
              },
            })
    )
    var debug = 'URLSearchParams' in globalScope && new URLSearchParams(globalScope.location.search).has('lunr-debug')
    searchInput.addEventListener(
            'keydown',
            debounce(function (e) {
              if (e.key === 'Escape' || e.key === 'Esc') return clearSearchResults(true)
              try {
                var query = searchInput.value
                if (!query) return clearSearchResults()
                searchIndex(index.index, index.store, searchInput.value)
              } catch (err) {
                if (debug) console.debug('Invalid search query: ' + query + ' (' + err.message + ')')
              }
            }, 100)
    )
    searchInput.addEventListener('click', confineEvent)
    searchResult.addEventListener('click', confineEvent)
    document.documentElement.addEventListener('click', clearSearchResults)
  }

  // disable the search input until the index is loaded
  enableSearchInput(false)

  globalScope.initSearch = initSearch
})(typeof globalThis !== 'undefined' ? globalThis : window)

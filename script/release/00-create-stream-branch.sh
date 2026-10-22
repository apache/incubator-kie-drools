#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

set -euo pipefail

# Branch cut (Automation A).  Two independent actions:
#
#   --create-branch   Create the X.Y.x development stream branch from the base
#                     branch (default: main) and push it.
#   --update-version  On X.Y.x, set the dev version X.Y.999-SNAPSHOT in all
#                     files (the "D commit") and push it.
#
# If neither flag is given, both actions are performed (normal branch cut).
# Run ONCE per minor version, BEFORE the first RC.
#
#   main  ──── c ──── c ──── c ───────────────────────────>  999-SNAPSHOT
#                            └── [D commit] ──── c ────>  X.Y.x (X.Y.999-SNAPSHOT)
#
# Usage:
#   ./script/release/00-create-stream-branch.sh <version> [--create-branch] [--update-version]
#                                               [--base <branch>] [--remote <name>] [--push] [--dry-run]
#
# Examples:
#   ./script/release/00-create-stream-branch.sh 10.3.0 --push                    # both
#   ./script/release/00-create-stream-branch.sh 10.3.0 --create-branch --push    # only create + push the branch
#   ./script/release/00-create-stream-branch.sh 10.3.0 --update-version --push   # branch exists: only set 10.3.999-SNAPSHOT
#   ./script/release/00-create-stream-branch.sh --version 10.3 --dry-run
#
# Options:
#   <version> | --version <ver>   Release version (10.3.0) or minor (10.3).  Only major.minor is used.
#   --create-branch               Create X.Y.x from --base (fails if it already exists)
#   --update-version              Update versions to X.Y.999-SNAPSHOT on X.Y.x, commit (fails if X.Y.x does not exist
#                                 unless --create-branch is also given)
#   --base <branch>               Branch to cut from (default: main). Used by --create-branch
#   --remote <name>               Git remote (default: origin)
#   --push                        Push to the remote (default: local only)
#   --dry-run                     Print what would happen without making any changes
#
# After this script the working tree is left checked out on X.Y.x.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

VERSION=""
BASE_BRANCH="main"
REMOTE="origin"
CREATE_BRANCH=false
UPDATE_VERSION=false
PUSH=false
DRY_RUN=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --version)         VERSION="${2:-}"; shift 2 ;;
        --base)            BASE_BRANCH="${2:-}"; shift 2 ;;
        --remote)          REMOTE="${2:-}"; shift 2 ;;
        --create-branch)   CREATE_BRANCH=true; shift ;;
        --update-version)  UPDATE_VERSION=true; shift ;;
        --push)            PUSH=true; shift ;;
        --dry-run)         DRY_RUN=true; shift ;;
        *)
            if [[ -z "${VERSION}" && ! "$1" =~ ^- ]]; then
                VERSION="$1"
                shift
            else
                echo "Unknown option: $1"
                echo "Usage: $0 [<version> | --version <version>] [--create-branch] [--update-version] [--base <branch>] [--push] [--dry-run]"
                exit 1
            fi
            ;;
    esac
done

# Neither action selected -> full branch cut.
if [[ "${CREATE_BRANCH}" == "false" && "${UPDATE_VERSION}" == "false" ]]; then
    CREATE_BRANCH=true
    UPDATE_VERSION=true
fi

if [[ -z "${VERSION}" ]]; then
    echo "ERROR: Version is required."
    echo "Usage: $0 10.3.0 [--create-branch] [--update-version] [--base main] [--push] [--dry-run]"
    exit 1
fi

if [[ ! "${VERSION}" =~ ^([0-9]+)\.([0-9]+)(\.[0-9A-Za-z.-]+)?$ ]]; then
    echo "ERROR: Cannot parse version '${VERSION}'. Expected e.g. 10.3.0 or 10.3"
    exit 1
fi

MAJOR_MINOR="${BASH_REMATCH[1]}.${BASH_REMATCH[2]}"
STREAM_BRANCH="${MAJOR_MINOR}.x"
DEV_VERSION="${MAJOR_MINOR}.999-SNAPSHOT"

cd "${REPO_ROOT}"

echo "========================================"
echo "Apache KIE repo — stream branch cut"
echo "Stream branch   : ${STREAM_BRANCH}"
echo "Create branch   : ${CREATE_BRANCH} (from ${REMOTE}/${BASE_BRANCH})"
echo "Update version  : ${UPDATE_VERSION} (${DEV_VERSION})"
echo "Push            : ${PUSH}"
echo "Dry run         : ${DRY_RUN}"
echo "========================================"

# ── Guards ───────────────────────────────────────────────────────────────────
if ! git diff --quiet || ! git diff --cached --quiet; then
    echo "ERROR: There are uncommitted changes in the working tree."
    exit 1
fi
if [[ -n "$(git ls-files --others --exclude-standard)" ]]; then
    echo "ERROR: There are untracked files in the working tree:"
    git ls-files --others --exclude-standard | sed 's/^/  /'
    exit 1
fi

REMOTE_HAS_BRANCH=false
if git ls-remote --exit-code --heads "${REMOTE}" "${STREAM_BRANCH}" &>/dev/null; then
    REMOTE_HAS_BRANCH=true
fi

if [[ "${CREATE_BRANCH}" == "true" ]]; then
    if [[ "${REMOTE_HAS_BRANCH}" == "true" ]]; then
        echo "ERROR: Branch '${STREAM_BRANCH}' already exists on '${REMOTE}'."
        echo "       The branch cut is done once per minor version. For rc2 / patch"
        echo "       releases reuse the existing branch and skip this step."
        exit 1
    fi
    if git rev-parse --verify "${STREAM_BRANCH}" &>/dev/null; then
        echo "ERROR: Local branch '${STREAM_BRANCH}' already exists. Delete it first:"
        echo "  git branch -D ${STREAM_BRANCH}"
        exit 1
    fi
else
    # --update-version only: the branch must already exist on the remote.
    if [[ "${REMOTE_HAS_BRANCH}" != "true" ]]; then
        echo "ERROR: Branch '${STREAM_BRANCH}' does not exist on '${REMOTE}'."
        echo "       Create it first (--create-branch) or run with both actions."
        exit 1
    fi
fi

if [[ "${DRY_RUN}" == "true" ]]; then
    echo ""
    echo "[DRY RUN] Would execute:"
    if [[ "${CREATE_BRANCH}" == "true" ]]; then
        echo "  git fetch ${REMOTE} +refs/heads/${BASE_BRANCH}:refs/remotes/${REMOTE}/${BASE_BRANCH}"
        echo "  git checkout -b ${STREAM_BRANCH} ${REMOTE}/${BASE_BRANCH}"
    else
        echo "  git fetch ${REMOTE} +refs/heads/${STREAM_BRANCH}:refs/remotes/${REMOTE}/${STREAM_BRANCH}"
        echo "  git checkout -B ${STREAM_BRANCH} ${REMOTE}/${STREAM_BRANCH}"
    fi
    if [[ "${UPDATE_VERSION}" == "true" ]]; then
        echo "  ./script/release/01-update-version.sh ${DEV_VERSION}"
        echo "  git add -A"
        echo "  git commit -m \"Update version to ${DEV_VERSION}\""
    fi
    if [[ "${PUSH}" == "true" ]]; then
        echo "  git push ${REMOTE} ${STREAM_BRANCH}"
    fi
    exit 0
fi

# ── Create branch / check out existing branch ────────────────────────────────
if [[ "${CREATE_BRANCH}" == "true" ]]; then
    echo ""
    echo "--- Creating ${STREAM_BRANCH} from ${REMOTE}/${BASE_BRANCH} ---"
    git fetch "${REMOTE}" "+refs/heads/${BASE_BRANCH}:refs/remotes/${REMOTE}/${BASE_BRANCH}"
    git checkout -b "${STREAM_BRANCH}" "${REMOTE}/${BASE_BRANCH}"
else
    echo ""
    echo "--- Checking out existing ${STREAM_BRANCH} ---"
    git fetch "${REMOTE}" "+refs/heads/${STREAM_BRANCH}:refs/remotes/${REMOTE}/${STREAM_BRANCH}"
    git checkout -B "${STREAM_BRANCH}" "${REMOTE}/${STREAM_BRANCH}"
fi

# ── Update version ───────────────────────────────────────────────────────────
if [[ "${UPDATE_VERSION}" == "true" ]]; then
    echo ""
    echo "--- Updating version to ${DEV_VERSION} ---"
    "${SCRIPT_DIR}/01-update-version.sh" "${DEV_VERSION}"

    echo ""
    echo "--- Committing D commit ---"
    git add -A
    if git diff --cached --quiet; then
        echo "No changes: ${STREAM_BRANCH} is already on ${DEV_VERSION}, nothing to commit."
    else
        git commit -m "Update version to ${DEV_VERSION}"
    fi
fi

if [[ "${PUSH}" == "true" ]]; then
    echo ""
    echo "--- Pushing ${STREAM_BRANCH} to ${REMOTE} ---"
    git push "${REMOTE}" "${STREAM_BRANCH}"
fi

echo ""
echo "========================================"
echo "Stream branch ${STREAM_BRANCH} done (create=${CREATE_BRANCH}, update-version=${UPDATE_VERSION})."
echo ""
if [[ "${PUSH}" != "true" ]]; then
    echo "Next step (not pushed yet):"
    echo "  git push ${REMOTE} ${STREAM_BRANCH}"
    echo ""
fi
echo "Next: run the RC release, e.g."
echo "  ./script/release/release-all.sh --version ${MAJOR_MINOR}.0 --tag ${MAJOR_MINOR}.0-rc1 --deploy --push-tag"
echo "========================================"

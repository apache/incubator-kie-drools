#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)

"${script_dir_path}"/../common/update_quarkus.sh

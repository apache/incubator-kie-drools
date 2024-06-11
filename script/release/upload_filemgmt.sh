#!/bin/bash -e

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

this_script_directory="${BASH_SOURCE%/*}"
if [[ ! -d "$this_script_directory" ]]; then
  this_script_directory="$PWD"
fi

kieVersion=$1

drools_project_root=$this_script_directory/../..
filemgmtServer=drools@filemgmt-prod.jboss.org
rsync_filemgmt=drools@filemgmt-prod-sync.jboss.org
droolsDocs=docs_htdocs/drools/release
droolsHtdocs=downloads_htdocs/drools/release

cd "${drools_project_root}"

# create directory on filemgmt-prod.jboss.org for new release
touch create_version
echo "-mkdir ${droolsDocs}/${kieVersion}" > create_version
echo "-mkdir ${droolsHtdocs}/${kieVersion}" >> create_version
chmod +x create_version
sftp -b create_version $filemgmtServer

# creates directory kie-api-javadoc for drools on filemgmt-prod.jboss.org
touch create_kie_api_javadoc_dir
echo "-mkdir ${droolsDocs}/${kieVersion}/kie-api-javadoc" > create_kie_api_javadoc_dir
chmod +x create_kie_api_javadoc_dir
sftp -b create_kie_api_javadoc_dir $filemgmtServer

# creates directory drools-docs on filemgmt-prod.jboss.org
touch create_drools_docs_dir
echo "-mkdir ${droolsDocs}/${kieVersion}/drools-docs" > create_drools_docs_dir
chmod +x create_drools_docs_dir
sftp -b create_drools_docs_dir $filemgmtServer

# upload binaries to filemgmt-prod.jboss.org
touch upload_binaries
echo "put drools-distribution/target/drools-distribution-${kieVersion}.zip ${droolsHtdocs}/${kieVersion}" > upload_binaries
chmod +x upload_binaries
sftp -b upload_binaries $filemgmtServer

# upload docs to filemgmt-prod.jboss.org
readonly remote_shell="ssh -p 2222"
rsync -Pavqr -e "$remote_shell" --protocol=28 --delete-after drools-docs/target/drools-docs-${kieVersion}/* ${rsync_filemgmt}:${droolsDocs}/${kieVersion}/drools-docs
rsync -Pavqr -e "$remote_shell" --protocol=28 --delete-after kie-api/target/apidocs/* ${rsync_filemgmt}:${droolsDocs}/${kieVersion}/kie-api-javadoc


# make filemgmt symbolic links for drools
mkdir filemgmt_links
cd filemgmt_links

readonly remote_shell_non_strict="ssh -p 2222 -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"

###############################################################################
# latest drools links
###############################################################################
touch ${kieVersion}
ln -s ${kieVersion} latest

# do not link latest to Beta
if [[ "${kieVersion}" == *Final* ]]; then
    echo "Uploading normal links..."

    rsync -e "$remote_shell_non_strict" --protocol=28 -a latest $rsync_filemgmt:${droolsDocs}
    rsync -e "$remote_shell_non_strict" --protocol=28 -a latest $rsync_filemgmt:${droolsHtdocs}
fi

###############################################################################
# latestFinal drools links
###############################################################################
if [[ "${kieVersion}" == *Final* ]]; then
    ln -s ${kieVersion} latestFinal
    echo "Uploading Final links..."
    rsync -e "$remote_shell_non_strict" --protocol=28 -a latestFinal $rsync_filemgmt:${droolsDocs}
    rsync -e "$remote_shell_non_strict" --protocol=28 -a latestFinal $rsync_filemgmt:${droolsHtdocs}
fi

# remove files and directories for uploading drools
cd ..
rm -rf create_version
rm -rf create_*_dir
rm -rf upload_binaries
rm -rf filemgmt_links
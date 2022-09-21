containerEngine = 'docker'
containerTlsOptions = ''

void loginRegistry() {
    loginContainerRegistry(getOperatorImageRegistry(), getOperatorImageRegistryCredentials())
}

void loginContainerRegistry(String registry, String credsId) {
    echo "Using credentials ($credsId) to login to registry ($registry)"
    withCredentials([usernamePassword(credentialsId: credsId, usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_PWD')]) {
        sh "${containerEngine} login ${containerTlsOptions} -u ${REGISTRY_USER} -p ${REGISTRY_PWD} ${registry}"
    }
}

String getOperatorImageRegistryCredentials() {
    return params.OPERATOR_IMAGE_REGISTRY_CREDENTIALS
}

String getOperatorImageRegistry() {
    return params.OPERATOR_IMAGE_REGISTRY
}

String getImageFullName(String namespace, String image, String tag) {
    return "${namespace}/${image}:$tag"
}

String getImageFullNameWithRegistry(String registry, String namespace, String image, String tag) {
    return "${registry}/${getImageFullName(namespace, image, tag)}"
}

void pullImage(String image) {
    retry(env.MAX_REGISTRY_RETRIES ?: 1) {
        sh "${containerEngine} pull ${containerTlsOptions} ${image}"
    }
}

void tagImage(String oldImage, String newImage) {
    sh "${containerEngine} tag ${oldImage} ${newImage}"
}

void pushImage(String image) {
    loginRegistry()
    retry(env.MAX_REGISTRY_RETRIES ?: 1) {
        sh "${containerEngine} push ${containerTlsOptions} ${image}"
    }
}

boolean removeQuayTag(String namespace, String imageName, String tag) {
    String image = "quay.io/${namespace}/${imageName}:${tag}"
    echo "Removing a temporary image tag ${image}"
    try {
        def output = 'false'
        withCredentials([usernamePassword(credentialsId: getOperatorImageRegistryCredentials(), usernameVariable: 'QUAY_USER', passwordVariable: 'QUAY_TOKEN')]) {
            output = sh(returnStdout: true, script: "curl -H 'Content-Type: application/json' -H 'Authorization: Bearer ${QUAY_TOKEN}' -X DELETE https://quay.io/api/v1/repository/${namespace}/${imageName}/tag/${tag}").trim()
            if (output != '') {
                echo "$output"
            }
        }
        return output == ''
    } catch (err) {
        echo "[ERROR] Cannot remove a temporary image tag ${image}."
    }
}

return this
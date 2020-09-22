#!/bin/bash
############################
#
# setup a service ci pipeline.
#
############################

PROFILE=dev
# the name of project.
PROJECT_NAME=libs-java-bps-web-spring-cloud-starter
# the concourse info.
CI_TARGET=main
CI_TEAM=main

# delete pipeline.
fly -t ${CI_TARGET} dp \
  -p ${PROJECT_NAME}

# setup build pipeline.
fly -t ${CI_TARGET} set-pipeline \
  -p ${PROJECT_NAME} \
  -v VAULT_ADDR="${BPFAAS_CI_VAULT_ADDR}" \
  -v VAULT_TOKEN="${BPFAAS_CI_VAULT_TOKEN}" \
  -v git-privateKey="$(cat ~/.ssh/bpfaas/bpfaas-lib-bps-web-spring-cloud-starter)" \
  -l ./vars-${PROFILE}.yaml \
  -c ./template/pipeline.yml \
  --team=${CI_TEAM}

# unpause.
fly -t ${CI_TARGET} unpause-pipeline \
  -p ${PROJECT_NAME}
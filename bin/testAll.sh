#!/usr/bin/env bash
SPF_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

${SPF_DIR}/gradlew clean test integrationTest

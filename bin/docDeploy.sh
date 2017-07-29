#!/usr/bin/env bash
SPF_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

cd ${SPF_DIR}
mkdocs gh-deploy

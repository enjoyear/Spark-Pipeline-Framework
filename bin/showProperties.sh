#!/usr/bin/env bash
SPF_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

${SPF_DIR}/gradlew properties


#References:
#https://docs.gradle.org/3.3/userguide/scala_plugin.html
#https://docs.gradle.org/3.3/userguide/tutorial_gradle_command_line.html#sec:dependency_insight

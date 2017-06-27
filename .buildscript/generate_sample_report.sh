#!/bin/bash

SCRIPT_DIRECTORY=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd )
cd "$SCRIPT_DIRECTORY/.." || exit 1

main() {
    abort_if_dirty_repo

    generate_report
    switch_branch_and_commit

    echo "Updated sample-report, feel free to run 'git push origin gh-pages'"
}

abort_if_dirty_repo() {
    git diff-index --quiet --cached HEAD | true
    if [[ ${PIPESTATUS[0]} -ne 0 ]]; then
        echo "You have staged but not committed changes that would be lost! Aborting."
        exit 1
    fi
    git diff-files --quiet | true
    if [[ ${PIPESTATUS[0]} -ne 0 ]]; then
        echo "You have unstaged changes that would be lost! Aborting."
        exit 1
    fi
    untracked=$(git ls-files --exclude-standard --others)
    if [ -n "$untracked" ]; then
        echo "You have untracked files that could be overwritten! Aborting."
        exit 1
    fi
}

generate_report() {
    ./gradlew clean installDist
    jxlint-impl/build/install/jxlint-impl/bin/jxlint-impl \
        -t sample-report.html \
        jxlint-impl || true
}

switch_branch_and_commit() {
    git checkout gh-pages
    mv sample-report.html sample-report/index.html
    git clean -fd
    git add sample-report
    git commit -m "Update sample-report"
}

main

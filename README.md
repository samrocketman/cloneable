# Cloneable :octocat: GitHub Backups

- :arrow_forward: Download `cloneable.jar` from [GitHub releases
  page][download].
- :white_check_mark: Supports GitHub App or personal access token (PAT) for
  querying and cloning.
- :white_check_mark: Clone over HTTP with app or PAT via [`GIT_ASKPASS`
  backend][git-credentials].
- :white_check_mark: Query projects over HTTP and clone using SSH private key.
- :white_check_mark: Flexibly query projects based on age, contents, and other
  factors.  See CLI Documentation below for a full list.
- :white_check_mark: Minimal dependencies: Java, `/bin/sh`, and standard
  utilities; GNU or BSD coreutils, and busybox supported.

A simple backup solution for offsite backups of GitHub repository source code.

Cloneable is a Java-based CLI application which queries GitHub and reports
cloneable projects.  It only does one thing: lists repositories.  Other Linux
utilities can be used in conjunction with cloneable to create and update a local
backup of GitHub.  Useful for personal backups of GitHub with minimal
dependencies.

Cloneable utilizes [GitHub API v4 GraphQL][v4].  If you need a more thorough
backup solution, check out [other solutions on GitHub][other-backup].
# Download

Binary download is available in [GitHub releases][download].

# Setup

In order to call cloneable, it is recommended install the wrapper shell script
along with the bash competion for it.

    java -jar cloneable.jar --print-cli-script > /usr/local/bin/cloneable
    chmod 755 /usr/local/bin/cloneable

Install bash completion script for interactive auto-complete of options.

    cloneable --print-bash-completion > /etc/bash_completion.d/cloneable_completion

You need to also set up one of two methods for authentication to query GraphQL
APIs.

### GitHub App authentication

Create and install a GitHub app.  Recommended permissions is repository
`contents:readonly` for private repositories or no scopes for public
repositories.

Set environment variables with GitHub App authentication.

    export CLONEABLE_GITHUB_APP_ID=1234
    export CLONEABLE_GITHUB_APP_KEY=/path/to/private_key

### GitHub Personal Access Tokens

Requires a [GitHub personal access token][github-token] with scopes:

- `repo` and `read:org` for an organization with private repositories.
- `repo` for a user with private repositories.
- No scopes required for a user or organization with public repositories only.

Setup authentication.

    export GITHUB_TOKEN=<gh pat>

# Example Usage

Example usage covers HTTP and SSH cloning.  The following methods work with both
App and personal access token auth.

### Backup over HTTP

Cloning over HTTP requires backend authentication via `GIT_ASKPASS`.  See
[git-credentials][git-credentials] for details.

Install a `GIT_ASKPASS` script set up for cloning.

    cloneable -o your-org --print-askpass-script | /bin/sh

    # Or alternalte location instead of /tmp/askpass
    cloneable -o integralads --print-askpass-script | \
        CLONEABLE_ASKPASS_LOCATION=/alternate/askpass /bin/sh

The `GIT_ASKPASS` script will default to always auth against `your-org`.  If you
want to clone from another associated org for the GitHub App, you can set
environment variable `CLONEABLE_OWNER`.

Clone all repositories.  You can pass any cloneable arguments.

    export GIT_ASKPASS=/tmp/askpass
    cloneable -o your-org --http --skip-local-bare-repos \
      --print-clone-script | /bin/sh

    export CLONEABLE_OWNER=another-org
    cloneable --http --skip-local-bare-repos \
      --print-clone-script | /bin/sh -x

Update clones using HTTP.

    cloneable --http --print-update-script | /bin/sh -x

### Backup over SSH

Assuming your already have an SSH clone key loaded within ssh-agent.  You can
clone and update repositories using SSH.

    cloneable --url --skip-local-bare-repos \
      --print-clone-script | /bin/sh -x

Update clones using SSH.

    cloneable --url --print-update-script | /bin/sh -x

List repository names under a user or organization.

```
export GITHUB_TOKEN
read -ersp token: GITHUB_TOKEN
cloneable --owner samrocketman
```

Clone all repositories.

```bash
cloneable --owner samrocketman --url --skip-local-bare-repos \
  | xargs -r -n1 -P16 -- git clone --mirror
```

Synchronize all repository mirrors.

```bash
find . -maxdepth 1 -name '*.git' -print0 \
  | xargs -0 -n1 -P16 -I'{}' -- /bin/bash -exc 'cd "{}"; git fetch'
```

Show samrocketman repositories which have a `jenkins` topic.

```bash
cloneable --owner samrocketman --match-topics jenkins
```

# Build Jar

    ./gradlew clean jar

Run tests

    ./gradlew clean check

# CLI Documentation

The following options are available from `java -jar cloneable.jar --help`.

```
Usage: cloneable [-abdefhipPsuV] [--http] [--owner-is-user]
                 [--print-askpass-script] [--print-auth-token]
                 [--print-bash-completion] [--print-cli-script]
                 [--print-clone-script] [--print-update-script]
                 [--after=<afterTimeframe>] [-B=<branch>]
                 [--before=<beforeTimeframe>] [-g=<ghAppId>] [-k=<ghAppKey>]
                 [-o=<owner>] [-t=<token>] [-E=<excludeAllFiles>]...
                 [-F=<matchAnyFiles>]... [-m=<matchAnyTopics>]...
Description:

  Gets a list of repositories if given a GitHub user or GitHub organization.
  The primary purpose of this program is to facilitate listing GitHub
  repositories and cloning them for an offline backup.

  Website: https://github.com/samrocketman/cloneable

Example Usage:

  Set up GitHub App authentication.
    export CLONEABLE_GITHUB_APP_ID=1234
    export CLONEABLE_GITHUB_APP_KEY=/path/to/private_key

  Or you can use a GitHub personal access token.
    export GITHUB_TOKEN=<github_pat_...>

  Create CLI integration with bash completion.
    java -jar cloneable.jar --print-cli-script > /usr/local/bin/cloneable
    chmod 755 /usr/local/bin/cloneable
    cloneable --print-bash-completion > /etc/bash_completion.
d/cloneable_completion

  Install GIT_ASKPASS script when using GitHub App auth.
    cloneable -o your-org --print-askpass-script | /bin/bash

  HTTP mirror clones using GIT_ASKPASS.
    export GIT_ASKPASS=/tmp/askpass
    cloneable -o your-org --http \
      --skip-local-bare-repos --print-clone-script \
      | /bin/sh

    export CLONEABLE_OWNER=another-org
    cloneable --http --skip-local-bare-repos --print-clone-script \
      | /bin/sh

  Update HTTP mirrors.
    cloneable --print-update-script | /bin/sh -x

Environment Variables:

  CLONEABLE_CACHE_KEY
    RSA private key used to encrypt cache.  By default,
    CLONEABLE_GITHUB_APP_KEY is used.

  CLONEABLE_CACHE_PATH
    Directory where persisted encrypted cache and lockfile get stored.

  CLONEABLE_DEBUG
    Same as --debug, enables debug mode showing more information.

  CLONEABLE_GITHUB_APP_ID
    GitHub App ID used in GitHub App authentication.

  CLONEABLE_GITHUB_APP_KEY
    GitHub App private key used in GitHub App authentication.

  GITHUB_GRAPHQL_URL
    GitHub API endpoint for GraphQL. Default: https://api.github.com/graphql

  GITHUB_API_URL
    GitHub API endpoint for REST.  Default: https://api.github.com/

  GITHUB_TOKEN
    GitHub personal token for API communication when GitHub App authentication
    is not available.

Options:

  -a, --skip-archived-repos  If a repository is archived, then it will be
                               skipped.
      --after=<afterTimeframe>
                             Find all repositories updated after the given
                               timeframe. Must be a positive integer followed
                               by one of: d, m, y.  For example, 1y will find
                               all repositories updated after 1 year ago; or
                               within the past 12m or 12 months.  If d, m, or y
                               is not provided then d will be assumed.
  -b, --skip-local-bare-repos
                             If a bare repository directory exists locally (the
                               name of the repo ending with '.git'), then it
                               will not be printed out.  This is useful for
                               cloning only missing repositories.
  -B, --branch=<branch>      If using -F or -E options, then choose the Git
                               branch to search for files.  The default branch
                               will be queried if this option is not specified.
      --before=<beforeTimeframe>
                             Find all repositories updated before the given
                               timeframe. Must be a positive integer followed
                               by one of: d, m, y.  For example, 1y will find
                               all repositories updated before 1 year ago.  If
                               d, m, or y is not provided then d will be
                               assumed.
  -d, --debug                Prints out stack traces.
  -e, --skip-empty-repos     If a repository does not contain any Git commits,
                               then it will be skipped.
  -E, --exclude-repos-with=<excludeAllFiles>
                             Match any repository containing which does NOT
                               contain the file at the repository root.  -E can
                               be specified more than once to exclude a
                               repository containing any or all of the listed
                               files.
  -f, --skip-forked-repos    If a repository is a fork from another user or
                               organization, then it will be skipped.
  -F, --contains=<matchAnyFiles>
                             Match any repository containing any file at the
                               repository root.  -F can be specified more than
                               once to match any one of multiple files.
  -g, --github-app-id=<ghAppId>
                             GitHub App app ID associated with GitHub App
                               private key.
  -h, --help                 Show this help message and exit.
      --http                 Prints HTTP clone URL instead of repository name.
                               Also dictates output of --print-update-script
                               and --print-clone-script
  -i, --inverse-search       Inverse the search results.  For example, instead
                               of skipping repositories it will match
                               repositories.  Adding the option
                               --inverse-search along side --skip-archived will
                               find all archived repositories.  If you provide
                               multiple options then the inverse finds any
                               match.  For example, adding --inverse-search
                               with options --skip-archived and --skip-empty
                               will find BOTH empty or archived repositories.
  -k, --github-app-key=<ghAppKey>
                             GitHub App private key.
  -m, --match-topics=<matchAnyTopics>
                             Require all repositories to have one of the listed
                               topics.  -m can be specified multiple times.
  -o, --owner=<owner>        GitHub account or organization for querying a list
                               of projects.
      --owner-is-user        If using GitHub App auth this flag indicates app
                               installation is for a user instead of
                               organization.
  -p, --skip-private-repos   If a repository is private, then it will be
                               skipped.
  -P, --skip-public-repos    If a repository is public, then it will be skipped.
      --print-askpass-script Print a GIT_ASKPASS script meant for cloning HTTP
                               repositories provided by --http option.  Pipe
                               into /bin/sh.
      --print-auth-token     Print GitHub token generated by GitHub app
                               installation used for cloning.
      --print-bash-completion
                             Print bash-completion script meant to help with
                               auto-complete on interactive command line. e.g.
                               redirect stdout to '/etc/bash_completion.d'.
      --print-cli-script     Prints 'cloneable' CLI wrapper script.  e.g.
                               redirect stdout to '/usr/local/bin/cloneable'.
      --print-clone-script   Prints a bash script meant for updating HTTP
                               clones created by app auth.  Pipe into /bin/sh
      --print-update-script  Prints a bash script meant for updating HTTP
                               clones created by app auth.  Pipe into /bin/sh
  -s, --skip-source-repos    If a repository is **not** a fork from another
                               user or organization, then it will be skipped.
  -t, --github-token=<token> GitHub personal access token or file containing a
                               token.  Falls back to checking GITHUB_TOKEN
                               environment variable.
  -u, --url                  Prints out SSH clone URL instead of repository
                               name.
  -V, --version              Print version information and exit.
```

[download]: https://github.com/samrocketman/cloneable/releases
[git-credentials]: https://git-scm.com/docs/gitcredentials
[github-token]: https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line
[other-backup]: https://github.com/topics/github-backup
[v4]: https://developer.github.com/v4/

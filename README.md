# Cloneable

A simple backup solution for offsite backups of GitHub repository source code.

Cloneable is a Java-based CLI application which queries GitHub and reports
cloneable projects.  It only does one thing: lists repositories.  Other Linux
utilities can be used in conjunction with cloneable to create and update a local
backup of GitHub.  Useful for personal backups of GitHub with minimal
dependencies.

Cloneable utilizes [GitHub API v4 GraphQL][v4].  If you need a more thorough
backup solution, check out [other solutions on GitHub][other-backup].

# Download

Binary download is available in [GitHub releases][releases].

# Setup


Requires a [GitHub personal access token][github-token] with scopes:

- `repo` and `read:org` for an organization with private repositories.
- `repo` for a user with private repositories.
- No scopes required for a user or organization with public repositories only.

# Example Usage

List repository names under a user or organization.

```
export GITHUB_TOKEN=<personal access token>
java -jar cloneable.jar --owner samrocketman
```

Clone all repositories.

```bash
java -jar cloneable.jar --owner samrocketman --url --skip-local-bare-repos \
  | xargs -r -n1 -P16 -- git clone --mirror
```

Synchronize all repository mirrors.

```bash
find . -maxdepth 1 -name '*.git' -print0 \
  | xargs -0 -n1 -P16 -I'{}' -- /bin/bash -exc 'cd "{}"; git fetch'
```

Show samrocketman repositories which have a `jenkins` topic.

```bash
java -jar cloneable.jar --owner samrocketman --match-topics jenkins
```

# Options

The following options are available from `java -jar cloneable.jar --help`.

```
Usage: cloneable [-bdhuV] -o=<owner> [-t=<token>] [-m=<matchAnyTopic>]...
Gets a list of repositories if given a GitHub user or GitHub organization.

https://github.com/samrocketman/cloneable

Options:
  -b, --skip-local-bare-repos
                        If a bare repository directory exists locally (the name
                          of the repo ending with '.git'), then it will not be
                          printed out.  This is useful for cloning only missing
                          repositories.
  -d, --debug           Prints out stack traces.
  -h, --help            Show this help message and exit.
  -m, --match-topics=<matchAnyTopic>
                        Require all repositories to have one of the listed
                          topics.  -m can be specified multiple times.
  -o, --owner=<owner>   GitHub account or organization for querying a list of
                          projects.
  -t, --github-token=<token>
                        GitHub personal access token or file containing a
                          token.  Falls back to checking GITHUB_TOKEN
                          environment variable.
  -u, --url             Prints out clone URL instead of repository name.
  -V, --version         Print version information and exit.
```

# Build Jar

    ./gradlew clean jar

Run tests

    ./gradlew clean check

[github-token]: https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line
[releases]: https://github.com/samrocketman/cloneable/releases
[v4]: https://developer.github.com/v4/
[other-backup]: https://github.com/topics/github-backup

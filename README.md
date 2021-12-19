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
export GITHUB_TOKEN
read -ersp token: GITHUB_TOKEN
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

# Environment Variables

- `GITHUB_GRAPHQL_URL` if using self-hosted GitHub Enterprise, then set
  environment variable to `https://[hostname]/api/graphql`.
- `GITHUB_TOKEN` set your personal access token so that cloneable can
  authenticate with the GraphQL API.  See also [`Setup` section](#setup).


# Options

The following options are available from `java -jar cloneable.jar --help`.

```
Usage: cloneable [-abdefhipPsuV] -o=<owner> [-t=<token>] [-m=<matchAnyTopic>]...
Gets a list of repositories if given a GitHub user or GitHub organization.

https://github.com/samrocketman/cloneable

Options:
  -a, --skip-archived-repos  If a repository is archived, then it will be
                               skipped.
  -b, --skip-local-bare-repos
                             If a bare repository directory exists locally (the
                               name of the repo ending with '.git'), then it
                               will not be printed out.  This is useful for
                               cloning only missing repositories.
  -d, --debug                Prints out stack traces.
  -e, --skip-empty-repos     If a repository does not contain any Git commits,
                               then it will be skipped.
  -f, --skip-forked-repos    If a repository is a fork from another user or
                               organization, then it will be skipped.
  -h, --help                 Show this help message and exit.
  -i, --inverse-search       Inverse the search results.  For example, instead
                               of skipping repositories it will match
                               repositories.  Adding the option
                               --inverse-search along side --skip-archived will
                               find all archived repositories.  If you provide
                               multiple options then the inverse finds any
                               match.  For example, adding --inverse-search
                               with options --skip-archived and --skip-empty
                               will find BOTH empty or archived repositories.
  -m, --match-topics=<matchAnyTopic>
                             Require all repositories to have one of the listed
                               topics.  -m can be specified multiple times.
  -o, --owner=<owner>        GitHub account or organization for querying a list
                               of projects.
  -p, --skip-private-repos   If a repository is private, then it will be
                               skipped.
  -P, --skip-public-repos    If a repository is public, then it will be skipped.
  -s, --skip-source-repos    If a repository is **not** a fork from another
                               user or organization, then it will be skipped.
  -t, --github-token=<token> GitHub personal access token or file containing a
                               token.  Falls back to checking GITHUB_TOKEN
                               environment variable.
  -u, --url                  Prints out clone URL instead of repository name.
  -V, --version              Print version information and exit.
```

# Build Jar

    ./gradlew clean jar

Run tests

    ./gradlew clean check

# Bash completion

Cloneable is an uber Jar which contains all of its dependencies necessary to run
it.  Bash completion is available for this app which increases the convenience
of its use.

Create a local directory for cloneable config.

```bash
mkdir ~/.local/share/cloneable
```

Generate bash completion file.

```bash
FULL_CLONEABLE_PATH="path/to/cloneable.jar"
java -cp "$FULL_CLONEABLE_PATH" picocli.AutoComplete -n cloneable cloneable.App
mv cloneable_completion ~/.local/share/cloneable/
cp "$FULL_CLONEABLE_PATH" ~/.local/share/cloneable/
```

Set up a `cloneable` command alias.

```bash
# install in bashrc
echo 'source ~/.local/share/cloneable/cloneable_completion' >> ~/.bashrc
echo "alias cloneable='java -jar ~/.local/share/cloneable/cloneable.jar'" >> ~/.bashrc

# install in bash_profile
echo 'source ~/.local/share/cloneable/cloneable_completion' >> ~/.bash_profile
echo "alias cloneable='java -jar ~/.local/share/cloneable/cloneable.jar'" >> ~/.bash_profile
```

Now all `cloneable` options will be able to TAB complete.

[github-token]: https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line
[releases]: https://github.com/samrocketman/cloneable/releases
[v4]: https://developer.github.com/v4/
[other-backup]: https://github.com/topics/github-backup

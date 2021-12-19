# 0.8

- Support for self-hosted GitHub Enterprise.  See README for details.
- New option: `--contains` or `-F` for short.  Match any repository containing
  any file at the repository root.
- New option: `--exclude-repos-with` or `-E` option for short.  Skips
  repositories which contain any listed files.
- New option: `--branch` or `-B` for short.  When using `-F` or `-E` options,
  choose the branch name to search for files.  It will use default branch if
  none specified.
- New option: `--before`.  Find all repositories contributed before some
  timeframe ago.  For example, `--before 1y` finds all repositories pushed more
  than 1 year ago.
- New option: `--after`.  Find all repositories contributed after some
  timeframe ago.  For example, the following options will find all repositories
  contributed within the past 30 days.  `--after 1m`, `--after 30d`, or `--after
  30`.
- README update: Bash completion options now available.

# 0.7

- New option: `--inverse-search` or `-i` for short.  Will invert the skip logic
  and instead treat it as criteria to print the repository.  Providing multiple
  skip options will mean that a repository need only match one of the multiple
  skip conditions.
- New option: `--skip-empty` or `-e` for short.  Will skip repositories which do
  not contain any Git commits.
- New option: `--skip-archived` or `-f` for short.  Will skip archived
  repositories.

# 0.6

- New option: `--skip-forked-repos` or `-f` for short.  If a repository is a fork from another user
  or organization, then it will be skipped.
- New option: `--skip-source-repos` or `-s` for short.  If a repository is
  **not** a fork from another user or organization, then it will be skipped.
- New option: `--skip-private-repos` or `-p` for short.  If a repository is
  private, then it will be skipped.
- New option: `--skip-public-repos` or `-P` for short.  If a repository is
  public, then it will be skipped.

# 0.5

- New option: `--match-topics` or `-m` for short will now filter for
  repositories that only contain the matching topic.  The list of topics is
  treated as a match for any topic.  This option can be specified multiple
  times.
- Errors now include the exception simple name.  This is necessary because when
  a `UnknownHostException` occurred it would only show the domain of the unknown
  host but didn't explain the issue.  With the exception name, the error becomes
  more clear.
- Now development snapshots versions are supported.  When a user calls
  `--version` and the jar was built from a development snapshot, the version
  information will include the git hash from the build.

# 0.4

- Version from gradle is the version used in CLI.

# 0.3

- Bugfix: when using option `--skip-local-bare-repos` a blank line would be
  printed if a paginated GraphQL query returned repositories which all had local
  bare repositories existing.  It no longer will print a blank line per GraphQL
  page.

# 0.2

- Optimized Jar includes minimum groovy parts.

#  0.1 - Initial release

Initial release features:

- Ability to choose user or organization to render a list of repositories.
- Option to skip local bare repositories when rendering GitHub repository list.
- Debug option to show stack traces for development.
- Optionally print clone URL instead  of repository name.
- Credentials can be loaded as an option, from a file, or from an environment
  variable `GITHUB_TOKEN`.  Note: file should have `600` permissions for
  security purposes.

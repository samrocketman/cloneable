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

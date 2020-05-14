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

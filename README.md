# Cloneable

A Java-based CLI application which queries GitHub and reports cloneable
projects.

# Build Jar

    ./gradlew clean jar

# Usage

```
Usage: cloneable [-bdhuV] -o=<owner> [-t=<token>]
Gets a list of repositories if given a user or org owner.
  -b, --skip-local-bare-repos
                        If a bare repository directory exists locally (the name
                          of the repo ending with '.git'), then it will not be
                          printed out.  This is useful for cloning only missing
                          repositories.
  -d, --debug           Prints out stack traces.
  -h, --help            Show this help message and exit.
  -o, --owner=<owner>   GitHub account or organization for querying a list of
                          projects.
  -t, --github-token=<token>
                        GitHub personal access token or file containing a
                          token.  Falls back to checking GITHUB_TOKEN
                          environment variable.
  -u, --url             Prints out clone URL instead of repository name.
  -V, --version         Print version information and exit.
```

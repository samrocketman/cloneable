# Cloneable

A Java-based CLI application which queries GitHub and reports cloneable
projects.

# Build Jar

    ./gradlew clean jar

# Usage

```
Usage: cloneable [-dhV] -o=<owner> [-t=<token>]
Gets a list of repositories if given a user or org owner.
  -d, --debug           Prints out stack traces.
  -h, --help            Show this help message and exit.
  -o, --owner=<owner>   GitHub account or organization for querying a list of
                          projects.
  -t, --github-token=<token>
                        GitHub personal access token or file containing a
                          token.  Falls back to checking GITHUB_TOKEN
                          environment variable.
  -V, --version         Print version information and exit.
```

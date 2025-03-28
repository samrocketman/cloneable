/*
 * Copyright 2020-2025 Sam Gleske
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloneable

import cloneable.errors.ShortErrorMessageHandler
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Option
import picocli.CommandLine.ParameterException
import picocli.CommandLine.Spec

import groovy.time.TimeCategory
import java.util.concurrent.Callable

@Command(
    name = 'cloneable',
    mixinStandardHelpOptions = true,
    versionProvider = ManifestVersionProvider.class,
    description = '''\
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
    cloneable --print-bash-completion \\
      > /etc/bash_completion.d/cloneable_completion

  Install GIT_ASKPASS script when using GitHub App auth.
    cloneable -o your-org --print-askpass-script | /bin/bash

  HTTP mirror clones using GIT_ASKPASS.
    export GIT_ASKPASS=/tmp/askpass
    cloneable -o your-org --http \\
      --skip-local-bare-repos --print-clone-script \\
      | /bin/sh

    export CLONEABLE_OWNER=another-org
    cloneable --http --skip-local-bare-repos --print-clone-script \\
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

  CLONEABLE_OWNER
    Set the --owner=<owner> via environment variable instead of option.

  CLONEABLE_USER_OWNER
    If using GitHub App authentication and the --owner is a GitHub user rather
    than an organization you should set this environment variable to be
    anything (non-zero length).  GitHub apps get API tokens for users
    differently than organizations.  As of this writing GitHub provides no easy
    way to auto-detect this without an API token so this is a catch 22.

  GITHUB_GRAPHQL_URL
    GitHub API endpoint for GraphQL. Default: https://api.github.com/graphql

  GITHUB_API_URL
    GitHub API endpoint for REST.  Default: https://api.github.com/

  GITHUB_TOKEN
    GitHub personal token for API communication when GitHub App authentication
    is not available.

Options:
''')
class App implements Callable<Integer> {

    @Spec
    CommandSpec spec

    // fields used in logic or script rendering.
    Boolean shouldPrintScript = false
    Boolean usesGitHubAppAuth = false
    String providedAfterTimeframe = ''
    String providedBeforeTimeframe = ''

    /*
       Options which print scripts
    */

    @Option(names = ["--print-askpass-script"], description = "Print a GIT_ASKPASS script meant for cloning HTTP repositories provided by --http option.  Pipe into /bin/sh.")
    Boolean printAskpassScript = false

    @Option(names = ["--print-update-script"], description = "Prints a bash script meant for updating HTTP clones created by app auth.  Pipe into /bin/sh")
    Boolean printUpdateScript = false

    @Option(names = ["--print-clone-script"], description = "Prints a bash script meant for updating HTTP clones created by app auth.  Pipe into /bin/sh")
    Boolean printCloneScript = false

    @Option(names = ["--print-bash-completion"], description = "Print bash-completion script meant to help with auto-complete on interactive command line. e.g. redirect stdout to '/etc/bash_completion.d'.")
    Boolean printBashCompletion = false

    @Option(names = ["--print-cli-script"], description = "Prints 'cloneable' CLI wrapper script.  e.g. redirect stdout to '/usr/local/bin/cloneable'.")
    Boolean printCliScript = false

    /*
       Options requiring --owner=<owner>.
    */

    @Option(names = ["--print-auth-token"], description = "Print GitHub token generated by GitHub app installation used for cloning.")
    Boolean printGhToken = false

    @Option(names = ["-g", "--github-app-id"], description = "GitHub App app ID associated with GitHub App private key.")
    String ghAppId = ''

    @Option(names = ["-k", "--github-app-key"], description = "GitHub App private key.")
    String ghAppKey = ''

    @Option(names = ["-a", "--skip-archived-repos"], description = "If a repository is archived, then it will be skipped.")
    Boolean skipArchived = false

    @Option(names = ["-b", "--skip-local-bare-repos"], description = "If a bare repository directory exists locally (the name of the repo ending with '.git'), then it will not be printed out.  This is useful for cloning only missing repositories.")
    Boolean skipLocalBare = false

    @Option(names = ["-B", "--branch"], description = "If using -F or -E options, then choose the Git branch to search for files.  The default branch will be queried if this option is not specified.")
    String branch

    @Option(names = ["-d", "--debug"], description = "Prints out stack traces.")
    Boolean debug = false

    @Option(names = ["-e", "--skip-empty-repos"], description = "If a repository does not contain any Git commits, then it will be skipped.")
    Boolean skipEmpty = false

    @Option(names = ["-E", "--exclude-repos-with"], description = "Match any repository containing which does NOT contain the file at the repository root.  -E can be specified more than once to exclude a repository containing any or all of the listed files.")
    List<String> excludeAllFiles = []

    @Option(names = ["-f", "--skip-forked-repos"], description = "If a repository is a fork from another user or organization, then it will be skipped.")
    Boolean skipForked = false

    @Option(names = ["-F", "--contains"], description = "Match any repository containing any file at the repository root.  -F can be specified more than once to match any one of multiple files.")
    List<String> matchAnyFiles = []

    @Option(names = ["-i", "--inverse-search"], description = "Inverse the search results.  For example, instead of skipping repositories it will match repositories.  Adding the option --inverse-search along side --skip-archived will find all archived repositories.  If you provide multiple options then the inverse finds any match.  For example, adding --inverse-search with options --skip-archived and --skip-empty will find BOTH empty or archived repositories.")
    Boolean inverseSearch = false

    @Option(names = ["-m", "--match-topics"], description = "Require all repositories to have one of the listed topics.  -m can be specified multiple times.")
    List<String> matchAnyTopics = []

    @Option(names = ["-o", "--owner"], description = "GitHub account or organization for querying a list of projects.")
    String owner = ''

    @Option(names = ["-p", "--skip-private-repos"], description = "If a repository is private, then it will be skipped.")
    Boolean skipPrivate = false

    @Option(names = ["-P", "--skip-public-repos"], description = "If a repository is public, then it will be skipped.")
    Boolean skipPublic = false

    @Option(names = ["-s", "--skip-source-repos"], description = "If a repository is **not** a fork from another user or organization, then it will be skipped.")
    Boolean skipSource = false

    @Option(names = ["-t", "--github-token"], description = "GitHub personal access token or file containing a token.  Falls back to checking GITHUB_TOKEN environment variable.")
    String token = ''

    @Option(names = ["-u", "--url"], description = "Prints out SSH clone URL instead of repository name.")
    Boolean printUrl = false

    @Option(names = ["--http"], description = "Prints HTTP clone URL instead of repository name.  Also dictates output of --print-update-script and --print-clone-script")
    Boolean httpUrl = false

    @Option(names = ["--owner-is-user"], description = "If using GitHub App auth this flag indicates app installation is for a user instead of organization.")
    Boolean ownerIsUser = false

    Date beforeTimeframe
    @Option(names = ["--before"], description = "Find all repositories updated before the given timeframe. Must be a positive integer followed by one of: d, m, y.  For example, 1y will find all repositories updated before 1 year ago.  If d, m, or y is not provided then d will be assumed.")
    void setBeforeTimeframe(String userInput) {
        this.providedBeforeTimeframe = userInput
        this.beforeTimeframe = getTimeframe('--before', userInput)
    }

    Date afterTimeframe
    @Option(names = ["--after"], description = "Find all repositories updated after the given timeframe. Must be a positive integer followed by one of: d, m, y.  For example, 1y will find all repositories updated after 1 year ago; or within the past 12m or 12 months.  If d, m, or y is not provided then d will be assumed.")
    void setAfterTimeframe(String userInput) {
        this.providedAfterTimeframe = userInput
        this.afterTimeframe = getTimeframe('--after', userInput)
    }

    /**
      Returns a date with a given offset.

      @param option The option using this function to give a richer help message on error.
      @param userInput User input from the option e.g. Nd, Nm, or Ny where N is
                       a positive integer.
      @return Returns a date offset with a time in the past.  e.g. userInput 1y
              will return today's date one year ago.
      */
    private Date getTimeframe(String option, String userInput) {
        Date timeframe = new Date()
        use(TimeCategory) {
            timeframe -= userInput.with { String s ->
                switch(s) {
                    case ~/^[0-9]+d?$/:
                        return Integer.parseInt(s -~ /d$/).day
                        break
                    case  ~/^[0-9]+m$/:
                        return Integer.parseInt(s -~ /m$/).month
                        break
                    case  ~/^[0-9]+y$/:
                        return Integer.parseInt(s -~ /y$/).year
                        break
                    default:
                        throw new ParameterException(spec.commandLine(), "${option} '${userInput}' must be a positive integer followed by {d = day, m = month, or y = year}.  For example,\n    " +
                            ["${option} 1d: for all repositories updated ${option -~ /^--/} 1 day ago.",
                            "${option} 1m: for all repositories updated ${option -~ /^--/} 1 month ago.",
                            "${option} 1y: for all repositories updated ${option -~ /^--/} 1 year ago."].join('\n    '))
                }
            }
        }
    }

    List<String> optionsToArgList() {
        List additional_args = []
        if(this.debug && !System.getenv('CLONEABLE_DEBUG')) {
            additional_args << '--debug'
        }
        if(this.owner) {
            additional_args << "--owner='${this.owner}'"
        }
        if(this.inverseSearch) {
            additional_args << '--inverse-search'
        }
        if(this.branch) {
            additional_args << "--branch='${this.branch}'"
        }
        if(this.matchAnyFiles) {
            this.matchAnyFiles.each { fileOption ->
                additional_args << "--contains='${fileOption}'"
            }
        }
        if(this.excludeAllFiles) {
            this.excludeAllFiles.each { fileOption ->
                additional_args << "--exclude-repos-with='${fileOption}'"
            }
        }
        if(this.providedAfterTimeframe) {
            additional_args << "--after='${this.providedAfterTimeframe}'"
        }
        if(this.providedBeforeTimeframe) {
            additional_args << "--before='${this.providedBeforeTimeframe}'"
        }
        if(this.matchAnyTopics) {
            this.matchAnyTopics.each { fileOption ->
                additional_args << "--match-topics='${fileOption}'"
            }
        }
        if(this.usesGitHubAppAuth) {
            if(this.ghAppId && !System.getenv('CLONEABLE_GITHUB_APP_ID')) {
                additional_args << "--github-app-id='${this.ghAppId}'"
            }
            if(this.ghAppKey && !System.getenv('CLONEABLE_GITHUB_APP_KEY')) {
                additional_args << "--github-app-key='${this.ghAppKey}'"
            }
        } else {
            if(this.token && !System.getenv('GITHUB_TOKEN')) {
                additional_args << "--github-token='${this.token}'"
            }
        }
        if(this.skipArchived) {
            additional_args << '--skip-archived-repos'
        }
        if(this.skipEmpty) {
            additional_args << '--skip-empty-repos'
        }
        if(this.skipForked) {
            additional_args << '--skip-forked-repos'
        }
        if(this.skipLocalBare) {
            additional_args << '--skip-local-bare-repos'
        }
        if(this.skipPrivate) {
            additional_args << '--skip-private-repos'
        }
        if(this.skipPublic) {
            additional_args << '--skip-public-repos'
        }
        if(this.skipSource) {
            additional_args << '--skip-source-repos'
        }
        if(this.httpUrl) {
            additional_args << '--http'
        } else {
            additional_args << '--url'
        }
        return additional_args
    }

    static void main(String... args) {
        int exitCode = new CommandLine(new App()).setParameterExceptionHandler(new ShortErrorMessageHandler()).execute(args);
        System.exit(exitCode);
    }

    void setDefaultsWithEnvironment() {
        if(!this.debug && System.getenv('CLONEABLE_DEBUG')) {
            this.debug = true
        }

        if(!this.ghAppId && System.getenv('CLONEABLE_GITHUB_APP_ID')) {
            this.ghAppId = System.getenv('CLONEABLE_GITHUB_APP_ID')
        }
        if(!this.ghAppKey && System.getenv('CLONEABLE_GITHUB_APP_KEY')) {
            this.ghAppKey = System.getenv('CLONEABLE_GITHUB_APP_KEY')
        }
        if(!this.token && System.getenv('GITHUB_TOKEN')) {
            this.token = System.getenv('GITHUB_TOKEN')
        }
        if(!this.owner && System.getenv('CLONEABLE_OWNER')) {
            this.owner = System.getenv('CLONEABLE_OWNER')
        }
        if(!this.ownerIsUser && System.getenv('CLONEABLE_USER_OWNER')) {
            this.ownerIsUser = true
        }
    }

    @Override
    Integer call() throws Exception {
        setDefaultsWithEnvironment()
        this.shouldPrintScript = (
            this.printAskpassScript ||
            this.printUpdateScript ||
            this.printBashCompletion ||
            this.printCliScript ||
            this.printCloneScript
        )
        this.usesGitHubAppAuth = (this.ghAppId && this.ghAppKey)
        // options which make owner not required
        Boolean ownerIsRequired = !(
            (shouldPrintScript ^ this.printCloneScript) ||
            (!this.usesGitHubAppAuth && this.printGhToken)
        )
        if(!this.owner && ownerIsRequired) {
            throw new ParameterException(spec.commandLine(), "Missing required option: '--owner=<owner>'")
        }
        if(this.printGhToken && !this.token && !this.usesGitHubAppAuth) {
            List requiredOptions = []
            if(!this.ghAppId) {
                requiredOptions << "Missing required option: '--github-app-id=<ghAppId>'"
            }
            if(!this.ghAppKey) {
                requiredOptions << "Missing required option: '--github-app-key=<ghAppKey>'"
            }
            throw new ParameterException(spec.commandLine(), requiredOptions.join('\n'))
        }
        try {
            AppLogic.main(this)
        } catch(Exception e) {
            if(this.debug)  {
                throw e
            } else {
                System.err.println("${e.class.simpleName}: ${e.message}")
                return 1
            }
        }
        return 0
    }
}

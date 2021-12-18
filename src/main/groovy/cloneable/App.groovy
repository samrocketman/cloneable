/*
 * Copyright 2020-2021 Sam Gleske
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

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

import java.util.concurrent.Callable

@Command(name = 'cloneable', mixinStandardHelpOptions = true, versionProvider = ManifestVersionProvider.class,
         description = 'Gets a list of repositories if given a GitHub user or GitHub organization.\n\nhttps://github.com/samrocketman/cloneable\n\nOptions:')
class App implements Callable<Integer> {

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

    @Option(names = ["-o", "--owner"], required = true, description = "GitHub account or organization for querying a list of projects.")
    String owner

    @Option(names = ["-p", "--skip-private-repos"], description = "If a repository is private, then it will be skipped.")
    Boolean skipPrivate = false

    @Option(names = ["-P", "--skip-public-repos"], description = "If a repository is public, then it will be skipped.")
    Boolean skipPublic = false

    @Option(names = ["-s", "--skip-source-repos"], description = "If a repository is **not** a fork from another user or organization, then it will be skipped.")
    Boolean skipSource = false

    @Option(names = ["-t", "--github-token"], description = "GitHub personal access token or file containing a token.  Falls back to checking GITHUB_TOKEN environment variable.")
    String token = ''

    @Option(names = ["-u", "--url"], description = "Prints out clone URL instead of repository name.")
    Boolean printUrl = false

    static void main(String... args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    Integer call() throws Exception {
        try {
            AppLogic.main(this)
        } catch(Exception e) {
            if(debug)  {
                throw e
            } else {
                System.err.println("${e.class.simpleName}: ${e.message}")
                return 1
            }
        }
        return 0
    }
}

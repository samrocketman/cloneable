/*
 * Copyright 2020 Sam Gleske
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

@Command(name = 'cloneable', mixinStandardHelpOptions = true, version = 'cloneable 0.1',
         description = 'Gets a list of repositories if given a user or org owner.')
class App implements Callable<Integer> {

    @Option(names = ["-d", "--debug"], description = "Prints out stack traces.")
    Boolean debug = false

    @Option(names = ["-t", "--github-token"], description = "GitHub personal access token or file containing a token.  Falls back to checking GITHUB_TOKEN environment variable.")
    String token = ''

    @Option(names = ["-o", "--owner"], required = true, description = "GitHub account or organization for querying a list of projects.")
    String owner

    @Option(names = ["-u", "--url"], description = "Prints out clone URL instead of repository name.")
    Boolean printUrl = false

    @Option(names = ["-b", "--skip-local-bare-repos"], description = "If a bare repository directory exists locally (the name of the repo ending with '.git'), then it will not be printed out.  This is useful for cloning only missing repositories.")
    Boolean skipLocalBare = false

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
                System.err.println e.message
                return 1
            }
        }
        return 0
    }
}

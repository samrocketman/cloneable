/*
 * This Groovy source file was generated by the Gradle 'init' task.
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

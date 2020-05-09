package cloneable

import net.gleske.jervis.remotes.GitHubGraphQL
import static net.gleske.jervis.tools.AutoRelease.getScriptFromTemplate

class AppLogic {
    AppLogic() {
        throw new IllegalStateException('ERROR: you\'ve encountered a bug.  Add --debug option and open an issue.')
    }

    static String graphql_template = '''
        |query {
        |  owned: repositoryOwner(login: "${owner}") {
        |    url
        |    repositories(first: ${ first ?: 100 }, after: ${nextPage ?: 'null'}) {
        |      pages: pageInfo {
        |        hasNextPage
        |        nextPage: endCursor
        |      }
        |      repoMeta: nodes {
        |        name
        |        isFork
        |        isPrivate
        |        cloneUrl: sshUrl
        |      }
        |    }
        |  }
        |}
        '''.stripMargin().trim()

    static Boolean hasNextPage(Map response) {
        response.data.owned.repositories.pages.hasNextPage
    }

    static String getNextPage(Map response) {
        '"' + response.data.owned.repositories.pages.nextPage + '"'
    }

    static void printRepositoryNames(Map response) {
        println response.data.owned.repositories.repoMeta*.name.join('\n')
    }

    static void main(App options) {
        GitHubGraphQL github = new GitHubGraphQL()
        github.credential = new Credential(options.token)
        Map variables = [
            owner: options.owner,
            first: 100,
            nextPage: null
        ]
        Map response = github.sendGQL(getScriptFromTemplate(graphql_template, variables))
        printRepositoryNames(response)
        while(hasNextPage(response)) {
            variables.nextPage = getNextPage(response)
            response = github.sendGQL(getScriptFromTemplate(graphql_template, variables))
            printRepositoryNames(response)
        }
    }
}

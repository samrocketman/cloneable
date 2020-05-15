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
        |    repositories(first: ${ first ?: 100 }, after: ${nextPage ?: 'null'}, affiliations: OWNER) {
        |      pages: pageInfo {
        |        hasNextPage
        |        nextPage: endCursor
        |      }
        |      repoMeta: nodes {
        |        name
        |        isFork
        |        isPrivate
        |        cloneUrl: sshUrl
        |        repositoryTopics(first: 100) {
        |          topics: nodes{
        |	         topic {
        |	           name
        |	         }
        |          }
        |        }
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

    static Boolean topicMatches(App options, Map repo) {
        List<String> topics = repo?.repositoryTopics?.topics*.topic*.name
        !options.matchAnyTopic || topics.any { String topic ->
            topic in options.matchAnyTopic
        }
    }

    static Boolean shouldNotSkipBare(App options, Map repo) {
            !options.skipLocalBare || ( options.skipLocalBare && !(new File(repo.name + '.git' ).exists()) )
    }

    static void printRepository(App options, Map response) {
        List repositories = response.data.owned.repositories.repoMeta.findAll { Map repo ->
            shouldNotSkipBare(options, repo) &&
            topicMatches(options, repo)
        }.collect { Map repo ->
            if(options.printUrl) {
                repo.cloneUrl
            } else {
                repo.name
            }
        }
        if(repositories) {
            println(repositories.join('\n'))
        }
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
        printRepository(options, response)
        while(hasNextPage(response)) {
            variables.nextPage = getNextPage(response)
            response = github.sendGQL(getScriptFromTemplate(graphql_template, variables))
            printRepository(options, response)
        }
    }
}

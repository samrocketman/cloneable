package cloneable

import spock.lang.Specification

class AppLogicTest extends Specification {
    def "Get next page which should return quotes"() {
        setup:
        Map response = [:]
        response.data = [:]
        response.data.owned = [:]
        response.data.owned.repositories = [:]
        response.data.owned.repositories.pages = [:]
        response.data.owned.repositories.pages.nextPage = 'foo'

        when:
        String result = AppLogic.getNextPage(response)

        then:
        result == '"foo"'
    }
}


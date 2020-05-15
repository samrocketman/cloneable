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
    def "Check if has next page"() {
        setup:
        Map response = [:]
        response.data = [:]
        response.data.owned = [:]
        response.data.owned.repositories = [:]
        response.data.owned.repositories.pages = [:]
        response.data.owned.repositories.pages.hasNextPage = true

        when:
        Boolean result = AppLogic.hasNextPage(response)

        then:
        result == true
    }
    def "Check if no next page"() {
        setup:
        Map response = [:]
        response.data = [:]
        response.data.owned = [:]
        response.data.owned.repositories = [:]
        response.data.owned.repositories.pages = [:]
        response.data.owned.repositories.pages.hasNextPage = false

        when:
        Boolean result = AppLogic.hasNextPage(response)

        then:
        result == false
    }
}


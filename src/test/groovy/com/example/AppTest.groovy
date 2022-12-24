package com.example

import spock.lang.Specification

class AppTest extends Specification {

    def "hello world test"() {
        given: "hello world"
        def helloWorld = "Hello World"

        when: "print"
        println (helloWorld)

        then: "assert"
        helloWorld == 'Hello World'
    }
}

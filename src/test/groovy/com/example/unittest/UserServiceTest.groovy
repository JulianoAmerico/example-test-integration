package com.example.unittest

import com.example.entity.User
import com.example.repository.UserRepository
import com.example.service.UserService
import com.example.service.exception.ServiceException
import spock.lang.Specification

import java.time.LocalDateTime

class UserServiceTest extends Specification {
    UserRepository repository = Mock()
    UserService service = new UserService(repository)

    def "it should be a valid user"() {
        given: "a valid user"
        def user = getUser(id: null, createdAt: null, updatedAt: null)

        when: "service save user"
        def result = service.save(user)

        then: "repository return a user saved"
        1 * repository.save(user) >> getUser()

        and: "assert result"
        result != null
        result.getId() != null
        result.getCreatedAt() != null
        result.getUpdatedAt() != null
    }

    def "it should not be a valid user"(String name, String document) {
        given: "a valid user"
        def user = getUser(name: name, document: document, id: null, createdAt: null, updatedAt: null)

        when: "service save user"
        service.save(user)

        then: "repository is not execute"
        0 * repository.save(user)

        and: "throw an exception"
        def result = thrown(ServiceException)
        result.getMessage() == 'Error when try to save user'

        where: "data table of name and document"
        name        | document
        null        | '12780459026'
        ' '         | '12780459026'
        ''          | '12780459026'
        'fake name' | null
        'fake name' | ''
        'fake name' | ' '

    }

    def getUser(Map args = [:]) {
        new User(
                args.get('id', UUID.randomUUID()),
                args.get('name', 'fake name'),
                args.get('document', '12780459026'),
                args.get('createdAt', LocalDateTime.now()),
                args.get('updatedAt', LocalDateTime.now())
        )
    }
}

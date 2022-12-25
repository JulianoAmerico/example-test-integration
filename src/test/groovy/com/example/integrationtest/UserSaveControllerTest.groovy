package com.example.integrationtest

import com.example.api.model.HandlerResponse
import com.example.api.model.UserRequest
import com.example.api.model.UserResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserSaveControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    private ObjectMapper mapper = new ObjectMapper()

    def setup() {
        mapper.findAndRegisterModules()
    }

    def "it should save user with success"() {
        given: "valid request"
        def request = getUserRequest()

        when: "perform controller request"
        def resultActions = mvc.perform(post('/users')
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        then: "expect http content and status"
        def responseAsString = resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString()

        and: "assert response"
        def response = mapper.readValue(responseAsString, UserResponse.class)
        verifyAll(response) {
            it.id() != null
            it.name() == request.name()
            it.document() == request.document()
            it.createdAt() != null
            it.updatedAt() != null
        }
    }

    def "it should NOT save user when name is invalid"(String name) {
        given: "user request"
        def request = getUserRequest(name: name)

        when: "perform controller request"
        def resultActions = mvc.perform(post('/users')
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        then: "expect http content and status"
        def responseAsString = resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString()

        and: "assert response"
        def response = mapper.readValue(responseAsString, HandlerResponse)
        response.message() == 'errors'
        response.messageErrors().get(0) == 'name cannot be null ou empty'

        where: "data table of name"
        name | _
        null | _
        " "  | _
        ""   | _
    }

    def "it should NOT save user when document is invalid"(String document) {
        given: "user request"
        def request = getUserRequest(document: document)

        when: "perform controller request"
        def resultActions = mvc.perform(post('/users')
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        then: "expect http content and status"
        def responseAsString = resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString()

        and: "assert response"
        def response = mapper.readValue(responseAsString, HandlerResponse)
        response.message() == 'errors'
        response.messageErrors().get(0) == 'document cannot be null ou empty'

        where: "data table of document"
        document | _
        null     | _
        " "      | _
        ""       | _
    }

    def getUserRequest(Map args = [:]) {
        new UserRequest(
                args.get('name', 'fake name'),
                args.get('document', '12780459026')
        )
    }
}

package com.example.integrationtest

import com.example.api.model.UserRequest
import com.example.rabbitmq.config.UpdateUserProperties
import com.example.rabbitmq.message.UserMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserUpdateControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private RabbitTemplate rabbitTemplate

    @Autowired
    private UpdateUserProperties properties

    private ObjectMapper mapper = new ObjectMapper()

    static GenericContainer rabbitmq = new GenericContainer<>(DockerImageName.parse("rabbitmq:3.8.27-alpine"))
            .withExposedPorts(5672)

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        rabbitmq.start()
        registry.add("spring.rabbitmq.host", rabbitmq::getHost)
        registry.add("spring.rabbitmq.port", rabbitmq::getFirstMappedPort)
    }

    def setup() {
        mapper.findAndRegisterModules()
    }

    def "it should produce update user with success"() {
        given: "valid request"
        def request = getUserRequest()
        def userId = UUID.randomUUID()

        when: "perform controller request"
        def resultActions = mvc.perform(put("/users/${userId}")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        then: "expect status code 200"
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())

        and: "get message produced"
        def message = rabbitTemplate.receiveAndConvert(properties.getQueue()) as UserMessage

        and: "assert message"
        verifyAll(message) {
            it != null
            it.id() == userId
            it.name() == request.name()
            it.document() == request.document()
        }

    }

    def getUserRequest(Map args = [:]) {
        new UserRequest(
                args.get('name', 'fake name'),
                args.get('document', '12780459026')
        )
    }
}

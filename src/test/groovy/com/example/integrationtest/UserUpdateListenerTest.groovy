package com.example.integrationtest

import com.example.rabbitmq.config.UpdateUserProperties
import com.example.rabbitmq.message.UserMessage
import com.example.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@Slf4j
class UserUpdateListenerTest extends Specification {

    @Autowired
    private RabbitTemplate rabbitTemplate

    @Autowired
    private UpdateUserProperties properties

    @Autowired
    private UserRepository repository

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

    @Sql(scripts = ["/scripts/insert_user.sql"])
    def "it should consume message to update user with success"() {
        given: "valid request"
        def message = getUserMessage()

        when: "produce message"
        rabbitTemplate.convertSendAndReceive(
                properties.getExchange(),
                properties.getRoutingKey(),
                message)

        then: "find user updated"
        def user = repository.findById(message.id()).get()

        and: "assert user updated"
        verifyAll(user) {
            it != null
            it.getId() == message.id()
            it.getName() == message.name()
            it.getDocument() == message.document()

            log.info("Name: {}", it.getName())
            log.info("Document: {}", it.getDocument())
        }

    }

    def getUserMessage(Map args = [:]) {
        new UserMessage(
                args.get('id', UUID.fromString('b8734d46-653f-42ec-aca4-97e8a3afb036')),
                args.get('name', 'John Doe'),
                args.get('document', '12345678900')
        )
    }
}

package hello

import java.util.concurrent.TimeUnit

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class Application : CommandLineRunner {

    @Autowired
    internal var context: AnnotationConfigApplicationContext? = null

    @Autowired
    internal var rabbitTemplate: RabbitTemplate? = null

    @Bean
    internal open fun queue(): Queue {
        return Queue(queueName, false)
    }

    @Bean
    internal open fun exchange(): TopicExchange {
        return TopicExchange("spring-boot-exchange")
    }

    @Bean
    internal open fun binding(queue: Queue, exchange: TopicExchange): Binding {
        return BindingBuilder.bind(queue).to(exchange).with(queueName)
    }

    @Bean
    internal open fun container(connectionFactory: ConnectionFactory, listenerAdapter: MessageListenerAdapter): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer()
        container.connectionFactory = connectionFactory
        container.setQueueNames(queueName)
        container.messageListener = listenerAdapter
        return container
    }

    @Bean
    internal open fun receiver(): Receiver {
        return Receiver()
    }

    @Bean
    internal open fun listenerAdapter(receiver: Receiver): MessageListenerAdapter {
        return MessageListenerAdapter(receiver, "receiveMessage")
    }

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        println("Waiting five seconds...")
        Thread.sleep(5000)
        println("Sending message...")
        rabbitTemplate!!.convertAndSend(queueName, "Hello from RabbitMQ!")
        receiver().latch.await(10000, TimeUnit.MILLISECONDS)
        context!!.close()
    }

    companion object {

        internal val queueName = "spring-boot"

        @Throws(InterruptedException::class)
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}

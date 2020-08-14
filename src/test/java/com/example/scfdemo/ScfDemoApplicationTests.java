package com.example.scfdemo;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScfDemoApplicationTests {

	@Test
	void testStubs() {
		TestChannelBinderConfiguration.applicationContextRunner(TestApp.class)
				.withPropertyValues(
						"spring.cloud.function.definition=f1|f2")
				.run(context -> {
					InputDestination inputDestination = context.getBean(InputDestination.class);
					OutputDestination outputDestination = context.getBean(OutputDestination.class);
					inputDestination.send(MessageBuilder.withPayload("hello".getBytes()).build());
					assertThat(outputDestination.receive(1000)).isNotNull();
				});
	}

	@SpringBootApplication
	static class TestApp {

		@Bean
		Function<Flux<byte[]>, Flux<byte[]>> f1() {
			return flux -> flux;
		}

		@Bean
		Function<Flux<byte[]>, Flux<Message<byte[]>>> f2() {
			return flux -> flux.map(GenericMessage::new);
		}

	}
}

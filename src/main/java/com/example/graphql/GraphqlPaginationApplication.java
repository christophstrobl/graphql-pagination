package com.example.graphql;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import graphql.scalars.ExtendedScalars;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.graphql.data.pagination.CursorEncoder;
import org.springframework.graphql.data.pagination.CursorStrategy;
import org.springframework.graphql.data.pagination.EncodingCursorStrategy;
import org.springframework.graphql.data.query.JsonKeysetCursorStrategy;
import org.springframework.graphql.data.query.ScrollPositionCursorStrategy;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

@SpringBootApplication
public class GraphqlPaginationApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlPaginationApplication.class, args);
	}

	/**
	 * We need this to support {@link java.time.LocalDate} as a GraphQL Scalar type.
	 * @return
	 */
	@Bean
	public RuntimeWiringConfigurer runtimeWiringConfigurer() {
		return wiringBuilder -> wiringBuilder.scalar(ExtendedScalars.Date);
	}

	@Bean
	EncodingCursorStrategy<ScrollPosition> cursorStrategy() {

		PolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
				.allowIfBaseType(Map.class)
				.allowIfSubType(Date.class)
				.build();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);

		ClientCodecConfigurer configurer = ClientCodecConfigurer.create();
		configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
		configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));

		return CursorStrategy.withEncoder(
				new ScrollPositionCursorStrategy(new JsonKeysetCursorStrategy(configurer)),
				CursorEncoder.base64());
	}
}

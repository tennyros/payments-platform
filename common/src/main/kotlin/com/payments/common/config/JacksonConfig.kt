package com.payments.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper

@Configuration
open class JacksonConfig {
    @Bean
    open fun objectMapper(): ObjectMapper = jacksonObjectMapper()
}

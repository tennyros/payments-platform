package com.payments.processor.api

import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentDto
import com.payments.processor.service.PaymentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/payments")
class PaymentController(
    private val paymentService: PaymentService,
) {
    @GetMapping
    fun listPayments(): Flux<PaymentDto> = paymentService.listPayments()

    @GetMapping("/{id}")
    fun getPayment(
        @PathVariable id: UUID,
    ): Mono<PaymentDto> = paymentService.getPayment(id)

    @PostMapping
    fun createPayment(
        @Valid @RequestBody request: CreatePaymentRequest,
    ): Mono<ResponseEntity<PaymentDto>> =
        paymentService.createPayment(request).map {
            ResponseEntity.status(HttpStatus.CREATED).body(it)
        }
}

package com.payments.processor.api

import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentDto
import com.payments.common.payment.PaymentStatus
import com.payments.processor.model.PaymentRecord
import com.payments.processor.repository.PaymentRepository
import com.payments.processor.service.PaymentService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentControllerTest {
    @Test
    fun `createPayment returns created payment`() {
        val repository: PaymentRepository = Mockito.mock(PaymentRepository::class.java)
        val paymentService = PaymentService(repository)
        val controller = PaymentController(paymentService)
        val request =
            CreatePaymentRequest(
                accountId = UUID.fromString("55555555-5555-5555-5555-555555555555"),
                amount = BigDecimal("99.95"),
                currency = "eur",
            )
        val responsePayment =
            PaymentDto(
                id = UUID.fromString("66666666-6666-6666-6666-666666666666"),
                accountId = request.accountId!!,
                amount = request.amount,
                currency = "EUR",
                status = PaymentStatus.PENDING,
                createdAt = Instant.parse("2026-07-15T10:15:30Z"),
            )
        val savedRecord =
            PaymentRecord(
                id = responsePayment.id,
                accountId = responsePayment.accountId,
                amount = responsePayment.amount,
                currency = responsePayment.currency,
                status = responsePayment.status.name,
                createdAt = responsePayment.createdAt,
            )

        Mockito.`when`(repository.save(Mockito.any(PaymentRecord::class.java))).thenReturn(Mono.just(savedRecord))

        val response = controller.createPayment(request).block()!!

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(responsePayment, response.body)
        assertNotNull(response.body)
    }

    @Test
    fun `listPayments returns empty flux by default`() {
        val repository: PaymentRepository = Mockito.mock(PaymentRepository::class.java)
        val paymentService = PaymentService(repository)
        val controller = PaymentController(paymentService)

        Mockito.`when`(repository.findAll()).thenReturn(Flux.empty())

        val payments = controller.listPayments().collectList().block()!!

        assertEquals(0, payments.size)
    }
}

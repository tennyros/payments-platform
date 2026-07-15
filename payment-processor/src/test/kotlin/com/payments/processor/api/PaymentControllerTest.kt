package com.payments.processor.api

import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentStatus
import com.payments.common.payment.UpdatePaymentStatusRequest
import com.payments.processor.repository.PaymentRepository
import com.payments.processor.repository.PaymentStatusRepository
import com.payments.processor.service.PaymentEventPublisher
import com.payments.processor.service.PaymentService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.UUID

class PaymentControllerTest {
    @Test
    fun `createPayment returns created payment`() {
        val repository: PaymentRepository = mock()
        val paymentStatusRepository: PaymentStatusRepository = mock()
        val paymentEventPublisher: PaymentEventPublisher = mock()
        val paymentService = PaymentService(repository, paymentStatusRepository, paymentEventPublisher)
        val controller = PaymentController(paymentService)
        val request =
            CreatePaymentRequest(
                accountId = UUID.fromString("55555555-5555-5555-5555-555555555555"),
                amount = BigDecimal("99.95"),
                currency = "eur",
            )

        whenever(repository.save(any())).thenAnswer { invocation ->
            Mono.just(invocation.getArgument(0))
        }
        whenever(paymentEventPublisher.publishPaymentCreated(any())).thenReturn(Mono.empty())

        val response = controller.createPayment(request).block()!!

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(request.accountId, response.body!!.accountId)
        assertEquals(request.amount, response.body!!.amount)
        assertEquals("EUR", response.body!!.currency)
        assertEquals(PaymentStatus.PENDING, response.body!!.status)
        assertEquals(7, response.body!!.id.version())
        assertTrue(
            response.body!!
                .id
                .toString()
                .isNotBlank(),
        )
    }

    @Test
    fun `listPayments returns empty flux by default`() {
        val repository: PaymentRepository = mock()
        val paymentStatusRepository: PaymentStatusRepository = mock()
        val paymentEventPublisher: PaymentEventPublisher = mock()
        val paymentService = PaymentService(repository, paymentStatusRepository, paymentEventPublisher)
        val controller = PaymentController(paymentService)

        whenever(repository.findAll()).thenReturn(Flux.empty())

        val payments = controller.listPayments().collectList().block()!!

        assertEquals(0, payments.size)
    }

    @Test
    fun `updatePaymentStatus returns updated payment`() {
        val repository: PaymentRepository = mock()
        val paymentStatusRepository: PaymentStatusRepository = mock()
        val paymentEventPublisher: PaymentEventPublisher = mock()
        val paymentService = PaymentService(repository, paymentStatusRepository, paymentEventPublisher)
        val controller = PaymentController(paymentService)
        val paymentId = UUID.fromString("77777777-7777-7777-7777-777777777777")
        val current =
            com.payments.processor.model.PaymentRecord(
                paymentId = paymentId,
                accountId = UUID.fromString("88888888-8888-8888-8888-888888888888"),
                amount = BigDecimal("12.34"),
                currency = "USD",
                status = PaymentStatus.PENDING.name,
                createdAt = java.time.Instant.parse("2026-07-15T10:15:30Z"),
                updatedAt = java.time.Instant.parse("2026-07-15T10:15:30Z"),
            )
        val updated = current.copy(status = PaymentStatus.FAILED.name)

        whenever(repository.findById(paymentId)).thenReturn(
            Mono.just(current),
            Mono.just(updated),
        )
        whenever(paymentStatusRepository.updateStatus(eq(paymentId), eq(PaymentStatus.FAILED.name), any()))
            .thenReturn(Mono.just(1))
        whenever(paymentEventPublisher.publishPaymentStatusChanged(any(), any(), any())).thenReturn(Mono.empty())

        val response = controller.updatePaymentStatus(paymentId, UpdatePaymentStatusRequest(PaymentStatus.FAILED)).block()!!

        assertEquals(PaymentStatus.FAILED, response.status)
    }
}

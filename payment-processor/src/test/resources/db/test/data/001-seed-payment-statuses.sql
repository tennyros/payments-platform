CREATE TABLE IF NOT EXISTS "payment_status_reference" (
    "code" VARCHAR(32) PRIMARY KEY,
    "description" VARCHAR(255) NOT NULL,
    "is_active" BOOLEAN NOT NULL DEFAULT TRUE
);

MERGE INTO "payment_status_reference" ("code", "description", "is_active") KEY ("code") VALUES
    ('PENDING', 'Payment waiting for processing', TRUE);

MERGE INTO "payment_status_reference" ("code", "description", "is_active") KEY ("code") VALUES
    ('PROCESSED', 'Payment processed successfully', TRUE);

MERGE INTO "payment_status_reference" ("code", "description", "is_active") KEY ("code") VALUES
    ('FAILED', 'Payment processing failed', TRUE);

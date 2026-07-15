CREATE TABLE IF NOT EXISTS "account" (
    "id" UUID PRIMARY KEY,
    "owner_name" VARCHAR(255) NOT NULL,
    "currency" VARCHAR(3) NOT NULL,
    "balance" NUMERIC(19,2) NOT NULL,
    "active" BOOLEAN NOT NULL,
    "created_at" TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_account_owner_name ON "account"("owner_name");

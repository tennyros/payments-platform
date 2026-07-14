# Branching Convention

## Purpose

This repository uses short-lived feature branches for all development work.

## Branch Names

- `dev` - integration branch for ongoing work
- `main` - stable branch for releases
- `feature/PAYMENTS-1-short-description` - feature branch
- `fix/PAYMENTS-2-short-description` - bugfix branch
- `chore/PAYMENTS-3-short-description` - maintenance or tooling work

## Rules

- Each task should map to one branch.
- Keep branch names aligned with ticket IDs when a tracker exists.
- Rebase or merge the feature branch into `dev` after review.
- Use a new branch for each feature or fix instead of stacking unrelated work.

## Examples

- `feature/PAYMENTS-1-account-balance`
- `feature/PAYMENTS-2-payment-initiation`
- `fix/PAYMENTS-14-null-response`
- `chore/PAYMENTS-20-gradle-update`

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
- Integrate feature, fix, and chore branches into `dev`.
- Prefer rebase before merge when the branch is local and not shared.
- Use merge when preserving branch history matters or the branch is already shared.
- Use a new branch for each feature or fix instead of stacking unrelated work.
- Batch small docs and spec edits into a single commit at the end of the task when practical.
- Do not split trivial documentation edits into many commits unless they belong to distinct tasks.

## Integration Flow

1. Create a branch from `dev`.
2. Implement the change and keep the branch synced with `dev` if needed.
3. Rebase onto `dev` before review when safe.
4. Merge the branch back into `dev` after approval.
5. Delete the branch after integration.

## Examples

- `feature/PAYMENTS-1-account-balance`
- `feature/PAYMENTS-2-payment-initiation`
- `fix/PAYMENTS-14-null-response`
- `chore/PAYMENTS-20-gradle-update`

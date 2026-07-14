# Specs

## How to Use

- `common.md` contains the product-wide specification.
- Every feature or fix gets its own spec file.
- The branch and spec IDs should match the ticket ID when available.

## Naming

- Feature spec: `feature/PAYMENTS-1-name.md`
- Fix spec: `fix/PAYMENTS-2-name.md`
- If no tracker exists yet, keep the same pattern with a local ID.
- The spec file name should match the branch ticket ID.

## Recommended Spec Structure

- Context
- Goal
- Scope
- Non-goals
- API and data model changes
- Implementation notes
- Testing notes
- Acceptance criteria

## Workflow

1. Update `common.md` when the product-level behavior changes.
2. Create or update the relevant feature/fix spec.
3. Implement the code on the matching branch.
4. Keep the spec and code aligned in the same commit set.
5. Reference the spec file in the PR description.

## Documentation Cadence

- Small documentation and spec edits may be accumulated locally during the task.
- Prefer a single grouped documentation commit at the end of the task or before switching to the next piece of work.
- Avoid leaving the code and spec out of sync for long periods.
- If a doc change affects implementation decisions, update the spec before merging the code.

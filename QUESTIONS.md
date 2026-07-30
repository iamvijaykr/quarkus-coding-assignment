# Questions

Here are 2 questions related to the codebase. There's no right or wrong answer - we want to understand your reasoning.

## Question 1: API Specification Approaches

When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded everything directly. 

What are your thoughts on the pros and cons of each approach? Which would you choose and why?

**Answer:**
```txt

OpenAPI-generated code (contract-first) and handwritten handlers each have clear trade-offs.

Pros (OpenAPI / generated):
- Contract-first: single source of truth for request/response shapes and status codes.
- Fast client/server stubs and SDK generation for consumers.
- Consistency across teams and fewer copy/paste mistakes.
- Good for stable, well-documented public APIs.

Cons (generated):
- Generators can produce awkward models or boilerplate that need manual cleanup.
- Harder to express rich domain logic inside generated controllers — often you still write adapters.
- Overhead when the API is small or rapidly changing.

Pros (handwritten):
- Full control of routing, validation, and business logic placement.
- Easier to optimize, refactor, and evolve internal APIs quickly.

Cons (handwritten):
- Risk of undocumented divergence between implementation and any external contract.
- More effort to keep client/server contracts synchronized and to produce SDKs.

Recommended approach: a hybrid, pragmatic model.
- Use OpenAPI generation for externally-facing or stable APIs (like `Warehouse`) to get contract-driven benefits.
- Hand-write endpoints when you need tight control over behavior, performance, or when the API is very small and evolving rapidly (like early-stage `Product`/`Store`).
- Keep a canonical OpenAPI spec (or snippets) for any API you intend clients to rely on and run contract tests against it.
- Invest in generator configuration, small adapter layers, and CI contract checks so generated code and handwritten logic remain consistent.
```

---


## Question 2: Testing Strategy

Given the need to balance thorough testing with time and resource constraints, how would you prioritize tests for this project? 

Which types of tests (unit, integration, parameterized, etc.) would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt

Follow a testing pyramid and focus effort where it gives most feedback for least cost.

1) Unit tests (highest priority)
- Fast, deterministic, cover domain rules, validation, and small helpers. Run in every PR.
- Use test doubles for external systems; assert behavior and edge cases (including parameterized tests for boundary inputs).

2) Contract & integration tests (second priority)
- Integration tests for persistence and transaction behavior (e.g., ensuring legacy gateway is not called on rollback).
- Contract tests (provider/consumer) for any external or generated API so client/servers stay compatible.
- Use Testcontainers in CI for full integration runs; allow a lightweight embedded DB (H2) for faster local dev runs.

3) End-to-end / smoke tests (lower frequency)
- Run a few representative end-to-end flows in a nightly or pre-release pipeline to catch system-level regressions.

Supporting practices
- Keep unit tests fast and reliable; run them on every commit. Run the full integration suite in CI on merges and nightly.
- Use seeded fixtures, test data builders, and deterministic seeds for reproducible integration tests.
- Monitor flaky tests and quarantine until fixed; enforce time budgets for test runs in PRs vs full pipelines.
- Track coverage as a guide (not a goal) and add targeted tests for uncovered complex logic; consider mutation testing selectively.

This mix keeps feedback fast for developers while preserving confidence for critical transactional and integration behavior.
```

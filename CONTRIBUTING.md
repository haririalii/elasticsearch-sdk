# Contributing

Thanks for improving Elasticsearch SDK.

## Development workflow

1. Create a focused branch from the default branch.
2. Make one logical change per pull request.
3. Add or update tests for behavior changes.
4. Run `mvn verify` before requesting review.
5. Describe the problem, solution, and verification in the pull request.

## Code standards

- Target Java 25 and keep public APIs backward compatible whenever practical.
- Use clear names, immutable values where possible, and SLF4J parameterized logging.
- Add JavaDoc for public types and public API methods whose behavior is not self-evident.
- Keep the library framework-neutral; Jakarta EE lifecycle and dependency-injection wiring belong in consuming
  applications.
- Never commit credentials, customer data, build output, or third-party binary JARs.

## Reporting bugs

Open a GitHub issue with the library version, Java version, Elasticsearch version, a minimal reproduction, and the
expected and actual behavior. Do not include secrets or sensitive data.

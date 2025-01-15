# Contributing to jAI Workflow

Thank you for considering contributing to jAI Workflow! We welcome contributions from the community and are excited to work with you.

## Getting Started

1. **Fork the repository**: Click the "Fork" button at the top right of the repository page.
2. **Clone your fork**:
    ```sh
    git clone https://github.com/your-username/jai-workflow.git
    cd jai-workflow
    ```
3. **Create a branch**:
    ```sh
    git checkout -b feature/your-feature-name
    ```

1. **Install dependencies**:
    ```sh
    mvn clean install
    ```

2. **Build the project**:
    ```sh
    mvn package
    ```
3. **Running Tests**:
   ```sh
   mvn test
   ```
## General guidelines
Here are some things to keep in mind for all types of contributions:

- Follow the ["fork and pull request"](https://docs.github.com/en/get-started/exploring-projects-on-github/contributing-to-a-project) workflow.
- Fill out the checked-in pull request template when opening pull requests. Note related issues and tag maintainers.
- Ensure your PR passes build and testing checks before requesting a review.
   - If you would like comments or feedback, please open an issue or discussion and tag a maintainer.
- Backwards compatibility is key. Your changes must not be breaking, except in case of critical bug and security fixes.
- Look for duplicate PRs or issues that have already been opened before opening a new one.
- Keep scope as isolated as possible. As a general rule, your changes should not affect more than one package at a time.

### Priorities
All [issues](https://github.com/czelabueno/jai-workflow/issues) are prioritized by maintainers. These will be reviewed in order of priority, with bugs being a higher priority than new features.

We have organized releases by year QUARTER. All features and bug fixes will be released progressively for testing and feedback in the corresponding quarter.

For example, the Q1-25 is planned to release the following feature list and we expect PRs related to these features to be merged by the end of Q1-25. The list is as follows:

#### Q1 2025 Features
- **Graph-Core**:
   - Split Nodes
   - Merge Nodes
   - Parallel transitions
   - Human-in-the-loop
- **Modular (Group of nodes)**:
   - Module
   - Remote Module
- **Integration**:
   - Model Context Protocol (MCP) integration as server and client.
   - Define remote module as MCP server.
- **API**:
   - Publish workflow as API (SSE for streaming runs and REST for sync runs).

### BugFixes
For bug fixes, please open up an issue before proposing a fix to ensure the proposal properly addresses the underlying problem. In general, bug fixes should all have an accompanying unit test that fails before the fix.

### New Features
For new features, please open up an issue before proposing a new feature to ensure the proposal aligns with the project's goals. Fill out the checked-in feature request template.

## Making Changes
1. **Write clear, concise commit messages:** Follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification.
2. **Ensure your code follows the style guidelines:** Use the existing code as a reference.
3. **Add tests:** Ensure that your changes are covered by tests.
4. **Update the documentation:** If your changes affect the existing JavaDoc, update the documentation accordingly.

## Submitting Changes
1. **Push your changes:**
    ```sh
    git push origin feature/your-feature-name
    ```
2. **Create a Pull Request:** Go to the repository on GitHub and click "New Pull Request". Fill out the template provided.

## Code of Conduct
Please note that this project is released with a [Contributor Code of Conduct](CODE_OF_CONDUCT.md). By participating in this project, you agree to abide by its terms.

## Additional Resources
- [Issue Tracker](https://github.com/czelabueno/jai-workflow/issues)
- [GitHub Discussions](https://github.com/czelabueno/jai-workflow/discussions)
- [Maven Documentation](https://maven.apache.org/guides/index.html)

Thank you for your contributions! 🎉

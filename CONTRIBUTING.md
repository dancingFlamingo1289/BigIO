#=========================================================
#  ____  _         ___ ___  
# | __ )(_) __ _  |_ _/ _ \ 
# |  _ \| |/ _` |  | | | | |
# | |_) | | (_| |  | | |_| |
# |____/|_|\__, | |___\___/ 
#          |___/            
# -> Heavy-Duty File Management Library
#=========================================================

# Contributing to BigIO

First off, thank you for considering contributing to **BigIO** ! It’s people like you who make open-source tools better for everyone. 

To maintain the architectural integrity and performance standards of this library, please follow these guidelines.

## Technical Standards

To keep the codebase consistent, all contributions must adhere to the following design patterns:

### 1. Singleton Enforcement
All Factories and Readers must implement the **Singleton** pattern with thread-safe initialization[cite: 1].
*   Use a `private static instance`.
*   The `getInstance()` method must be `public static synchronized`.
*   The constructor must be `private`.

### 2. Registry Integration
New "Big" file types must be integrated with the `BigFileRegistry`. 
*   Always use `BigFileRegistry.getSharedInstance(path, creator)` within your factory.
*   Ensure paths are normalized using `.getAbsolutePath()` before they reach the registry logic.

### 3. Error Handling
*   Do not let raw `IOException` or `ClassNotFoundException` leak into the API surface.
*   Wrap creation-time errors in a `RuntimeException` with a descriptive message inside Factories.
*   Wrap loading-time errors in a `BigFileReaderException` within Reader classes.

## Branching Policy
1.  **Fork** the repository and create your branch from `main`.
2.  **Naming Convention**: Use descriptive names like `feature/add-tiff-support` or `fix/registry-memory-leak`.
3.  **Documentation**: Every new class must include **Javadoc** (in English) with the `@author` tag.

## Pull Request Process
1.  **Update the README**: If you add a new file type (e.g., `BigVideo`), update the features list in the `README.md`.
2.  **Code Style**: 
    *   Use 4 spaces for indentation.
    *   Ensure all new classes are placed in the correct package (`bigio.fileFactories`, `bigio.fileReaders`, or `bigio.files`).
3.  **Self-Review**: Before submitting, ensure that your new `BigFile` subtype doesn't load the entire file into the heap immediately—remember, "Big" implies **Lazy Loading**.

## Code of Conduct
*   Be respectful and professional in all communications.
*   Focus on technical excellence and sustainable software design.

---

**Questions?** Reach out to the maintainer, **Elias Kassas**, or open an issue for architectural discussions.
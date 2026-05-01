```text
=========================================================
  ____  _         ___ ___  
 | __ )(_) __ _  |_ _/ _ \ 
 |  _ \| |/ _` |  | | | | |
 | |_) | | (_| |  | | |_| |
 |____/|_|\__, | |___\___/ 
          |___/            
 -> Heavy-Duty File Management Library
=========================================================
```

# Changelog
All notable changes to BigIO will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-05-01
### Added
- Initial architectural release of the BigIO library.
- Implementation of the `BigFileRegistry` for centralized instance management.
- Creation of `BigFileFactory` and `BigFileReader` abstractions.
- Support for specialized types: `BigAudio`, `BigImage`, `BigBinary`, `BigTabular`, and `BigText`.
- Singleton pattern enforcement for all Readers and Factories to optimize memory.
- Path normalization to handle absolute and relative file references.

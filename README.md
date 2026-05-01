#=========================================================
#  ____  _         ___ ___  
# | __ )(_) __ _  |_ _/ _ \ 
# |  _ \| |/ _` |  | | | | |
# | |_) | | (_| |  | | |_| |
# |____/|_|\__, | |___\___/ 
#          |___/            
# -> Heavy-Duty File Management Library
#=========================================================
**BigIO** is a robust Java library designed to handle file operations with a focus on memory efficiency and architectural cleanlines. It provides a specialized dual-track system to distinguish between standard assets and massive data files, ensuring your JVM remains stable even when dealing with high-resolution images, large audio buffers, or heavy binary data[cite: 1].

## 🚀 Key Features

*   **Dual-Layer Architecture**: Separate modules for `basicFiles` (lightweight) and `bigFiles` (resource-intensive) to optimize memory footprint.
*   **Instance Uniqueness (Registry)**: The `BigFileRegistry` ensures that a single physical file is represented by exactly one object in memory, preventing redundant allocations and synchronization conflicts.
*   **Lazy Loading Support**: Built-in logic for on-demand data retrieval, essential for processing massive datasets without full heap saturation.
*   **Path Normalization**: Automatic conversion of relative paths to absolute paths within the registry to maintain data integrity across different execution contexts.
*   **Decoupled Factories**: Implementation of the Factory Pattern to isolate instantiation logic from business logic, making the API easy to extend[cite: 1].

## 📂 Project Structure

The library is organized into two main packages based on performance requirements:

### 1. `basicFiles`
Designed for standard operations where files fit comfortably in memory.
*   **Supported Types**: Text, Tabular, Binary, Audio, and Image.
*   **Usage**: Best for UI icons, small configuration files, and short audio clips.

### 2. `bigFiles`
The core of the library, built for high-performance and large-scale data handling.
*   **Optimization**: Utilizes the `BigFileRegistry` for thread-safe instance sharing[cite: 1].
*   **Error Handling**: Centralized through `BigFileReaderException` to provide clear feedback during disk I/O or resource resolution failures[cite: 1].

## 🛠️ Getting Started

### Installation
Simply include the `bigio` source folders in your project and import the required components:

```java
import bigFiles.fileFactories.*;
import bigFiles.fileReaders.*;
```

### Usage Example: Loading a Large Image
```java
try {
    // Access the shared reader instance
    BigFile myImage = BigImageFileReader.getInstance().loadFromPath("assets/maps/world_4k.png");
    
    // The Registry ensures that subsequent calls for the same path return
    // the existing instance, saving significant RAM.
} catch (BigFileReaderException e) {
    System.err.println("Failed to load asset: " + e.getMessage());
}
```

### Loading from Classpath Resources
```java
// Seamlessly load files bundled within your JAR
BigFile data = BigTabularFileReader.getInstance().loadFromResources("data/stats.csv");
```

## 🏗️ Technical Architecture

BigIO follows strict software engineering principles:
*   **Singleton Pattern**: Readers and Factories are singletons to provide global access points and manage state consistently[cite: 1].
*   **Functional Integration**: Factories leverage Java's `Function` interface to pass instantiation logic into the Registry, keeping the system generic and flexible[cite: 1].
*   **Resource Safety**: All "Big" implementations are designed to interface with streams or random-access pointers rather than loading raw byte arrays[cite: 1].

---

## ⚖️ License & Credits
Developed by **Elias Kassas**.
Designed for sustainable software engineering and high-performance Java applications.
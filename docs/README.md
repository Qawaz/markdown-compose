# KMP-Storage

## Description

Storage For Kotlin Multiplatform

Supports 
- Android
- JVM

### Usage
```kotlin
    val file = StorageFile("path/file.png")
    val outputStream = file.getOutputStream()
    val anotherFile = StorageFile("anotherpath/anotherfile.png")
    val inputStream = file.getInputStream()
    outputStream.write(inputStream) // copies input stream to output stream
    outputStream.close()
    inputStream.close()

    // Storage file has extension methods like
    // getName & Extension that call Java Native Functions
```

StorageFile is a typealias for Java File on Android & JVM

StorageOutputStream & StorageInputStream are also typealiases for Java OutputStream & InputStreamon Java & JVM
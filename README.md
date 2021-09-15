# Compose Markdown

Compose Markdown is a jetpack compose markdown rendering and editing library which uses CommonMark (java , markdown parsing library)

It boasts following features

* Uses Jetpack Compose (Does not make use of any Android View)
* Syntax Highlighting using [Compose Code Editor](https://github.com/timeline-notes/compose-code-editor)
* Its compatible with LazyColumn , It does not initialize multiple instances of required classes when rendering in a Lazy Column
  Uses older instances but still provides a way to be able to alter the behaviour of one Preview Composable
  
## Usage
  
For preview use `MarkdownPreview`
  
```kotlin
MarkdownPreview(
    markdown = text
)
```

It has paramters `typography`,`colors`,`marker` (A class containing default styling) which will let you control how a node is rendered

# Markdown Compose

An awesome markdown viewer & editor that works on Web,JVM and Android. This library is used
with out app [Timeline](https://play.google.com/store/apps/details?id=com.wakaztahir.timeline) , A note taking app , so development decisions will be made based on that.

## [Live Preview](https://qawaz.github.io/markdown-compose)

## Preview

- [x] Headings,Paragraphs
- [x] Blockquotes
- [x] Links , Links in Headings
- [x] Ordered , Bullet List & Unordered List
- [ ] Tables

### Usage

```kotlin
MarkdownPreview(
    markdown = "# heading"
)
```

- MathJax works on Android & JVM , We will soon update on how to integrate with it
- CodeEditor / Code Highlighting for Preview , We will soon update on how to integrate with it.

#### Highlighting features of preview

#### Search inside markdown using modify
a simple lambda function that can modify the annotated string , The
use case of this is searching , for example user searched something in markdown and you want to highlight it
this function will give you the annotated string as parameter and you can modify it.
 
#### Check or uncheck list items in preview
you can give a lambda that takes the new checked state and updated markdown as parameters then set the new markdown , just like in our live demo.

## Editor

Currently Compose does not support multiline editing , so we've partially developed the markdown editor
with the following features

1 - Basic Text Editing (Bold,Italic,Strikethrough) Works but as soon as the text changes , styles go away

2 - Get Link Preview (pasting a link gets its preview and adds it to bottom)

3 - Code Editing (since we also have a library of compose-code-editor , we integrated it to allow code editing)

4 - Reorder list items using compose-reorderable

5 - Export back to markdown

### Usage

We will update further on how to use the Editor as we update the demo samples
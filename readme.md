
# PlantUML Tool

This tool builds `plantuml-tool.jar` which displays a GUI and
lets the user edit a PlantUML file (`plantuml-src.txt` in CWD).
After editing the file an .svg graph can be generated and modified
in a web view, then exported to a `.png` image.

This application runs on Java 11+. Older versions cannot be supported because JavaFX
will need to be built in a completely different manner.

# Building

```bash
gradle jar
```

# Running

```bash
java -jar app/build/libs/app.jar
```

# Controls

Set the `PLANTUML_SRC_F` environment variable to change the source file
which is modified in the "source" tab.




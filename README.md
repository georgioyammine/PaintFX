# PaintFX

PaintFX is an application in Java for **drawing**, **graphics editing**, and **image editing**, similar to MSPaint. 

### Author
This Tool is built by **Georgio Yammine**.

## Drawing tools
**Pen**, **Brush**, **Eraser**, **Color Picker**, **Bucket Fill**, along **2 color selectors** one for left click and the other for the right click.

## Shapes
**Rectangle**, **Round Rectangle**, **Ellipse**.

## Features
**Canvas zoom and drag**, **open/save image**,  **undo/redo**, **canvas resize**, **Escape to discard current drawing**.

## Getting PaintFx - Releases
Application builds can be found under [releases](https://github.com/georgioyammine/PaintFX/releases) for both Windows and Mac devices.

You can run the release by opening  bin/PaintFX[.bat]

### Source code
We have two important branches:

* **main** -> Java 11 and javaFx 11 and above (actively developed branch)
* **jdk-8** -> Java 8 (no longer in development)

## Getting Started
- ### Required Software dependencies
  1. JDK 11+: OpenJDK 11 (LTS) - JVM: Hotspot, AdoptOpenJDK 11, available at https://adoptopenjdk.net/.

- ### Dependencies automatically installed by Maven
  * org.openjfx JavaFX 16
  * org.jfxtras jmetro 11.6.15
  * org.openjfx javafx-swing 15.0.1

- ### Running Process
  1. Run via IDE launcher.Main
  2. Run via Maven: `mvn javafx:run`

- ### Building Process
  `mvn clean javafx:jlink`

  - #### cross building
    To create a cross platform build (for a different OS):
    First, you will need the JDKs for those other platforms. Download them as archives (zip or tar.gz), not installers from https://adoptopenjdk.net/releases.html, and unpack them to directories of your choice.
    Then pass this to the jmodsPath tag in the pom.xml file as follows:
    ``` 
    <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.6</version>
            <configuration>
                <launcher>Information Theory Calculator</launcher>
                <mainClass>launcher.Main</mainClass>
                <!--for building for cross platform include the platform jdk below (/Contents/Home/jmods)-->
                <jmodsPath>
                        <!--mac-->
                    C:/Program Files/AdoptOpenJDK/mac/OpenJDK11U-jdk_x64_mac_hotspot_11.0.11_9/jdk-11.0.11+9/Contents/Home/jmods
                </jmodsPath>
            </configuration>
        </plugin>
    ```

    After passing the target JDK, running `mvn clean javafx:jlink` will generate the image for the target OS. 
    
    Generated Image can be found under target/image.

## License

The project is licensed under GPL 3. See [LICENSE](https://github.com/georgioyammine/PaintFX/blob/main/LICENSE) file for the full license.

## Preview
![new project](https://user-images.githubusercontent.com/61707078/120885302-e6cdc500-c5f0-11eb-8e9f-f2bf27de6b9f.png)
![drawings](https://user-images.githubusercontent.com/61707078/120885317-fa792b80-c5f0-11eb-91ef-4a311e2e60cf.png)
![edit image](https://user-images.githubusercontent.com/61707078/120885329-04029380-c5f1-11eb-86c0-7c29f0d7de27.png)
![change size](https://user-images.githubusercontent.com/61707078/120885352-1d0b4480-c5f1-11eb-94c1-94d1d13a4190.png)



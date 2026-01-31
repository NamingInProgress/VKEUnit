# Usage Guide

## Running
To run tests, add this maven plugin
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>exec-maven-plugin</artifactId>
      <version>3.1.0</version>
      <executions>
        <execution>
          <id>run-custom-tests</id>
          <phase>test</phase>
          <goals>
            <goal>java</goal>
          </goals>
          <configuration>
            <mainClass>com.vke.TestRunner</mainClass>
            <classpathScope>test</classpathScope>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

## Configuration Options
```text
vke.test.disabledTags -> Disables tests with specific tags
vke.test.largeStackTrace -> Disables/Enables slightly larger stack traces
```
Usage: In VM options on your run task, add
```text
-Dvke.test.disabledTags=<tags>
```
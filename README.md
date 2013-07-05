atg-maven-plugin
================

using atg-maven-plugin to packaging to ear file.it very easy to use.
Config example:
================================================================================
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>atg-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>generate-sources</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>atg-assemble</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <atgHome>ATG</atgHome>
                    <finalName>atg-maven-plugin</finalName>
                    <j2eePath>webapp</j2eePath>
                    <atgConfigPath>config</atgConfigPath>
                    <atgMetaInfPath>META-INF</atgMetaInfPath>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
================================================================================

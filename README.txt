Addition for plugin attributes in pom.xml file
 parameter StandaloneAndRuninplace
     When StandaloneAndRuninplace==true will assemble both standalone and run-in-place EARs (also see comment in xml below)
 parameter pack
     When pack==true will assemble packed EAR archive.
	 
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>atg-maven-plugin</artifactId>
				<executions>
					<execution>
						<id>generate-sources</id>
						<phase>generate-sources</phase>
						<configuration>
							<atgHome>c:/work/ATG/ATG2007.1/</atgHome>
							<!-- By default (when StandaloneAndRuninplace==false or absent) assemble standalone EAR-->
							<StandaloneAndRuninplace>false</StandaloneAndRuninplace>
							<!-- IF StandaloneAndRuninplace==true EAR filenames will have "_dev" suffix for run-in-place(for developers server) and "_tes" suffix for -standalone(for testers server)-->
							<pack>false</pack>
						</configuration>
						<goals>
							<goal>run</goal>
						</goals>
					</execution>
				</executions>
				<dependencies>

				</dependencies>
			</plugin>
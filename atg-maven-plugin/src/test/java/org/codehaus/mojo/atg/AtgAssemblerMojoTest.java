package org.codehaus.mojo.atg;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class AtgAssemblerMojoTest extends AbstractMojoTestCase {
	/** {@inheritDoc} */
	protected void setUp() throws Exception {
		// required
		super.setUp();

	}

	/** {@inheritDoc} */
	protected void tearDown() throws Exception {
		// required
		super.tearDown();

	}

	/**
	 * @throws Exception
	 *             if any
	 */
	public void test_maven_project_should_not_return_null() throws Exception {
		File pom = getTestFile("src/test/resources/unit/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		DarMojo myMojo = (DarMojo) lookupMojo("run", pom);

		assertNotNull(myMojo);
	}
}

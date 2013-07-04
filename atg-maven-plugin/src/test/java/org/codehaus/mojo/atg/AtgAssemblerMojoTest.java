
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
     * @throws Exception if any
     */
    public void test_should_not_return_null_when_lookup_goal_atg_assemble() throws Exception {
        File pom = getTestFile("src/test/resources/unit/pom.xml");
        AtgAssemblerMojo myMojo = (AtgAssemblerMojo) lookupMojo("atg-assemble", pom);
        assertNotNull(myMojo);
    }

    /**
     * @throws Exception if any
     */
    public void test_should_return_default_atg_home_when_get_atg_home() throws Exception {
        File pom = getTestFile("src/test/resources/unit/pom.xml");
        AtgAssemblerMojo myMojo = (AtgAssemblerMojo) lookupMojo("atg-assemble", pom);
        assertEquals("D:\\GCWorkbench\\ATG", myMojo.atgHome.getPath());
    }
}


package org.codehaus.mojo.atg;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class AtgAssemblerMojoTest extends AbstractMojoTestCase {
    private File pom;
    private AtgAssemblerMojo myMojo;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        // required
        super.setUp();
        pom = getTestFile("src/test/resources/unit/pom.xml");
        myMojo = (AtgAssemblerMojo) lookupMojo("atg-assemble", pom);
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
        assertNotNull(myMojo);
    }

    /**
     * @throws Exception if any
     */
    public void test_should_return_default_atg_home_when_get_atg_home() throws Exception {
        assertEquals("D:\\GCWorkbench\\ATG", myMojo.atgHome.getPath());
    }
}

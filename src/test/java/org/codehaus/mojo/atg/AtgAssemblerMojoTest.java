
package org.codehaus.mojo.atg;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import static org.mockito.Mockito.*;

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
    public void test_should_return_atg_home_when_get_atg_home() throws Exception {
        assertEquals("ATG", myMojo.atgHome.getPath());
    }

    /**
     * @throws Exception if any
     */
    public void test_should_return_atg_config_path__when_get_atg_config_path() throws Exception {
        assertEquals("config", myMojo.atgConfigPath.getPath());
    }

    /**
     * @throws Exception if any
     */
    public void test_should_return_atg_meta_info_path__when_get_atg_meta_info() throws Exception {
        assertEquals("META-INF", myMojo.atgMetaInfPath.getPath());
    }

    /**
     * @throws Exception if any
     */
    public void test_should_return_atg_j2ee_info_path__when_get_j2ee_apps_path() throws Exception {
        assertEquals("webapp", myMojo.j2eePath.getPath());
    }

    /**
     * @throws Exception if any
     */
    public void test_should_throws_exception_when_get_atg_home_is_empty() throws Exception {
        AtgAssemblerMojo atgAssemblerMojo = mock(AtgAssemblerMojo.class);
        atgAssemblerMojo.atgHome = mock(File.class);
        when(atgAssemblerMojo.atgHome.getPath()).thenReturn("");

        assertEquals("", atgAssemblerMojo.atgHome.getPath());
    }

}

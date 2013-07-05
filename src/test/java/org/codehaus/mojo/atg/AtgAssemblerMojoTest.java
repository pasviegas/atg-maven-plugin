
package org.codehaus.mojo.atg;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

import static org.mockito.Mockito.*;

public class AtgAssemblerMojoTest extends AbstractMojoTestCase {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    public static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

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

    public void test_should_return_not_null_when_get_maven_project() {
        assertNotNull(myMojo.getMavenProject());
    }

    public void test_should_return_atg_project_dir_when_call_create_atg_project_dir() {
        if (isWindows()) {
            assertEquals("ATG\\app-ear", myMojo.createAtgProjectDir().getPath());
        }
        else if (isLinux()) {
            assertEquals("ATG/app-ear", myMojo.createAtgProjectDir().getPath());
        }
    }

    public void test_should_return_atg_config_dir_when_call_create_atg_config_dir() {
        if (isWindows()) {
            assertEquals("ATG\\app-ear\\config", myMojo.createAtgConfigDir().getPath());
        }
        else if (isLinux()) {
            assertEquals("ATG/app-ear/config", myMojo.createAtgConfigDir().getPath());
        }
    }

    public void test_should_return_atg_meta_info_dir_when_call_create_atg_meta_info_dir() {
        if (isWindows()) {
            assertEquals("ATG\\app-ear\\META-INF", myMojo.createAtgMetaInfoDir().getPath());
        }
        else if (isLinux()) {
            assertEquals("ATG/app-ear/META-INF", myMojo.createAtgMetaInfoDir().getPath());
        }
    }

    public void test_should_return_atg_lib_dir_when_call_create_atg_lib_dir() {
        if (isWindows()) {
            assertEquals("ATG\\app-ear\\lib", myMojo.createAtgLibDir().getPath());
        }
        else if (isLinux()) {
            assertEquals("ATG/app-ear/lib", myMojo.createAtgLibDir().getPath());
        }
    }

    public void test_should_return_atg_j2ee_apps_dir_when_call_create_atg_j2ee_apps_dir() {
        if (isWindows()) {
            assertEquals("ATG\\app-ear\\j2ee-apps", myMojo.createJ2eeAppsDir().getPath());
        }
        else if (isLinux()) {
            assertEquals("ATG/app-ear/j2ee-apps", myMojo.createJ2eeAppsDir().getPath());
        }
    }
}

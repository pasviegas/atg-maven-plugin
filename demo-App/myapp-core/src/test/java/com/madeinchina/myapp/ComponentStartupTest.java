
package com.madeinchina.myapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;

public class ComponentStartupTest extends TestCase {
    public void testComponentStartup() throws Exception {
        File configpath = NucleusTestUtils.getConfigpath(this.getClass(),
                "config");
        // Put test .properties file in configpath path
        Properties props = new Properties();
        File propFile = NucleusTestUtils.createProperties(
                "test/resources/config/atg/modules/ToBeTested",
                configpath, "com.madeinchina.myapp.ToBeTested", props);
        propFile.deleteOnExit();
        List initial = new ArrayList();
        initial.add("/test/resources/config/atg/modules/ToBeTested");
        NucleusTestUtils.createInitial(configpath, initial);
        Nucleus n = NucleusTestUtils.startNucleus(configpath);
        ToBeTested testComponent = null;
        try {
            testComponent = (ToBeTested) n
                    .resolveName("/test/resources/config/atg/modules/ToBeTested");
            assertNotNull("Could not resolve test componet", testComponent);
            assertTrue("Test component did not start up cleanly.",
                    testComponent.mCleanStart);

        } finally {
            n.stopService();
            assertNotNull(testComponent);
            assertFalse("Test component did not shut down cleanly.",
                    testComponent.mCleanStart);
            testComponent = null;
        }

    }

}

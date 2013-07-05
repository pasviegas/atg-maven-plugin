
package org.codehaus.mojo.atg;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.Iterator;
import java.util.List;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal atg-assemble
 * @phase generate-sources
 * @requiresDependencyResolution runtime
 */
public class AtgAssemblerMojo extends AbstractMojo {

    public static final String CONFIG = "/config";
    public static final String META_INF = "/META-INF";
    public static final String LIB = "/lib";
    public static final String J2EE_APPS = "/j2ee-apps";

    public static final String MANIFEST = "/MANIFEST.MF";

    /**
     * Root install location of ATG
     * 
     * @parameter expression="${atg.atgHome}" default-value="${env.ATG_HOME}"
     */
    protected File atgHome;

    /**
     * The enclosing project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject mavenProject;

    /**
     * The enclosing project.
     * 
     * @return enclosing project.
     */
    protected MavenProject getMavenProject() {
        return mavenProject;
    }

    /**
     * EAR Assembler path
     * 
     * @parameter expression="home/bin/runAssembler"
     * @required
     */
    protected String assemblerPath;

    /**
     * Config path
     * 
     * @parameter expression="${basedir}/config"
     * @required
     */
    protected File atgConfigPath;

    /**
     * j2ee-apps path
     * 
     * @parameter expression="${basedir}/j2ee-apps"
     * @required
     */
    protected File j2eePath;

    /**
     * META-INF path
     * 
     * @parameter expression="${basedir}/META-INF"
     * @required
     */
    protected File atgMetaInfPath;

    /**
     * The name of the DAR file to generate.
     * 
     * @parameter alias="earName" expression="${project.build.finalName}"
     * @required
     */
    protected String finalName;

    /**
     * The directory for the generated EAR.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected String outputDirectory;

    protected String suffix = "";

    protected String standalone_runinplace;

    protected File moduleRoot;

    /**
     * pack - should we get packed EAR or not
     * 
     * @parameter expression="${atg.pack}" default-value="false"
     */
    protected boolean pack;

    public void setPack(boolean pack) {
        this.pack = pack;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {

        try {
            initModule();
            initConfig();
            generateConfigpath(); // to Manifest file
            // generateClasspath(); //to Manifest file
            generateWebpath(); // to Manifest file
        } catch (IOException ioe) {
            throw new MojoExecutionException(ioe.getMessage());
        }
        try {
            copyDependencies();
        } catch (IOException ioe) {
            throw new MojoExecutionException(ioe.getMessage());
        }

        if (!StandaloneAndRuninplace) {
            suffix = "";

            standalone_runinplace = "-standalone";

            executeCommandLine(getCommandLine());
        } else {
            // for testers
            suffix = "_tes";

            standalone_runinplace = "-standalone";

            executeCommandLine(getCommandLine());
            // for developers
            this.pack = false;
            suffix = "_dev";

            standalone_runinplace = "-run-in-place";

            executeCommandLine(getCommandLine());
        }

        // packDar();
        // cleanup();
    }

    /**
     * Creates temporary ATG Module.
     * 
     * @throws IOException
     */
    protected void initModule() throws IOException {
        String[] dirs = {
                "", CONFIG, META_INF, LIB, J2EE_APPS
        };

        for (int i = 0; i < dirs.length; i++) {
            // File dir = new File(atgHome,
            // mavenProject.getParent().getArtifactId() + dirs[i]);
            File dir = new File(atgHome, mavenProject.getArtifactId() + dirs[i]);
            dir.mkdir();
            getLog().info("Dependency: " + dir.getAbsolutePath());
        }
    }

    /**
     * Creates configurations in temporary ATG module.
     * 
     * @throws IOException
     */
    protected void initConfig() throws IOException {
        // moduleRoot = new File(atgHome,
        // mavenProject.getParent().getArtifactId());
        moduleRoot = new File(atgHome, mavenProject.getArtifactId());

        getLog().info("Module Path: " + moduleRoot.getAbsolutePath());
        getLog().info("Config Path: " + atgConfigPath.getAbsolutePath());
        getLog().info("META-INF Path: " + atgMetaInfPath.getAbsolutePath());

        FileUtils.copyDirectoryToDirectory(atgConfigPath, moduleRoot);
        FileUtils.copyDirectoryToDirectory(atgMetaInfPath, moduleRoot);
        FileUtils.copyDirectoryToDirectory(j2eePath, moduleRoot);
    }

    /**
     * Generates config path for ATG Module
     * 
     * @throws IOException
     */
    protected void generateConfigpath() throws IOException {
        String configPathAttributes = "ATG-Config-Path: " + CONFIG;
        appendConfig(configPathAttributes);
    }

    /**
     * Generates classpath for ATG module
     * 
     * @throws IOException
     */
    protected void generateClasspath() throws IOException {
        generatePath("ATG-Class-Path:", "jar");
    }

    /**
     * Generate web path for ATG Module.
     * 
     * @throws IOException
     */
    protected void generateWebpath() throws IOException {
        generatePath("ATG-Web-Module:", "war");
    }

    private void generatePath(String pathAttribute, String attributeType)
            throws IOException {

        String libDir = J2EE_APPS;

        if (attributeType != null && attributeType.equals("jar")) {
            libDir = LIB;
        }

        Set artifacts = getMavenProject().getArtifacts();
        Iterator dependencies = artifacts.iterator();

        while (dependencies.hasNext()) {
            Artifact artifact = (Artifact) dependencies.next();

            if (artifact.getType() != null
                    && artifact.getType().equals(attributeType)) {
                String fileName = artifact.getFile().getName();
                pathAttribute += " " + libDir + "/" + fileName;
            }
        }

        appendConfig(pathAttribute);

    }

    private void appendConfig(String attributes) throws IOException {
        // File metaInf = new File(atgHome,
        // mavenProject.getParent().getArtifactId() + META_INF + MANIFEST);
        File metaInf = new File(atgHome, mavenProject.getArtifactId()
                + META_INF + MANIFEST);

        List collection = FileUtils.readLines(metaInf);
        collection.add(attributes);

        FileUtils.writeLines(metaInf, collection);
    }

    /**
     * Returns runAssembler location from ATG home.
     * 
     * @return
     * @throws MojoExecutionException
     */
    protected File getAssemblerExecutable() throws MojoExecutionException {
        File executableFile = new File(atgHome, assemblerPath
                + (SystemUtils.IS_OS_UNIX ? "" : ".bat"));

        if (!executableFile.isFile()) {
            throw new MojoExecutionException(executableFile.getAbsolutePath()
                    + " does not exist or is not a file");
        }

        return executableFile;
    }

    /**
     * Generates the commandline to be executed.
     * 
     * @return the commandline created
     * @throws MojoExecutionException if any failure occurs
     */
    protected Commandline getCommandLine() throws MojoExecutionException {
        Commandline commandLine = new Commandline();
        commandLine.setExecutable(getAssemblerExecutable().getAbsolutePath());

        File target = createTargetDyrectory();

        commandLine.setWorkingDirectory(target.getAbsolutePath());

        if (pack)
            commandLine.createArg().setLine("-pack");

        commandLine.createArg().setLine("-liveconfig");
        // commandLine.createArg().setLine(standalone_runinplace);
        commandLine.createArg().setLine(
                target.getAbsolutePath() + "\\" + finalName + suffix + ".ear");

        commandLine.createArg().setLine(
                "-m " + getMavenProject().getArtifactId());

        System.out.println("Cmd to run atg module assembler:\n"
                + commandLine.toString());

        return commandLine;
    }

    public File createTargetDyrectory() {
        File target = new File(outputDirectory);

        if (!target.exists())
            target.mkdir();
        return target;
    }

    /**
     * Executes a commandLine
     * 
     * @param commandline to execute
     * @throws MojoFailureException if any failure occurs.
     */
    protected void executeCommandLine(Commandline commandline)
            throws MojoFailureException {
        try {
            StreamConsumer errConsumer = getStreamConsumer("error");
            StreamConsumer infoConsumer = getStreamConsumer("info");

            int returncode = CommandLineUtils.executeCommandLine(commandline,
                    infoConsumer, errConsumer);

            String logmsg = "Return code: " + returncode;
            if (returncode != 0) {
                throw new MojoFailureException(logmsg);
            } else {
                getLog().info(logmsg);
            }
        } catch (CommandLineException e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    protected StreamConsumer getStreamConsumer(final String level) {
        StreamConsumer consumer = new StreamConsumer() {
            /*
             * (non-Javadoc)
             * @see
             * org.codehaus.plexus.util.cli.StreamConsumer#consumeLine(java.
             * lang.String)
             */
            public void consumeLine(String line) {
                if (level.equalsIgnoreCase("info")) {
                    getLog().info(line);
                } else {
                    getLog().error(line);
                }
            }
        };

        return consumer;
    }

    /**
     * StandaloneAndRuninplace - show if we need to build two different types of
     * EAR standalone and run-in-place both
     * 
     * @parameter expression="${atg.StandaloneAndRuninplace}"
     *            default-value="false"
     */
    protected boolean StandaloneAndRuninplace;

    public void setStandaloneAndRuninplace(boolean standaloneAndRuninplace) {
        StandaloneAndRuninplace = standaloneAndRuninplace;
    }

    /**
     * The Jar archiver.
     * 
     * @parameter 
     *            expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
     * @required
     */
    private JarArchiver jarArchiver;

    /**
     * The maven archiver to use.
     * 
     * @parameter
     */
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * Copy all dependencies into temporary ATG Module
     * 
     * @throws IOException
     */
    protected void copyDependencies() throws IOException {
        // File libFolder = new File(atgHome,
        // getMavenProject().getParent().getArtifactId() + LIB);
        // File j2eeFolder = new File(atgHome,
        // getMavenProject().getParent().getArtifactId() + J2EE_APPS);
        File libFolder = new File(atgHome, getMavenProject().getArtifactId()
                + LIB);
        File j2eeFolder = new File(atgHome, getMavenProject().getArtifactId()
                + J2EE_APPS);

        Set artifacts = getMavenProject().getArtifacts();
        Iterator dependencies = artifacts.iterator();

        while (dependencies.hasNext()) {
            Artifact artifact = (Artifact) dependencies.next();
            File file = artifact.getFile();
            if (artifact.getType() != null && artifact.getType().equals("jar")) {
                FileUtils.copyFileToDirectory(file, libFolder);
            } else {
                FileUtils.copyFileToDirectory(file, j2eeFolder);
            }
        }
    }

    /**
     * Creates DAR archive from temporary ATG Module.
     * 
     * @throws MojoFailureException
     */
    protected void packDar() throws MojoFailureException {
        try {
            MavenArchiver archiver = new MavenArchiver();
            final JarArchiver jarArchiver = getJarArchiver();
            archiver.setArchiver(jarArchiver);

            File darFile = new File(outputDirectory, finalName + ".dar");
            getLog().info("darFile = " + darFile.getAbsolutePath());

            archiver.setOutputFile(darFile);

            getLog().info("Module Root: " + moduleRoot.getAbsolutePath());

            File metaInf = new File(moduleRoot, META_INF + MANIFEST);

            getLog().info("METANIFEST: " + metaInf.getAbsolutePath());

            archive.setManifestFile(metaInf);

            archiver.getArchiver().addDirectory(moduleRoot);
            archiver.createArchive(getMavenProject(), archive);
        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    /**
     * Deletes temporary ATG Module
     * 
     * @throws MojoFailureException
     */
    protected void cleanup() throws MojoFailureException {
        try {
            FileUtils.deleteDirectory(moduleRoot);
        } catch (IOException ioe) {
            throw new MojoFailureException(ioe.getMessage());
        }
    }

    /**
     * Returns the {@link JarArchiver} implementation used to package the DAR
     * file.
     * <p/>
     * By default the archiver is obtained from the Plexus container.
     * 
     * @return the archiver
     */
    protected JarArchiver getJarArchiver() {
        return jarArchiver;
    }

    private File createDir(String childPath) {
        File dir = new File(atgHome, childPath);
        dir.mkdir();
        return dir;
    }

    public File createAtgProjectDir() {
        return createDir(mavenProject.getArtifactId());
    }

    public File createAtgConfigDir() {
        return createDir(mavenProject.getArtifactId() + CONFIG);
    }

    public File createAtgMetaInfoDir() {
        return createDir(mavenProject.getArtifactId() + META_INF);
    }

    public File createAtgLibDir() {
        return createDir(mavenProject.getArtifactId() + LIB);
    }

    public File createJ2eeAppsDir() {
        return createDir(mavenProject.getArtifactId() + J2EE_APPS);
    }

}

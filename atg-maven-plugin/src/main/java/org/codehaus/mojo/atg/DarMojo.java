package org.codehaus.mojo.atg;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.archiver.MavenArchiveConfiguration;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal run
 * @phase generate-sources
 * @requiresDependencyResolution runtime
 */
public class DarMojo extends AbstractDarMojo {

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
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {

		super.execute();

		try {
			copyDependencies();
		} catch (IOException ioe) {
			throw new MojoExecutionException(ioe.getMessage());
		}

		if (!StandaloneAndRuninplace) {
			suffix = "";

			standalone_runinplace = "-standalone";

			executeCommandLine(getCommandLine());
		}else{
			//for testers
			suffix = "_tes";

			standalone_runinplace = "-standalone";

			executeCommandLine(getCommandLine());
			//for developers
			this.pack = false;
			suffix = "_dev";
          
			standalone_runinplace = "-run-in-place";

			executeCommandLine(getCommandLine());			
		}
			

		// packDar();
		// cleanup();
	}

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

}

package com.bungleton.changes;

import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

public class ChangesMain {
	public static void main(String[] args) throws Exception
	{
		RepositorySystem system = new DefaultPlexusContainer().lookup(RepositorySystem.class);

		MavenRepositorySystemSession session = new MavenRepositorySystemSession();
		LocalRepository localRepo =
			new LocalRepository(System.getProperty("user.home") + "/.m2/repository");
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(localRepo));

		Dependency root = new Dependency(new DefaultArtifact(
				"org.apache.maven:maven-profile:2.2.1"), "compile");
		RemoteRepository central = new RemoteRepository("central", "default",
				"http://repo1.maven.org/maven2/");

		CollectRequest collectRequest = new CollectRequest().setRoot(root).addRepository(central);

		List<ArtifactResult> artifacts = system.resolveDependencies(session, collectRequest, null);
		for (ArtifactResult artifact : artifacts) {
			System.out.println(artifact.getArtifact().getFile());
		}
	}
}

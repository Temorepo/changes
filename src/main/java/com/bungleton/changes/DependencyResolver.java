package com.bungleton.changes;

import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import org.sonatype.aether.RepositoryException;
import org.sonatype.aether.RepositorySystem;

import org.sonatype.aether.artifact.Artifact;

import org.sonatype.aether.collection.CollectRequest;

import org.sonatype.aether.graph.Dependency;

import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;

import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResult;

import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.google.common.collect.Lists;

public class DependencyResolver {

    public DependencyResolver()
    {
        try {
            _system = new DefaultPlexusContainer().lookup(RepositorySystem.class);
        } catch (ComponentLookupException cle) {
            throw new RuntimeException(cle);
        } catch (PlexusContainerException pce) {
            throw new RuntimeException(pce);
        }

        _session = new MavenRepositorySystemSession();
        LocalRepository localRepo =
            new LocalRepository(System.getProperty("user.home") + "/.m2/repository");
        _session.setLocalRepositoryManager(_system.newLocalRepositoryManager(localRepo));
        _repo = new RemoteRepository("central", "default", "http://repo1.maven.org/maven2/");
    }

    public Iterable<Artifact> resolveDependencies (String artifactCoordinates)
        throws RepositoryException
    {
        CollectRequest req = new CollectRequest().
        	setRoot(new Dependency(new DefaultArtifact(artifactCoordinates), "compile")).
        	addRepository(_repo);
        List<ArtifactResult> results = _system.resolveDependencies(_session, req, null);
        List<Artifact> artifacts = Lists.newArrayListWithCapacity(results.size());
        for (ArtifactResult result : results) {
            artifacts.add(result.getArtifact());
        }
        return artifacts;
   }

    public Artifact resolveArtifact (String artifactCoordinates)
        throws RepositoryException
    {
        ArtifactRequest req = new ArtifactRequest().
            setArtifact(new DefaultArtifact(artifactCoordinates)).
            addRepository(_repo);
        ArtifactResult result = _system.resolveArtifact(_session, req);
        return result.getArtifact();
   }

   protected final MavenRepositorySystemSession _session;
   protected final RepositorySystem _system;
   protected final RemoteRepository _repo;
}

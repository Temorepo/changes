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

import org.sonatype.aether.resolution.ArtifactResult;

import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.google.common.base.Function;

import com.google.common.collect.Iterables;
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
        _repos.add(new RemoteRepository("central", "default", "http://repo1.maven.org/maven2/"));
    }

    public Iterable<Artifact> resolve (String artifactCoordinates)
        throws RepositoryException
    {
        Dependency root = new Dependency(new DefaultArtifact(artifactCoordinates), "compile");

        CollectRequest req = new CollectRequest().setRoot(root);
        for (RemoteRepository repo : _repos) {
            req.addRepository(repo);
        }
        return Iterables.transform(_system.resolveDependencies(_session, req, null),
            ARTIFACT_RESULT_TO_ARTIFACT);
   }


   protected static final Function<ArtifactResult, Artifact> ARTIFACT_RESULT_TO_ARTIFACT =
       new Function<ArtifactResult, Artifact>() {
           @Override public Artifact apply(ArtifactResult result) {
               return result.getArtifact();
           }
       };

   protected final MavenRepositorySystemSession _session;
   protected final RepositorySystem _system;
   protected final List<RemoteRepository> _repos = Lists.newArrayList();
}

package com.bungleton.changes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.aether.RepositoryException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.bungleton.changes.difference.ClassDifference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DependencyResolver
{

    public DependencyResolver ()
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

    public List<ClassDifference> diffArtifacts (String groupAndArtifact, String oldVersion,
        String newVersion) throws RepositoryException, IOException
    {
        return diffArtifacts(resolveArtifact(groupAndArtifact + ":" + oldVersion),
            resolveArtifact(groupAndArtifact + ":" + newVersion));
    }

    public List<ClassDifference> diffArtifacts (Artifact expected, Artifact resolved)
        throws IOException, ArtifactResolutionException
    {
        expected = resolveArtifact(expected);
        resolved = resolveArtifact(resolved);
        JarClasses oldClasses = new JarClasses(expected.getFile());
        JarClasses newClasses = new JarClasses(resolved.getFile());
        return oldClasses.findDifferences(newClasses);

    }

    public List<VersionConflict> findVersionConflicts (String artifactCoordinates)
        throws RepositoryException
    {
        CollectRequest req = createCollectRequest(artifactCoordinates);
        DependencyNode root = _system.collectDependencies(_session, req).getRoot();
        final Map<String, Artifact> resolved = Maps.newHashMap();
        for (DependencyNode child : root.getChildren()) {
            Artifact artifact = child.getDependency().getArtifact();
            resolved.put(makeUnversionedId(artifact), artifact);
        }
        List<VersionConflict> found = Lists.newArrayList();
        Set<Artifact> checked = Sets.newHashSet();
        for (DependencyNode child : root.getChildren()) {
            findVersionConflicts(resolved, child, found, checked);
        }
        return found;
    }

    /**
     * Recursively checks for differences between the versions in dependencies of child and those
     * found in resolved. We collect dependencies at each level of child check as parent
     * collectDependencies don't build the entire tree.
     */
    protected void findVersionConflicts (Map<String, Artifact> resolved, DependencyNode child,
        List<VersionConflict> conflicts, Set<Artifact> checked)
        throws DependencyCollectionException
    {
        Artifact checking = child.getDependency().getArtifact();
        if (!checked.add(checking)) {
            return;
        }
        CollectRequest req = createCollectRequest(checking);
        DependencyNode childRoot = _system.collectDependencies(_session, req).getRoot();
        for (DependencyNode grandchild : childRoot.getChildren()) {
            Artifact expected = grandchild.getDependency().getArtifact();
            Artifact found = resolved.get(makeUnversionedId(expected));
            if (found != null && !found.equals(expected)) {
                conflicts.add(new VersionConflict(checking, expected, found));
            }
            findVersionConflicts(resolved, grandchild, conflicts, checked);
        }
    }

    protected static String makeUnversionedId (Artifact artifact)
    {
        return artifact.getGroupId() + ":" + artifact.getArtifactId();
    }

    public Iterable<Artifact> resolveDependencies (String artifactCoordinates)
        throws RepositoryException
    {
        CollectRequest req = createCollectRequest(artifactCoordinates);
        List<ArtifactResult> results = _system.resolveDependencies(_session, req, null);
        List<Artifact> artifacts = Lists.newArrayListWithCapacity(results.size());
        for (ArtifactResult result : results) {
            artifacts.add(result.getArtifact());
        }
        return artifacts;
    }

    protected CollectRequest createCollectRequest (String artifactCoordinates)
    {
        return createCollectRequest(new DefaultArtifact(artifactCoordinates));
    }

    protected CollectRequest createCollectRequest (Artifact artifact)
    {
        return new CollectRequest().
            setRoot(new Dependency(artifact, "compile")).
            addRepository(_repo);
    }

    public Artifact resolveArtifact (String artifactCoordinates)
        throws RepositoryException
    {
        return resolveArtifact(new DefaultArtifact(artifactCoordinates));
    }

    public Artifact resolveArtifact (Artifact unresolved)
        throws ArtifactResolutionException
    {
        if (unresolved.getFile() != null) {
            return unresolved;
        }
        ArtifactRequest req = new ArtifactRequest().
            setArtifact(unresolved).
            addRepository(_repo);
        ArtifactResult result = _system.resolveArtifact(_session, req);
        return result.getArtifact();
    }

    protected final MavenRepositorySystemSession _session;
    protected final RepositorySystem _system;
    protected final RemoteRepository _repo;
}

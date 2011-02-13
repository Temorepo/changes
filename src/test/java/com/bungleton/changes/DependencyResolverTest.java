package com.bungleton.changes;

import static org.junit.Assert.assertEquals;
import java.util.List;

import org.junit.Test;
import org.sonatype.aether.RepositoryException;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

public class DependencyResolverTest
{
    @Test
    public void findVersionConflict ()
        throws RepositoryException
    {
        List<VersionConflict> conflicts =
            new DependencyResolver().findVersionConflicts("com.bungleton.changestest:testapp:1");
        assertEquals(1, conflicts.size());
        Artifact parent = new DefaultArtifact("com.bungleton.changestest:testcomp:jar:1");
        Artifact expected = new DefaultArtifact("com.bungleton.changestest:testlib:jar:1");
        Artifact resolved = new DefaultArtifact("com.bungleton.changestest:testlib:jar:2");
        assertEquals(conflicts.get(0), new VersionConflict(parent, expected, resolved));
    }



}

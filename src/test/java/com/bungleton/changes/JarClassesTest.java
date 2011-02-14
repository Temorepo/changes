package com.bungleton.changes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.sonatype.aether.RepositoryException;

import com.bungleton.changes.difference.ClassAdded;
import com.bungleton.changes.difference.ClassDifference;
import com.bungleton.changes.difference.ClassRemoved;
import com.bungleton.changes.difference.MethodAdded;

public class JarClassesTest
{

    @Test
    public void checkBasicDiff () throws RepositoryException, IOException
    {
        List<ClassDifference> diffs =
            new DependencyResolver().diffArtifacts("com.bungleton.changestest:testlib", "1", "2");
        assertEquals(3, diffs.size());
        assertTrue(diffs.contains(new ClassAdded("com.bungleton.changestest.lib.ThatThingAddedInV2")));
        assertTrue(diffs.contains(new ClassRemoved("com.bungleton.changestest.lib.ThatOtherThing")));
        assertTrue(diffs.contains(new MethodAdded("com.bungleton.changestest.lib.ThatThing", "void frobble(java.lang.String, int)")));
    }
}

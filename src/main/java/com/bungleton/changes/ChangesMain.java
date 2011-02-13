package com.bungleton.changes;

import java.io.IOException;
import java.util.List;

import org.sonatype.aether.RepositoryException;
import org.sonatype.aether.artifact.Artifact;

import com.bungleton.changes.difference.ClassDifference;

public class ChangesMain
{
    public static void main (String[] args)
        throws Exception
    {
        for (ClassDifference difference : diffArtifacts("com.samskivert:samskivert", "1.1", "1.2")) {
            System.out.println(difference);
        }
    }

    public static List<ClassDifference> diffArtifacts (String groupAndArtifact,
        String oldVersion, String newVersion)
        throws RepositoryException, IOException
    {
        DependencyResolver resolver = new DependencyResolver();
        Artifact oldJar = resolver.resolveArtifact(groupAndArtifact + ":" + oldVersion);
        JarClasses oldClasses = new JarClasses(oldJar.getFile());

        Artifact newJar = resolver.resolveArtifact(groupAndArtifact + ":" + newVersion);
        JarClasses newClasses = new JarClasses(newJar.getFile());

        return oldClasses.findDifferences(newClasses);
    }
}

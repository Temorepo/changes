package com.bungleton.changes;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.sonatype.aether.RepositoryException;
import org.sonatype.aether.artifact.Artifact;

import com.google.common.collect.Sets;

public class ChangesMain
{
    public static void main (String[] args)
        throws Exception
    {
        diffArtifacts("com.bungleton.changestest:testlib", "1", "2");
//        diffArtifacts("com.samskivert:samskivert", "1.1", "1.2");
    }

    protected static void diffArtifacts (String groupAndArtifact, String oldVersion,
        String newVersion)
        throws RepositoryException, IOException
    {
        DependencyResolver resolver = new DependencyResolver();
        Artifact oldJar = resolver.resolveArtifact(groupAndArtifact + ":" + oldVersion);
        JarClasses oldClasses = new JarClasses(oldJar.getFile());


        Artifact newJar = resolver.resolveArtifact(groupAndArtifact + ":" + newVersion);
        JarClasses newClasses = new JarClasses(newJar.getFile());

        System.out.println("Added: " + newClasses.findMissingClasses(oldClasses));
        System.out.println("Removed: " + oldClasses.findMissingClasses(newClasses));

        Set<String> intersection = Sets.newHashSet(newClasses.classes.keySet());
        intersection.retainAll(oldClasses.classes.keySet());
        for (String className : intersection) {
            List<String> differences =
                oldClasses.classes.get(className).findDifferences(newClasses.classes.get(className));
            if (differences.isEmpty()) {
                continue;
            }
            System.out.println(className + ":");
            for (String difference : differences) {
                System.out.println("  " + difference);
            }
        }
    }
}

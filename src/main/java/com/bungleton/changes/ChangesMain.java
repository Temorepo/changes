package com.bungleton.changes;

import java.util.List;
import java.util.Set;

import org.sonatype.aether.artifact.Artifact;

import com.google.common.collect.Sets;

public class ChangesMain
{
    public static void main (String[] args)
        throws Exception
    {
        DependencyResolver resolver = new DependencyResolver();

        Artifact oldJar = resolver.resolveArtifact("com.samskivert:samskivert:1.1");
        JarClasses oldClasses = new JarClasses(oldJar.getFile());


        Artifact newJar = resolver.resolveArtifact("com.samskivert:samskivert:1.2");
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

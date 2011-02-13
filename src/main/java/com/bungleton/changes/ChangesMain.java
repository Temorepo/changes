package com.bungleton.changes;

import org.sonatype.aether.artifact.Artifact;

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

        System.out.println("Added in new: " + newClasses.findMissingClasses(oldClasses));
        System.out.println("Removed in new: " + oldClasses.findMissingClasses(newClasses));

    }
}

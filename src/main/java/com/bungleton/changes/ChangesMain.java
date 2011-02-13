package com.bungleton.changes;

import org.sonatype.aether.artifact.Artifact;

public class ChangesMain {
	public static void main(String[] args) throws Exception
	{
        DependencyResolver resolver = new DependencyResolver();

		for (Artifact artifact : resolver.resolve("com.samskivert:samskivert:1.2")) {
			System.out.println(artifact.getFile());
		}
	}
}

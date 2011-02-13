package com.bungleton.changes;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.commons.EmptyVisitor;
import org.sonatype.aether.artifact.Artifact;

public class ChangesMain
{
    public static void main (String[] args)
        throws Exception
    {
        Artifact afact = new DependencyResolver().resolveArtifact("com.samskivert:samskivert:1.2");
        JarFile jar = new JarFile(afact.getFile(), false);
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().endsWith(".class")) {
                continue;
            }

            ClassReader reader = new ClassReader(jar.getInputStream(entry));
            reader.accept(new ClassAdapter(new EmptyVisitor()) {
                @Override
                public void visit (int version, int access, String name, String signature,
                    String superName, String[] interfaces)
                {
                    System.out.println(name);

                }

                @Override
                public FieldVisitor visitField (int access, String name, String desc,
                    String signature, Object value)
                {
                    System.out.println("  " + name);
                    return null;
                }
            }, 0);
        }
    }
}

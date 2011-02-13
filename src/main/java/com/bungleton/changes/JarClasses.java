package com.bungleton.changes;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.ImmutableMap;

public class JarClasses
{
    public final ImmutableMap<String, ChangesClass> classes;

    public JarClasses (File jarFile)
        throws IOException
    {
        JarFile jar = new JarFile(jarFile, false);
        Enumeration<JarEntry> entries = jar.entries();
        ImmutableMap.Builder<String, ChangesClass> builder = ImmutableMap.builder();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().endsWith(".class")) {
                continue;
            }

            ChangesClass changes = new ChangesClass(jar.getInputStream(entry));
            builder.put(changes.getName(), changes);
        }
        classes = builder.build();
    }

    /**
     * Returns the set of classes in this jar that aren't in other.
     */
    public Iterable<String> findMissingClasses (JarClasses other)
    {
        return ChangesClass.findMissing(classes.keySet(), other.classes.keySet());
    }
}

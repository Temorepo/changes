package com.bungleton.changes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Lists;

public class ChangesClass
{
    public ChangesClass (InputStream classBytes) throws IOException
    {
        _node = new ClassNode();
        new ClassReader(classBytes).accept(_node, ClassReader.SKIP_DEBUG);
    }

    public String getName()
    {
        return _node.name;
    }

    @Override
    public String toString ()
    {
        return "ChangesClass[" + getName() + "]";
    }

    public Iterable<String> findDifferences (ChangesClass other)
    {
        List<String> differences = Lists.newArrayList();
        ClassNode onode = other._node;
        if (_node.access != onode.access) {
            differences.add("Class accessibility");
        }
        return differences;
    }

    protected final ClassNode _node;
}

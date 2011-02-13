package com.bungleton.changes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

    public List<String> findDifferences (ChangesClass other)
    {
        List<String> differences = Lists.newArrayList();
        ClassNode onode = other._node;
        if (_node.access != onode.access) {
            differences.add("Class accessibility");
        }

        Set<String> ourMethodSignatures = makeMethodSet(_node);
        Set<String> otherMethodSignatures = makeMethodSet(onode);
        for (String sig : findMissing(ourMethodSignatures, otherMethodSignatures)) {
            differences.add("Removed " + sig);
        }
        for (String sig : findMissing(otherMethodSignatures, ourMethodSignatures)) {
            differences.add("Added " + sig);
        }
        return differences;
    }

    public static Iterable<String> findMissing (Set<String> source, Set<String> modified)
    {
        Set<String> removed = Sets.newHashSet(source);
        removed.removeAll(modified);
        return removed;
    }

    protected static Set<String> makeMethodSet (ClassNode node)
    {
        Set<String> otherMethodSignatures = Sets.newHashSet();
        @SuppressWarnings("unchecked")
        List<MethodNode> meths = node.methods;
        for (MethodNode meth : meths) {
            otherMethodSignatures.add(meth.name + " " + meth.signature);
        }
        return otherMethodSignatures;
    }

    protected final ClassNode _node;
}

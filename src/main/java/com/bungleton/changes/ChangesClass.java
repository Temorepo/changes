package com.bungleton.changes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.bungleton.changes.difference.ClassDifference;
import com.bungleton.changes.difference.MethodAdded;
import com.bungleton.changes.difference.MethodRemoved;
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
        return _node.name.replace('/', '.');
    }

    @Override
    public String toString ()
    {
        return "ChangesClass[" + getName() + "]";
    }

    public List<ClassDifference> findDifferences (ChangesClass other)
    {
        List<ClassDifference> differences = Lists.newArrayList();
        ClassNode onode = other._node;

        Set<String> ourMethodSignatures = makeMethodSet(_node);
        Set<String> otherMethodSignatures = makeMethodSet(onode);
        for (String sig : findMissing(ourMethodSignatures, otherMethodSignatures)) {
            differences.add(new MethodRemoved(getName(), sig));
        }
        for (String sig : findMissing(otherMethodSignatures, ourMethodSignatures)) {
            differences.add(new MethodAdded(getName(), sig));
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
        Set<String> sigs= Sets.newHashSet();
        @SuppressWarnings("unchecked")
        List<MethodNode> meths = node.methods;
        for (MethodNode meth : meths) {
            StringBuilder builder = new StringBuilder(Type.getReturnType(meth.desc).getClassName());
            builder.append(' ').append(meth.name).append('(');
            Type[] args = Type.getArgumentTypes(meth.desc);
            for (int ii = 0; ii < args.length; ii++) {
                builder.append(args[ii].getClassName());
                if (ii < args.length - 1){
                    builder.append(", ");
                }
            }
            builder.append(')');
            sigs.add(builder.toString());
        }
        return sigs;
    }

    protected final ClassNode _node;
}

package com.bungleton.changes;

import java.util.List;

import com.bungleton.changes.difference.ClassDifference;

public class ChangesMain
{
    public static void main (String[] args)
        throws Exception
    {
        DependencyResolver resolver = new DependencyResolver();
        List<VersionConflict> conflicts =
            resolver.findVersionConflicts("com.bungleton.changestest:testapp:1");
        for (VersionConflict conflict : conflicts) {
            System.out.println(conflict.parent + " expected version "
                + conflict.expected.getBaseVersion() + " for " + conflict.expected.getGroupId() + ":"
                + conflict.expected.getArtifactId() + " but got "
                + conflict.resolved.getBaseVersion());
            List<ClassDifference> differences =
                resolver.diffArtifacts(conflict.expected, conflict.resolved);
            System.out.println("Changes in " + conflict.resolved.getBaseVersion() + " from " + conflict.expected.getBaseVersion() + ":");
            for (ClassDifference diff : differences) {
                System.out.println("  " + diff);
            }
        }
    }
}

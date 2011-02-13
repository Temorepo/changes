package com.bungleton.changes;

import org.sonatype.aether.artifact.Artifact;

import com.google.common.base.Preconditions;

public class VersionConflict
{
    public final Artifact parent, expected, resolved;

    public VersionConflict (Artifact parent, Artifact expected, Artifact resolved)
    {
        Preconditions.checkNotNull(parent, "Parent must not be null");
        Preconditions.checkNotNull(expected, "Expected must not be null");
        Preconditions.checkNotNull(resolved, "Resolved must not be null");
        this.parent = parent;
        this.expected = expected;
        this.resolved = resolved;
    }

    @Override
    public String toString ()
    {
        return "VersionConflict [parent=" + parent + ", expected=" + expected + ", resolved="
            + resolved + "]";
    }

    @Override
    public boolean equals (Object obj)
    {
        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }
        VersionConflict oconf = (VersionConflict)obj;
        return eq(parent, oconf.parent) && eq(expected, oconf.expected)
            && eq(resolved, oconf.resolved);
    }

    @Override
    public int hashCode ()
    {
        int result = 12 * 37 + hash(parent);
        result = result * 37 + hash(expected);
        return result * 37 + hash(resolved);
    }

    // Ignore file and properties in the artifacts in our equals and hashCode.
    protected static boolean eq (Artifact one, Artifact two)
    {
        return one.getArtifactId().equals(two.getArtifactId())
            && one.getGroupId().equals(two.getGroupId())
            && one.getVersion().equals(two.getVersion())
            && one.getExtension().equals(two.getExtension())
            && one.getClassifier().equals(two.getClassifier());
    }

    protected static int hash (Artifact artifact)
    {
        int hash = 17;
        hash = hash * 31 + artifact.getGroupId().hashCode();
        hash = hash * 31 + artifact.getArtifactId().hashCode();
        hash = hash * 31 + artifact.getExtension().hashCode();
        hash = hash * 31 + artifact.getClassifier().hashCode();
        hash = hash * 31 + artifact.getVersion().hashCode();
        return hash;
    }

}

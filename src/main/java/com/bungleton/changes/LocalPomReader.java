package com.bungleton.changes;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class LocalPomReader implements WorkspaceReader
{
    public final File pom;
    public final String groupId, artifactId, version;

    public LocalPomReader(File pom) throws IOException
    {
        this.pom = pom;
        // TODO - xml charset detection - oh boy!
        String contents = Files.toString(pom, Charsets.UTF_8);
        groupId = extractTag(pom, "groupId", contents);
        artifactId = extractTag(pom, "artifactId", contents);
        version = extractTag(pom, "version", contents);
    }

    protected String extractTag (File pom, String tag, String contents)
    {
        Matcher matcher = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">").matcher(contents);
        Preconditions.checkArgument(matcher.find(), "Couldn't find groupId in '%s'", pom);
        return  matcher.group(1);
    }

    public String getArtifactCoordinates ()
    {
        return groupId + ":" + artifactId + ":" + version;
    }

    @Override
    public WorkspaceRepository getRepository ()
    {
        return new WorkspaceRepository("localPom", pom.getAbsolutePath());
    }

    @Override
    public File findArtifact (Artifact artifact)
    {
        if (isOurPom(artifact)) {
            return pom;
        }
        return null;
    }

    private boolean isOurPom (Artifact artifact)
    {
        return artifact.getGroupId().equals(groupId)
            && artifact.getArtifactId().equals(artifactId)
            && artifact.getBaseVersion().equals(version)
            && artifact.getExtension().equals("pom");
    }

    @Override
    public List<String> findVersions (Artifact artifact)
    {
        if (isOurPom(artifact)) {
            return Lists.newArrayList(version);
        }
        return Collections.emptyList();
    }

}

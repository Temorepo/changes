package com.bungleton.changes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.sonatype.aether.RepositoryException;

import com.bungleton.changes.difference.ClassDifference;
import com.bungleton.yarrgs.Positional;
import com.bungleton.yarrgs.Usage;
import com.bungleton.yarrgs.Yarrgs;
import com.google.common.collect.Lists;

public class ChangesMain
{
    @Usage("Pom file to check for version conflicts and changes. Defaults to 'pom.xml' in the current directory.")
    @Positional(optional=true)
    public File pom;

    public List<String> repository = Lists.newArrayList();

    public void run ()
        throws RepositoryException, IOException
    {
        if (pom == null) {
            pom = new File("pom.xml");
        }
        if (!pom.exists()) {
            System.err.println("'" + pom.getPath() + "' doesn't exist!");
            System.exit(1);
        }
        LocalPomReader pomReader = new LocalPomReader(pom);
        DependencyResolver resolver = new DependencyResolver().addWorkspaceReader(pomReader);
        for (String repo : repository) {
            resolver.addRemoteRepository(repo);
        }
        List<VersionConflict> conflicts =
            resolver.findVersionConflicts(pomReader.getArtifactCoordinates());
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
    public static void main (String[] args)
        throws Exception
    {
        Yarrgs.parseInMain(ChangesMain.class, args).run();
    }
}

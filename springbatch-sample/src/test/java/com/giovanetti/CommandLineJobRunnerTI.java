package com.giovanetti;

import com.giovanetti.support.ExternalConfiguration;
import com.giovanetti.support.rule.BatchProperties;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TemporaryFolder;
import org.springframework.batch.core.launch.support.CommandLineJobRunner;
import org.springframework.batch.core.launch.support.ExitCodeMapper;

public class CommandLineJobRunnerTI {

    @ClassRule
    public final static TemporaryFolder outputFile = new TemporaryFolder();

    @ClassRule
    public final static BatchProperties batchProperties = new BatchProperties()
            .addTechnicalHsql()
            .addFunctionalHsql()
            .add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL
                            .toString(),
                    "1"
            );

    /**
     * @see <a href="http://www.stefan-birkner.de/system-rules/index.html">System Rules</a>
     */
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void run() throws Exception {

        exit.expectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_COMPLETED);

        CommandLineJobRunner.main(new String[]{
                TestConfiguration.class.getName(),
                JobConfiguration.JOB_NAME.toString(),
                JobConfiguration.OUTPUT_FILE_PARAMETER + "=" + outputFile.getRoot().getPath()});

    }

    @Test
    public void runKO() throws Exception {

        exit.expectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR);

        CommandLineJobRunner.main(new String[]{
                TestConfiguration.class.getName(),
                JobConfiguration.JOB_NAME.toString()});

    }
}

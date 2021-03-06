package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.JobAlimentationTestConfiguration;
import com.giovanetti.sample.batch.item.User;
import com.giovanetti.support.batch.rule.BatchProperties;
import com.giovanetti.support.batch.template.ItemReaderTemplate;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static com.giovanetti.sample.batch.item.ItemHelper.listOf2Users;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JobAlimentationTestConfiguration.class})
@TestExecutionListeners(
        {DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class FlatFileItemReaderTest {

    public static StepExecution getStepExecution() throws IOException {
        inputFile = temporaryFolder.newFile();
        return MetaDataInstanceFactory.createStepExecution(
                new JobParametersBuilder().addString(JobAlimentationConfiguration.INPUT_FILE_PARAMETER,
                        inputFile.getPath()).toJobParameters());
    }

    private static File inputFile;

    @ClassRule
    public final static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    @Inject
    private ItemReaderTemplate<User> itemReader;

    @Test
    public void read() throws IOException {

        Files.write(inputFile.toPath(), Arrays.asList("1,prenom1,nom1", "2,prenom2,nom2"));

        assertThat(itemReader.readAll())
                .hasSize(2)
                .usingFieldByFieldElementComparator()
                .containsAll(listOf2Users());
    }

}

package org.jbehave.core.integration.steps;

import org.jbehave.core.annotations.Given;
import freemarker.template.*;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.integration.StoryRunner;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.StoryReporterBuilder;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.jbehave.core.reporters.Format.ANSI_CONSOLE;
import static org.jbehave.core.reporters.Format.TXT;
import static org.junit.Assert.assertEquals;

public class FiltringSteps {
    private final String dirName = "integration";
    private String testStoryAdr;
    private Configuration config;
    private Template template;
    private Writer output,fileOutput;
    private File storyFile;
    ClassLoader classLoader;
    StoryRunner testStory;

    @Given("I have config")
    public void crateConfig() throws IOException, URISyntaxException {
        // Configure freemarker
        classLoader = Thread.currentThread().getContextClassLoader();

        config = new Configuration();
        config.setDirectoryForTemplateLoading(
                new File(getFullPath(dirName))
        );

        // Create new Story
        testStoryAdr = getFullPath(dirName)+"/test.istory";
        storyFile = new File(testStoryAdr);
        // if not exist...
        if (!storyFile.exists()) {
            storyFile.createNewFile();
        }

        output = new StringWriter();
        fileOutput = new FileWriter(storyFile);
    }

    @When("I take story file '$template' as template")
    public void takeStoryFile(String filename) throws IOException {
        template = config.getTemplate(filename);

    }

    @When("I add meta info to the story: $MetaTable")
    public void setMetaInfo(ExamplesTable MetaTable) throws IOException, TemplateException {
        Map filters = new HashMap();
        for (Map<String,String> row : MetaTable.getRows()) {
            String meta = row.get("meta");
            String value = row.get("value");
            filters.put(meta, value);
        }
        template.process(filters,fileOutput);
        fileOutput.flush();
        fileOutput.close();
    }

    @When("I run story with meta filters: $FiltersTable")
    public void runStoryWithFilters(ExamplesTable FiltersTable) throws Throwable {
        List<String> filters = new ArrayList<String>();
        for (Map<String,String> row : FiltersTable.getRows()) {
            String filter = row.get("filter");
            filters.add(filter);
        }
        testStory = new StoryRunner();
        testStory.setSteps(new TestSteps());
        testStory.setFilters(filters);
        testStory.useStoriesAsStoryFinder(dirName + "/test.istory");
        testStory.setConfiguration(new MostUsefulConfiguration()
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withFormats(TXT)));
        testStory.run();

    }

    @Then("verify the following '$metaTegs'")
    public void checkResultTesting(String metaTegs) throws IOException {
        Story parser = new RegexStoryParser().parseStory(getStoryReport());
        String metas = getMetaTegs(parser);
        assertEquals(metaTegs, metas);
        //System.out.println("Meta - "+getMetaTegs(parser));
    }

    public String getFullPath(String path) throws URISyntaxException {
        URL url = classLoader.getResource(path);
        return url.getPath();
    }

    public String getMetaTegs(Story parser) {
        Meta globalMeta = parser.getMeta();
        String metaTegs = "";
        for (String teg : globalMeta.getPropertyNames()) {
            metaTegs += teg/*+":"+globalMeta.getProperty(teg)*/+";";
        }
        //metaTegs += "|";
        for (Scenario scenario : parser.getScenarios()) {
            //System.out.println(scenario.getTitle());
            Meta meta = scenario.getMeta();
            for (String teg : meta.getPropertyNames()) {
                metaTegs += teg/*+":"+meta.getProperty(teg)*/+";";
                //System.out.println("Meta["+teg+"]:"+meta.getProperty(teg));
            }
        }
        return metaTegs;
    }

    public String getStoryReport() throws IOException {
        File outputDirectory = testStory.configuration().storyReporterBuilder().outputDirectory();
        File reportFile = new File(outputDirectory.getPath()+"/integration.test.txt");
        assertTrue("Report file exist",reportFile.exists());
        return readFileToString(reportFile);
    }


}

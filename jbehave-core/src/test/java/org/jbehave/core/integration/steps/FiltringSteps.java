package org.jbehave.core.integration.steps;

import org.jbehave.core.annotations.Given;
import freemarker.template.*;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.integration.StoryRunner;
import org.jbehave.core.model.ExamplesTable;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FiltringSteps {
    private final String dirName = "integration";
    private String testStoryAdr;
    private Configuration config;
    private Template template;
    private Writer output,fileOutput;
    private File storyFile;
    ClassLoader classLoader;

    @Given("I have config")
    public void crateConfig() throws IOException, URISyntaxException {
        // Configure freemarker
        classLoader = Thread.currentThread().getContextClassLoader();

        config = new Configuration();
        config.setDirectoryForTemplateLoading(
                new File(getFullPath(dirName))
        );

        // Create new Story
        testStoryAdr = getFullPath(dirName)+"/test.story";
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
        StoryRunner test = new StoryRunner();
        test.setSteps(new TestSteps());
        test.setFilters(filters);
        test.useStoriesAsStoryFinder(dirName+"/test.story");
        test.run();
    }

    @Then("verify the following")
    public void checkResultTesting() {

    }

    public String getFullPath(String path) throws URISyntaxException {
        URL url = classLoader.getResource(path);
        return url.getPath();
    }
}

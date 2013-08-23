package org.jbehave.core.integration.steps;

import org.jbehave.core.annotations.Given;
import freemarker.template.*;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.integration.TestStories;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class FiltringSteps {
    private Configuration config;
    private Template template;
    private Writer output;
    ClassLoader classLoader;

    @Given("I have config")
    public void crateConfig() throws IOException, URISyntaxException {
        // Configure freemarker
        classLoader = Thread.currentThread().getContextClassLoader();

        config = new Configuration();
        config.setDirectoryForTemplateLoading(
                new File(getFullPath("integration"))
        );
        // Create new Story
        File storyFile = new File(
                getFullPath("integration/test.story")
        );
        assertTrue("Create new test.story", storyFile.createNewFile());
        output = new StringWriter();//new FileWriter(storyFile);
    }

    @When("I take story file '$filename' as template")
    public void takeStoryFile(String filename) throws IOException {
        template = config.getTemplate("story.ftl");

    }

    @When("I add meta info to the story: global is '<global>', first is '<first>'")
    public void setMetaInfo(String global) throws IOException, TemplateException {
        Map filters = new HashMap();
        filters.put("global", global);
        template.process(filters,output);
        System.out.println(output.toString());
    }

    @When("I run story with meta filters: <filters>")
    public void runStoryWithFilters(String filter) {
        List<String> filters = new ArrayList<String>();
        filters.add(filter);
        new TestStories(filters, "integration/test.story");
    }

    @Then("verify the following")
    public void checkResultTesting() {

    }

    public URI getFullPath(String path) throws URISyntaxException {
        URL url = classLoader.getResource("integration");
        return url.toURI();
    }
}

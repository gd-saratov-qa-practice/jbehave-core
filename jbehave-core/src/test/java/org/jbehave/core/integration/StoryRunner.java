package org.jbehave.core.integration;

import org.jbehave.core.Embeddable;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterConverters;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.XML;

public class StoryRunner implements Embeddable{
    private Embedder embedder = new Embedder();
    private List<String> stories;
    private Configuration configuration = configuration();

    public StoryRunner() {
       // Configure embedder
       embedder.embedderControls()
               .doGenerateViewAfterStories(true)
               .doIgnoreFailureInStories(true)
               .doIgnoreFailureInView(true)
               .useThreads(1)
               .useStoryTimeoutInSecs(60);
        embedder.useConfiguration(configuration);
    }

    public void setFilters(List<String> filters) {
        embedder.useMetaFilters(filters);
    }

    public void setSteps(Object Steps) {
        embedder.useStepsFactory(
                new InstanceStepsFactory(
                        configuration,
                        Steps)
        );
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    // --- Configure stories ---
    public void useStoriesAsPath(String story) {
        useStoriesAsPaths(Arrays.asList(story));
    }

    public void useStoriesAsStoryFinder(String story) {
        useStoriesAsPaths(
                new StoryFinder().findPaths(codeLocationFromClass(this.getClass()), story, "")
        );
    }

    public void useStoriesAsPaths(List<String> stories) {
        this.stories = stories;
    }
    // -------------------------


    public Configuration configuration() {
        return new MostUsefulConfiguration()
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withFormats(CONSOLE, XML));
    }

    public void useEmbedder(Embedder embedder) {
        this.embedder = embedder;
    }

    public List<String> storyPaths() {
        return this.stories;
    }

    public void run() throws Throwable {
        try {
            embedder.runStoriesAsPaths(storyPaths());
        } finally {
            embedder.generateCrossReference();
        }
    }
}

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.XML;

public class StoryRunner {
    private Embedder embedder;

   public StoryRunner() {
       // Configure embedder
       embedder = new Embedder();

       embedder.embedderControls()
               .doGenerateViewAfterStories(true)
               .doIgnoreFailureInStories(true)
               .doIgnoreFailureInView(true)
               .useThreads(1)
               .useStoryTimeoutInSecs(60);
   }

    public void setFilters(List<String> filters) {
        embedder.useMetaFilters(filters);
    }

    public void setSteps(Object Steps) {
        embedder.useStepsFactory(
                new InstanceStepsFactory(
                        configuration(),
                        Steps)
        );
    }

    public void runStoriesAsPath(String story) {
        embedder.runStoriesAsPaths(Arrays.asList(story));
    }

    public void runStoriesAsPaths(List<String> stories) {
        embedder.runStoriesAsPaths(stories);
    }

    public void runStoriesAsStoryFinder(String story) {
        runStoriesAsPaths(
                new StoryFinder().findPaths(codeLocationFromClass(this.getClass()), story, "")
        );
    }

    public Configuration configuration() {
        Class<?> embeddableClass = Embeddable.class;
        // Start from default ParameterConverters instance
        ParameterConverters parameterConverters = new ParameterConverters();
        // factory to allow parameter conversion and loading from external resources (used by StoryParser too)
        ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(new LocalizedKeywords(), new LoadFromClasspath(embeddableClass), parameterConverters);
        // add custom converters
        parameterConverters.addConverters(new ParameterConverters.DateConverter(new SimpleDateFormat("yyyy-MM-dd")),
                new ParameterConverters.ExamplesTableConverter(examplesTableFactory));
        return new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath(embeddableClass))
                .useStoryParser(new RegexStoryParser(examplesTableFactory))
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withCodeLocation(CodeLocations.codeLocationFromClass(embeddableClass))
                        .withDefaultFormats()
                        .withFormats(CONSOLE, XML))
                .useParameterConverters(parameterConverters);
    }
}

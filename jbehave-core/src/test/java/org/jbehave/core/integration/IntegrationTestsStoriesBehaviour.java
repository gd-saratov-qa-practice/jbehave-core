package org.jbehave.core.integration;

import org.jbehave.core.integration.steps.FiltringSteps;
import org.junit.Test;

public class IntegrationTestsStoriesBehaviour{

    @Test
    public void execute() throws Throwable {
        // Filtering test
        StoryRunner test = new StoryRunner();
        test.setSteps(new FiltringSteps());
        test.useStoriesAsStoryFinder("integration/integ_filtring.istory");
        test.run();

    }
}

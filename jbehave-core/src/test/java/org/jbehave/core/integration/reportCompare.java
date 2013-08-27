package org.jbehave.core.integration;

import java.util.ArrayList;
import java.util.List;

public class reportCompare {
   private String storyPath;

   public void buildReport() {

   }

   class Story {
       public String path;
       public String title="";
       public List<Meta> metas = new ArrayList<Meta>();
       public List<Scenario> scenarios = new ArrayList<Scenario>();

       Story(String path) {
           this.path = path;
       }
   }

   class Scenario {
       public String keyword = "Scenario";
       public String title;
       public List<Meta> metas = new ArrayList<Meta>();
       public List<Step> steps = new ArrayList<Step>();

       Scenario(String title) {
           this.title = title;
       }
   }

   class Meta {
       public String keyword="@";
       public String name;
       public String value;

       Meta (String name, String value) {
           this.name = name;
           this.value = value;
       }

       Meta(String name) {
           this.name = name;
           this.value = "";
       }
   }

   class Step {
       public String outcome;
       public String value;

       Step(String outcome, String value) {
           this.outcome = outcome;
           this.value = value;
       }
   }
}

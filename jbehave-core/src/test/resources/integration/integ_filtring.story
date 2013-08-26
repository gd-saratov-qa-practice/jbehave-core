
Scenario: Check freemarker

Given I have config
When I take story file 'test.ftl' as template
And I add meta info to the story:
|meta|value|
|global|@global|
|first|@first|
|second|@second|
|third|@second|
|fourth|@fourth|
And I run story with meta filters:
|filter|
|-first|
Then verify the following
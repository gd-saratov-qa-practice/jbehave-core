
Scenario: Check filters 1

Given I have config
When I take story file 'test.ftl' as template
And I add meta info to the story:
|meta|value|
|global|@global|
|first|@first|
|second|@second|
|third|@third@foo|
|fourth|@fourth@foo|
And I run story with meta filters:
|filter|
|-foo|
|-second|
Then verify the following 'global;first;'

Scenario: Check filters 2

Given I have config
When I take story file 'test.ftl' as template
And I add meta info to the story:
|meta|value|
|global|@global|
|first|@first|
|second|@second|
|third|@third@foo|
|fourth|@fourth@foo|
And I run story with meta filters:
|filter|
|-foo|
Then verify the following 'global;first;second;'

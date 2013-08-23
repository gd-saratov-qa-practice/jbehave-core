Scenario: Check freemarker
Given I have config
When I take story file 'test.ftl' as template
And I add meta info to the story: global is '<global>', first is '<first>'
And I run story with meta filters: <filters>
Then verify the following

Examples:
|global|first|filter|
|@global|@first|-first|

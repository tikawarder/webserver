package databaseserver.ai;

import java.util.List;

record EvalCase(String jobDescription, List<String> expectedRequiredSkills) {}

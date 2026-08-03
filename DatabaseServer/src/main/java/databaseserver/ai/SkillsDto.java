package databaseserver.ai;

import java.util.List;

public record SkillsDto(
        List<String> requiredSkills,
        List<String> niceToHave,
        String seniority
) {}

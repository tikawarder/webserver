package databaseserver.ai.agent;

import dev.langchain4j.service.SystemMessage;

/*
 * A declarative LangChain4j "AI Service" — no method body here. AiServices.builder(...)
 * (see AiConfig.jobApplicationAgent()) generates a runtime proxy that turns a call to
 * helpWithApplication() into a ReAct-style loop: the model reads the system prompt and
 * the tool descriptions from AgentTools, decides which tool to call next, reads the
 * result, and repeats until it has enough information to produce a final answer.
 */
public interface JobApplicationAgent {

    @SystemMessage("""
            You are an assistant helping a candidate apply to a job. Given a job posting URL,
            use your tools to: 1) fetch the posting, 2) extract required skills, 3) check the
            candidate's matching experience, 4) draft a short cover letter section using the
            required skills and the matching experience you found. Think step by step and only
            use the tools provided. Return only the final cover letter paragraph.
            """)
    String helpWithApplication(String jobPostingUrl);
}

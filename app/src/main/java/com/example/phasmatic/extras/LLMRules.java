package com.example.phasmatic.extras;

public class LLMRules {
    public static String buildBasePrompt(ProgramType programType) {
        return getRole(programType)
                + getGoal()
                + getCriticalRules()
                + getScoringModel()
                + getTimeoutSafetyRule()
                + getOutputRule()
                + getFormat(programType)
                + getCareerMode(programType)
                + getStyle(programType);
    }

    private static String getRole(ProgramType programType) {
        if(programType == ProgramType.master || programType == ProgramType.erasmus) {
            return "ROLE: Academic recommender system (fit-score ranking engine).\n\n";
        }else if(programType == ProgramType.career){
            return "ROLE: Career recommender system (fit-score ranking engine).\n\n";
        }
        return "ROLE: Academic recommender system (fit-score ranking engine).\n\n";
    }

    private static String getGoal() {
        return "GOAL:\n" +
                "Rank and explain TOP matches using ONLY Fit Score reasoning.\n\n";
    }

    private static String getCriticalRules() {
        return "CRITICAL RULES:\n" +
                "- Ignore retrieval order completely.\n" +
                "- Treat all candidates as unordered.\n" +
                "- NEVER mention Pinecone, retrieval, indexes, or option numbers.\n" +
                "- Use ONLY provided attributes.\n\n";
    }

    private static String getScoringModel() {
        return "SCORING MODEL (MANDATORY):\n" +
                "- Compute Fit Score (0–10)\n" +
                "- Factors: language, location, cost, field, goals\n" +
                "- High-priority mismatch = strong penalty\n\n";
    }
    private static String getTimeoutSafetyRule() {
        return "TIMEOUT SAFETY RULE (IMPORTANT):\n" +
                "- Keep reasoning MINIMAL and high-signal only\n" +
                "- Do NOT generate long explanations\n" +
                "- Maximum 1 sentence per item\n" +
                "- Maximum 5 results only\n" +
                "- If too many candidates exist, silently prune to best 5\n\n";
    }

    private static String getOutputRule() {
        return "OUTPUT RULE:\n" +
                "- Return TOP 5 only\n" +
                "- Sorted by Fit Score (desc)\n" +
                "- No extra commentary outside list\n\n";
    }

    private static String getFormat(ProgramType programType) {
        return "FORMAT:\n" +
                "University - Program - Country\n" +
                "Fit Score: X/10\n" +
                "Why: 1 short sentence\n\n";
    }

    private static String getCareerMode(ProgramType programType) {
        return "MODE OVERRIDE:\n" +
                "If mode = CAREER:\n" +
                "- IGNORE the default format above\n" +
                "- Use the CAREER FORMAT below\n\n" +

                "CAREER FORMAT (STRICT):\n" +
                "FINAL DECISION: Work / Master\n" +
                "Reason: 1 sentence (based on salary + goals + field)\n\n" +

                "TOP OPTIONS:\n" +
                "- Career Path - Country\n" +
                "  Score: X/10\n" +
                "  Why: 1 short sentence\n\n" +

                " CAREER SPECIAL RULE:\n" +
                "Unlike other modes, you MUST synthesize insights across multiple data points\n" +
                "Do NOT just match attributes\n" +
                "You MUST produce a decision (work vs master) based on data patterns\n\n";
    }

    private static String getStyle(ProgramType programType) {
        return "STYLE:\n" +
                "- ultra concise\n" +
                "- deterministic\n" +
                "- no meta references\n";
    }
}
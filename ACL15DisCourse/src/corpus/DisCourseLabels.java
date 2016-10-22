package corpus;

import java.util.*;

public class DisCourseLabels {
	
	public static String GetFineGrainedLabelHead(String theOrginalLabel)
	{
		Map<String, String> labelMap = new HashMap<String, String>();
		
		labelMap.put("span", "Nucleus");
		labelMap.put("elaboration-additional", "Satellite");
		labelMap.put("List", "Nucleus");
		labelMap.put("attribution", "Satellite");
		labelMap.put("Same-Unit", "Nucleus");
		labelMap.put("elaboration-object-attribute-e", "Satellite");
		labelMap.put("elaboration-additional-e", "Satellite");
		labelMap.put("elaboration-process-step-e", "Satellite");
		labelMap.put("background-e", "Satellite");
		labelMap.put("Contrast", "Nucleus");
		labelMap.put("circumstance", "Satellite");
		labelMap.put("explanation-argumentative", "Satellite");
		labelMap.put("purpose", "Satellite");
		labelMap.put("Root", "Root");
		labelMap.put("elaboration-general-specific", "Satellite");
		labelMap.put("antithesis", "Satellite");
		labelMap.put("Sequence", "Nucleus");
		labelMap.put("TextualOrganization", "Nucleus");
		labelMap.put("example", "Satellite");
		labelMap.put("concession", "Satellite");
		labelMap.put("consequence-s", "Satellite");
		labelMap.put("Comparison", "Nucleus");
		labelMap.put("background", "Satellite");
		labelMap.put("condition", "Satellite");
		labelMap.put("interpretation-s", "Satellite");
		labelMap.put("reason", "Satellite");
		labelMap.put("evidence", "Satellite");
		labelMap.put("evaluation-s", "Satellite");
		labelMap.put("comment", "Satellite");
		labelMap.put("Topic-Shift", "Nucleus");
		labelMap.put("Temporal-Same-Time", "Nucleus");
		labelMap.put("Topic-Drift", "Nucleus");
		labelMap.put("comparison", "Satellite");
		labelMap.put("result", "Satellite");
		labelMap.put("consequence-n", "Satellite");
		labelMap.put("attribution-e", "Satellite");
		labelMap.put("Cause-Result", "Nucleus");
		labelMap.put("means", "Satellite");
		labelMap.put("Problem-Solution", "Nucleus");
		labelMap.put("manner", "Satellite");
		labelMap.put("temporal-after", "Satellite");
		labelMap.put("temporal-same-time", "Satellite");
		labelMap.put("elaboration-set-member", "Satellite");
		labelMap.put("restatement", "Satellite");
		labelMap.put("elaboration-object-attribute", "Satellite");
		labelMap.put("summary-n", "Satellite");
		labelMap.put("attribution-n", "Satellite");
		labelMap.put("elaboration-general-specific-e", "Satellite");
		labelMap.put("restatement-e", "Satellite");
		labelMap.put("definition", "Satellite");
		labelMap.put("cause", "Satellite");
		labelMap.put("Consequence", "Nucleus");
		labelMap.put("circumstance-e", "Satellite");
		labelMap.put("Question-Answer", "Nucleus");
		labelMap.put("elaboration-set-member-e", "Satellite");
		labelMap.put("example-e", "Satellite");
		labelMap.put("purpose-e", "Satellite");
		labelMap.put("Statement-Response", "Nucleus");
		labelMap.put("Disjunction", "Nucleus");
		labelMap.put("hypothetical", "Satellite");
		labelMap.put("temporal-before", "Satellite");
		labelMap.put("elaboration-part-whole", "Satellite");
		labelMap.put("enablement", "Satellite");
		labelMap.put("Otherwise", "Nucleus");
		labelMap.put("contingency", "Satellite");
		labelMap.put("Inverted-Sequence", "Nucleus");
		labelMap.put("summary-s", "Satellite");
		labelMap.put("condition-e", "Satellite");
		labelMap.put("rhetorical-question", "Satellite");
		labelMap.put("interpretation-n", "Satellite");
		labelMap.put("antithesis-e", "Satellite");
		labelMap.put("elaboration-part-whole-e", "Satellite");
		labelMap.put("evaluation-n", "Satellite");
		labelMap.put("analogy", "Satellite");
		labelMap.put("concession-e", "Satellite");
		labelMap.put("Reason", "Nucleus");
		labelMap.put("preference", "Satellite");
		labelMap.put("definition-e", "Satellite");
		labelMap.put("Evaluation", "Nucleus");
		labelMap.put("comment-e", "Satellite");
		labelMap.put("manner-e", "Satellite");
		labelMap.put("consequence-s-e", "Satellite");
		labelMap.put("Topic-Comment", "Nucleus");
		labelMap.put("comparison-e", "Satellite");
		labelMap.put("problem-solution-s", "Satellite");
		labelMap.put("means-e", "Satellite");
		labelMap.put("interpretation-s-e", "Satellite");
		labelMap.put("problem-solution-n", "Satellite");
		labelMap.put("temporal-after-e", "Satellite");
		labelMap.put("conclusion", "Satellite");
		labelMap.put("Comment-Topic", "Nucleus");
		labelMap.put("evaluation-s-e", "Satellite");
		labelMap.put("result-e", "Satellite");
		labelMap.put("consequence-n-e", "Satellite");
		labelMap.put("analogy-e", "Satellite");
		labelMap.put("question-answer-n", "Satellite");
		labelMap.put("topic-shift", "Satellite");
		labelMap.put("temporal-same-time-e", "Satellite");
		labelMap.put("explanation-argumentative-e", "Satellite");
		labelMap.put("topic-drift", "Satellite");
		labelMap.put("reason-e", "Satellite");
		labelMap.put("statement-response-s", "Satellite");
		labelMap.put("otherwise", "Satellite");
		labelMap.put("Analogy", "Nucleus");
		labelMap.put("Interpretation", "Nucleus");
		labelMap.put("elaboration-process-step", "Satellite");
		labelMap.put("statement-response-n", "Satellite");
		labelMap.put("question-answer-s", "Satellite");
		labelMap.put("Proportion", "Nucleus");
		labelMap.put("enablement-e", "Satellite");
		labelMap.put("evidence-e", "Satellite");
		labelMap.put("temporal-before-e", "Satellite");
		labelMap.put("preference-e", "Satellite");
		
		if(labelMap.containsKey(theOrginalLabel))
		{
			return labelMap.get(theOrginalLabel);
		}
		else
		{
			return null;
		}
	}
	
	
	public static String GetCoarseGrainedLabelsWithHead(String theOrginalLabel)
	{
		Map<String, String> labelMap = new HashMap<String, String>();
		
		labelMap.put("span", "span#N");
		labelMap.put("elaboration-additional", "elab#S");
		labelMap.put("List", "list#N");
		labelMap.put("attribution", "attr#S");
		labelMap.put("Same-Unit", "same#N");
		labelMap.put("elaboration-object-attribute-e", "elab#S");
		labelMap.put("elaboration-additional-e", "elab#S");
		labelMap.put("elaboration-process-step-e", "elab#S");
		labelMap.put("background-e", "back#S");
		labelMap.put("Contrast", "cont#N");
		labelMap.put("circumstance", "back#S");
		labelMap.put("explanation-argumentative", "evid#S");
		labelMap.put("purpose", "purp#S");
		labelMap.put("Root", "Root");
		labelMap.put("elaboration-general-specific", "elab#S");
		labelMap.put("antithesis", "cont#S");
		labelMap.put("Sequence", "temp#N");
		labelMap.put("TextualOrganization", "text#N");
		labelMap.put("example", "elab#S");
		labelMap.put("concession", "cont#S");
		labelMap.put("consequence-s", "cause#S");
		labelMap.put("Comparison", "comp#N");
		labelMap.put("background", "back#S");
		labelMap.put("condition", "cond#S");
		labelMap.put("interpretation-s", "eval#S");
		labelMap.put("reason", "evid#S");
		labelMap.put("evidence", "evid#S");
		labelMap.put("evaluation-s", "eval#S");
		labelMap.put("comment", "eval#S");
		labelMap.put("Topic-Shift", "topic#N");
		labelMap.put("Temporal-Same-Time", "temp#N");
		labelMap.put("Topic-Drift", "topic#N");
		labelMap.put("comparison", "comp#S");
		labelMap.put("result", "cause#S");
		labelMap.put("consequence-n", "cause#S");
		labelMap.put("attribution-e", "attr#S");
		labelMap.put("Cause-Result", "cause#N");
		labelMap.put("means", "mann#S");
		labelMap.put("Problem-Solution", "prob#N");
		labelMap.put("manner", "mann#S");
		labelMap.put("temporal-after", "temp#S");
		labelMap.put("temporal-same-time", "temp#S");
		labelMap.put("elaboration-set-member", "elab#S");
		labelMap.put("restatement", "summ#S");
		labelMap.put("elaboration-object-attribute", "elab#S");
		labelMap.put("summary-n", "summ#S");
		labelMap.put("attribution-n", "attr#S");
		labelMap.put("elaboration-general-specific-e", "elab#S");
		labelMap.put("restatement-e", "summ#S");
		labelMap.put("definition", "elab#S");
		labelMap.put("cause", "cause#S");
		labelMap.put("Consequence", "cause#N");
		labelMap.put("circumstance-e", "back#S");
		labelMap.put("Question-Answer", "prob#N");
		labelMap.put("elaboration-set-member-e", "elab#S");
		labelMap.put("example-e", "elab#S");
		labelMap.put("purpose-e", "purp#S");
		labelMap.put("Statement-Response", "prob#N");
		labelMap.put("Disjunction", "list#N");
		labelMap.put("hypothetical", "cond#S");
		labelMap.put("temporal-before", "temp#S");
		labelMap.put("elaboration-part-whole", "elab#S");
		labelMap.put("enablement", "purp#S");
		labelMap.put("Otherwise", "cond#N");
		labelMap.put("contingency", "cond#S");
		labelMap.put("Inverted-Sequence", "temp#N");
		labelMap.put("summary-s", "summ#S");
		labelMap.put("condition-e", "cond#S");
		labelMap.put("rhetorical-question", "prob#S");
		labelMap.put("interpretation-n", "eval#S");
		labelMap.put("antithesis-e", "cont#S");
		labelMap.put("elaboration-part-whole-e", "elab#S");
		labelMap.put("evaluation-n", "eval#S");
		labelMap.put("analogy", "comp#S");
		labelMap.put("concession-e", "cont#S");
		labelMap.put("Reason", "evid#N");
		labelMap.put("preference", "comp#S");
		labelMap.put("definition-e", "elab#S");
		labelMap.put("Evaluation", "eval#N");
		labelMap.put("comment-e", "eval#S");
		labelMap.put("manner-e", "mann#S");
		labelMap.put("consequence-s-e", "cause#S");
		labelMap.put("Topic-Comment", "prob#N");
		labelMap.put("comparison-e", "comp#S");
		labelMap.put("problem-solution-s", "prob#S");
		labelMap.put("means-e", "mann#S");
		labelMap.put("interpretation-s-e", "eval#S");
		labelMap.put("problem-solution-n", "prob#S");
		labelMap.put("temporal-after-e", "temp#S");
		labelMap.put("conclusion", "eval#S");
		labelMap.put("Comment-Topic", "eval#N");
		labelMap.put("evaluation-s-e", "eval#S");
		labelMap.put("result-e", "cause#S");
		labelMap.put("consequence-n-e", "cause#S");
		labelMap.put("analogy-e", "comp#S");
		labelMap.put("question-answer-n", "prob#S");
		labelMap.put("topic-shift", "topic#S");
		labelMap.put("temporal-same-time-e", "temp#S");
		labelMap.put("explanation-argumentative-e", "evid#S");
		labelMap.put("topic-drift", "topic#S");
		labelMap.put("reason-e", "evid#S");
		labelMap.put("statement-response-s", "prob#S");
		labelMap.put("otherwise", "cond#S");
		labelMap.put("Analogy", "comp#N");
		labelMap.put("Interpretation", "eval#N");
		labelMap.put("elaboration-process-step", "elab#S");
		labelMap.put("statement-response-n", "prob#S");
		labelMap.put("question-answer-s", "prob#S");
		labelMap.put("Proportion", "comp#N");
		labelMap.put("enablement-e", "purp#S");
		labelMap.put("evidence-e", "evid#S");
		labelMap.put("temporal-before-e", "temp#S");
		labelMap.put("preference-e", "comp#S");
		
		if(labelMap.containsKey(theOrginalLabel))
		{
			return labelMap.get(theOrginalLabel);
		}
		else
		{
			return null;
		}
	}
	
	
	
	public static String GetCoarseGrainedLabelsNoHead(String theOrginalLabel)
	{
		Map<String, String> labelMap = new HashMap<String, String>();
		
		labelMap.put("span", "span");
		labelMap.put("elaboration-additional", "elab");
		labelMap.put("List", "list");
		labelMap.put("attribution", "attr");
		labelMap.put("Same-Unit", "same");
		labelMap.put("elaboration-object-attribute-e", "elab");
		labelMap.put("elaboration-additional-e", "elab");
		labelMap.put("elaboration-process-step-e", "elab");
		labelMap.put("background-e", "back");
		labelMap.put("Contrast", "cont");
		labelMap.put("circumstance", "back");
		labelMap.put("explanation-argumentative", "evid");
		labelMap.put("purpose", "purp");
		labelMap.put("Root", "Root");
		labelMap.put("elaboration-general-specific", "elab");
		labelMap.put("antithesis", "cont");
		labelMap.put("Sequence", "temp");
		labelMap.put("TextualOrganization", "text");
		labelMap.put("example", "elab");
		labelMap.put("concession", "cont");
		labelMap.put("consequence-s", "cause");
		labelMap.put("Comparison", "comp");
		labelMap.put("background", "back");
		labelMap.put("condition", "cond");
		labelMap.put("interpretation-s", "eval");
		labelMap.put("reason", "evid");
		labelMap.put("evidence", "evid");
		labelMap.put("evaluation-s", "eval");
		labelMap.put("comment", "eval");
		labelMap.put("Topic-Shift", "topic");
		labelMap.put("Temporal-Same-Time", "temp");
		labelMap.put("Topic-Drift", "topic");
		labelMap.put("comparison", "comp");
		labelMap.put("result", "cause");
		labelMap.put("consequence-n", "cause");
		labelMap.put("attribution-e", "attr");
		labelMap.put("Cause-Result", "cause");
		labelMap.put("means", "mann");
		labelMap.put("Problem-Solution", "prob");
		labelMap.put("manner", "mann");
		labelMap.put("temporal-after", "temp");
		labelMap.put("temporal-same-time", "temp");
		labelMap.put("elaboration-set-member", "elab");
		labelMap.put("restatement", "summ");
		labelMap.put("elaboration-object-attribute", "elab");
		labelMap.put("summary-n", "summ");
		labelMap.put("attribution-n", "attr");
		labelMap.put("elaboration-general-specific-e", "elab");
		labelMap.put("restatement-e", "summ");
		labelMap.put("definition", "elab");
		labelMap.put("cause", "cause");
		labelMap.put("Consequence", "cause");
		labelMap.put("circumstance-e", "back");
		labelMap.put("Question-Answer", "prob");
		labelMap.put("elaboration-set-member-e", "elab");
		labelMap.put("example-e", "elab");
		labelMap.put("purpose-e", "purp");
		labelMap.put("Statement-Response", "prob");
		labelMap.put("Disjunction", "list");
		labelMap.put("hypothetical", "cond");
		labelMap.put("temporal-before", "temp");
		labelMap.put("elaboration-part-whole", "elab");
		labelMap.put("enablement", "purp");
		labelMap.put("Otherwise", "cond");
		labelMap.put("contingency", "cond");
		labelMap.put("Inverted-Sequence", "temp");
		labelMap.put("summary-s", "summ");
		labelMap.put("condition-e", "cond");
		labelMap.put("rhetorical-question", "prob");
		labelMap.put("interpretation-n", "eval");
		labelMap.put("antithesis-e", "cont");
		labelMap.put("elaboration-part-whole-e", "elab");
		labelMap.put("evaluation-n", "eval");
		labelMap.put("analogy", "comp");
		labelMap.put("concession-e", "cont");
		labelMap.put("Reason", "evid");
		labelMap.put("preference", "comp");
		labelMap.put("definition-e", "elab");
		labelMap.put("Evaluation", "eval");
		labelMap.put("comment-e", "eval");
		labelMap.put("manner-e", "mann");
		labelMap.put("consequence-s-e", "cause");
		labelMap.put("Topic-Comment", "prob");
		labelMap.put("comparison-e", "comp");
		labelMap.put("problem-solution-s", "prob");
		labelMap.put("means-e", "mann");
		labelMap.put("interpretation-s-e", "eval");
		labelMap.put("problem-solution-n", "prob");
		labelMap.put("temporal-after-e", "temp");
		labelMap.put("conclusion", "eval");
		labelMap.put("Comment-Topic", "eval");
		labelMap.put("evaluation-s-e", "eval");
		labelMap.put("result-e", "cause");
		labelMap.put("consequence-n-e", "cause");
		labelMap.put("analogy-e", "comp");
		labelMap.put("question-answer-n", "prob");
		labelMap.put("topic-shift", "topic");
		labelMap.put("temporal-same-time-e", "temp");
		labelMap.put("explanation-argumentative-e", "evid");
		labelMap.put("topic-drift", "topic");
		labelMap.put("reason-e", "evid");
		labelMap.put("statement-response-s", "prob");
		labelMap.put("otherwise", "cond");
		labelMap.put("Analogy", "comp");
		labelMap.put("Interpretation", "eval");
		labelMap.put("elaboration-process-step", "elab");
		labelMap.put("statement-response-n", "prob");
		labelMap.put("question-answer-s", "prob");
		labelMap.put("Proportion", "comp");
		labelMap.put("enablement-e", "purp");
		labelMap.put("evidence-e", "evid");
		labelMap.put("temporal-before-e", "temp");
		labelMap.put("preference-e", "comp");
		
		if(labelMap.containsKey(theOrginalLabel))
		{
			return labelMap.get(theOrginalLabel);
		}
		else
		{
			return null;
		}
	}
	

	public static String GetCoarseGrainedLabels(String theOrginalLabel)
	{
		Map<String, String> labelMap = new HashMap<String, String>();
		
		labelMap.put("span", "span");
		labelMap.put("elaboration-additional", "elab");
		labelMap.put("List", "list");
		labelMap.put("attribution", "attr");
		labelMap.put("Same-Unit", "same");
		labelMap.put("elaboration-object-attribute-e", "elab");
		labelMap.put("elaboration-additional-e", "elab");
		labelMap.put("elaboration-process-step-e", "elab");
		labelMap.put("background-e", "back");
		labelMap.put("Contrast", "cont");
		labelMap.put("circumstance", "back");
		labelMap.put("explanation-argumentative", "evid");
		labelMap.put("purpose", "purp");
		labelMap.put("Root", "Root");
		labelMap.put("elaboration-general-specific", "elab");
		labelMap.put("antithesis", "cont");
		labelMap.put("Sequence", "temp");
		labelMap.put("TextualOrganization", "text");
		labelMap.put("example", "elab");
		labelMap.put("concession", "cont");
		labelMap.put("consequence-s", "cause");
		labelMap.put("Comparison", "comp");
		labelMap.put("background", "back");
		labelMap.put("condition", "cond");
		labelMap.put("interpretation-s", "eval");
		labelMap.put("reason", "evid");
		labelMap.put("evidence", "evid");
		labelMap.put("evaluation-s", "eval");
		labelMap.put("comment", "eval");
		labelMap.put("Topic-Shift", "topic");
		labelMap.put("Temporal-Same-Time", "temp");
		labelMap.put("Topic-Drift", "topic");
		labelMap.put("comparison", "comp");
		labelMap.put("result", "cause");
		labelMap.put("consequence-n", "cause");
		labelMap.put("attribution-e", "attr");
		labelMap.put("Cause-Result", "cause");
		labelMap.put("means", "mann");
		labelMap.put("Problem-Solution", "prob");
		labelMap.put("manner", "mann");
		labelMap.put("temporal-after", "temp");
		labelMap.put("temporal-same-time", "temp");
		labelMap.put("elaboration-set-member", "elab");
		labelMap.put("restatement", "summ");
		labelMap.put("elaboration-object-attribute", "elab");
		labelMap.put("summary-n", "summ");
		labelMap.put("attribution-n", "attr");
		labelMap.put("elaboration-general-specific-e", "elab");
		labelMap.put("restatement-e", "summ");
		labelMap.put("definition", "elab");
		labelMap.put("cause", "cause");
		labelMap.put("Consequence", "cause");
		labelMap.put("circumstance-e", "back");
		labelMap.put("Question-Answer", "prob");
		labelMap.put("elaboration-set-member-e", "elab");
		labelMap.put("example-e", "elab");
		labelMap.put("purpose-e", "purp");
		labelMap.put("Statement-Response", "prob");
		labelMap.put("Disjunction", "list");
		labelMap.put("hypothetical", "cond");
		labelMap.put("temporal-before", "temp");
		labelMap.put("elaboration-part-whole", "elab");
		labelMap.put("enablement", "purp");
		labelMap.put("Otherwise", "cond");
		labelMap.put("contingency", "cond");
		labelMap.put("Inverted-Sequence", "temp");
		labelMap.put("summary-s", "summ");
		labelMap.put("condition-e", "cond");
		labelMap.put("rhetorical-question", "prob");
		labelMap.put("interpretation-n", "eval");
		labelMap.put("antithesis-e", "cont");
		labelMap.put("elaboration-part-whole-e", "elab");
		labelMap.put("evaluation-n", "eval");
		labelMap.put("analogy", "comp");
		labelMap.put("concession-e", "cont");
		labelMap.put("Reason", "evid");
		labelMap.put("preference", "comp");
		labelMap.put("definition-e", "elab");
		labelMap.put("Evaluation", "eval");
		labelMap.put("comment-e", "eval");
		labelMap.put("manner-e", "mann");
		labelMap.put("consequence-s-e", "cause");
		labelMap.put("Topic-Comment", "prob");
		labelMap.put("comparison-e", "comp");
		labelMap.put("problem-solution-s", "prob");
		labelMap.put("means-e", "mann");
		labelMap.put("interpretation-s-e", "eval");
		labelMap.put("problem-solution-n", "prob");
		labelMap.put("temporal-after-e", "temp");
		labelMap.put("conclusion", "eval");
		labelMap.put("Comment-Topic", "eval");
		labelMap.put("evaluation-s-e", "eval");
		labelMap.put("result-e", "cause");
		labelMap.put("consequence-n-e", "cause");
		labelMap.put("analogy-e", "comp");
		labelMap.put("question-answer-n", "prob");
		labelMap.put("topic-shift", "topic");
		labelMap.put("temporal-same-time-e", "temp");
		labelMap.put("explanation-argumentative-e", "evid");
		labelMap.put("topic-drift", "topic");
		labelMap.put("reason-e", "evid");
		labelMap.put("statement-response-s", "prob");
		labelMap.put("otherwise", "cond");
		labelMap.put("Analogy", "comp");
		labelMap.put("Interpretation", "eval");
		labelMap.put("elaboration-process-step", "elab");
		labelMap.put("statement-response-n", "prob");
		labelMap.put("question-answer-s", "prob");
		labelMap.put("Proportion", "comp");
		labelMap.put("enablement-e", "purp");
		labelMap.put("evidence-e", "evid");
		labelMap.put("temporal-before-e", "temp");
		labelMap.put("preference-e", "comp");
		
		if(labelMap.containsKey(theOrginalLabel))
		{
			return labelMap.get(theOrginalLabel);
		}
		else
		{
			return null;
		}
	}
	


}

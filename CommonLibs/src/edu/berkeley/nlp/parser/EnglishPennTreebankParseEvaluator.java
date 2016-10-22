package edu.berkeley.nlp.parser;

import java.io.PrintWriter;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees;

/**
 * Evaluates precision and recall for English Penn Treebank parse trees. NOTE:
 * Unlike the standard evaluation, multiplicity over each span is ignored. Also,
 * punction is NOT currently deleted properly (approximate hack), and other
 * normalizations (like AVDP ~ PRT) are NOT done.
 * 
 * @author Dan Klein
 */
public class EnglishPennTreebankParseEvaluator<L> {
	static class UnlabeledConstituent<L> {

		int start;
		int end;

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof UnlabeledConstituent))
				return false;

			final UnlabeledConstituent unlabeledConstituent = (UnlabeledConstituent) o;

			if (end != unlabeledConstituent.end)
				return false;
			if (start != unlabeledConstituent.start)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result;

			result = start;
			result = 29 * result + end;
			return result;
		}

		@Override
		public String toString() {
			return "[" + start + "," + end + "]";
		}

		public UnlabeledConstituent(int start, int end) {

			this.start = start;
			this.end = end;
		}
	}

	abstract static class AbstractEval<L> {

		protected String str = "";

		private int exact = 0;
		private int total = 0;

		private int correctEvents = 0;
		private int guessedEvents = 0;
		private int goldEvents = 0;

		abstract Set<Object> makeObjects(Tree<L> tree);

		public double evaluate(Tree<L> guess, Tree<L> gold) {
			return evaluate(guess, gold, new PrintWriter(System.out, true));
		}

		public double evaluate(Tree<L> guess, Tree<L> gold, boolean b) {
			return evaluate(guess, gold, null);
		}

		/*
		 * evaluates precision and recall by calling makeObjects() to make a set
		 * of structures for guess Tree and gold Tree, and compares them with
		 * each other.
		 */
		public double evaluate(Tree<L> guess, Tree<L> gold, PrintWriter pw) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);

			correctEvents += correctSet.size();
			guessedEvents += guessedSet.size();
			goldEvents += goldSet.size();

			int currentExact = 0;
			if (correctSet.size() == guessedSet.size()
					&& correctSet.size() == goldSet.size()) {
				exact++;
				currentExact = 1;
			}
			total++;

			// guess.pennPrint(pw);
			// gold.pennPrint(pw);
			double f1 = displayPRF(str + " [Current] ", correctSet.size(),
					guessedSet.size(), goldSet.size(), currentExact, 1, pw);
			return f1;

		}

		public double evaluateMultiple(List<Tree<L>> guesses,
				List<Tree<L>> golds, PrintWriter pw) {
			assert (guesses.size() == golds.size());
			int correctCount = 0;
			int guessedCount = 0;
			int goldCount = 0;
			DecimalFormat df=new DecimalFormat("#.00"); 
			for (int i = 0; i < guesses.size(); i++) {
				Tree<L> guess = guesses.get(i);
				Tree<L> gold = golds.get(i);
				Set<Object> guessedSet = makeObjects(guess);
				Set<Object> goldSet = makeObjects(gold);
				Set<Object> correctSet = new HashSet<Object>();
				correctSet.addAll(goldSet);
				correctSet.retainAll(guessedSet);
				correctCount += correctSet.size();
				guessedCount += guessedSet.size();
				goldCount += goldSet.size();
				if (correctSet.size() == guessedSet.size() && correctSet.size() == goldSet.size()) {
					exact++;
				}
				total++;
			}

			correctEvents += correctCount;
			guessedEvents += guessedCount;
			goldEvents += goldCount;

			//int currentExact = 0;
			

			// guess.pennPrint(pw);
			// gold.pennPrint(pw);
			double f1 = displayPRF(str + " [Current] ", correctCount,
					guessedCount, goldCount, exact, total, pw);
			return f1;

		}
		
		public double evaluateMultipleForSIG(List<Tree<L>> guesses,
				List<Tree<L>> golds, PrintWriter pw) {
			assert (guesses.size() == golds.size());
			int correctCount = 0;
			int guessedCount = 0;
			int goldCount = 0;
			DecimalFormat df=new DecimalFormat("#.00"); 
			pw.println("     Sent.          Attachment      Correct        Scoring          ");
			pw.println("    ID Tokens  -   Unlab. Lab.   HEAD HEAD+DEPREL   tokens   - - - -");
			pw.println("  ============================================================================");
			for (int i = 0; i < guesses.size(); i++) {
				Tree<L> guess = guesses.get(i);
				Tree<L> gold = golds.get(i);
				Set<Object> guessedSet = makeObjects(guess);
				Set<Object> goldSet = makeObjects(gold);
				Set<Object> correctSet = new HashSet<Object>();
				correctSet.addAll(goldSet);
				correctSet.retainAll(guessedSet);
				correctCount += correctSet.size();
				guessedCount += guessedSet.size();
				goldCount += goldSet.size();
				if (correctSet.size() == guessedSet.size() && correctSet.size() == goldSet.size()) {
					exact++;
				}
				total++;
				String ourstr = String.format("     %d    %d    0   %s  0.00     %d       0           %d    0 0 0 0", 
						i+1, guessedSet.size()+goldSet.size(), df.format(2.0*correctSet.size()/(guessedSet.size()+goldSet.size())),
						2*correctSet.size(), guessedSet.size()+goldSet.size());
				pw.println(ourstr);
			}

			pw.println();
			pw.println();
			pw.println();
			
			correctEvents += correctCount;
			guessedEvents += guessedCount;
			goldEvents += goldCount;
			

			//int currentExact = 0;
			

			// guess.pennPrint(pw);
			// gold.pennPrint(pw);
			double f1 = displayPRF(str + " [Current] ", correctCount,
					guessedCount, goldCount, exact, total, pw);
			pw.println("  ================================================================================");
			return f1;

		}

		public double analyzeMultiple(List<Tree<L>> guesses,
				List<Tree<L>> golds, PrintWriter pw, Map<String, Map<String, Integer>> errors) {
			assert (guesses.size() == golds.size());
			int correctCount = 0;
			int guessedCount = 0;
			int goldCount = 0;
			for (int i = 0; i < guesses.size(); i++) {
				Tree<L> guess = guesses.get(i);
				Tree<L> gold = golds.get(i);
				Set<Object> guessedSet = makeObjects(guess);
				Set<Object> goldSet = makeObjects(gold);
				Set<Object> correctSet = new HashSet<Object>();
				correctSet.addAll(goldSet);
				correctSet.retainAll(guessedSet);
				List<L> terminals = gold.getTerminalYield();
				for(Object obj : guessedSet)
				{
					if(!correctSet.contains(obj))
					{
						LabeledConstituent curSpan = (LabeledConstituent)(obj);
						String curError = "";
						for(int idx = curSpan.start; idx < curSpan.end; idx++)
						{
							curError = curError + terminals.get(idx).toString();
						} 
						String errorname = curSpan.label.toString().trim();
						int firstbracket = errorname.indexOf("(");
						int firstmark = errorname.indexOf("#");
						String posLabel = errorname.substring(firstbracket+1, firstmark);
						curError = curError + "_" + posLabel;
						if(!errors.containsKey(curError))
						{
							errors.put(curError, new HashMap<String, Integer>());
						}
												
						if(!errors.get(curError).containsKey(errorname))
						{
							errors.get(curError).put(errorname, 0);
						}
						errors.get(curError).put(errorname, errors.get(curError).get(errorname)+1);
					}
					else
					{
						LabeledConstituent curSpan = (LabeledConstituent)(obj);
						String curError = "";
						for(int idx = curSpan.start; idx < curSpan.end; idx++)
						{
							curError = curError + terminals.get(idx).toString();
						}
						String errorname = "[R]" + curSpan.label.toString().trim();
						int firstbracket = errorname.indexOf("(");
						int firstmark = errorname.indexOf("#");
						String posLabel = errorname.substring(firstbracket+1, firstmark);
						curError = curError + "_" + posLabel;
						if(!errors.containsKey(curError))
						{
							errors.put(curError, new HashMap<String, Integer>());
						}					
						if(!errors.get(curError).containsKey(errorname))
						{
							errors.get(curError).put(errorname, 0);
						}
						
						errors.get(curError).put(errorname, errors.get(curError).get(errorname)+1);
					}
				}
				correctCount += correctSet.size();
				guessedCount += guessedSet.size();
				goldCount += goldSet.size();
				if (correctSet.size() == guessedSet.size() && correctSet.size() == goldSet.size()) {
					exact++;
				}
				total++;
			}

			correctEvents += correctCount;
			guessedEvents += guessedCount;
			goldEvents += goldCount;

			//int currentExact = 0;
			

			// guess.pennPrint(pw);
			// gold.pennPrint(pw);
			double f1 = displayPRF(str + " [Current] ", correctCount,
					guessedCount, goldCount, exact, total, pw);
			return f1;

		}

		public double[] massEvaluate(Tree<L> guess, Tree<L>[] goldTrees) {
			Set<Object> guessedSet = makeObjects(guess);
			double cEvents = 0;
			double guEvents = 0;
			double goEvents = 0;
			double exactM = 0, precision = 0, recall = 0, f1 = 0;

			for (int treeI = 0; treeI < goldTrees.length; treeI++) {
				Tree<L> gold = goldTrees[treeI];
				Set<Object> goldSet = makeObjects(gold);
				Set<Object> correctSet = new HashSet<Object>();
				correctSet.addAll(goldSet);
				correctSet.retainAll(guessedSet);
				cEvents = correctSet.size();
				guEvents = guessedSet.size();
				goEvents = goldSet.size();

				double p = cEvents / guEvents;
				double r = cEvents / goEvents;
				double f = (p > 0.0 && r > 0.0 ? 2.0 / (1.0 / p + 1.0 / r)
						: 0.0);

				precision += p;
				recall += r;
				f1 += f;

				if (cEvents == guEvents && cEvents == goEvents) {
					exactM++;
				}
			}
			double ex = exactM / goldTrees.length;
			double[] results = { precision, recall, f1, ex };

			return results;

		}

		private double displayPRF(String prefixStr, int correct, int guessed,
				int gold, int exact, int total, PrintWriter pw) {
			double precision = (guessed > 0 ? correct / (double) guessed : 1.0);
			double recall = (gold > 0 ? correct / (double) gold : 1.0);
			double f1 = (precision > 0.0 && recall > 0.0 ? 2.0 / (1.0 / precision + 1.0 / recall)
					: 0.0);

			double exactMatch = exact / (double) total;

			String displayStr = " P: " + correct + "/" + guessed + "=" +((int) (precision * 10000)) / 100.0
					+ " R: " + correct + "/" + gold + "="  + ((int) (recall * 10000)) / 100.0 + " F1: "
					+ ((int) (f1 * 10000)) / 100.0 + " EX: "
					+ ((int) (exactMatch * 10000)) / 100.0;

			if (pw != null)
				pw.println(prefixStr + displayStr);
			return f1;
		}

		public double display(boolean verbose) {
			return display(verbose, new PrintWriter(System.out, true));
		}

		public double display(boolean verbose, PrintWriter pw) {
			return displayPRF(str + " [Average] ", correctEvents,
					guessedEvents, goldEvents, exact, total, pw);
		}
	}

	static class LabeledConstituent<L> {
		L label;
		int start;
		int end;

		public L getLabel() {
			return label;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof LabeledConstituent))
				return false;

			final LabeledConstituent labeledConstituent = (LabeledConstituent) o;

			if (end != labeledConstituent.end)
				return false;
			if (start != labeledConstituent.start)
				return false;
			if (label != null ? !label.equals(labeledConstituent.label)
					: labeledConstituent.label != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result;
			result = (label != null ? label.hashCode() : 0);
			result = 29 * result + start;
			result = 29 * result + end;
			return result;
		}

		@Override
		public String toString() {
			return label + "[" + start + "," + end + "]";
		}

		public LabeledConstituent(L label, int start, int end) {
			this.label = label;
			this.start = start;
			this.end = end;
		}
	}
	
	
	static class LabeledConstituentSegmentation<L> {
		L label;
		int start;
		int end;
		List<Integer> segmentations;

		public L getLabel() {
			return label;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof LabeledConstituentSegmentation))
				return false;

			final LabeledConstituentSegmentation labeledConstituent = (LabeledConstituentSegmentation) o;

			if (end != labeledConstituent.end)
				return false;
			if (start != labeledConstituent.start)
				return false;
			if (label != null ? !label.equals(labeledConstituent.label)
					: labeledConstituent.label != null)
				return false;
			
			if(segmentations != null)
			{
				if(labeledConstituent.segmentations.size() != segmentations.size())
				{
					return false;
				}
				int segnum = segmentations.size();
				for(int idx = 0; idx < segnum; idx++)
				{
					int curSeg = segmentations.get(idx);
					int oSeg =  (Integer) labeledConstituent.segmentations.get(idx);
					if(curSeg != oSeg)
					{
						return false;
					}
				}
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result;
			result = (label != null ? label.hashCode() : 0);
			result = 29 * result + start;
			result = 29 * result + end;
			return result;
		}

		@Override
		public String toString() {
			return label + "[" + start + "," + end + "]";
		}

		public LabeledConstituentSegmentation(L label, int start, int end, List<Integer> segs) {
			this.label = label;
			this.start = start;
			this.end = end;
			this.segmentations = segs;
		}
	}

	public static class UnlabeledConstituentEval<L> extends AbstractEval<L> {

		public UnlabeledConstituentEval() {

		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = LabeledConstituentEval.stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			if (tree.getYield().size() == 1) {

				return 1;
			}
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}

			set.add(new UnlabeledConstituent<L>(start, end));

			return end - start;
		}

	}

	public static class LabeledConstituentEval<L> extends AbstractEval<L> {

		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return null;
			if (tree.isPreTerminal())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			if (tree.isLeaf()) {
				if (punctuationTags.contains(tree.getLabel()))
					return 0;
				else
					return 1;
			}
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}
			L label = tree.getLabel();
			if (!labelsToIgnore.contains(label)) {
				set.add(new LabeledConstituent<L>(label, start, end));
			}
			return end - start;
		}

		public LabeledConstituentEval(Set<L> labelsToIgnore,
				Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

		public int getHammingDistance(Tree<L> guess, Tree<L> gold) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);
			return (guessedSet.size() - correctSet.size())
					+ (goldSet.size() - correctSet.size());
		}

	}
	
	
	public static class CLTLabeledConstituentEval<L> extends AbstractEval<L> {

		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			
			if(tree.getLabel().toString().endsWith("#b")
			  || tree.getLabel().toString().endsWith("#i")
			  || tree.getLabel().toString().endsWith("#z")
			  || tree.getLabel().toString().endsWith("#y")
			  || tree.getLabel().toString().endsWith("#x")
					)
			{
				return -1;
			}
			
			if(tree.getLabel().toString().endsWith("#t"))
			{
				String token = tree.getTerminalStr();
				if(punctuationTags.contains((L)(token)))
					return 0;
				else
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					return token.length();
				}
			}
			
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}
			L label = tree.getLabel();
			if (!labelsToIgnore.contains(label)) {
				set.add(new LabeledConstituent<L>(label, start, end));
			}
			return end - start;
		}

		public CLTLabeledConstituentEval(Set<L> labelsToIgnore,
				Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

		public int getHammingDistance(Tree<L> guess, Tree<L> gold) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);
			return (guessedSet.size() - correctSet.size())
					+ (goldSet.size() - correctSet.size());
		}

	}
	
	
	public static class CLTLabeledConstituentSegEval<L> extends AbstractEval<L> {

		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			
			if(tree.getLabel().toString().endsWith("#b")
			  || tree.getLabel().toString().endsWith("#i")
			  || tree.getLabel().toString().endsWith("#z")
			  || tree.getLabel().toString().endsWith("#y")
			  || tree.getLabel().toString().endsWith("#x")
					)
			{
				return -1;
			}
			
			if(tree.getLabel().toString().endsWith("#t"))
			{
				String token = tree.getTerminalStr();
				if(punctuationTags.contains((L)(token)))
					return 0;
				else
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					return token.length();
				}
			}
			
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}
			L label = tree.getLabel();
			if (!labelsToIgnore.contains(label)) {
				List<Tree<L>> nonTerminals = tree.getNonTerminals();
				List<Integer> segs = new ArrayList<Integer>();
				
				for(Tree<L> subTree : nonTerminals)
				{
					if(subTree.getLabel().toString().endsWith("#t"))
					{
						segs.add(subTree.bigger);
					}
				}
				Collections.sort(segs);
				set.add(new LabeledConstituentSegmentation<L>(label, start, end, segs));
			}
			return end - start;
		}

		public CLTLabeledConstituentSegEval(Set<L> labelsToIgnore,
				Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

		public int getHammingDistance(Tree<L> guess, Tree<L> gold) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);
			return (guessedSet.size() - correctSet.size())
					+ (goldSet.size() - correctSet.size());
		}

	}
	
	
	public static class WordEval<L> extends AbstractEval<L> {

		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			if(tree.getLabel().toString().endsWith("#b")
			  || tree.getLabel().toString().endsWith("#i")
			  || tree.getLabel().toString().endsWith("#z")
			  || tree.getLabel().toString().endsWith("#y")
			  || tree.getLabel().toString().endsWith("#x")
					)
			{
				return -1;
			}
			
			if(tree.getLabel().toString().endsWith("#t"))
			{
				String token = tree.getTerminalStr();
				if(punctuationTags.contains((L)(token)))
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					return token.length();
				}					
				else
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					set.add(new LabeledConstituent<L>((L)"TOKEN", start, start + token.length()));
					return token.length();
				}
			}
			
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}

			return end - start;
		}

		public WordEval(Set<L> labelsToIgnore,
				Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

		public int getHammingDistance(Tree<L> guess, Tree<L> gold) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);
			return (guessedSet.size() - correctSet.size())
					+ (goldSet.size() - correctSet.size());
		}

	}
	
	
	public static class WordPOSEval<L> extends AbstractEval<L> {

		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			if(tree.getLabel().toString().endsWith("#b")
			  || tree.getLabel().toString().endsWith("#i")
			  || tree.getLabel().toString().endsWith("#z")
			  || tree.getLabel().toString().endsWith("#y")
			  || tree.getLabel().toString().endsWith("#x")
					)
			{
				return -1;
			}
			
			if(tree.getLabel().toString().endsWith("#t"))
			{
				String token = tree.getTerminalStr();
				if(punctuationTags.contains((L)(token)))
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					return token.length();
				}
				else
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					set.add(new LabeledConstituent<L>(tree.getLabel(), start, start + token.length()));
					return token.length();
				}
			}
			
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}

			return end - start;
		}

		public WordPOSEval(Set<L> labelsToIgnore,
				Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

		public int getHammingDistance(Tree<L> guess, Tree<L> gold) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);
			return (guessedSet.size() - correctSet.size())
					+ (goldSet.size() - correctSet.size());
		}

	}
	
/*	
	public static class WordStructureEval<L> extends AbstractEval<L> {

		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			
			if (tree.isLeaf()) {
					return 1;
			}
			
			/*
			if(tree.getLabel().toString().endsWith("#t"))
			{
				String token = tree.getTerminalStr();
				if(punctuationTags.contains((L)(token)))
					return 0;
				else
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					return token.length();
				}
			}
			*/
	/*
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}
			
			if( tree.getLabel().toString().endsWith("#b")
			  || tree.getLabel().toString().endsWith("#i")
			  ||  tree.getLabel().toString().endsWith("#z")
			  || tree.getLabel().toString().endsWith("#y")
			  || tree.getLabel().toString().endsWith("#x")
							)
			{
				//L label = (L)(tree.getLabel().toString().substring(tree.getLabel().toString().length()-2));
				if (!labelsToIgnore.contains(tree.getLabel())) {
					set.add(new LabeledConstituent<L>(tree.getLabel(), start, end));
				}
			}

			return end - start;
		}

		public WordStructureEval(Set<L> labelsToIgnore,
				Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

		public int getHammingDistance(Tree<L> guess, Tree<L> gold) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);
			return (guessedSet.size() - correctSet.size())
					+ (goldSet.size() - correctSet.size());
		}

	}
	
*/	
	public static class WordStructureByWordEval<L> extends AbstractEval<L> {

		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			if(tree.getLabel().toString().endsWith("#b")
			  || tree.getLabel().toString().endsWith("#i")
			  || tree.getLabel().toString().endsWith("#z")
			  || tree.getLabel().toString().endsWith("#y")
			  || tree.getLabel().toString().endsWith("#x")
					)
			{
				return -1;
			}
			
			if(tree.getLabel().toString().endsWith("#t"))
			{
				String token = tree.getTerminalStr();
				if(punctuationTags.contains((L)(token)) || token.length() == 1)
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					return token.length();
				}					
				else
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					L labeltemp = (L)(tree.toString().trim().replaceAll("\\s+", ""));
					set.add(new LabeledConstituent<L>(labeltemp, start, start + token.length()));
					return token.length();
				}
			}
			
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}

			return end - start;
		}

		public WordStructureByWordEval(Set<L> labelsToIgnore,
				Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

		public int getHammingDistance(Tree<L> guess, Tree<L> gold) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);
			return (guessedSet.size() - correctSet.size())
					+ (goldSet.size() - correctSet.size());
		}

	}

	/*
	public static class WordStructureWoPOSByWordEval<L> extends AbstractEval<L> {

		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			if(tree.getLabel().toString().endsWith("#b")
			  || tree.getLabel().toString().endsWith("#i")
			  || tree.getLabel().toString().endsWith("#z")
			  || tree.getLabel().toString().endsWith("#y")
			  || tree.getLabel().toString().endsWith("#x")
					)
			{
				return -1;
			}
			
			if(tree.getLabel().toString().endsWith("#t"))
			{
				String token = tree.getTerminalStr();
				if(punctuationTags.contains((L)(token)))
					return 0;
				else
				{
					List<Tree<L>> terminals = tree.getTerminals();
					assert(terminals.size() == token.length());
					String labTree = tree.getLabel().toString();
					String thePOS = labTree.substring(0, labTree.length()-1);
					String labeltemp = tree.toString().trim();//.replaceAll(thePOS, "");
					set.add(new LabeledConstituent<L>((L)(labeltemp), start, start + token.length()));
					return token.length();
				}
			}
			
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}

			return end - start;
		}

		public WordStructureWoPOSByWordEval(Set<L> labelsToIgnore,
				Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

		public int getHammingDistance(Tree<L> guess, Tree<L> gold) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);
			return (guessedSet.size() - correctSet.size())
					+ (goldSet.size() - correctSet.size());
		}

	}
	
*/	
	public static class TotalTreeEval<L> extends AbstractEval<L> {

		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			
			if (tree.isLeaf()) {
				return 1;
		}
			String labelstr = tree.getLabel().toString();
			if(tree.getLabel().toString().endsWith("#b")
			  || tree.getLabel().toString().endsWith("#i")
			  || tree.getLabel().toString().endsWith("#z")
			  || tree.getLabel().toString().endsWith("#y")
			  || tree.getLabel().toString().endsWith("#x")
					)
			{
				//labelstr = labelstr.substring(labelstr.length()-1);
			}
			
			if(tree.getLabel().toString().endsWith("#t"))
			{
				labelstr = labelstr.substring(0, labelstr.length()-2);
			}
			
			int end = start;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				end += childSpan;
			}
			
			L label = (L)(labelstr);
			if (!labelsToIgnore.contains(label)) {
				set.add(new LabeledConstituent<L>(label, start, end));
			}

			return end - start;
		}

		public TotalTreeEval(Set<L> labelsToIgnore,
				Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

		public int getHammingDistance(Tree<L> guess, Tree<L> gold) {
			Set<Object> guessedSet = makeObjects(guess);
			Set<Object> goldSet = makeObjects(gold);
			Set<Object> correctSet = new HashSet<Object>();
			correctSet.addAll(goldSet);
			correctSet.retainAll(guessedSet);
			return (guessedSet.size() - correctSet.size())
					+ (goldSet.size() - correctSet.size());
		}

	}

	public static void main(String[] args) throws Throwable {
		Tree<String> goldTree = (new Trees.PennTreeReader(new StringReader(
				"(ROOT (S (NP (DT the) (NN can)) (VP (VBD fell))))"))).next();
		Tree<String> guessedTree = (new Trees.PennTreeReader(new StringReader(
				"(ROOT (S (NP (DT the)) (VP (MB can) (VP (VBD fell)))))")))
				.next();
		LabeledConstituentEval<String> eval = new LabeledConstituentEval<String>(
				Collections.singleton("ROOT"), new HashSet<String>());
		RuleEval<String> rule_eval = new RuleEval<String>(
				Collections.singleton("ROOT"), new HashSet<String>());
		System.out.println("Gold tree:\n"
				+ Trees.PennTreeRenderer.render(goldTree));
		System.out.println("Guessed tree:\n"
				+ Trees.PennTreeRenderer.render(guessedTree));
		eval.evaluate(guessedTree, goldTree);
		eval.display(true);
		rule_eval.evaluate(guessedTree, goldTree);
		rule_eval.display(true);
	}

	public static class RuleEval<L> extends AbstractEval<L> {
		Set<L> labelsToIgnore;
		Set<L> punctuationTags;

		static <L> Tree<L> stripLeaves(Tree<L> tree) {
			if (tree.isLeaf())
				return null;
			if (tree.isPreTerminal())
				return new Tree<L>(tree.getLabel());
			List<Tree<L>> children = new ArrayList<Tree<L>>();
			for (Tree<L> child : tree.getChildren()) {
				children.add(stripLeaves(child));
			}
			return new Tree<L>(tree.getLabel(), children);
		}

		@Override
		Set<Object> makeObjects(Tree<L> tree) {
			Tree<L> noLeafTree = stripLeaves(tree);
			Set<Object> set = new HashSet<Object>();
			addConstituents(noLeafTree, set, 0);
			return set;
		}

		private int addConstituents(Tree<L> tree, Set<Object> set, int start) {
			if (tree == null)
				return 0;
			if (tree.isLeaf()) {
				/*
				 * if (punctuationTags.contains(tree.getLabel())) return 0; else
				 */
				return 1;
			}
			int end = start, i = 0;
			L lC = null, rC = null;
			for (Tree<L> child : tree.getChildren()) {
				int childSpan = addConstituents(child, set, end);
				if (i == 0)
					lC = child.getLabel();
				else
					/* i==1 */rC = child.getLabel();
				i++;
				end += childSpan;
			}
			L label = tree.getLabel();
			if (!labelsToIgnore.contains(label)) {
				set.add(new RuleConstituent<L>(label, lC, rC, start, end));
			}
			return end - start;
		}

		public RuleEval(Set<L> labelsToIgnore, Set<L> punctuationTags) {
			this.labelsToIgnore = labelsToIgnore;
			this.punctuationTags = punctuationTags;
		}

	}

	static class RuleConstituent<L> {
		L label, lChild, rChild;
		int start;
		int end;

		public L getLabel() {
			return label;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof RuleConstituent))
				return false;

			final RuleConstituent labeledConstituent = (RuleConstituent) o;

			if (end != labeledConstituent.end)
				return false;
			if (start != labeledConstituent.start)
				return false;
			if (label != null ? !label.equals(labeledConstituent.label)
					: labeledConstituent.label != null)
				return false;
			if (lChild != null ? !lChild.equals(labeledConstituent.lChild)
					: labeledConstituent.lChild != null)
				return false;
			if (rChild != null ? !rChild.equals(labeledConstituent.rChild)
					: labeledConstituent.rChild != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result;
			result = (label != null ? label.hashCode() : 0) + 17
					* (lChild != null ? lChild.hashCode() : 0) - 7
					* (rChild != null ? rChild.hashCode() : 0);
			result = 29 * result + start;
			result = 29 * result + end;
			return result;
		}

		@Override
		public String toString() {
			String rChildStr = (rChild == null) ? "" : rChild.toString();
			return label + "->" + lChild + " " + rChildStr + "[" + start + ","
					+ end + "]";
		}

		public RuleConstituent(L label, L lChild, L rChild, int start, int end) {
			this.label = label;
			this.lChild = lChild;
			this.rChild = rChild;
			this.start = start;
			this.end = end;
		}
	}

}

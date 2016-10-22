package corpus;

//import ir.hit.edu.util.UniversPostager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DepInstance {
    public List<String> forms;
    public List<String> postags;
    public List<Integer> heads;
    public List<String> deprels;

    // for evaluate
    // 0 sent length; 1 uas_correct_num; 2 las_correct_num; 3
    // sentence_all_uas_correct
    // 4 sentence_all_las_correct; 5 root_correct
    public int[] eval_res;

    public DepInstance() {
        forms = new ArrayList<String>();
        postags = new ArrayList<String>();
        heads = new ArrayList<Integer>();
        deprels = new ArrayList<String>();

        eval_res = new int[8];
        for (int i = 0; i < 8; i++) {
            eval_res[i] = 0;
        }
    }

    public DepInstance(DepInstance other) {
        forms = new ArrayList<String>();
        postags = new ArrayList<String>();
        heads = new ArrayList<Integer>();
        deprels = new ArrayList<String>();

        for (int i = 0; i < other.size(); i++) {
            forms.add(other.forms.get(i));
            postags.add(other.postags.get(i));
            heads.add(other.heads.get(i));
            deprels.add(other.deprels.get(i));
        }

        eval_res = new int[8];
        for (int i = 0; i < 8; i++) {
            eval_res[i] = other.eval_res[i];
        }
    }

    public void reset() {
        forms = new ArrayList<String>();
        postags = new ArrayList<String>();
        heads = new ArrayList<Integer>();
        deprels = new ArrayList<String>();

        eval_res = new int[8];
        for (int i = 0; i < 8; i++) {
            eval_res[i] = 0;
        }

    }

    public int size() {
        return forms.size();
    }

    public void toCONLLString(List<String> outputs) {
        outputs.clear();
        int length = forms.size();
        for (int i = 0; i < length; i++) {
            String tmpOut = String.format("%d\t%s\t%s\t%s\t%s\t_\t%d\t%s\t_\t_", i + 1, forms.get(i), "_", "_", postags.get(i),
                    heads.get(i), deprels.get(i));

            outputs.add(tmpOut);
        }
    }

    public void toZhangYueString(List<String> outputs) {
        outputs.clear();
        int length = forms.size();
        for (int i = 0; i < length; i++) {
            String tmpOut = String.format("%s\t%s\t%d\t%s", forms.get(i), postags.get(i), heads.get(i) - 1, deprels.get(i));

            outputs.add(tmpOut);
        }
    }

    public boolean evaluateWithOther(DepInstance other) {
        for (int i = 0; i < 8; i++) {
            eval_res[i] = 0;
        }

        int length = forms.size();
        if (other.forms.size() != length)
            return false;
        for (int i = 0; i < length; i++) {
            if (isPunc(forms.get(i), postags.get(i))) {
                continue;
            }
            eval_res[0]++;
            int curHead = heads.get(i);
            int otherHead = other.heads.get(i);
            if (otherHead == curHead) {
                eval_res[1]++;
            }

            if (otherHead == curHead && other.deprels.get(i).equals(deprels.get(i))) {
                eval_res[2]++;
            }

            if (other.heads.get(i) == 0 && heads.get(i) == 0) {
                eval_res[5] = 1;
            }
        }

        if (eval_res[1] == eval_res[0]) {
            eval_res[3] = 1;
        }

        if (eval_res[2] == eval_res[0]) {
            eval_res[4] = 1;
        }

        return true;
    }

    public boolean evaluateWithOthers(List<DepInstance> others) {
        if (others.size() == 0) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            eval_res[i] = 0;
        }

        int length = forms.size();

        for (DepInstance other : others) {
            if (other.forms.size() != length)
                return false;
        }
        boolean rootallright = true;
        for (int i = 0; i < length; i++) {
            // if (isPunc(forms.get(i), cpostags.get(i)) || isPunc(forms.get(i),
            // postags.get(i))) {
            // continue;
            // }
            eval_res[0]++;
            boolean headallright = true;
            boolean headlabelallright = true;

            for (DepInstance other : others) {
                if (other.heads.get(i) != heads.get(i)) {
                    headallright = false;
                    // eval_res[1]++;
                }

                if (other.heads.get(i) != heads.get(i) || (!other.deprels.get(i).equals("_") && !other.deprels.get(i).equals(deprels.get(i)))) {
                    // eval_res[2]++;
                    headlabelallright = false;
                }

                if ((other.heads.get(i) != 0 && heads.get(i) == 0) || (other.heads.get(i) == 0 && heads.get(i) != 0)) {
                    // eval_res[5] = 1;
                    rootallright = false;
                }
            }
            if (headallright)
                eval_res[1]++;
            if (headlabelallright)
                eval_res[2]++;
        }
        if (rootallright)
            eval_res[5] = 1;
        if (eval_res[1] == eval_res[0]) {
            eval_res[3] = 1;
        }

        if (eval_res[2] == eval_res[0]) {
            eval_res[4] = 1;
        }
        return true;
    }

    public static boolean isPunc(String theWord, String thePostag) {

        if (thePostag.equals("PU") || thePostag.equals("``") || thePostag.equals("''") || thePostag.equals(",") || thePostag.equals(".")
                || thePostag.equals(":") || thePostag.equals("-LRB-") || thePostag.equals("-RRB-") || thePostag.equals("$") || thePostag.equals("#")) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isPunc(String thePostag) {

        if (thePostag.equals("PU") || thePostag.equals("``") || thePostag.equals("''") || thePostag.equals(",") || thePostag.equals(".")
                || thePostag.equals(":")) {
            return true;
        } else {
            return false;
        }

    }

    public int Hight(int i) {
        Set<Integer> leaves = new HashSet<Integer>();
        for (int idx = 0; idx < forms.size(); idx++) {
            int ghead = heads.get(idx);
            if (ghead - 1 == i) {
                leaves.add(idx);
            }
        }
        if (leaves.size() == 0)
            return 1;

        int maxDepth = -1;
        for (Integer idx : leaves) {
            int curDepth = Hight(idx);
            if (curDepth > maxDepth) {
                maxDepth = curDepth;
            }
        }

        return maxDepth + 1;
    }

    public static int ChildDegree(List<Integer> curHeads, int curId) {
        if (curId < 0 || curId > curHeads.size()) {
            return -1;
        }

        int childCount = 0;

        for (int curHead : curHeads) {
            if (curHead == curId + 1)
                childCount++;
        }

        return childCount;
    }

    public void parseString(List<String> inputs, boolean bZhangYue) {
        reset();
        for (int i = 0; i < inputs.size(); i++) {
            String[] unit_labels = inputs.get(i).trim().split("\t");
            if (unit_labels.length == 4 && bZhangYue) {
                forms.add(unit_labels[0]);

                String cpostag = unit_labels[1];
                String postag = unit_labels[1];

                postags.add(postag);
                if (unit_labels[2].equals("_")) {
                    unit_labels[2] = "-2";
                }
                heads.add(Integer.parseInt(unit_labels[2]) + 1);
                deprels.add(unit_labels[3]);
            } else if (unit_labels.length >= 8 && !bZhangYue) {
                forms.add(unit_labels[1]);
                String cpostag = unit_labels[3];
                String postag = unit_labels[4];
                if (cpostag.equals("_") && !postag.equals("_")) {
                    cpostag = postag;
                } else if (postag.equals("_") && !cpostag.equals("_")) {
                    postag = cpostag;
                } else if (!postag.equals("_") && !cpostag.equals("_")) {

                } else {
                    System.out.println("Error intput strings: no pos tags!");
                }

                postags.add(postag);
                if (unit_labels[6].equals("_")) {
                    unit_labels[6] = "-1";
                }
                heads.add(Integer.parseInt(unit_labels[6]));
                deprels.add(unit_labels[7]);
            } else {
                System.out.println("Error intput strings: col number does not match!");
            }

        }
    }

    public void parseStringByMartins(String[][] inputs) {
        reset();
        for (int i = 0; i < inputs.length; i++) {
            String[] unit_labels = inputs[i];
            if (unit_labels.length == 10) {
                forms.add(unit_labels[1]);
                String postag = unit_labels[3];
                postags.add(postag);
                if (unit_labels[5].equals("_")) {
                    unit_labels[5] = "-1";
                }
                heads.add(Integer.parseInt(unit_labels[5]));
                deprels.add(unit_labels[6]);
            } else {
                System.out.println("Error intput strings: col number does not match!");
            }

        }
    }

    public int getSpanLastHead(int start, int end) {

        for (int j = end; j >= start; j--) {
            int head = heads.get(j) - 1;
            if (head < start || head > end) {
                return j;
            }
        }

        return -1;
    }

    public void getHeadSpan(int root, List<Integer> spans) {

        int begin = -1;
        for (int idx = 0; idx < heads.size(); idx++) {
            if (bIsInSpan(idx, root)) {
                begin = idx;
            }
        }
        int end = begin;
        for (int idx = heads.size() - 1; idx > begin; idx--) {
            if (bIsInSpan(idx, root)) {
                end = idx;
            }
        }

        spans.clear();
        spans.add(begin);
        spans.add(end);
    }

    public boolean bIsInSpan(int child, int parent) {
        int head = child;
        while (head != parent && head != -1) {
            head = heads.get(head) - 1;
        }

        return head == parent;
    }


}

package corpus;

import java.util.*;

public class DepOpinion {
    public DepInstance dep;
    public List<Integer> op_forms;
    public List<Integer> op_forms_start;
    public List<Integer> op_forms_end;
    public List<Integer> op_heads;
    public List<String> op_deprels;
    public List<Dependency> op_arcs;
    public boolean bProjective;

    public DepOpinion() {
        dep = new DepInstance();
        op_forms = new ArrayList<Integer>();
        op_forms_start = new ArrayList<Integer>();
        op_forms_end = new ArrayList<Integer>();
        op_heads = new ArrayList<Integer>();
        op_deprels = new ArrayList<String>();
        op_arcs = new ArrayList<Dependency>();
        op_forms.add(0);
        op_forms_start.add(0);
        op_forms_end.add(0);
        op_heads.add(0);
        op_deprels.add("-OP-ROOT-");
        bProjective = true;
    }

    public DepOpinion(DepInstance ydep) {
        dep = new DepInstance(ydep);
        op_forms = new ArrayList<Integer>();
        op_forms_start = new ArrayList<Integer>();
        op_forms_end = new ArrayList<Integer>();
        op_heads = new ArrayList<Integer>();
        op_deprels = new ArrayList<String>();
        op_arcs = new ArrayList<Dependency>();
        op_forms.add(0);
        op_forms_start.add(0);
        op_forms_end.add(0);
        op_heads.add(0);
        op_deprels.add("-OP-ROOT-");
        bProjective = true;
    }

    public void reset() {
        dep.reset();
        op_forms = new ArrayList<Integer>();
        op_forms_start = new ArrayList<Integer>();
        op_forms_end = new ArrayList<Integer>();
        op_heads = new ArrayList<Integer>();
        op_deprels = new ArrayList<String>();
        op_arcs = new ArrayList<Dependency>();
        op_forms.add(0);
        op_forms_start.add(0);
        op_forms_end.add(0);
        op_heads.add(0);
        op_deprels.add("-OP-ROOT-");
        bProjective = true;
    }

    public int wordsize() {
        return dep.size();
    }

    public int arcsize() {
        return op_arcs.size();
    }

    public int op_wordsize() {
        return op_forms.size();
    }

    public void toCONLLString(List<String> outputs) {
        outputs.clear();
        dep.toCONLLString(outputs);
        int length = op_forms.size();
        /*
        for (int i = 0; i < arcsize(); i++) {
            //outputs.add(op_arcs.get(i).toString());
            outputs.add(String.format("(%s %s %s)",  dep.forms.get(op_arcs.get(i).child-1),
                    op_arcs.get(i).head > 0 ? dep.forms.get(op_arcs.get(i).head-1) : "-1", op_arcs.get(i).label));
        }
        outputs.add(String.format("%s", bProjective ? "projective" : "non-projective"));
        */
        for (int i = 0; i < length; i++) {
            String tmpOut = String.format("%d\t%d\t%d\t%d\t_\t_\t%d\t%s\t_\t_", i + 1, op_forms.get(i), op_forms_start.get(i), op_forms_end.get(i), op_heads.get(i), op_deprels.get(i));
            outputs.add(tmpOut);
        }
    }

    public void toZhangYueString(List<String> outputs) {
        outputs.clear();
        dep.toZhangYueString(outputs);
        int length = op_forms.size();
        /*
        for (int i = 0; i < arcsize(); i++) {
            //outputs.add(op_arcs.get(i).toString());
            outputs.add(String.format("(%s %s %s)",  dep.forms.get(op_arcs.get(i).child-1),
                    op_arcs.get(i).head > 0 ? dep.forms.get(op_arcs.get(i).head-1) : "-1", op_arcs.get(i).label));
        }
        outputs.add(String.format("%s", bProjective ? "projective" : "non-projective"));
        */
        for (int i = 0; i < length; i++) {
            String tmpOut = String.format("%d\t%d\t%d\t%d\t%s", op_forms.get(i) - 1, op_forms_start.get(i) - 1, op_forms_end.get(i) - 1, op_heads.get(i) - 1, op_deprels.get(i));
            outputs.add(tmpOut);
        }
    }

}

package corpus;

import java.util.*;

//all conll format, no Yue format
public class Dependency {
    public int child;
    public int head;
    public String label;
    public int cs_start;
    public int cs_end;


    public Dependency() {
        child = -1;
        head = -1;
        label = "-INVALID-";
        cs_start = -1;
        cs_end = -1;
    }

    
    public Dependency(int curChild, int curHead, String curLabel, int cur_cs_start, int cur_cs_end) {
        child = curChild;
        head = curHead;
        label = curLabel;
        cs_start = cur_cs_start;
        cs_end = cur_cs_end;
    }

    public void reset() {
        child = -1;
        head = -1;
        label = "-INVALID-";
        cs_start = -1;
        cs_end = -1;
    }

    public String toString() {
        return String.format("(%d %d %s)", child, head, label);
    }

    public void parse(String input) {
        if (input.length() < 7) {
            reset();
            return;
        }
        String cleanInput = input.substring(1, input.length() - 1);
        String[] smallunits = input.split("\\s+");
        if (smallunits.length != 3) {
            reset();
            return;
        }
        child = Integer.parseInt(smallunits[0]);
        head = Integer.parseInt(smallunits[1]);
        label = smallunits[2];
    }

    public static boolean Sort(List<Dependency> input) {
        Collections.sort(input, new Comparator() {
            public int compare(Object o1, Object o2) {
                Dependency obj1 = (Dependency) o1;
                Dependency obj2 = (Dependency) o2;
                
                if(obj1.child != obj2.child){
                    return obj1.child - obj2.child;
                }
                else if(obj1.head != obj2.head){
                    return obj1.head - obj2.head;
                }
                else{
                    return obj1.label.compareTo(obj2.label);
                }
            }
        });
        
        
        int idx = 1;
        int totalSize = input.size();
        while(idx < totalSize){
            Dependency obj1 = input.get(idx-1);
            Dependency obj2 = input.get(idx);  
            if(obj1.child == obj2.child
            && obj1.head == obj2.head
            && obj1.label.equals(obj2.label)){
                input.remove(idx);
                totalSize--;
            }
            else{
                idx++;
            }
        }
        

        /*
        int totalSize = input.size();
        int idx = 1;
        Set<Integer> invalidOPs = new HashSet<Integer>();
        while(idx < totalSize){
            Dependency obj1 = input.get(idx-1);
            Dependency obj2 = input.get(idx);            
            if(obj1.child == obj2.child
            && obj1.head == obj2.head){
                if(obj1.head == 0){
                    input.remove(idx);
                    totalSize--;
                }
                else{
                    boolean bTarget = obj1.label.equals("-is-about-") || obj2.label.equals("-is-about-") ? true : false;
                    int idy = idx+1;
                    while(idy < totalSize){
                        Dependency obj3 = input.get(idy);
                        if(obj2.child == obj3.child
                                && obj2.head == obj3.head){
                            idy++;
                            if(obj3.label.equals("-is-about-")){
                                bTarget = true;
                            }
                        }
                        else{
                            break;
                        }
                    }
                    for(int idk = idy -1; idk >= idx; idk--){
                        input.remove(idk);
                        totalSize--;
                    }
                    if(bTarget){
                        input.get(idx-1).label = "-is-about-";
                    }
                    else{
                        invalidOPs.add(input.get(idx-1).head);
                        input.remove(idx-1);
                        totalSize--;
                    }
                    
                    //idx--;
                }
            }
            else{
                idx++;
            }
            
        }
        
        idx = 0; 
        totalSize = input.size();
        while(idx < totalSize){
            Dependency obj1 = input.get(idx);           
            if(obj1.head == 0 && invalidOPs.contains(obj1.child)){
                input.remove(idx);
                totalSize--;
            }
            else if(invalidOPs.contains(obj1.head)){
                input.remove(idx);
                totalSize--;
            }
            else{
                idx++;
            }
        }
        */
        return true;
    }

}

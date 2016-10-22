package corpus;

public class Span {
    public int start;
    public int end;
    public String type;

    public Span() {
        start = -1;
        end = -1;
        type = "";
    }

    public Span(int curStart, int curEnd, String curType) {
        start = curStart;
        end = curEnd;
        type = curType;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]%s", start, end, type);
    }

    @Override
    public int hashCode() {

        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(((Span) obj).toString());
    }

}

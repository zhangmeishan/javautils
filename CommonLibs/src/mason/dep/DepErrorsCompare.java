package mason.dep;

import java.util.Comparator;

public class DepErrorsCompare implements Comparator<DepErrors> {

	@Override
	public int compare(DepErrors o1, DepErrors o2) {

		// TODO Auto-generated method stub
		int compareValue = o1.m_type.compareTo(o2.m_type);
		if ( compareValue < 0) {
			return -1;
		}
		
		if (compareValue > 0) {
			return 1;
		}

		if (compareValue == 0) {
			if (o1.m_times > o2.m_times) {
				return -1;
			}
			if (o1.m_times < o2.m_times) {
				return 1;
			}
		}

		return 0;
	}
}
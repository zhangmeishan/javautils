package corpus;

public class ZHUtil {

	/** 半角!：ASCII表中可见字符从!开始，偏移位值为33(Decimal) */
	private static final char DBC_CHAR_START = 33;
	/** 半角~：ASCII表中可见字符到~结束，偏移位值为126(Decimal) */
	private static final char DBC_CHAR_END = 126;
	/** 半角空格的值：在ASCII中为32(Decimal) */
	private static final char DBC_SPACE = ' ';

	/** 全角！：全角对应于ASCII表的可见字符从！开始，偏移值为65281 */
	private static final char SBC_CHAR_START = 65281;
	/** 全角～：全角对应于ASCII表的可见字符到～结束，偏移值为65374 */
	private static final char SBC_CHAR_END = 65374;
	/** 全角空格的值：它没有遵从与ASCII的相对偏移，必须单独处理 */
	private static final char SBC_SPACE = 12288;

	/** 全角半角转换间隔：ASCII表中除空格外的可见字符与对应的全角字符的相对偏移 */
	private static final int CONVERT_STEP = 65248;


	/**
	 * 全角字符->半角字符转换<br/>
	 * 只处理全角的空格，全角！到全角～之间的字符，忽略其他
	 * @param src 原文本
	 * @return 转换后的文本
	 */
	public static String toDBC(String src) {
		if (src == null)
			return src;

		char[] ca = src.toCharArray();
		StringBuilder buf = new StringBuilder(ca.length);
		for (char c : ca) {
			if (c >= SBC_CHAR_START && c <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内
				buf.append((char) (c - CONVERT_STEP));
			} else if (c == SBC_SPACE) { // 如果是全角空格
				buf.append(DBC_SPACE);
			} else { // 不处理全角空格，全角！到全角～区间外的字符
				buf.append(c);
			}
		}
		return buf.toString();
	}

	/**
	 * 半角字符->全角字符转换<br/>
	 * 只处理空格，!到˜之间的字符，忽略其他
	 * @param src 原文本
	 * @return 转换后的文本
	 */
	public static String toSBC(String src) {
		if (src == null)
			return src;

		char[] ca = src.toCharArray();
		StringBuilder buf = new StringBuilder(ca.length);
		for (char c : ca) {
			if (c == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
				buf.append(SBC_SPACE);
			} else if ((c >= DBC_CHAR_START) && (c <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
				buf.append((char) (c + CONVERT_STEP));
			} else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
				buf.append(c);
			}
		}
		return buf.toString();
	}
}

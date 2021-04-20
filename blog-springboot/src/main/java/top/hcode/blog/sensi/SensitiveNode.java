package top.hcode.blog.sensi;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * 敏感词节点，每个节点包含了以相同的2个字符开头的所有词
 *
 */
public class SensitiveNode implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 头两个字符的Hash，Hash相同，两个字符相同
	 */
	protected final int headTwoCharHash;

	/**
	 * 所有以这两个字符开头的词表
	 */
	protected final TreeSet<StringPointer> words = new TreeSet<StringPointer>();
	
	/**
	 * 下一个节点
	 */
	protected SensitiveNode next;

	public SensitiveNode(int headTwoCharHash){
		this.headTwoCharHash = headTwoCharHash;
	}

	public SensitiveNode(int headTwoCharHash, SensitiveNode parent){
		this.headTwoCharHash = headTwoCharHash;
		parent.next = this;
	}

}

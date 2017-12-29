package trees.segmenttree;
import interval.Interval;

import java.util.List;

/**
 * 
 */

/**
 * @author Burcak Otlu
 * @date Jan 5, 2017
 * @project JOA
 *  
 *
 */
public class SegmentTreeNode {

	SegmentTreeNode left;
	SegmentTreeNode right;
	Interval segment;
	List<Interval> canonicalSubset;
	
	
	SegmentTreeNode backwardNode = null;
	SegmentTreeNode forwardNode = null;
	
	//26 April 2017
	int level;
	
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	
	public SegmentTreeNode getBackwardNode() {
		return backwardNode;
	}
	public void setBackwardNode(SegmentTreeNode backwardNode) {
		this.backwardNode = backwardNode;
	}
	
	
	public SegmentTreeNode getForwardNode() {
		return forwardNode;
	}
	public void setForwardNode(SegmentTreeNode forwardNode) {
		this.forwardNode = forwardNode;
	}
	
	
	public SegmentTreeNode getLeft() {
		return left;
	}
	public void setLeft(SegmentTreeNode left) {
		this.left = left;
	}
	
	
	public SegmentTreeNode getRight() {
		return right;
	}
	public void setRight(SegmentTreeNode right) {
		this.right = right;
	}
	
	
	public Interval getSegment() {
		return segment;
	}
	public void setSegment(Interval segment) {
		this.segment = segment;
	}
	

	
	public List<Interval> getCanonicalSubset() {
		return canonicalSubset;
	}
	public void setCanonicalSubset(List<Interval> canonicalSubset) {
		this.canonicalSubset = canonicalSubset;
	}
	
	
	public SegmentTreeNode(
			SegmentTreeNode left, 
			SegmentTreeNode right, 
			Interval segment, 
			List<Interval> canonicalSubset) {
		super();
		this.left = left;
		this.right = right;
		this.segment = segment;
		this.canonicalSubset = canonicalSubset;
	}
	
	public SegmentTreeNode(
			SegmentTreeNode left, 
			SegmentTreeNode right, 
			Interval segment) {
		super();
		this.left = left;
		this.right = right;
		this.segment = segment;
	}
	
	
	//26 April 2017
	public SegmentTreeNode(
			SegmentTreeNode left, 
			SegmentTreeNode right, 
			Interval segment,
			int level) {
		super();
		this.left = left;
		this.right = right;
		this.segment = segment;
		this.level = level;
	}
	
	public SegmentTreeNode(Interval segment) {
		
		super();
		this.left = null;
		this.right = null;
		this.segment = segment;
	}
	
	//26 April 2017
	public SegmentTreeNode(Interval segment,int level) {
		
		super();
		this.left = null;
		this.right = null;
		this.segment = segment;
		this.level = level;
		
	}
	
	public SegmentTreeNode(
			SegmentTreeNode left, 
			SegmentTreeNode right, 
			Interval segment, 
			List<Interval> canonicalSubset, 
			int level) {
		
		super();
		this.left = left;
		this.right = right;
		this.segment = segment;
		this.canonicalSubset = canonicalSubset;
		this.level = level;
		
	};
	
	
	public SegmentTreeNode() {
		super();
		
	}
	
	
}

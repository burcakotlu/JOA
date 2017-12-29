/**
 * 
 */
package trees.segmenttree;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import gnu.trove.map.TIntObjectMap;
import interval.Interval;

/**
 * @author Burcak Otlu 
 * @date Dec 15, 2017
 * @project JOA
 */
public class SegmentTreeSearchInParallelChromosomeBased_ResultingIntervalOnly  extends RecursiveAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5489039255246008052L;
	
	private int lowerChrNumber;
	private int upperChrNumber;
	private TIntObjectMap<List<Interval>> chrNumber2IntervalsListMap;
	private TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap;
	
	
	

	public SegmentTreeSearchInParallelChromosomeBased_ResultingIntervalOnly(
			int lowerChrNumber, 
			int upperChrNumber,
			TIntObjectMap<List<Interval>> chrNumber2IntervalsListMap,
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap) {
		
		super();
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.chrNumber2IntervalsListMap = chrNumber2IntervalsListMap;
		this.chrNumber2SegmentTreeNodeMap = chrNumber2SegmentTreeNodeMap;
	}




	@Override
	protected void compute() {
		
		
		if (lowerChrNumber == upperChrNumber){			
			
			List<Interval> intervalsList = null;
			SegmentTreeNode segmentTreeNode = null;
			List<Interval> updatedIntervalsList = null;
			
			//We will search for these intervals in the list
			intervalsList = chrNumber2IntervalsListMap.get(lowerChrNumber);		
			
			//We will find overlaps with the intervals in index2SegmentTreeNodeMap
			segmentTreeNode = chrNumber2SegmentTreeNodeMap.get(lowerChrNumber);
				
			//Now start search in chromosome based			
			updatedIntervalsList = SegmentTree.search(
							intervalsList,
							segmentTreeNode);					
						
			chrNumber2IntervalsListMap.put(lowerChrNumber, updatedIntervalsList);
		}
							
		
		else {
			
			int middleChrNumber = (lowerChrNumber + upperChrNumber)/2;
			
			SegmentTreeSearchInParallelChromosomeBased_ResultingIntervalOnly leftChromosomes = new SegmentTreeSearchInParallelChromosomeBased_ResultingIntervalOnly(
					lowerChrNumber,
					middleChrNumber,
					chrNumber2IntervalsListMap,
					chrNumber2SegmentTreeNodeMap);
			
			
			SegmentTreeSearchInParallelChromosomeBased_ResultingIntervalOnly rightChromosomes = new SegmentTreeSearchInParallelChromosomeBased_ResultingIntervalOnly(
					middleChrNumber+1,
					upperChrNumber,
					chrNumber2IntervalsListMap,
					chrNumber2SegmentTreeNodeMap);
						

			invokeAll(leftChromosomes, rightChromosomes);
			
		}
		
	}
	
	
	

}

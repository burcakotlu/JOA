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
 * @date Oct 18, 2017
 * @project JOA
 */
public class SegmentTreeSearchInParallelChromosomeBased extends RecursiveAction{
	
	private static final long serialVersionUID = -5470597604898019994L;
	
	private int lowerChrNumber;
	private int upperChrNumber;
	private TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap;
	private TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap;
	private Boolean searchWithInfo;
	
	
	public SegmentTreeSearchInParallelChromosomeBased(
			int lowerChrNumber, 
			int upperChrNumber,
			TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap,
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeNodeMap, 
			Boolean searchWithInfo) {
		
		super();
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.chrNumber2OverlappingIntervalsListListMap = chrNumber2OverlappingIntervalsListListMap;
		this.chrNumber2SegmentTreeNodeMap = chrNumber2SegmentTreeNodeMap;
		this.searchWithInfo = searchWithInfo;
		
	}


	protected void compute() {
		
		
		if (lowerChrNumber == upperChrNumber){			
			
			List<List<Interval>> overlappingIntervalsListList = null;
			SegmentTreeNode segmentTreeNode = null;
			List<List<Interval>> updatedOverlappingIntervalsListList = null;
			
			//We will search for these intervals in the list
			overlappingIntervalsListList = chrNumber2OverlappingIntervalsListListMap.get(lowerChrNumber);		
			
			//We will find overlaps with the intervals in index2SegmentTreeNodeMap
			segmentTreeNode = chrNumber2SegmentTreeNodeMap.get(lowerChrNumber);
				
			//Now start search in chromosome based			
			updatedOverlappingIntervalsListList = SegmentTree.search(
							overlappingIntervalsListList,
							segmentTreeNode,
							searchWithInfo);					
			
			
			chrNumber2OverlappingIntervalsListListMap.put(lowerChrNumber, updatedOverlappingIntervalsListList);
		}
							
		
		else {
			
			int middleChrNumber = (lowerChrNumber + upperChrNumber)/2;
			
			SegmentTreeSearchInParallelChromosomeBased leftChromosomes = new SegmentTreeSearchInParallelChromosomeBased(
					lowerChrNumber,
					middleChrNumber,
					chrNumber2OverlappingIntervalsListListMap,
					chrNumber2SegmentTreeNodeMap,
					searchWithInfo);
			
			
			SegmentTreeSearchInParallelChromosomeBased rightChromosomes = new SegmentTreeSearchInParallelChromosomeBased(
					middleChrNumber+1,
					upperChrNumber,
					chrNumber2OverlappingIntervalsListListMap,
					chrNumber2SegmentTreeNodeMap,
					searchWithInfo);
						
//			leftChromosomes.fork();
//			rightChromosomes.fork();			
//			leftChromosomes.join();
//			rightChromosomes.join();

			invokeAll(leftChromosomes, rightChromosomes);
			
		}
		
	}


	
}

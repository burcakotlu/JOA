/**
 * 
 */
package trees.segmenttree;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import enumtypes.SearchMethod;
import gnu.trove.map.TIntObjectMap;
import interval.Interval;

/**
 * @author Burcak Otlu
 *
 */
public class IndexedSegmentTreeForestSearchInParallelChromosomeBased extends RecursiveAction{
	
	private static final long serialVersionUID = 8146486248772910155L;
	
	private int lowerChrNumber;
	private int upperChrNumber;
	
	private int presetValue;
	private TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap;
	private TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap;
	
	private SearchMethod searchMethod;
	private Boolean searchInfo;
		
	public IndexedSegmentTreeForestSearchInParallelChromosomeBased(
			int lowerChrNumber, 
			int upperChrNumber, 
			int presetValue, 
			TIntObjectMap<List<List<Interval>>> chrNumber2OverlappingIntervalsListListMap, 
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap,
			SearchMethod searchMethod,
			Boolean searchInfo) {
		
		super();
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.presetValue = presetValue;
		this.chrNumber2OverlappingIntervalsListListMap = chrNumber2OverlappingIntervalsListListMap;
		this.chrNumber2HashIndex2SegmentTreeNodeMapMap = chrNumber2HashIndex2SegmentTreeNodeMapMap;
		this.searchMethod = searchMethod;
		this.searchInfo = searchInfo;
	}


	protected void compute() {
				
		if (lowerChrNumber == upperChrNumber){			
			
			List<List<Interval>> overlappingIntervalsListList = null;
			TIntObjectMap<SegmentTreeNode> index2SegmentTreeNodeMap = null;
			List<List<Interval>> updatedOverlappingIntervalsListList = null;
			
			//We will search for these intervals in the list
			overlappingIntervalsListList = chrNumber2OverlappingIntervalsListListMap.get(lowerChrNumber);		
			
			//We will find overlaps with the intervals in index2SegmentTreeNodeMap
			index2SegmentTreeNodeMap = chrNumber2HashIndex2SegmentTreeNodeMapMap.get(lowerChrNumber);
				
			//Now start search in chromosome based	
			updatedOverlappingIntervalsListList = SegmentTree.search(
					presetValue,
					overlappingIntervalsListList,
					index2SegmentTreeNodeMap,
					searchMethod,
					searchInfo);
			
			
			chrNumber2OverlappingIntervalsListListMap.put(lowerChrNumber, updatedOverlappingIntervalsListList);
							
		}
		else {
			
			int middleChrNumber = (lowerChrNumber + upperChrNumber)/2;
			
			IndexedSegmentTreeForestSearchInParallelChromosomeBased leftChromosomes = new IndexedSegmentTreeForestSearchInParallelChromosomeBased(
					lowerChrNumber,
					middleChrNumber,
					presetValue,
					chrNumber2OverlappingIntervalsListListMap,
					chrNumber2HashIndex2SegmentTreeNodeMapMap,
					searchMethod,
					searchInfo);
			
			
			IndexedSegmentTreeForestSearchInParallelChromosomeBased rightChromosomes = new IndexedSegmentTreeForestSearchInParallelChromosomeBased(
					middleChrNumber+1,
					upperChrNumber,
					presetValue,
					chrNumber2OverlappingIntervalsListListMap,
					chrNumber2HashIndex2SegmentTreeNodeMapMap,
					searchMethod,
					searchInfo);
						
			
//			leftChromosomes.fork();
//			rightChromosomes.fork();
//			leftChromosomes.join();
//			rightChromosomes.join();
			
			invokeAll(leftChromosomes, rightChromosomes);

			
		}
		
	}

}

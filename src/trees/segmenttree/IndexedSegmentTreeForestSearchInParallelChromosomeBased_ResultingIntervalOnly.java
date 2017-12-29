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
 * @date Dec 14, 2017
 * @project JOA
 */
public class IndexedSegmentTreeForestSearchInParallelChromosomeBased_ResultingIntervalOnly extends RecursiveAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6225023199407850729L;
	
	private int lowerChrNumber;
	private int upperChrNumber;	
	private int presetValue;
	private TIntObjectMap<List<Interval>> chrNumber2OverlappingIntervalsListMap;
	private TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap;
	private SearchMethod searchMethod;
	
	
	
	

	public IndexedSegmentTreeForestSearchInParallelChromosomeBased_ResultingIntervalOnly(
			int lowerChrNumber,
			int upperChrNumber, 
			int presetValue,
			TIntObjectMap<List<Interval>> chrNumber2OverlappingIntervalsListMap,
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap,
			SearchMethod searchMethod) {
		
		super();
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.presetValue = presetValue;
		this.chrNumber2OverlappingIntervalsListMap = chrNumber2OverlappingIntervalsListMap;
		this.chrNumber2HashIndex2SegmentTreeNodeMapMap = chrNumber2HashIndex2SegmentTreeNodeMapMap;
		this.searchMethod = searchMethod;
		
	}


	@Override
	protected void compute() {
		
		
		if (upperChrNumber==lowerChrNumber){			
			
			List<Interval> overlappingIntervalsList = null;
			TIntObjectMap<SegmentTreeNode> index2SegmentTreeNodeMap = null;
			List<Interval> updatedOverlappingIntervalsList = null;
			
			//We will search for these intervals in the list
			overlappingIntervalsList = chrNumber2OverlappingIntervalsListMap.get(lowerChrNumber);		
			
			//We will find overlaps with the intervals in index2SegmentTreeNodeMap
			index2SegmentTreeNodeMap = chrNumber2HashIndex2SegmentTreeNodeMapMap.get(lowerChrNumber);
				
			//Now start search in chromosome based	
			updatedOverlappingIntervalsList = SegmentTree.search(
					presetValue,
					overlappingIntervalsList,
					index2SegmentTreeNodeMap,
					searchMethod);
			
			
			chrNumber2OverlappingIntervalsListMap.put(lowerChrNumber, updatedOverlappingIntervalsList);
							
		}
		else {
			
			int middleChrNumber = (lowerChrNumber+upperChrNumber)/2;
			
			IndexedSegmentTreeForestSearchInParallelChromosomeBased_ResultingIntervalOnly leftChromosomes = new IndexedSegmentTreeForestSearchInParallelChromosomeBased_ResultingIntervalOnly(
					lowerChrNumber,
					middleChrNumber,
					presetValue,
					chrNumber2OverlappingIntervalsListMap,
					chrNumber2HashIndex2SegmentTreeNodeMapMap,
					searchMethod);
			
			
			IndexedSegmentTreeForestSearchInParallelChromosomeBased_ResultingIntervalOnly rightChromosomes = new IndexedSegmentTreeForestSearchInParallelChromosomeBased_ResultingIntervalOnly(
					middleChrNumber+1,
					upperChrNumber,
					presetValue,
					chrNumber2OverlappingIntervalsListMap,
					chrNumber2HashIndex2SegmentTreeNodeMapMap,
					searchMethod);
						
			
			invokeAll(leftChromosomes, rightChromosomes);


		}
	}

}

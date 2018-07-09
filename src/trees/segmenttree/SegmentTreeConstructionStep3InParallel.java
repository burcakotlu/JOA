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
 * @date May 10, 2017
 * @project Joa 
 *
 *
 * This parallel class constructs in chromosome based in parallel for One Segment Tree for each Interval Set
 *
 */

//Think and Code
//10 May 2017 starts
//Normal Segment Tree and IndexedSegmentTree in Step3 for 75Percentange and AfterConstruction modes utilizes this code.
public class SegmentTreeConstructionStep3InParallel extends RecursiveAction{
	
	private static final long serialVersionUID = 1L;

	
	private int lowerChrNumber;
	private int upperChrNumber;
	private TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap;
	private TIntObjectMap<List<Interval>> chrNumber2IntervalsMap;
	private TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeRootNodeMap;
	
	public SegmentTreeConstructionStep3InParallel(
			int lowerChrNumber, 
			int upperChrNumber,
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalsMap,
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeRootNodeMap){
		
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.chrNumber2SortedEndPointsArrayMap = chrNumber2SortedEndPointsArrayMap;
		this.chrNumber2IntervalsMap = chrNumber2IntervalsMap;
		this.chrNumber2SegmentTreeRootNodeMap=chrNumber2SegmentTreeRootNodeMap;
		
	}
	
	public void compute(){
		
		
		if (lowerChrNumber == upperChrNumber){
			
			//What if chrNumber2IntervalsMap.get(lowerChrNumber) is null						
			if (chrNumber2IntervalsMap.get(lowerChrNumber)!=null) {
				
				Interval[] intervals = new Interval[chrNumber2IntervalsMap.get(lowerChrNumber).size()];
				chrNumber2IntervalsMap.get(lowerChrNumber).toArray(intervals);
				
				SegmentTreeNode root = SegmentTree.constructSegmentTree(chrNumber2SortedEndPointsArrayMap.get(lowerChrNumber), intervals);

				chrNumber2SegmentTreeRootNodeMap.put(lowerChrNumber,root);	
			}
					
			
		}
		else{
			
			int middleChrNumber = (lowerChrNumber+upperChrNumber)/2;
			
			SegmentTreeConstructionStep3InParallel leftChromosomes = new SegmentTreeConstructionStep3InParallel(
					lowerChrNumber,
					middleChrNumber,
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalsMap,
					chrNumber2SegmentTreeRootNodeMap);
			
			
			SegmentTreeConstructionStep3InParallel rightChromosomes = new SegmentTreeConstructionStep3InParallel(
					middleChrNumber+1,
					upperChrNumber,
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalsMap,
					chrNumber2SegmentTreeRootNodeMap);
						
//			leftChromosomes.fork();
//			rightChromosomes.fork();			
//			leftChromosomes.join();
//			rightChromosomes.join();
			
			invokeAll(leftChromosomes, rightChromosomes);
					
		}
		
	}
	
	
	 		
}
//10 May 2017 ends

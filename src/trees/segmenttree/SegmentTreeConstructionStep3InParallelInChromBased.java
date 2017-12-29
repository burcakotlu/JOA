/**
 * 
 */
package trees.segmenttree;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import interval.Interval;

/**
 * @author Burcak Otlu
 * @date September 27, 2017
 *
 */
public class SegmentTreeConstructionStep3InParallelInChromBased extends RecursiveTask<TIntObjectMap<SegmentTreeNode>>{
	
	private static final long serialVersionUID = 2965862627450783792L;
		
	private int lowerChrNumber;
	private int upperChrNumber;
	private TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap;
	private TIntObjectMap<List<Interval>> chrNumber2IntervalsMap;
	private TIntIntMap chrNumber2IndexingLevelMap;
	private int numberofPercent;
	
	public SegmentTreeConstructionStep3InParallelInChromBased(
			int lowerChrNumber, 
			int upperChrNumber,
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalsMap,
			TIntIntMap chrNumber2IndexingLevelMap,
			int numberofPercent){
		
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.chrNumber2SortedEndPointsArrayMap = chrNumber2SortedEndPointsArrayMap;
		this.chrNumber2IntervalsMap = chrNumber2IntervalsMap;
		this.chrNumber2IndexingLevelMap = chrNumber2IndexingLevelMap;
		this.numberofPercent = numberofPercent;
		
	}
	
	public TIntObjectMap<SegmentTreeNode> compute(){
		
		
		
		if (upperChrNumber==lowerChrNumber){
						
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeRootNodeMap = new TIntObjectHashMap<SegmentTreeNode>();
			
			if (chrNumber2IntervalsMap.get(lowerChrNumber)!=null) {
				
				TIntIntMap levelNumber2NumberofIntervalsMap = new TIntIntHashMap();
				
				
				Interval[] intervals = new Interval[chrNumber2IntervalsMap.get(lowerChrNumber).size()];
				chrNumber2IntervalsMap.get(lowerChrNumber).toArray(intervals);
				
				SegmentTreeNode root = SegmentTree.constructSegmentTree(chrNumber2SortedEndPointsArrayMap.get(lowerChrNumber),intervals,levelNumber2NumberofIntervalsMap);
				chrNumber2SegmentTreeRootNodeMap.put(lowerChrNumber, root);
				
				//Calculate indexingLevel from levelNumber2NumberofIntervalsMap
				int indexingLevel = SegmentTree.calculateIndexingLevel(levelNumber2NumberofIntervalsMap,numberofPercent);
				
				//This part is added
				chrNumber2IndexingLevelMap.put(lowerChrNumber,indexingLevel);
				
			}
			
			return chrNumber2SegmentTreeRootNodeMap;			
			
		}
		else{
			
			int middleChrNumber = (lowerChrNumber+upperChrNumber)/2;
			
			SegmentTreeConstructionStep3InParallelInChromBased leftChromosomes = new SegmentTreeConstructionStep3InParallelInChromBased(
					lowerChrNumber,
					middleChrNumber,
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalsMap,
					chrNumber2IndexingLevelMap,
					numberofPercent);
			
			
			SegmentTreeConstructionStep3InParallelInChromBased rightChromosomes = new SegmentTreeConstructionStep3InParallelInChromBased(
					middleChrNumber+1,
					upperChrNumber,
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalsMap,
					chrNumber2IndexingLevelMap,
					numberofPercent);

			
			leftChromosomes.fork();				
			TIntObjectMap<SegmentTreeNode> rightChrNumber2SegmentTreeRootMap = rightChromosomes.compute();
			TIntObjectMap<SegmentTreeNode> leftChrNumber2SegmentTreeRootMap =leftChromosomes.join();
			
			return combine(leftChrNumber2SegmentTreeRootMap,rightChrNumber2SegmentTreeRootMap);
					
		}
		
	}
	
	 public TIntObjectMap<SegmentTreeNode> combine(
			 TIntObjectMap<SegmentTreeNode> leftChrNumber2SegmentTreeRootMap,
			 TIntObjectMap<SegmentTreeNode> rightChrNumber2SegmentTreeRootMap){
		 		
		leftChrNumber2SegmentTreeRootMap.putAll(rightChrNumber2SegmentTreeRootMap);
		
		//for less memory usage
		rightChrNumber2SegmentTreeRootMap = null;
		 
		return leftChrNumber2SegmentTreeRootMap;
		 
	 }
	 		
}
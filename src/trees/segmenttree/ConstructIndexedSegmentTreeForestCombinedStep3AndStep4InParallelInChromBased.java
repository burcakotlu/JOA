/**
 * 
 */
package trees.segmenttree;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import enumtypes.IndexingLevelDecisionMode;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import interval.Interval;

/**
 * @author Burcak Otlu 
 * @date Dec 2, 2017
 * @project JOA
 */
public class ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased extends RecursiveTask<TIntObjectMap<TIntObjectMap<SegmentTreeNode>>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3011724262451870869L;

	
	private int lowerChrNumber;
	private int upperChrNumber;
	private TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap;
	private TIntObjectMap<List<Interval>> chrNumber2IntervalsMap;
	private int numberofPercent;	
	private IndexingLevelDecisionMode mode;
	private int presetValue;
	
	
	
	
	public ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased(
			int lowerChrNumber,
			int upperChrNumber, 
			TIntObjectMap<int[]> chrNumber2SortedEndPointsArrayMap,
			TIntObjectMap<List<Interval>> chrNumber2IntervalsMap, 
			int numberofPercent, 
			IndexingLevelDecisionMode mode,
			int presetValue) {
		
		super();
		this.lowerChrNumber = lowerChrNumber;
		this.upperChrNumber = upperChrNumber;
		this.chrNumber2SortedEndPointsArrayMap = chrNumber2SortedEndPointsArrayMap;
		this.chrNumber2IntervalsMap = chrNumber2IntervalsMap;
		this.numberofPercent = numberofPercent;
		this.mode = mode;
		this.presetValue = presetValue;
	}




	protected TIntObjectMap<TIntObjectMap<SegmentTreeNode>> compute() {
		
		//Base Case
		if (lowerChrNumber == upperChrNumber){
			
			TIntObjectMap<SegmentTreeNode> chrNumber2SegmentTreeRootNodeMap = new TIntObjectHashMap<SegmentTreeNode>();
			int hashTableWillBeConstructedAtThisLevel = -1;
			
			TIntObjectMap<List<SegmentTreeNode>> hashIndex2SegmentTreeNodeListMap = null;		
			
			//Second fill this data structure
			TIntObjectMap<SegmentTreeNode> hashIndex2SegmentTreeNodeMap = null;
			
			//Third fill this one
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> chrNumber2HashIndex2SegmentTreeNodeMapMap = new  TIntObjectHashMap<TIntObjectMap<SegmentTreeNode>>();		
			
			
			if (chrNumber2IntervalsMap.get(lowerChrNumber)!=null) {
				
				
				TIntIntMap levelNumber2NumberofIntervalsMap = new TIntIntHashMap();
				
				/***********************************************************/
				/************* Step3 starts*********************************/
				/***********************************************************/				
				Interval[] intervals = new Interval[chrNumber2IntervalsMap.get(lowerChrNumber).size()];
				chrNumber2IntervalsMap.get(lowerChrNumber).toArray(intervals);
				
				//Construct Segment Tree
				SegmentTreeNode root = SegmentTree.constructSegmentTree(chrNumber2SortedEndPointsArrayMap.get(lowerChrNumber),intervals,levelNumber2NumberofIntervalsMap);
				chrNumber2SegmentTreeRootNodeMap.put(lowerChrNumber, root);
				
				//Calculate indexingLevel from levelNumber2NumberofIntervalsMap
				int indexingLevel = SegmentTree.calculateIndexingLevel(levelNumber2NumberofIntervalsMap,numberofPercent);
				/***********************************************************/
				/************* Step3 ends **********************************/
				/***********************************************************/				
				
				
				/***********************************************************/
				/************* Step4 starts*********************************/
				/***********************************************************/				
				hashTableWillBeConstructedAtThisLevel = root.getLevel()-indexingLevel+1;
				
				//Construct indexed segment tree forest at the decided level
				hashIndex2SegmentTreeNodeListMap = new TIntObjectHashMap<List<SegmentTreeNode>>();
				
				//Step4 Fill hashIndex2SegmentTreeNodeListMap for this chromosome at the decided level
				//For any intervals in nodes at lower levels than indexing level, add them to their offsprings nodes	
				//For the nodes that have no children and at levels less than indexing level add them to the composite data structure 
				//Like higher the level first add to the composite data structure 
				//So we add nodes to the composite data structure 
				//We move intervals to the associated nodes
				
//				//new way
//				//get the nodeList and such intervals and then store such intervals on the nodeList
//				SegmentTree.constructIndexedSegmentTreeForest(
//						root,
//						hashTableWillBeConstructedAtThisLevel,
//						hashIndex2SegmentTreeNodeListMap,
//						presetValue);
				
				
				//Old way
				//get the nodeList and during this process move intervals downward till the cutoff level
				SegmentTree.fillCompositeDataStructureAndCopyAssociatedIntervals(
						root,
						hashTableWillBeConstructedAtThisLevel,
						hashIndex2SegmentTreeNodeListMap,
						presetValue);
				
				
									
				//Step5 Put forward and backward links between segment tree nodes where the hash table is constructed at.
				SegmentTree.connectOriginalNodes(hashIndex2SegmentTreeNodeListMap);
				
				//Step6 Construct hashIndex2SegmentTreeNodeMap
				//For collisions construct BST for the nodes with the same index and now hash index points to the root of the BST
				hashIndex2SegmentTreeNodeMap = new TIntObjectHashMap<SegmentTreeNode>();
				SegmentTree.fill(hashIndex2SegmentTreeNodeMap,hashIndex2SegmentTreeNodeListMap);
				
				//for less memory usage
				hashIndex2SegmentTreeNodeListMap = null;
				chrNumber2IntervalsMap.put(lowerChrNumber, null);
				levelNumber2NumberofIntervalsMap = null;
													
				//Put it into map
				chrNumber2HashIndex2SegmentTreeNodeMapMap.put(lowerChrNumber, hashIndex2SegmentTreeNodeMap);
				/***********************************************************/
				/************* Step4 ends **********************************/
				/***********************************************************/		
											
				
			}
		
			return chrNumber2HashIndex2SegmentTreeNodeMapMap;
		
		} else {

			int middleChrNumber = (lowerChrNumber + upperChrNumber)/2;
			
			ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased leftChromosomes = new ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased(
					lowerChrNumber,
					middleChrNumber, 
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalsMap, 
					numberofPercent, 
					mode,
					presetValue);
			
			
			ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased rightChromosomes = new ConstructIndexedSegmentTreeForestCombinedStep3AndStep4InParallelInChromBased(
					middleChrNumber+1,
					upperChrNumber, 
					chrNumber2SortedEndPointsArrayMap,
					chrNumber2IntervalsMap, 
					numberofPercent, 
					mode,
					presetValue);
			
			leftChromosomes.fork();				
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> rightChrNumber2HashIndex2SegmentTreeNodeMapMap = rightChromosomes.compute();
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> leftChrNumber2HashIndex2SegmentTreeNodeMapMap = leftChromosomes.join();
			
			return combine(leftChrNumber2HashIndex2SegmentTreeNodeMapMap,rightChrNumber2HashIndex2SegmentTreeNodeMapMap);	
			

		}
		
		
	}
	
	
	public TIntObjectMap<TIntObjectMap<SegmentTreeNode>> combine(
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> leftChrNumber2HashIndex2SegmentTreeNodeMapMap,
			TIntObjectMap<TIntObjectMap<SegmentTreeNode>> rightChrNumber2HashIndex2SegmentTreeNodeMapMap){
		 		
		leftChrNumber2HashIndex2SegmentTreeNodeMapMap.putAll(rightChrNumber2HashIndex2SegmentTreeNodeMapMap);
		 
		//for less memory usage
		rightChrNumber2HashIndex2SegmentTreeNodeMapMap = null;
		
		return leftChrNumber2HashIndex2SegmentTreeNodeMapMap;
		 
	 }

		
	
	
}

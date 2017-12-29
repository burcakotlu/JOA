/**
 * 
 */
package preset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import auxiliary.FileOperations;
import enumtypes.ChromosomeName;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Burcak Otlu 
 * @date Oct 5, 2017
 * @project JOA
 * 
 *  Since preset value decision is crucial, we can decide on
 *	it beforehand by an offline procedure. We can start with a
 *	preset value of 50000 and increment by 50000 until we reach
 *	1000000. 
 *
 *	For each tested preset value, we can compute the number of
 *	intervals assigned to each hash index and we can calculate the
 *	mean and standard deviation of number of intervals assigned
 *	to each of the hash indexes. 
 *
 *	We can choose the preset value with low standard deviation and with average mean.
 *
 *	Preset value must be file specific. Since we will be constructing one indexed segment tree forest for each file.
 *	Preset value must be chr number specific. 
 */


public class OfflinePresetValueDecision {
	
	
	public static void fillMap(TIntIntMap chrNumber2ChromSizeMap) {
		
		
		//chr1	249250621
		chrNumber2ChromSizeMap.put(1, 249250621);

		//chr2	243199373
		chrNumber2ChromSizeMap.put(2, 243199373);

		//chr3	198022430
		chrNumber2ChromSizeMap.put(3, 198022430);
		
		//chr4	191154276
		chrNumber2ChromSizeMap.put(4, 191154276);
		
		//chr5	180915260
		chrNumber2ChromSizeMap.put(5, 180915260);

		//chr6	171115067
		chrNumber2ChromSizeMap.put(6, 171115067);

		//chr7	159138663
		chrNumber2ChromSizeMap.put(7, 159138663);
		
		//chr8	146364022
		chrNumber2ChromSizeMap.put(8, 146364022);
		
		//chr9	141213431
		chrNumber2ChromSizeMap.put(9, 141213431);
		
		//chr10	135534747
		chrNumber2ChromSizeMap.put(10, 135534747);
		
		//chr11	135006516
		chrNumber2ChromSizeMap.put(11, 135006516);
		
		//chr12	133851895
		chrNumber2ChromSizeMap.put(12, 133851895);
		
		//chr13	115169878
		chrNumber2ChromSizeMap.put(13, 115169878);
		
		//chr14	107349540
		chrNumber2ChromSizeMap.put(14, 107349540);
		
		//chr15	102531392
		chrNumber2ChromSizeMap.put(15, 102531392);
		
		//chr16	90354753
		chrNumber2ChromSizeMap.put(16, 90354753);
		
		//chr17	81195210
		chrNumber2ChromSizeMap.put(17, 81195210);
		
		//chr18	78077248
		chrNumber2ChromSizeMap.put(18, 78077248);
		
		//chr19	59128983
		chrNumber2ChromSizeMap.put(19, 59128983);
		
		//chr20	63025520
		chrNumber2ChromSizeMap.put(20, 63025520);
		
		//chr21	48129895
		chrNumber2ChromSizeMap.put(21, 48129895);
		
		//chr22	51304566
		chrNumber2ChromSizeMap.put(22, 51304566);
		
		//chrX	155270560
		chrNumber2ChromSizeMap.put(23, 155270560);
		
		//chrY	59373566
		chrNumber2ChromSizeMap.put(24, 59373566);
				
	}
	
	
	public static void calculateFilebasedChrBasedPresetValue(
			int intervalSetFileNumber,
			TIntObjectMap<TIntObjectMap<TIntIntMap>> chrNumber2PresetValue2HashIndex2NumberOfElementsMap,
			TIntObjectMap<TIntIntMap> intervalSetFileNumber2ChrNumber2PresetValueMap) {
		
		int chrNumber;
		int presetValue;
				
		
		TIntObjectMap<TIntIntMap> presetValue2HashIndex2NumberOfElementsMap = null;
		TIntIntMap hashIndex2NumberOfIntervals = null;
		
		
		TIntIntMap chrNumber2PresetValueMap = new TIntIntHashMap(25);;
		intervalSetFileNumber2ChrNumber2PresetValueMap.put(intervalSetFileNumber, chrNumber2PresetValueMap);
		
		
		//For each chromosome
		for (TIntObjectIterator<TIntObjectMap<TIntIntMap>> itr1= chrNumber2PresetValue2HashIndex2NumberOfElementsMap.iterator(); itr1.hasNext();) {
			
			itr1.advance();
			
			chrNumber = itr1.key();
			presetValue2HashIndex2NumberOfElementsMap = itr1.value();
			
			//For each preset value
			//Put the smallest presetValue that has number of hash index less than 1000
			for(presetValue=50000; presetValue <= 1000000; presetValue += 50000) {
				
				hashIndex2NumberOfIntervals = presetValue2HashIndex2NumberOfElementsMap.get(presetValue);
				
				//Nov 4,2017 starts
				if(hashIndex2NumberOfIntervals.size()<1000) {
					
					chrNumber2PresetValueMap.put(chrNumber, presetValue);
					break;
				}
				//Nov 4,2017 ends
								
				
			}//For each presetValue
			
			//What if there is no presetValue satisfying number of hash index less than 10000
			//then set preset as 50000
			if (chrNumber2PresetValueMap.get(chrNumber)<=0) {
				chrNumber2PresetValueMap.put(chrNumber, 50000);
			}
						
						
		}// End of each chromosome
			
			
	
	}
	
	
	
	public static void calculateMeanStdDev(
			int intervalSetFileNumber,
			String fileName,
			TIntObjectMap<TIntObjectMap<TIntIntMap>> chrNumber2PresetValue2HashIndex2NumberOfElementsMap,
			TIntIntMap chrNumber2ChromSizeMap,
			BufferedWriter bufferedWriter) {
		
		int chrNumber;
		int presetValue;
		
		int numberofIntervals;
		int totalNumberOfIntervals;
		
		double mean = 0.0f;
		double stdDev = 0.0f;
		
		TIntObjectMap<TIntIntMap> presetValue2HashIndex2NumberOfElementsMap = null;
		TIntIntMap hashIndex2NumberOfIntervals = null;
		
		
		
		try {
			
			//Now calculate mean of number of intervals and standard deviation for each file, preset value and chr
			
			//For each chromosome
			for (TIntObjectIterator<TIntObjectMap<TIntIntMap>> itr1= chrNumber2PresetValue2HashIndex2NumberOfElementsMap.iterator(); itr1.hasNext();) {
				
				itr1.advance();
				
				chrNumber = itr1.key();
				presetValue2HashIndex2NumberOfElementsMap = itr1.value();
				
				//For each preset value
				for(presetValue=50000; presetValue <= 1000000; presetValue += 50000) {
					
					hashIndex2NumberOfIntervals = presetValue2HashIndex2NumberOfElementsMap.get(presetValue);
					
								
					//initialization 
					totalNumberOfIntervals =0;
					
					//for each hash index
					for (TIntIntIterator itr3 = hashIndex2NumberOfIntervals.iterator();itr3.hasNext();) {
						itr3.advance();
						
						numberofIntervals = itr3.value();
						
						totalNumberOfIntervals += numberofIntervals;
					}//End of each hash index
					
					//Calculate  mean
					mean = (totalNumberOfIntervals*1.0f)/hashIndex2NumberOfIntervals.size();
					
					stdDev = 0.0f;							
					//Calculate the standard deviation
					//for each hash index
					for (TIntIntIterator itr3 = hashIndex2NumberOfIntervals.iterator();itr3.hasNext();) {
						itr3.advance();
						
						numberofIntervals = itr3.value();
						
						stdDev +=  Math.pow((mean-numberofIntervals),2);
						
						
					}//End of each hash index
					
					stdDev = Math.sqrt(stdDev/hashIndex2NumberOfIntervals.size());
					
					if (bufferedWriter!=null) {
						bufferedWriter.write( fileName +"\t" + chrNumber  + "\t" + chrNumber2ChromSizeMap.get(chrNumber) + "\t" +presetValue + "\t" + mean + "\t" + stdDev + "\t" + hashIndex2NumberOfIntervals.size() + System.getProperty("line.separator"));						
					}
					
					
				}
			
				
			}// End of each chromosome
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	 
	
	
	 public static void readtheFileAndFillTheMap(String fileName,
			 TIntObjectMap<TIntObjectMap<TIntIntMap>> chrNumber2PresetValue2HashIndex2NumberOfElementsMap) {
		 
		 
		 	int indexofFirstTab = -1;
			int indexofSecondTab = -1;
			//int indexofThirdTab = -1;
			
			String chrName = null;
			int chrNumber = -1;
			int lowerEndPoint = -1;
			//int higherEndPoint = -1;
			String strLine = null;
			
			FileReader inputFileReader = null;
			BufferedReader inputBufferedReader = null;
			
			int hashIndex=0;
			
			TIntObjectMap<TIntIntMap> presetValue2HashIndex2NumberOfElementsMap = null;
			TIntIntMap hashIndex2NumberOfIntervals = null;
			int numberOfIntervals = -1;
			
			int presetValue = -1;
						
			
			try {
				
				inputFileReader = FileOperations.createFileReader(fileName);		
				inputBufferedReader = new BufferedReader(inputFileReader);
						
				while((strLine = inputBufferedReader.readLine())!=null){
					
					indexofFirstTab = strLine.indexOf('\t');
					indexofSecondTab = strLine.indexOf('\t',indexofFirstTab +1);
					//indexofThirdTab = strLine.indexOf('\t',indexofSecondTab +1);
		
					chrName = strLine.substring(0, indexofFirstTab);
					
					chrNumber =  ChromosomeName.convertStringtoEnum(chrName).getChromosomeName();
					
					lowerEndPoint =  Integer.parseInt(strLine.substring(indexofFirstTab+1, indexofSecondTab));
					
//					if (indexofThirdTab > -1){
//						higherEndPoint =  Integer.parseInt(strLine.substring(indexofSecondTab+1, indexofThirdTab));
//					}else{
//						higherEndPoint =  Integer.parseInt(strLine.substring(indexofSecondTab+1));
//					}
					
					
					presetValue2HashIndex2NumberOfElementsMap = chrNumber2PresetValue2HashIndex2NumberOfElementsMap.get(chrNumber);
					
					if (presetValue2HashIndex2NumberOfElementsMap==null) {
						
						presetValue2HashIndex2NumberOfElementsMap = new TIntObjectHashMap<TIntIntMap>();
						
						//For each preset value
						for (presetValue=50000; presetValue <= 1000000; presetValue=presetValue+50000) {
							
							//hashIndex is based on lowerEndPoint
							hashIndex= lowerEndPoint/presetValue;
							
							hashIndex2NumberOfIntervals = presetValue2HashIndex2NumberOfElementsMap.get(presetValue);
							
							if (hashIndex2NumberOfIntervals!=null) {								
								numberOfIntervals = hashIndex2NumberOfIntervals.get(hashIndex);
								hashIndex2NumberOfIntervals.put(hashIndex, numberOfIntervals+1);
							}else {
								hashIndex2NumberOfIntervals = new TIntIntHashMap();
								hashIndex2NumberOfIntervals.put(hashIndex, 1);
								presetValue2HashIndex2NumberOfElementsMap.put(presetValue, hashIndex2NumberOfIntervals);
							}
							
						}//End of each preset value
						
							
					   chrNumber2PresetValue2HashIndex2NumberOfElementsMap.put(chrNumber, presetValue2HashIndex2NumberOfElementsMap);	
					   
					} else {
						
						//For each preset value
						for (presetValue=50000; presetValue <= 1000000; presetValue=presetValue+50000) {
							
							//hashIndex is based on lowerEndPoint
							hashIndex= lowerEndPoint/presetValue;
							
							hashIndex2NumberOfIntervals = presetValue2HashIndex2NumberOfElementsMap.get(presetValue);
							
							if (hashIndex2NumberOfIntervals!=null) {								
								numberOfIntervals = hashIndex2NumberOfIntervals.get(hashIndex);
								hashIndex2NumberOfIntervals.put(hashIndex, numberOfIntervals+1);
							}else {
								hashIndex2NumberOfIntervals = new TIntIntHashMap();
								hashIndex2NumberOfIntervals.put(hashIndex, 1);
								presetValue2HashIndex2NumberOfElementsMap.put(presetValue, hashIndex2NumberOfIntervals);
							}
							
						}//End of each preset value
						
					}					
											
								
				}//End of WHILE
				
				//Close
				inputBufferedReader.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		 
	 }
		
	
	 
	 public static void write(
			 String[] intervalSetsFileNames,
			 TIntObjectMap<TIntIntMap> intervalSetFileNumber2ChrNumber2PresetValueMap,
			 TIntIntMap chrNumber2ChromSizeMap) {
		 
		 FileWriter fileWriter = null;
		 BufferedWriter bufferedWriter = null;
					 
		//write intervalSetFileNumber2ChrNumber2PresetValueMap
		TIntIntMap chrNumber2PresetValueMap  = null;
		
		String outputFileName="/home/burcak/Developer/Java/JOA/MeanStdDevofNumberofIntervalsForEachHashIndex.txt";
		
		
		try {
			fileWriter = new FileWriter(outputFileName,true);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			//headerline
			bufferedWriter.write("FileName" + "\t" +"chr" + "\t"  + "chromSize" + "\t" + "presetValue"   + System.getProperty("line.separator"));
			
			for(int i=0; i<intervalSetsFileNames.length;i++) {
				chrNumber2PresetValueMap = intervalSetFileNumber2ChrNumber2PresetValueMap.get(i);
				
				for(int j=1; j<=24;j++) {
					bufferedWriter.write(intervalSetsFileNames[i] + "\t" + j + "\t" +chrNumber2ChromSizeMap.get(j) + "\t" + chrNumber2PresetValueMap.get(j) + System.getProperty("line.separator"));
					
				}//End of for each chr 
				
			}//End of for each interval set file
			
			bufferedWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}


	 }
	
	 public static void	findBestPresetValue(
				int numberofIntervalSetInputFiles,
				String[] intervalSetsFileNames) {
		 
		 
		 TIntObjectMap<TIntIntMap> intervalSetFileNumber2ChrNumber2PresetValueMap = new TIntObjectHashMap<TIntIntMap>(intervalSetsFileNames.length);
		 
		TIntIntMap chrNumber2ChromSizeMap = new  TIntIntHashMap();
		fillMap(chrNumber2ChromSizeMap);
		
		String outputFileName="/home/burcak/Developer/Java/JOA/MeanStdDevofNumberofIntervalsForEachHashIndex.txt";
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		try {
			
			fileWriter = new FileWriter(outputFileName,true);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			//headerline
			bufferedWriter.write("FileName" + "\t" +"chr" + "\t"  + "chromSize" + "\t" + "presetValue"  + "\t" + "mean(mean number of intervals for hash indexes)"  + "\t"+  "stdDev(std dev of number of intervals for hash indexes)"  +"\t" + "NumberofHashIndex" + System.getProperty("line.separator"));					

			for(int i=0; i<numberofIntervalSetInputFiles; i++) {
				 
				 TIntObjectMap<TIntObjectMap<TIntIntMap>> chrNumber2PresetValue2HashIndex2NumberOfElementsMap = new TIntObjectHashMap<TIntObjectMap<TIntIntMap>> ();
				 
				 readtheFileAndFillTheMap(intervalSetsFileNames[i],chrNumber2PresetValue2HashIndex2NumberOfElementsMap);
				 calculateMeanStdDev(i,intervalSetsFileNames[i],chrNumber2PresetValue2HashIndex2NumberOfElementsMap,chrNumber2ChromSizeMap,bufferedWriter);			 
				 calculateFilebasedChrBasedPresetValue(i,chrNumber2PresetValue2HashIndex2NumberOfElementsMap,intervalSetFileNumber2ChrNumber2PresetValueMap);
				 //write(intervalSetsFileNames,intervalSetFileNumber2ChrNumber2PresetValueMap,chrNumber2ChromSizeMap);
				 
			}//End of for each file
			
			//Close
			bufferedWriter.close();
		 
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		 
	 }

	public static void main(String[] args) {
		
			
		//1st Argument numberofIntervalSetInputFiles
		int numberofIntervalSetInputFiles = Integer.parseInt(args[0]);
		
		//2nd Argument and so on input interval set file names
		String[] intervalSetsFileNames = new String[numberofIntervalSetInputFiles]; 
		for(int i=0; i<numberofIntervalSetInputFiles; i++){
			intervalSetsFileNames[i] = args[1+i];
		}
 		
		//Last argument output file name
		//String outputFileName = args[1+numberofIntervalSetInputFiles];
				
		findBestPresetValue(
				numberofIntervalSetInputFiles,
				intervalSetsFileNames);
		
	}

}

============
JOA Overview
============

---
JOA
---

JOA stands for joint overlap analysis of n interval sets.

Next-generation sequencing (NGS) technologies results in large volumes of genomic data.
Genomic interval intersection is crucial for attaining biological insights from genomic datasets.

.. Here we present joint overlap analysis (JOA) of n interval sets.
JOA accepts n interval sets in Browser Extensible Data (BED) format and finds the overlapping intervals utilizing segment trees in a divide and conquer algorithm design in parallel.

JOA also proposes a novel composite data structure "Indexed Segment Tree Forest" as an alternative data structure for segment tree. 
We show that while application is being run on, indexed segment tree forest reduces the search time.

JOA can be run using its GUI or on command line.
                                                                                                                                   
------------
JOA Features
------------

* JOA has no constaints on the input such as sorted bed files or nonoverlapping intervals in a bed file.
* JOA utilizes segment tree and indexed segment tree forest data structures for finding overlapping intervals.
* JOA provides two options for output. JOA can output only  the resulting overlapping interval for each found overlap. As a second option, JOA can provide the n overlapping intervals coming from n interval sets and plus the resulting overlapping interval for each interval.

----------
JOA Output
----------

After a successful JOA execution 

* JOA command line provides the overlappping intervals in stdout if no redirection to a file is provided. 

 Example commad line run which outputs to stdout::

	$ java -jar "path/to/joa.jar" -f "path/to/BEDFile1" "path/to/BEDFile2" . . . "path/to/BEDFileN"  

 Example commad line run which redirects output to a file::

	$ java -jar "path/to/joa.jar" -f "path/to/BEDFile1" "path/to/BEDFile2" . . . "path/to/BEDFileN"  > outputFileName

* JOA GUI provides the overlappping intervals in the outputFileName provided by the user. 
			   | ~path/to/providedFolder/outputFileName



============
JOA Tutorial
============

.. JOA includes both graphical user interface (GUI) and command-line interface.
.. You can run JOA using its graphical user interface (GUI) or command line arguments.

.. By double clicking the joa.jar you can open JOA's GUI. In this case, GUI will be opened with default amount of memory allocated for JVM which is 256MB.
.. In order to allocate specific amount of memory for joa.jar and run it through its GUI, one should write the following basic command on Terminal (Linux or Mac OS X) or on Command Prompt (Windows)\*::

.. 	$ java -Xms8G -Xmx8G −jar ~path/to/joa.jar

.. Note that with this command above, 8GM of memory is allocated for joa.jar. Depending on the number of interval sets and number of intervals in the sets you may need to increase the memory accordingly... ..

.. Throughout the guide, we will use *~path/to/joa.jar* to indicate your absolute path to **joa.jar**.


JOA provides joint overlapping intervals of *n* interval sets.

1)	**Input Files: (Mandatory)**

	You have to provide input files in bed format. 

2)	**Data Structure Type: (Optional)**

	JOA finds overlapping intervals of *n* interval sets using Segment Tree and/or Indexed Segment Tree Forest.

	* **Segment Tree (Default)**
	* *Indexed Segment Tree Forest*
	
3)	**Output Type: (Optional)**

	JOA provides output in two different ways.

	* **Only the resulting interval per each found overlap (Default)**
	* *All the overlapping intervals and the resulting interval per each found overlap* 
	

---------------------------
JOA Command-Line Parameters
---------------------------

In the following table, command-line arguments are specified. 

+-------------------------+---------------+-------------------+----------------------+--------------------+
| Description             | Parameter     | Optional/Required | Parameter Options    | Default Options    |
+=========================+===============+===================+======================+====================+
| Input Files             | `-f`_         |  Required         | None ("path/to/file")|                    |
+-------------------------+---------------+-------------------+----------------------+--------------------+
| Data Structure          | `-t`_         |  Optional         | `-t "st"`_           | `-t "st"`_         |
|                         |               |                   +----------------------+                    |
|                         |               |                   | `-t "istf"`_         |                    |
+-------------------------+---------------+-------------------+----------------------+--------------------+
| Output                  | `-o`_         |  Optional         | `-o "one"`_          | `-o "one"`_        |
|                         |               |                   +----------------------+                    |
|                         |               |                   | `-o "all"`_          |                    |
+-------------------------+---------------+-------------------+----------------------+--------------------+


------------------------------------
Command-Line Parameters Descriptions
------------------------------------
The order of parameters is not fixed. One may set the parameters in any order. 


-f
^^

**Required** Absolute paths to input files must be specified just after `-f`_ parameter.


-t
^^

**Optional** This parameter specifies the data structure type used in finding *n* overlapping intervals. If you do not set anything, `-t "st"`_ is set as default.

-t "st"
^^^^^^^

This option utilizes "segment tree" data structure in finding *n* overlapping intervals. 

-t "istf"
^^^^^^^^^

This option utilizes "indexed segment tree forest" data structure in finding *n* overlapping intervals. 

-o
^^

**Optional** This parameter specifies the output type. If you do not set anything, `-o "one"`_ is set as default.

-o "one"
^^^^^^^^

This option outputs only the resulting interval per each found overlap.

-o "all"
^^^^^^^^

This option outputs all the *n* overlapping intervals and the resulting interval per each found overlap.


----------------------------
JOA Command-Line Sample Runs
----------------------------

 Example sample run which outputs to stdout::

	$ java -jar /path/to/joa.jar -f  /path/to/input1.bed  /path/to/input2.bed

 Example sample run which outputs to a file with redirection operator::

	$ java -jar /path/to/joa.jar -f  /path/to/input1.bed  /path/to/input2.bed /path/to/input3.bed /path/to/input4.bed /path/to/input5.bed  > /path/to/joa_output_5files.txt

 Example sample run using Indexed Segment Tree Forest::

	$ java -jar /path/to/joa.jar -f  /path/to/input1.bed  /path/to/input2.bed /path/to/input3.bed /path/to/input4.bed /path/to/input5.bed  -t "istf" > /path/to/joa_output_5files.txt

 Example sample run which outputs all *n* overlapping intervals and plus the resulting interval::

	$ java -jar /path/to/joa.jar -f  /path/to/input1.bed  /path/to/input2.bed /path/to/input3.bed /path/to/input4.bed /path/to/input5.bed  -o "all" > /path/to/joa_output_5files.txt

 Example sample run for very large BED files with 16GB memory allocation::

	$ java Xms16G -Xmx16G -jar /path/to/joa.jar -f  /path/to/bigInput1.bed  /path/to/bigInput2.bed > /path/to/joa_output_big2files.txt


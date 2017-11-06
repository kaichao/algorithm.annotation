package jobreading.algorithm.experiment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jobreading.annotation.Algorithm;

public class B {
	@Algorithm(name = "listSort")
	public static List<Integer> listSort(List<Integer> lst){
		Collections.sort(lst);
		return lst;
	}

	@Algorithm(name = "arraySort")
	public static int[] listSort(int[] arr){
		Arrays.sort(arr);
		return arr;
	}
	
	@Algorithm(name = "arraySearch")
	public static int listSort(int[]elements, int key){
		Arrays.sort(elements);
		return Arrays.binarySearch(elements, key);
	}
	
}

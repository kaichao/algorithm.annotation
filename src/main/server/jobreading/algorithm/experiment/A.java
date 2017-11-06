package jobreading.algorithm.experiment;

import jobreading.annotation.Algorithm;

public class A {
	@Algorithm(name = "max")
	public static int max(int n1, int n2){
		return n1>n2 ? n1 : n2;
	}
	@Algorithm(name = "min")
	public static int min(int n1, int n2){
		return n1<n2 ? n1 : n2;
	}
}

package jobreading.algorithm.annotation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jobreading.algorithm.backend.AlgorithmServiceImpl;
import static jobreading.algorithm.util.KryoTool.*;

public class AnnotationTest {
	private AlgorithmServiceImpl algo;
	@Before
	public void setUp() throws Exception {
		algo = new AlgorithmServiceImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testListAlgorithms() throws TException{
		Set<String> algos = algo.listAlgorithms();
		assertTrue(algos.contains("max"));
		assertTrue(algos.contains("min"));
		assertTrue(algos.contains("arraySort"));

//		System.out.println(algo.listAlgorithms());
	}

	@Test
	public void testDescribeAlgorithm() throws TException{
		List<String> desc = algo.describeAlgorithm("max");
		assertTrue(desc.contains("Algorithm Name:max"));

//		System.out.println(algo.describeAlgorithm("max"));
	}

	@Test
	public void testPerformAlgorithm() throws TException{
		assertEquals(toObject(algo.performAlgorithm("max", toByteBuffer(new Integer[] { 3, 1 }))), 3);
		assertEquals(toObject(algo.performAlgorithm("min", toByteBuffer(new Integer[] { 3, 1 }))), 1);

		List<Integer> list1 = new ArrayList<>();
		for (int i : new Integer[] { 5, 3, 6, 1 }) {
			list1.add(i);
		}
		@SuppressWarnings("unchecked")
		List<Integer> list2 = (List<Integer>) toObject(algo.performAlgorithm("listSort", toByteBuffer(list1)));
		assertArrayEquals(list2.toArray(new Integer[0]), new Integer[] { 1, 3, 5, 6 });

		int[] arr1 = new int[] { 5, 3, 6, 1 };
		int[] arr2 = new int[] { 1, 3, 5, 6 };
		assertArrayEquals((int[])toObject(algo.performAlgorithm("arraySort", toByteBuffer(arr1))), arr2);

		int[] arr3 = new int[] { 5, 3, 6, 1 };
		assertEquals(toObject(algo.performAlgorithm("arraySearch",toByteBuffer(new Object[]{arr3,3}))),1);
	}

	@Test
	public void testDoPerform() throws TException{
		assertEquals(algo.doPerform("max", 1, 3), 3);

		assertEquals(algo.doPerform("min", 1, 3), 1);

		List<Integer> list1 = Arrays.asList(new Integer[] { 5, 3, 6, 1 });
		List<Integer> list2 = Arrays.asList(new Integer[] { 1, 3, 5, 6 });
		assertEquals(algo.doPerform("listSort", list1), list2);

		int[] arr1 = new int[] { 5, 3, 6, 1 };
		int[] arr2 = new int[] { 1, 3, 5, 6 };
		assertArrayEquals((int[]) algo.doPerform("arraySort", arr1), arr2);

		int[] arr3 = new int[] { 5, 3, 6, 1 };
		assertEquals(algo.doPerform("arraySearch", arr3, 3), 1);
	}
}

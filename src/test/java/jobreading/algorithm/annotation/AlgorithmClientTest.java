package jobreading.algorithm.annotation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jobreading.algorithm.AlgorithmClient;

/**
 * start SimpleBackendServer first.
 * @author kaichao
 *
 */
public class AlgorithmClientTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		new SimpleBackendServer().startServer();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		client = new AlgorithmClient();
	}
	private AlgorithmClient client;

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testListAlgorithms() {
		Set<String> algos = client.listAlgorithms();
		assertTrue(algos.contains("max"));
		assertTrue(algos.contains("min"));
		assertTrue(algos.contains("arraySort"));

//		System.out.println(client.listAlgorithms());
	}

	@Test
	public void testDescribeAlgorithm() {
		List<String> desc = client.describeAlgorithm("max");
		assertTrue(desc.contains("Algorithm Name:max"));

//		System.out.println(client.describeAlgorithm("max"));
	}

	@Test
	public void testPerformAlgorithm() {
		assertEquals(client.performAlgorithm("max", 1, 3), 3);

		assertEquals(client.performAlgorithm("min", 1, 3), 1);
		// 只能是ArrayList，Arrays.asList产生的实例Arrays.ArrayList无参数的构造函数
		// List<Integer> list1 = Arrays.asList(new Integer[] { 5, 3, 6, 1 });
		List<Integer> list1 = new ArrayList<>();
		for (int i : new Integer[] { 5, 3, 6, 1 }) {
			list1.add(i);
		}
		List<Integer> list2 = new ArrayList<>();
		for (int i : new Integer[] { 1, 3, 5, 6 }) {
			list2.add(i);
		}
		assertEquals(client.performAlgorithm("listSort", list1), list2);

		int[] arr1 = new int[] { 5, 3, 6, 1 };
		int[] arr2 = new int[] { 1, 3, 5, 6 };
		assertArrayEquals((int[])client.performAlgorithm("arraySort", arr1), arr2);

		int[] arr3 = new int[] { 5, 3, 6, 1 };
		assertEquals(client.performAlgorithm("arraySearch", arr3, 3), 1);
	}
}

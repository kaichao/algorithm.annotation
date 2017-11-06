package jobreading.algorithm.annotation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static jobreading.algorithm.util.KryoTool.*;

public class ByteBufferObjectTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Object s="Hello";
		assertEquals(toObject(toByteBuffer(s)), s);
		Object i=3;
		assertEquals(toObject(toByteBuffer(i)), i);
		Object a = new int[]{1,2,3,4,5};
		assertArrayEquals((int[])toObject(toByteBuffer(a)), (int[])a);
		List<String> list = new ArrayList<>();
		list.add("A");
		list.add("B");
		Object a2d=new Object[]{"String",'A',1,2L,3.0,true,list ,new int[]{1,2,3,4,5} };
		assertArrayEquals((Object[])toObject(toByteBuffer(a2d)), (Object[])a2d);
	}
}

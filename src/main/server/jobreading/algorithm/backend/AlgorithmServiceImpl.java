package jobreading.algorithm.backend;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import jobreading.algorithm.AlgorithmService.Iface;
import jobreading.algorithm.util.KryoTool;
import jobreading.annotation.Algorithm;

public class AlgorithmServiceImpl implements Iface {
	private Map<String, Class<?>> map = null;
	public AlgorithmServiceImpl() {
		map = new HashMap<>();
		List<URL> urlList = new ArrayList<>();
		for(String f:System.getProperty("java.class.path").split(":")){
			System.err.println(f);
			try {
				urlList.add(new File(f).toURI().toURL());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		URL[] urls = urlList.toArray(new URL[0]);
		Reflections reflections = new Reflections("jobreading.algorithm.experiment",new MethodAnnotationsScanner(),urls);
		for(Method method : reflections.getMethodsAnnotatedWith(Algorithm.class)){
			final Algorithm my = method.getAnnotation(Algorithm.class);
			Class<?> clazz = method.getDeclaringClass();
			if (null != my) {
				map.put(my.name(), clazz);
			}
		}
	}

	@Override
	public void ping() throws TException {
	}
	@Override
	public ByteBuffer performAlgorithm(String name, ByteBuffer params) throws TException {
		return KryoTool.toByteBuffer(doPerform(name,KryoTool.toObject(params)));
	}

	public Object doPerform(String algoName, Object... params) {
		try {
			Class<?> clazz = map.get(algoName);
			if (clazz == null) {
				throw new RuntimeException("No such algorithm!");
			}
			Object obj = clazz.getConstructor(new Class[] {}).newInstance(new Object[] {});
			for (final Method method : clazz.getDeclaredMethods()) {
				final Algorithm my = method.getAnnotation(Algorithm.class);
				if ((null != my) && my.name().equals(algoName)) {
					try{
						return method.invoke(obj, params);
					}catch(IllegalArgumentException x){
						// 处理对象序列化的错误参数调用
						return method.invoke(obj, (Object[])params[0]);
					}
				}
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Unexcepted Exception!");
	}

	@Override
	public Set<String> listAlgorithms() throws TException {
		return map.keySet();
	}

	@Override
	public List<String> describeAlgorithm(String name) throws TException {
		List<String> ret = new ArrayList<>();
		Class<?> clazz = map.get(name);
		if(clazz == null){
			throw new RuntimeException("No such algorithm!");
		}
		for (final Method method : clazz.getDeclaredMethods()){
			final Algorithm my= method.getAnnotation(Algorithm.class); 
			if ((null != my) && my.name().equals(name)){
				ret.add("Algorithm Name:" + name);
				ret.add("Return Type:" + method.getReturnType().toString());
				for(Parameter param : method.getParameters()){
					ret.add(param.toString());
				}
			}
		}
		return ret;
	}
}

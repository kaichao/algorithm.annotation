package jobreading.algorithm.backend;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.thrift.TException;

import jobreading.algorithm.AlgorithmService.Iface;
import jobreading.algorithm.util.KryoTool;
import jobreading.annotation.Algorithm;

public class AlgorithmServiceImpl implements Iface {
	private Map<String, Class<?>> map = null;
	public AlgorithmServiceImpl() {
		map = new HashMap<>();
		for (final Class<?> clazz : getClasses("jobreading.algorithm.experiment")) {
			final Method[] methods = clazz.getDeclaredMethods();
			for (final Method method : methods) {
				final Algorithm my = method.getAnnotation(Algorithm.class);
				if (null != my) {
					map.put(my.name(), clazz);
				}
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

	/**
	 * 从包package中获取所有的Class
	 */
	private static Set<Class<?>> getClasses(String pack) {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		// 递归获取子包中的class？
		boolean recursive = true;
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		try {
			Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {	// *.class
					// 包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 扫描并添加所有class文件
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {	// *.jar
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						if (name.charAt(0) == '/') {
							// 以/开头的，获取后面的字符串
							name = name.substring(1);
						}
						// 如果前半部分和定义的包名相同
						if (name.startsWith(packageDirName)) {
							int idx = name.lastIndexOf('/');
							if (idx != -1) {
								// 如果以"/"结尾 是一个包，获取包名，把"/"替换成"."
								packageName = name.substring(0, idx).replace('/', '.');
							}
							// 如果可迭代并且是一个包，递归处理子包
							if (((idx != -1) || recursive) && name.endsWith(".class") && !entry.isDirectory()) {
								// 去掉后面的".class" 获取真正的类名
								String className = name.substring(packageName.length() + 1, name.length() - 6);
								classes.add(Class.forName(packageName + '.' + className));
							}
						}
					}
				}
			}
		} catch (IOException|ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classes;
	}

	/*
	 * 以文件的形式来获取包下的所有Class
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
			final boolean recursive, Set<Class<?>> classes) {
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), 
						file.getAbsolutePath(), recursive, classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 添加到集合中去
					// classes.add(Class.forName(packageName + '.' + className));
					// 用forName可能会触发static方法，没有使用classLoader的load干净
					classes.add(Thread.currentThread().getContextClassLoader()
							.loadClass(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					// log.error("添加用户自定义视图类错误 找不到此类的.class文件");
					e.printStackTrace();
				}
			}
		}
	}
}

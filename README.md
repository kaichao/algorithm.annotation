# algorithm.annotation

在Server端的jobreading.algorithm.experiments包中类的静态方法上标注为 @Algorithm(name="xxx", description="xxxxxx")
在Client端可以通过AlgorithClient.performAlgorithm("xxx",......)来调用该方法。

输入通过静态方法的参数传入，输出通过返回值传出。
输入、输出对象要求为原子类型或具有空参数的构造子，以支持kryo的序列化。

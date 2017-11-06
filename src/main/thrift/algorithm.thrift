namespace java jobreading.algorithm

// thrift -r -gen java -out ../gen-java algorithm.thrift
service AlgorithmService {
	// used by thrift pool
	void ping(),

	binary performAlgorithm(1:string name, 2:binary params),
	set<string> listAlgorithms(),
	list<string> describeAlgorithm(1:string name);
}

package jobreading.algorithm;

import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import jobreading.algorithm.AlgorithmService.Client;
import jobreading.algorithm.util.KryoTool;

public class AlgorithmClient {
	public Set<String> listAlgorithms(){
		Set<String> ret = null;
		try {
			ret = newClient().listAlgorithms();
			transport.close();
		} catch (TException x) {
			x.printStackTrace();
		}
		return ret;
	}

	public List<String> describeAlgorithm(String name){
		List<String> ret = null;
		try {
			ret = newClient().describeAlgorithm(name);
			transport.close();
		} catch (TException x) {
			x.printStackTrace();
		}
		return ret;
	}

	public Object performAlgorithm(String name, Object... params) {
		Object ret = null;
		try {
			ret = KryoTool.toObject(newClient().performAlgorithm(name, KryoTool.toByteBuffer(params)));
			transport.close();
		} catch (TException x) {
			x.printStackTrace();
		}
		return ret;
	}

	private Client newClient() throws TException{
		transport = new TSocket("localhost", PORT);
		transport.open();
		TMultiplexedProtocol protocol = new TMultiplexedProtocol(
				new TBinaryProtocol(transport), "Algorithm");
		return new Client(protocol);
	}
	private int PORT=9998;
	private TTransport transport;
}

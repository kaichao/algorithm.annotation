package jobreading.algorithm.backend;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jobreading.algorithm.AlgorithmService;


public class SimpleBackendServer {
	private static final Logger logger = LoggerFactory.getLogger(SimpleBackendServer.class);

	public static final int SERVER_PORT = 9998;
	 
	public void startServer() {
		try {
			logger.info("TSimpleServer start ....");
 
			// TMultiplexedProcessor
			TMultiplexedProcessor processor = new TMultiplexedProcessor();
			processor.registerProcessor("Algorithm", 
					new AlgorithmService.Processor<>(new AlgorithmServiceImpl()));

			TServerSocket serverTransport = new TServerSocket(SERVER_PORT);
			TServer.Args args = new TServer.Args(serverTransport);
			args.processor(processor);
			args.protocolFactory(new TBinaryProtocol.Factory());
			// args.protocolFactory(new TJSONProtocol.Factory());
			TServer server = new TSimpleServer(args);
			server.serve();
 		} catch (Exception e) {
			logger.error("Server start error!!!");
			e.printStackTrace();
		}
	}
 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleBackendServer server = new SimpleBackendServer();
		server.startServer();
	}
}

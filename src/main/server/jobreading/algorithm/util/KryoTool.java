package jobreading.algorithm.util;

import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoTool {
	public static ByteBuffer toByteBuffer(Object obj){
		Kryo kryo = new Kryo();
		Output output = new Output(1024*1024);
		kryo.writeClassAndObject(output, obj);
		output.close();
		return ByteBuffer.wrap(output.toBytes());
	}

	public static Object toObject(ByteBuffer buf){
		Kryo kryo = new Kryo();
		Input input = new Input(buf.array());
		input.close();
		return kryo.readClassAndObject(input);
	}
}

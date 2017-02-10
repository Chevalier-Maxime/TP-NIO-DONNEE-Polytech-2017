package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author morat 
 */
public class ReadCont  extends Continuation{
	// state automata
	private enum State{READING_LENGTH,READING_DATA;}
	// current state
	protected State state = State.READING_LENGTH;
	// buffer for reading the length of a message 
	protected ByteBuffer buf = ByteBuffer.allocate(4);
	// the length of the message to read
	protected int length = 0;
	// the number of read operations needed
	private int nbReadSteps = 0;
	/**
	 * @param sc
	 */
	public ReadCont(SocketChannel sc){
		super(sc);
	}
	/**
	 * @return the message
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	protected Message handleRead() throws IOException, ClassNotFoundException{
		int nbRead = 0;
		nbReadSteps++; // on comptabilise une tentative complémentaire d'acquisition 
		if(state == State.READING_LENGTH){
			nbRead = socketChannel.read(buf);
			if(nbRead == -1){
				// The channel has been closed (eof)
				throw new IOException("nio channel closed");
			}
			if(buf.remaining() == 0){
				// we have read the four bytes containing the length
				length = bytesToInt(buf);
				// by allocating a buffer with the expected length, we ensure that
				// the buffer will not contain extra bytes belonging to the next
				// message to read
				buf = ByteBuffer.allocate(length);
				state = State.READING_DATA;
			}
		} 
		if(state == State.READING_DATA){
			nbRead = socketChannel.read(buf);
			if(nbRead == -1){
				// The channel has been closed (eof)
				throw new IOException("nio channel closed");
			}
			if(buf.remaining() == 0){
				// the full message has been received
				byte[] data = buf.array();
				buf = ByteBuffer.allocate(4);
				state = State.READING_LENGTH;
				/*
				 * Here we can return a message containing statistics
				 * we reinitialize nbReadSteps after save the old value
				 */				
				int nb = nbReadSteps;
				nbReadSteps = 0;
				return new Message(data, nb);
			}
		}
		return null;
	}
}


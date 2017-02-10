package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author morat 
 */
public class ReadCont  extends Continuation{

	private enum State {READING_DATA, READING_LENGTH};
	private State state = State.READING_LENGTH;
	private ByteBuffer buffTaille = ByteBuffer.allocate(4);
	private int nbByteToRead = 0;
	private ByteBuffer inBuffer;
	private int nbStep = 0;
	// to complete

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
		int nbread = 0;
		nbStep ++;
		switch(state){
			case READING_DATA :
				nbread = socketChannel.read(inBuffer);	
				if(nbread == -1){
					throw new IOException();
				}
				if(inBuffer.remaining() == 0){
					this.state = State.READING_LENGTH;
					nbByteToRead = 0;
					buffTaille.clear();
					Message m = new Message(inBuffer.array(), nbStep);
					nbStep =0;
					return m;
					
				}
				break;
			case READING_LENGTH :
				
				nbread = socketChannel.read(buffTaille);
				
				if(nbread == -1){
					throw new IOException();
				}
				if(buffTaille.remaining() == 0){
					this.state = State.READING_DATA;
					nbByteToRead = this.bytesToInt(this.buffTaille);
					inBuffer = ByteBuffer.allocate(nbByteToRead);
				}
				break;
		}
		return null;
	}
}


package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import javax.swing.JComboBox.KeySelectionManager;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;


/**
 * @author morat 
 */
public class WriteCont extends Continuation{
	
	//private enum State {WRITING_DATA, WRITING_LENGTH};
	
	private SelectionKey key; 
	// state automata
	private enum State{WRITING_DONE, WRITING_LENGTH,WRITING_DATA};
	// initial state
	protected State state = State.WRITING_DONE;
	// the list of bytes messages to write
	protected ArrayList<byte[]> msgs = new ArrayList<>() ;
	// buf contains the byte array that is currently written
	protected ByteBuffer bufTaille = null;
	protected ByteBuffer bufData = null;


	/**
	 * @param k
	 * @param sc
	 */
	public WriteCont(SelectionKey k,SocketChannel sc){
		super(sc);
		key = k;
	}


	/**
	 * @return true if the msgs are not completly write.
	 */
	protected boolean isPendingMsg(){
		return msgs.size() > 0;
	}


	/**
	 * @param data
	 * @throws IOException 
	 */
	protected void sendMsg(Message data) throws IOException{
		msgs.add(data.marshall());
		key.interestOps(SelectionKey.OP_WRITE);
		
	}


	/**
	 * @throws IOException
	 */
	protected void handleWrite()throws IOException{
		int nbwrite = 0;
		Message message;
		switch(state){
			case WRITING_DATA :
				nbwrite = socketChannel.write(bufData);	
				if(nbwrite == -1){
					throw new IOException();
				}
				if(bufData.remaining() == 0){
					this.state = State.WRITING_DONE;
					if(msgs.isEmpty()){
						key.interestOps(SelectionKey.OP_READ);
					}
				}
				break;
			case WRITING_LENGTH :
				
				nbwrite = socketChannel.write(bufTaille);
				
				if(nbwrite == -1){
					throw new IOException();
				}
				if(bufTaille.remaining() == 0){
					this.state = State.WRITING_DATA;
				}
				break;
			case WRITING_DONE:
				if(msgs.size() > 0)
				{
					bufTaille = intToBytes(msgs.get(0).length);
					bufData = ByteBuffer.wrap(msgs.remove(0));
					state = State.WRITING_LENGTH;
				}
				break;
		}
	}
}

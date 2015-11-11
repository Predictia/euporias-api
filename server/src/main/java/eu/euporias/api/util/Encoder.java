package eu.euporias.api.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Encoder {

	private Encoder(){}
	
	/**
	 * Method used for encode the file to base64 binary format
	 * 
	 * @param file
	 * @return encoded file format
	 */
	public static String encodeFileToBase64(File file) {
		try(RandomAccessFile aFile = new RandomAccessFile(file, "r")) {
			FileChannel inChannel = aFile.getChannel();
	        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
	        buffer.load(); 
	        ByteBuffer oBuffer = Base64.getEncoder().encode(buffer);
			return new String(oBuffer.array());
		}catch (IOException e) {
			LOGGER.error("Error encoding file {}: {}", file, e.getMessage());
		}
		return null;
	}
	
	/**
	 * Method used for decode base64 text to a temporal File
	 * 
	 * @param base64-encoded text
	 * @return Temporal file with decoded content
	 * @throws IOException 
	 */
	public static File decodeBase64ToFile(String base64Text) throws IOException {
		File file = File.createTempFile("tmpenc-", ".tmp");
		file.deleteOnExit();
        try(FileOutputStream fos = new FileOutputStream(file, true)) {
			ByteBuffer srcBuffer = Base64.getDecoder().decode(ByteBuffer.wrap(base64Text.getBytes()));
            FileChannel channel = fos.getChannel();
	        channel.write(srcBuffer);
	        channel.close();
	        return file;
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Encoder.class);

}

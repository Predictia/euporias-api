package eu.euporias.api.util;

import java.io.File;
import java.util.Base64;

import org.junit.Assert;
import org.junit.Test;

public class EncoderTest {
	
	@Test
	public void testEncodeDecode() throws Exception{
		String myFileContent = "testing some random\ncontent";
		String base64Text = new String(Base64.getEncoder().encode(myFileContent.getBytes()));
		File file = Encoder.decodeBase64ToFile(base64Text);
		Assert.assertEquals(base64Text, Encoder.encodeFileToBase64(file));
	}
	
}

package com.sdl.dxa.modules.generic.utilclasses;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

@Scope("session")
public class AESEncryption {
	private static Logger logger = LoggerFactory.getLogger(AESEncryption.class);
	
	private Cipher encryptionCipher;
	private Cipher decryptionCipher;
	private String encryptionKey;
	
	public Cipher getEncryptionCipher() {
		return encryptionCipher;
	}

	public void setEncryptionCipher(Cipher encryptionCipher) {
		this.encryptionCipher = encryptionCipher;
	}

	public Cipher getDecryptionCipher() {
		return decryptionCipher;
	}

	public void setDecryptionCipher(Cipher decryptionCipher) {
		this.decryptionCipher = decryptionCipher;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public AESEncryption() {
		setDefaltValues();
	}
	
	public void setDefaltValues() {
		try {
            /* TODO:Sudha Remember to set this from Tridion.MyHttpServlet
            // encryptionKey = SettingsVariables.environmentVariables.get("encryptionMasterKey");
            */
            encryptionKey = "S3rv1c3 H3r0";
			String salt = "Th3 qu!ck br0wn f0x jumps 0v3r th3 lazy d0g";
			byte[] saltBytes = salt.getBytes("UTF-8");
			byte[] ivBytes = new byte[]{
					(byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A,
					(byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A
			};
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

			int iterationCount = 3;
			int keySize = 128;
			PBEKeySpec spec = new PBEKeySpec(encryptionKey.toCharArray(), saltBytes, iterationCount, keySize);
			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

			encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			encryptionCipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(ivBytes));

			decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			decryptionCipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
			logger.info("AESEncryption initiated: ");
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public String encrypt(String stringToEncrypt) {
		String encrypted, tempdecrypt;
        int k = 0;
		if(stringToEncrypt != null) {
			try {
				byte[] encryptedTextBytes = encryptionCipher.doFinal(stringToEncrypt.getBytes("UTF-8"));
				encrypted = Base64.encodeBase64String(encryptedTextBytes);
                k = 0;
                do {
                    tempdecrypt = decrypt(encrypted);
                    if(!tempdecrypt.equals(stringToEncrypt)) {
                        encrypted = Base64.encodeBase64String(encryptedTextBytes);
                    } else {
                        encrypted = Base64.encodeBase64String(encryptedTextBytes);
                        break;
                    }
                    k++;
                } while(k < 5);
                if(k == 5)
                    encrypted = stringToEncrypt;
				if (encrypted.indexOf("=") > 0)
					encrypted = (encrypted.length() - encrypted.indexOf("=")) + "~" + encrypted.substring(0, encrypted.indexOf("="));
				else
					encrypted = "0" + "~" + encrypted;
			} catch(Exception e) {
				encrypted = stringToEncrypt;
				logger.error(e.getMessage(), e);
			}
		} else
			encrypted = stringToEncrypt;
        if(encrypted.indexOf("+") > -1)
            encrypted = encrypted.replace('+', '$');
		return encrypted;
	}

	public byte[] encrypt(byte[] plain) throws Exception {
		return encryptionCipher.doFinal(encryptionCipher.doFinal(plain));
	}

	public String decrypt(String encryptedText) {
		try {
			if (encryptedText == null) {
				return null;
			} else {
				int iCount = 0;
				if (encryptedText.indexOf("~") > 0) {
					iCount = Integer.parseInt(encryptedText.substring(0, encryptedText.indexOf("~")));
					for (int i = 0; i < iCount; i++)
						encryptedText += "=";
					encryptedText = encryptedText.substring(encryptedText.indexOf("~") + 1);
				}
                if(encryptedText.indexOf("$") > -1)
                    encryptedText = encryptedText.replace('$', '+');

                byte[] encryptedTextBytes = Base64.decodeBase64(encryptedText);
				byte[] decryptedTextBytes = null;
				try {
					decryptedTextBytes = decryptionCipher.doFinal(encryptedTextBytes);
				} catch (IllegalBlockSizeException e) {

						if(logger != null) {
							logger.error(e.getMessage(), e);
						} else {
							System.out.println(e.getMessage());
							e.printStackTrace();
						}

				} catch (BadPaddingException e) {

						if(logger != null) {
							logger.error(e.getMessage(), e);
						} else {
							System.out.println(e.getMessage());
							e.printStackTrace();
						}

				}
				return new String(decryptedTextBytes, "UTF-8");
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			return encryptedText;
		}
	}

	public byte[] decrypt(byte[] encrypt) throws Exception {
		return decryptionCipher.doFinal(encrypt);
	}

	/*public static void main(String[] args) throws Exception {
		//  prompt the user to enter their name
		System.out.print("Enter your password: ");

		//  open up standard input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String stringToEncrypt = null;
		String password = null;

		//  read the username from the command-line; need to use try/catch with the
		//  readLine() method
		try {
			password = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to read your password!");
			System.exit(1);
		}

		System.out.print("Enter your stringToEncrypt: ");

		//  read the password from the command-line; need to use try/catch with the
		//  readLine() method
		try {
			stringToEncrypt = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to read your stringToEncrypt!");
			System.exit(1);
		}

		br.close();


		AESEncrypterDecrypter aesEncrypterDecrypter;
		aesEncrypterDecrypter = new AESEncrypterDecrypter(password);
//		aesEncrypterDecrypter.setPlainText(stringToEncrypt);
		System.out.println("stringToEncrypt: " + stringToEncrypt);
		System.out.println("password: " + password);

		String encrytpted = aesEncrypterDecrypter.encrypt(stringToEncrypt, true);
		System.out.println("Encrypted: " + encrytpted);
		String decrytpted = aesEncrypterDecrypter.decrypt(encrytpted);
		System.out.println("Decrytpted: " + decrytpted);
	}*/	
	
	
}

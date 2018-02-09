package com.melay.sync.data;

import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

/**
 * Created by matth on 1/31/2018.
 * All data classes implement this
 * Implements an interface to encrypt a particular piece of data
 * <p>
 * TODO figure out how to store private and public keys securely
 */

public abstract class Encryptable {
    private CryptLib _cryptor;
    protected boolean encrypted;
    protected String iv; //this must be included in the syncing process otherwise it cannot be decrypted

    public void CheckSetup() {
        try {
            if (_cryptor == null)
                _cryptor = new CryptLib();
            if (iv == null) {
                CryptLib.generateRandomIV(16);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * This encrypts the whole object
     * @param key
     */
    public abstract void Encrypt(String key);

    /**
     * @param key
     * @return
     */
    public abstract boolean Decrypt(String key);

    /**
     * @return
     */
    public boolean IsEncrypted() {
        return encrypted;
    }

    protected String EncryptString(String plainText, String key){
        try {
            CheckSetup();
            String output= "";

            output = _cryptor.encrypt(plainText, key, iv); //encrypt

            System.out.println("encrypted text=" + output);
            return output;

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }
        return null;
    }

    protected String DecryptString(String input, String key){
        try {
            CheckSetup();
            String output= "";
            output = _cryptor.decrypt(input, key,iv); //decrypt

            System.out.println("decrypted text=" + output);
            return output;

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }
        return null;
    }
}

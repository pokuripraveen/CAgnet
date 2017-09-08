package com.unique.agent.network;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by praveenpokuri on 18/08/17.
 */

public abstract class NetworkUploadInterface {

    /**
     * Return the total size of the uploading request.
     * @return the total size of the request being uploaded.
     */
    public abstract long getTotalSize();

    /**
     * Get back a byte array of specified size of the data being uploaded.
     * @param maxSize size of byte array to get back.
     * @return the byte array of specified size.
     */
    public abstract byte [] getDataChunk(int maxSize);

    /**
     * Given a huge chunk of byte array, split it into smaller byte array chunks
     * stored in an ArrayList.
     *
     * @param fullBuffer the byte[] buffer to be split.
     * @param maxArraySize maximum number of bytes in each split array.
     * @return an ArrayList containing as many byte arrays as it takes to split the full byte buffer
     *          with the specified size.
     */
    protected ArrayList<byte[]> splitByteArray(byte[] fullBuffer, int maxArraySize){
        ArrayList<byte[]> byteArrays = new ArrayList<>();
        int length = maxArraySize;
        byte [] buff;
        try {
            for (int i=0; i<fullBuffer.length/maxArraySize+1; i++){
                if (i==fullBuffer.length/maxArraySize){
                    length = fullBuffer.length - (i*maxArraySize);
                }
                buff = new byte[length];
                System.arraycopy(fullBuffer, i * maxArraySize, buff, 0, length);
                byteArrays.add(buff);
            }
        } catch (Exception e){
            Log.e("PRAV", "Split byte array operation failed: "+ Log.getStackTraceString(e));
        }
        return byteArrays;
    }
}

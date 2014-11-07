/*
 * This software is OSI Certified Open Source Software
 *
 * The MIT License (MIT)
 * Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */

package encode.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class FrameDecompressor {
    private static final int ALPHA = 0xFF000000;

    public class FramePacket { // bring this one out and join it with other class

        private InputStream iStream;
        private int[] previousData;
        private int result; // -1 denotes the end of stream, i should check the result
        private long frameTimeStamp;
        private byte[] packed;
        private int frameSize;
        private int[] newData;

        private FramePacket(InputStream iStream, int expectedSize) {
            frameSize = expectedSize;
            this.iStream = iStream;
            previousData = new int[frameSize];
        }

        private void nextFrame() {
            if (newData != null) {
                previousData = newData;
            }
        }

        public int[] getData() {
            return newData;
        } // the actual frame data

        public int getResult() {
            return result;
        }

        public long getTimeStamp() {
            return frameTimeStamp;
        } // and the timestamp neatly serialized just as i need
    }

    public FramePacket frame;

    public FrameDecompressor(InputStream iStream, int frameSize) {
        frame = new FramePacket(iStream, frameSize);
    }

    public FramePacket unpack() throws IOException {
        frame.nextFrame();

        // try{
        int i = frame.iStream.read();
        int time = i;
        time = time << 8;
        i = frame.iStream.read();
        time += i;
        time = time << 8;
        i = frame.iStream.read();
        time += i;
        time = time << 8;
        i = frame.iStream.read();
        time += i;

        frame.frameTimeStamp = (long) time;
        // System.out.println("ft:"+frameTime);

        byte type = (byte) frame.iStream.read();
        // System.out.println("Packed Code:"+type);

        if (type <= 0) {
            frame.result = type;
            return frame;
        }

        ByteArrayOutputStream bO = new ByteArrayOutputStream(); // this is potentially not good, may cause slow down
        try {
            i = frame.iStream.read();
            int zSize = i;
            zSize = zSize << 8;
            i = frame.iStream.read();
            zSize += i;
            zSize = zSize << 8;
            i = frame.iStream.read();
            zSize += i;
            zSize = zSize << 8;
            i = frame.iStream.read();
            zSize += i;

            // System.out.println("Zipped Frame size:"+zSize);

            byte[] zData = new byte[zSize];
            int readCursor = 0;
            int sizeRead = 0;

            while (sizeRead > -1) {
                readCursor += sizeRead;
                if (readCursor >= zSize) {
                    break;
                }

                sizeRead = frame.iStream
                        .read(zData, readCursor, zSize - readCursor);
            }

            ByteArrayInputStream bI = new ByteArrayInputStream(zData); // again constant costly initialization, may be a slowdown
            GZIPInputStream zI = new GZIPInputStream(bI); // lz4 may prove faster

            byte[] buffer = new byte[1000];
            sizeRead = zI.read(buffer);

            while (sizeRead > -1) {
                bO.write(buffer, 0, sizeRead);
                bO.flush();

                sizeRead = zI.read(buffer);
            }
            bO.flush();
            bO.close();
        } catch (Exception e) {
            e.printStackTrace();
            frame.result = 0;
            return frame;
        }

        frame.packed = bO.toByteArray();

        runLengthDecode();

        return frame;
    }

    private void runLengthDecode() {
        frame.newData = new int[frame.frameSize];

        int inCursor = 0;
        int outCursor = 0;

        int blockSize;

        int rgb;

        while (inCursor < frame.packed.length && outCursor < frame.frameSize) {
            if (frame.packed[inCursor] == -1) {
                inCursor++;

                int count = frame.packed[inCursor] & 0xFF;
                inCursor++;

                for (int loop = 0; loop < 126 * count; loop++) {
                    frame.newData[outCursor] = frame.previousData[outCursor];
                    outCursor++;
                    if (outCursor == frame.newData.length) {
                        break;
                    }
                }

            } else {
                if (frame.packed[inCursor] < 0) // uncomp
                {
                    blockSize = frame.packed[inCursor] & 0x7F;
                    inCursor++;

                    for (int loop = 0; loop < blockSize; loop++) {
                        rgb = (frame.packed[inCursor] & 0xFF) << 16
                                | (frame.packed[inCursor + 1] & 0xFF) << 8
                                | frame.packed[inCursor + 2] & 0xFF | ALPHA;
                        if (rgb == ALPHA) {
                            rgb = frame.previousData[outCursor];
                        }
                        inCursor += 3;
                        frame.newData[outCursor] = rgb;
                        outCursor++;
                        if (outCursor == frame.newData.length) {
                            break;
                        }
                    }
                } else {
                    blockSize = frame.packed[inCursor];
                    inCursor++;
                    rgb = (frame.packed[inCursor] & 0xFF) << 16
                            | (frame.packed[inCursor + 1] & 0xFF) << 8
                            | frame.packed[inCursor + 2] & 0xFF | ALPHA;

                    boolean transparent = false;
                    if (rgb == ALPHA) {
                        transparent = true;
                    }
                    inCursor += 3;
                    for (int loop = 0; loop < blockSize; loop++) {
                        if (transparent) {
                            frame.newData[outCursor] = frame.previousData[outCursor];
                        } else {
                            frame.newData[outCursor] = rgb;
                        }
                        outCursor++;
                        if (outCursor == frame.newData.length) {
                            break;
                        }
                    }
                }
            }
        }
        frame.result = outCursor;
    }
}
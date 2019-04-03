package algorithm;

import staticMethods.Values;

import java.util.BitSet;

public class Key {
    private static byte[] secondKey = "abcdefgh".getBytes();

    public static byte[][] generateSubkeys() {
        byte[] keyPC1 = permutation(secondKey, Values.keyPermutation1);

        byte[] c = splitBytes(keyPC1, 0, 28);
        byte[] d = splitBytes(keyPC1, 28, 28);

        byte[][] subkeys = new byte[Values.rotations.length][];
        for (int i = 0; i < Values.rotations.length; i++) {
            c = leftShift(c, Values.rotations[i]);
            d = leftShift(d, Values.rotations[i]);
            byte[] cd = concatBytes(c, 28, d, 28);
            subkeys[i] = permutation(cd, Values.keypermutation2);
        }
        return subkeys;
    }

    static byte[] permutation(byte[] a, byte[] b) {
        BitSet blockBitSet = BitSet.valueOf(a);
        BitSet outputBitset = new BitSet();
        for (int j = 0; j < b.length; j++) {
            boolean temp = blockBitSet.get(b[j]);
            outputBitset.set(j, temp);
        }
        outputBitset.set(48, true);
        byte[] output = outputBitset.toByteArray();
        return output;
    }

    private static void setBit(byte[] data, int index, boolean bit) {
        int whichByte = index / 8;
        int whichBit = index % 8;
        if (bit)
            data[whichByte] |= 0x80 >> whichBit;
        else
            data[whichByte] &= ~(0x80 >> whichBit);

    }

    private static boolean checkBit(byte[] data, int index) {
        int whichByte = index / 8;
        int whichBit = index % 8;

        return (data[whichByte] >> (8 - (whichBit + 1)) & 1) == 1;
    }

    static byte[] splitBytes(byte[] input, int index, int length) {
        byte[] output = prepareOutput(length);
        for (int i = 0; i < length; i++) {
            boolean bit = checkBit(input, index + i);
            setBit(output, i, bit);
        }
        return output;
    }

    private static byte[] prepareOutput(int length) {
        int bytesNumb = ((length - 1) / 8) + 1;
        return new byte[bytesNumb];
    }

    private static byte[] leftShift(byte[] input, int shiftNumb) {
        byte[] out = new byte[4];
        int halfKeySize = 28;
        for (int i = 0; i < halfKeySize; i++) {
            boolean bit = checkBit(input, (i + shiftNumb) % halfKeySize);
            setBit(out, i, bit);
        }
        return out;
    }

    private static byte[] prepareOutput(int aLen, int bLen) {
        int numOfBytes = ((aLen + bLen - 1) / 8) + 1;
        return new byte[numOfBytes];
    }

    static byte[] concatBytes(byte[] a, int aLength, byte[] b, int bLength) {
        byte[] output = prepareOutput(aLength, bLength);

        int i = 0;
        for (; i < aLength; i++) {
            boolean bit = checkBit(a, i);
            setBit(output, i, bit);
        }
        for (int j = 0; j < bLength; j++, i++) {
            boolean bit = checkBit(b, j);
            setBit(output, i, bit);
        }
        return output;
    }


}

package algorithm;

import staticMethods.Methods;
import staticMethods.Values;

import java.util.BitSet;

public class DESX {
    private byte[] plainText;
    private byte[][] plainText2D;
    private byte[][] leftSide;
    private byte[][] rightSide;
    private byte[][] subkeys;
    private int size;


    public void start(byte[] plainText, int size, boolean encryption) {
        this.size = size;
        this.plainText = plainText;
        this.plainText2D = Methods.saveAs2DBytesArray(plainText, size);
        System.out.println("PRZED ALGORYTMEM");
        Methods.showArrayAsText(this.plainText2D, 8, this.size);
        Methods.showArray(this.plainText2D, 8, this.size);
        System.out.println("*************************************");
//Preparing keys
        subkeys = Key.generateSubkeys();
        if (encryption) {
            this.plainText2D = xorBytesArray(this.plainText2D, Values.FIRSTKEY);
        } else {
            this.plainText2D = xorBytesArray(this.plainText2D, Values.THRIDKEY);
        }


        this.plainText2D = this.permutation(this.plainText2D, Values.startPermutation);

        this.blocksDividing(plainText2D);



        this.FeislFunction(encryption);


        this.plainText2D = this.permutation(this.plainText2D, Values.endPermutation);
        if (encryption) {
            this.plainText2D = xorBytesArray(this.plainText2D, Values.THRIDKEY);
        } else {
            this.plainText2D = xorBytesArray(this.plainText2D, Values.FIRSTKEY);
        }

        System.out.println("AFTER ALGORITHM");
        Methods.showArray(this.plainText2D, 8, this.size);
        System.out.println("************************");
        Methods.showArrayAsText(this.plainText2D, 8, this.size);

    }

    private byte[][] xorBytesArray(byte input[][], byte key[]) {
        BitSet bitSetKey1 = BitSet.valueOf(key);

        byte[][] temp = new byte[this.size][8];

        for (int i = 0; i < this.size; i++) {
            BitSet bitSetPlainText = BitSet.valueOf(input[i]);
            bitSetPlainText.xor(bitSetKey1);
            bitSetPlainText.set(64, true); // 65 bit ustawiony na 1 aby w przypadku gdy ostatnie 8 bitów bitsetu bylo zerem do tablicy zapisywalo sie 8 podtablic
            temp[i] = bitSetPlainText.toByteArray();
        }
        return temp;
    }

    private byte[][] permutation(byte input[][], byte[] permutationArray) {
        byte[][] result = new byte[size][8];
        boolean temp;
        for (int i = 0; i < size; i++) {

            BitSet bitSet = BitSet.valueOf(input[i]);
            BitSet resultBitSet = new BitSet();

            for (int j = 0; j < permutationArray.length; j++) {
                temp = bitSet.get(permutationArray[j] - 1);
                resultBitSet.set(j, temp);
            }
            resultBitSet.set(64, true);
            result[i] = resultBitSet.toByteArray();
        }
        return result;
    }

    byte[] xorValues(byte[] a, byte[] b) {
        BitSet ba = BitSet.valueOf(a);
        BitSet bb = BitSet.valueOf(b);
        ba.xor(bb);
        ba.set(48, true);
        byte[] out = ba.toByteArray();
        return out;
    }

    byte[] permutation(byte[] a, byte[] b) {
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

    private void blocksDividing(byte input[][]) {
        int k;
        this.leftSide = new byte[size][4];
        this.rightSide = new byte[size][4];
        for (int i = 0; i < size; i++) {
            k = 0;
            for (int j = 0; j < 4; j++) {
                this.leftSide[i][j] = input[i][k];
                k++;
            }
            for (int j = 0; j < 4; j++) {
                this.rightSide[i][j] = input[i][k];
                k++;
            }
        }
    }

    private void FeislFunction(boolean encrypt) {
        for (int j = 0; j < plainText2D.length; j++) {
            int iteration = subkeys.length;
            for (int i = 0; i < iteration; i++) {
                byte[] oldRightSide = rightSide[j];
                rightSide[j] = permutation(rightSide[j], Values.expantionPermutation);
                if (encrypt)
                    rightSide[j] = xorValues(rightSide[j], subkeys[i]);
                else
                    rightSide[j] = xorValues(rightSide[j], subkeys[iteration - i - 1]);

                rightSide[j] = doSBox(rightSide[j]);
                rightSide[j] = permutation(rightSide[j], Values.permutationBoxes);
                rightSide[j] = xorValues(leftSide[j], rightSide[j]);
                leftSide[j] = oldRightSide;
            }
            plainText2D[j] = concatBytes(rightSide[j], leftSide[j]);
        }
    }

    private byte[] doSBox(byte[] input) {
        input = splitBytes(input);
        byte[] output = new byte[input.length / 2];

        for (int i = 0, firstSBoxValue = 0; i < input.length; i++) {
            byte sixBitsFragment = input[i];
            int rowNumb = 2 * (sixBitsFragment >> 7 & 0x0001) + (sixBitsFragment >> 2 & 0x0001);
            int columnNumb = sixBitsFragment >> 3 & 0x000F;
            int secondSBoxValue = (int) Values.substitutionBoxes[64 * i + 16 * rowNumb + columnNumb];
            if (i % 2 == 0)
                firstSBoxValue = secondSBoxValue;
            else
                output[i / 2] = createByteFromSBoxValues(firstSBoxValue, secondSBoxValue);
        }
        return output;
    }

    private byte[] concatBytes(byte[] leftside, byte[] rightside) {
        byte[] temp = new byte[8];

        int l = 0;
        int r = 0;
        for (int j = 0; j < 4; j++) {
            temp[j] = leftside[l];
            l++;
            temp[j + 4] = rightside[r];
            r++;
        }

        return temp;
    }
    private byte createByteFromSBoxValues(int firstSBoxValue, int secondSBoxValue) {
        return (byte) (16 * firstSBoxValue + secondSBoxValue);
    }



    public byte[] getBytes() {
        return Methods.saveAs1DBytesArray(this.plainText2D, this.size);
    }

    private void setBit(byte[] data, int index, boolean bit) {
        int whichByte = index / 8;
        int whichBit = index % 8;
        if (bit)
            data[whichByte] |= 0x80 >> whichBit;
        else
            data[whichByte] &= ~(0x80 >> whichBit);

    }

    private boolean checkBit(byte[] data, int index) {
        int whichByte = index / 8;
        int whichBit = index % 8;

        return (data[whichByte] >> (8 - (whichBit + 1)) & 1) == 1;
    }
    private byte[]  splitBytes(byte[] in) {
        int numOfBytes = 8;
        byte[] out = new byte[numOfBytes];
        for (int i = 0; i < numOfBytes; i++) {
            for (int j = 0; j < 6; j++) {
                boolean val = checkBit(in, (6 * i) + j);
                setBit(out, (8 * i) + j, val);
            }
        }
        return out;
    }






}

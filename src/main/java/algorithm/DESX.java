package algorithm;

import staticMethods.Methods;
import staticMethods.Values;

import java.util.BitSet;

public class DESX {
    private byte[] plainText;
    private byte[][] plainText2D;
    private byte[][] leftSide;
    private byte[][] rightSide;
    private byte[] secondKey;
    private long subkeys[];
    private int size;

    public void start(byte[] plainText, int size, boolean encryption) {
        this.size = size;
        this.plainText = plainText;
        this.plainText2D = Methods.saveAs2DBytesArray(plainText, size);
        System.out.println("PRZED ALGORYTMEM");
        Methods.showArrayAsText(this.plainText2D,8,this.size);
        Methods.showArray(this.plainText2D,8,this.size);
        System.out.println("*************************************");
//Preparing keys
        this.secondKey = DESX.firstKeyPermutation(Values.SECONDKEY);
        this.subkeys = DESX.createSubkeys(this.secondKey);
/////////////////////////////////////////////////////////////////// SZYFROWANIE/ODSZYFROWANIE
        if (encryption) {
            this.plainText2D = xorBytesArray(this.plainText2D, Values.FIRSTKEY);
        } else {
            this.plainText2D = xorBytesArray(this.plainText2D, Values.THRIDKEY);
        }
///////////////////////////////////////////////////////////////////

        this.plainText2D = this.permutation(this.plainText2D, Values.startPermutation);

        this.blocksDividing(plainText2D);

//Feisl Functions


            this.functionF(encryption);


        this.plainText2D = this.permutation(this.plainText2D, Values.endPermutation);
/////////////////////////////////////////////////////////////////////////////// SZYFR/ODSZFR
        if (encryption) {
            this.plainText2D = xorBytesArray(this.plainText2D, Values.THRIDKEY);
        } else {
            this.plainText2D = xorBytesArray(this.plainText2D, Values.FIRSTKEY);
        }
//////////////////////////////////////////////////////////////////////////////

        System.out.println("AFTER ALGORITHM");
        Methods.showArray(this.plainText2D, 8, this.size);
        System.out.println("8888888888888888888888888888888888888888888");
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

    private void functionF(boolean encrypt) {
        for (int i = 0; i < this.size; i++) {

            for (int j = 0; j < this.subkeys.length; j++) {

                byte[] oldRightSide = this.rightSide[i];

/**
 * Expansion
 */
                BitSet blockBitSet = BitSet.valueOf(this.rightSide[i]);
                BitSet outputBitset = new BitSet();
                outputBitset.clear();

                expantionPermutation(blockBitSet, outputBitset);

                blockBitSet.clear();
/**
 * Key mixing
 * xoring created 48bit key with right side after expansion to 48 bit
 */
                long[] key = new long[1];
                if (encrypt) {
                    key[0] = subkeys[j];
                } else {
                    key[0] = subkeys[this.subkeys.length - j -1];
                }
                BitSet keyBitSet = BitSet.valueOf(key);
                outputBitset.xor(keyBitSet);
/**
 * Substitution
 */
                byte[] wartosciZSBOX = new byte[8];


                sBlocks(outputBitset, wartosciZSBOX);

                BitSet sBoxValuesBitSet = BitSet.valueOf(wartosciZSBOX);
                keyBitSet.clear();
                //pętla do przekonwertowania 64 bit na 32 bit (usuwanie czterech zer w każdym bajcie)
                  convertingOutputSBOX(keyBitSet, sBoxValuesBitSet);
                byte[] sBoxValues = keyBitSet.toByteArray();


                /**      Permutation
                 *
                 */

                outputBitset.clear();

                permutationBoxes(outputBitset, keyBitSet);

                //sumowanie modulo powstałego prawego bloku danych z lewą połową

                BitSet bitSet = BitSet.valueOf(this.leftSide[i]);
                bitSet.xor(outputBitset);
                bitSet.set(33, true);
                this.rightSide[i] = bitSet.toByteArray();
////////////////////////////////////////WYJSCIE 32BIT SPERMUTOWANE Z P BOXAMI

                this.leftSide[i] = oldRightSide;

            }
            this.plainText2D[i] = concatBytes(this.leftSide[i], this.rightSide[i]);
        }
    }

    private void sBlocks(BitSet outputBitset, byte[] wartosciZSBOX) {
        boolean value;
        byte[] values;
        byte[] values2;
        BitSet dwuBitowaLiczba = new BitSet(2);
        BitSet czteroBitowaLiczba = new BitSet(4);
        for (int j = 0; j < 8; j++) {
            for (int p = 0; p < 6; p++) {
                value = outputBitset.get((j * 6) + p);
                if (p == 0) {
                    dwuBitowaLiczba.set(0, value);
                } else if (p == 5) {
                    dwuBitowaLiczba.set(1, value);
                } else {
                    czteroBitowaLiczba.set(p - 1, value);
                }
            }
            dwuBitowaLiczba.set(9, true);
            czteroBitowaLiczba.set(9, true);
            values = dwuBitowaLiczba.toByteArray();

            values2 = czteroBitowaLiczba.toByteArray();

            wartosciZSBOX[j] = Values.substitutionBoxes[j][values[0]*16+values2[0]];
        }
    }

    private void permutationBoxes(BitSet outputBitset, BitSet keyBitSet) {
        boolean permutedValue;
        for (int permutaionIndex = 0; permutaionIndex < 32; permutaionIndex++) {
            permutedValue = keyBitSet.get(Values.permutationBoxes[permutaionIndex]);
            outputBitset.set(permutaionIndex, permutedValue);
        }
    }

    private void convertingOutputSBOX(BitSet keyBitSet, BitSet sBoxValuesBitSet) {
        int sBoxIndex = 0;
        for (int z = 0; z < 8; z++) {
            boolean sBoxValueAtIndex;
            for (int o = 0; o < 4; o++) {
                sBoxValueAtIndex = sBoxValuesBitSet.get(o + z * 8);
                keyBitSet.set(sBoxIndex, sBoxValueAtIndex);
                sBoxIndex++;
            }
        }
    }

    private void expantionPermutation(BitSet blockBitSet, BitSet outputBitset) {
        for (int j = 0; j < Values.expantionPermutation.length; j++) {
            boolean temp = blockBitSet.get(Values.expantionPermutation[j]);
            outputBitset.set(j, temp);
        }
    }

    private static byte[] firstKeyPermutation(byte key[]) {
        BitSet bitSet = new BitSet();
        bitSet = BitSet.valueOf(key);

        BitSet bitSet1 = new BitSet(56);
        bitSet1.clear();

        boolean temp;
        for (int i = 0; i < 56; i++) {
            temp = bitSet.get(Values.keyPermutation1[i] - 1);
            bitSet1.set(i, temp);
        }

        return bitSet1.toByteArray();
    }

    private static long compresionPermutation(long longKey) {
        long[] longKeysArray = new long[1];
        longKeysArray[0] = longKey;
        BitSet longKeysBitset = BitSet.valueOf(longKeysArray);
        BitSet permutedBitset = new BitSet();
        boolean temp;

        for (int i = 0; i < Values.keypermutation2.length; i++) {
            temp = longKeysBitset.get(Values.keypermutation2[i] - 1);
            permutedBitset.set(i, temp);

        }
        permutedBitset.set(65, true);
        longKeysArray = permutedBitset.toLongArray();
        return longKeysArray[0];
    }

    private static long[] createSubkeys(byte[] key1) {
        //Conversion key1 to long
        long[] key = new long[1];
        key[0] = 0L;
        BitSet bitSet = new BitSet();
        bitSet = BitSet.valueOf(key1);
        key = bitSet.toLongArray();

        long subkeys[] = new long[16];
        // split into 28-bit left and right (c and d) pairs.
        int c = (int) (key[0] >> 28);
        int d = (int) (key[0] & 0x0FFFFFFF);
        for (int i = 0; i < 16; i++) {
            // rotate the 28-bit values
            if (Values.rotations[i] == 1) {
                // rotate by 1 bit
                c = ((c << 1) & 0x0FFFFFFF) | (c >> 27);
                d = ((d << 1) & 0x0FFFFFFF) | (d >> 27);
            } else {
                // rotate by 2 bits
                c = ((c << 2) & 0x0FFFFFFF) | (c >> 26);
                d = ((d << 2) & 0x0FFFFFFF) | (d >> 26);
            }
            // join the two keystuff halves together.
            long cd = (c & 0xFFFFFFFFL) << 28 | (d & 0xFFFFFFFFL);

            subkeys[i] = compresionPermutation(cd);
        }
        return subkeys; /* 48-bit values */
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

    public byte[] getBytes() {
        return Methods.saveAs1DBytesArray(this.plainText2D, this.size);
    }

}

package staticMethods;

public class Methods {


    public static byte[][] saveAs2DBytesArray(byte[] fileContent, int size) {
        byte[][] temp = new byte[size][8];
        int temp1 = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 8; j++) {
                if (temp1 == fileContent.length) {
                    temp[i][j] = 0;
                } else {
                    temp[i][j] = fileContent[temp1];
                    temp1++;
                }
            }
        }
        return temp;
    }

    public static void showArray(byte tab[][], int param, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < param; j++) {
                System.out.print(" " + tab[i][j]);
            }
            System.out.println();
        }
    }

    public static void showArrayAsText(byte tab[][], int param, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < param; j++) {
                System.out.print(" " + (char) tab[i][j]);
            }
            System.out.println();
        }
    }

    public static byte[] saveAs1DBytesArray(byte[][] outputContent, int size) {
        byte[] temp = new byte[size * 8];
        int licznik = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 8; j++) {
                temp[licznik] = outputContent[i][j];
                licznik++;
            }
        }
return temp;
    }
}
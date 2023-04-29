package me.murin.milos.utils;

import me.murin.milos.geometry.Line;

public class MyMatrix {

    private final double[][] matrix = new double[3][2];
    private final double[] result = new double[3];

    private boolean hasSolution = true;
    private boolean solved = false;

    private int tRow = -1;
    private int sRow = -1;

    public MyMatrix(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21,
            double m22) {
        matrix[0][0] = m00;
        matrix[0][1] = m01;
        matrix[1][0] = m10;
        matrix[1][1] = m11;
        matrix[2][0] = m20;
        matrix[2][1] = m21;
        result[0] = m02;
        result[1] = m12;
        result[2] = m22;
    }


    public MyMatrix(Line line1, Line line2) {
        matrix[0][0] = line1.getUx();
        matrix[0][1] = -line2.getUx();
        matrix[1][0] = line1.getUy();
        matrix[1][1] = -line2.getUy();
        matrix[2][0] = line1.getUz();
        matrix[2][1] = -line2.getUz();
        result[0] = line2.getX0() - line1.getX0();
        result[1] = line2.getY0() - line1.getY0();
        result[2] = line2.getZ0() - line1.getZ0();
    }


    public void solve() {
        solved = true;
        int col = 0;
        if (!Utils.isAlmostEqual(matrix[0][col], 0)) {
            addMulToRow(0, 1, col);
            addMulToRow(0, 2, col);
            tRow = 0;
        } else if (!Utils.isAlmostEqual(matrix[1][col], 0)) {
            addMulToRow(1, 0, col);
            addMulToRow(1, 2, col);
            tRow = 1;
        } else if (!Utils.isAlmostEqual(matrix[2][col], 0)) {
            addMulToRow(2, 0, col);
            addMulToRow(2, 1, col);
            tRow = 2;
        } else {
            hasSolution = false;
            return;
        }
        col = 1;
        if (!Utils.isAlmostEqual(matrix[0][col], 0) && tRow != 0) {
            addMulToRow(0, 1, col);
            addMulToRow(0, 2, col);
            sRow = 0;
        } else if (!Utils.isAlmostEqual(matrix[1][col], 0) && tRow != 1) {
            addMulToRow(1, 0, col);
            addMulToRow(1, 2, col);
            sRow = 1;
        } else if (!Utils.isAlmostEqual(matrix[2][col], 0) && tRow != 2) {
            addMulToRow(2, 0, col);
            addMulToRow(2, 1, col);
            sRow = 2;
        } else {
            hasSolution = false;
            return;
        }
        if (tRow == sRow) {
            hasSolution = false;
            return;
        }
        if (tRow == 0) {
            if (sRow == 1) {
                if (isNotZeroEqualsZero(2)) {
                    hasSolution = false;
                }
            } else {
                if (isNotZeroEqualsZero(1)) {
                    hasSolution = false;
                }
            }
        } else if (tRow == 1) {
            if (sRow == 0) {
                if (isNotZeroEqualsZero(2)) {
                    hasSolution = false;
                }
            } else {
                if (isNotZeroEqualsZero(0)) {
                    hasSolution = false;
                }
            }
        } else if (tRow == 2) {
            if (sRow == 0) {
                if (isNotZeroEqualsZero(1)) {
                    hasSolution = false;
                }
            } else {
                if (isNotZeroEqualsZero(0)) {
                    hasSolution = false;
                }
            }
        } else {
            hasSolution = false;
        }
    }

    public boolean isNotZeroEqualsZero(int row) {
        return !Utils.isAlmostEqual(matrix[row][0], 0) || !Utils.isAlmostEqual(matrix[row][1], 0) ||
                !Utils.isAlmostEqual(result[row], 0);
//        return matrix[row][0] != 0 || matrix[row][1] != 0 || result[row] != 0;
    }

    public void addMulToRow(int fromRow, int toRow, int col) {
        double p = -(matrix[toRow][col] / matrix[fromRow][col]);
        for (int i = 0; i < 2; i++) {
            matrix[toRow][i] = matrix[toRow][i] + p * matrix[fromRow][i];
        }
        result[toRow] = result[toRow] + p * result[fromRow];
    }

    public boolean hasSolution() {
        return hasSolution;
    }

    public double getT() {
        return result[tRow] / matrix[tRow][0];
    }

    public double getS() {
        return result[sRow] / matrix[sRow][1];
    }

    public void print() {
        for (int i = 0; i < 3; i++) {
            System.out.printf("%.4f*t + %.4f*s = %.4f\n", matrix[i][0], matrix[i][1], result[i]);
        }
        if (solved && hasSolution) {
            System.out.printf("t = %.4f\n", getT());
            System.out.printf("s = %.4f\n", getS());
        } else if (solved) {
            System.out.println("No solution found!");
        }
        System.out.println();
    }

}

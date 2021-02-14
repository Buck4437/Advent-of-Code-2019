package com.company;

import java.util.Hashtable;
import java.util.Scanner;

public class Intcode {

    Hashtable<Integer, Long> memory = new Hashtable<>();
    Scanner sc = new Scanner(System.in);
    int pointer = 0;
    int relativeBase = 0;

    public Intcode(long[] memory) {
        for (int i = 0; i < memory.length; i++) {
            this.memory.put(i, memory[i]);
        }
    }

    public long run() {
        while (pointer < memory.size()) {
            int instruction = (int) getValue(pointer);
            int opcode = instruction % 100;
            long[] params;
            int[] modes;

//            System.out.println(memory);
//
//            System.out.println("Running opcode " + opcode + " at position " + pointer);

            switch (opcode) {
                case 1:
                    params = getParameters(3);
                    modes = getModes(instruction, 3);

                    setValue(modes[2], params[2], getValue(modes[0], params[0]) + getValue(modes[1], params[1]));

                    pointer += 4;
                    break;
                case 2:
                    params = getParameters(3);
                    modes = getModes(instruction, 3);

                    setValue(modes[2], params[2], getValue(modes[0], params[0]) * getValue(modes[1], params[1]));

                    pointer += 4;
                    break;
                case 3:
                    params = getParameters(1);
                    modes = getModes(instruction, 1);

                    System.out.println("Awaiting input...");
                    setValue(modes[0], params[0], sc.nextLong());

                    pointer += 2;
                    break;
                case 4:
                    params = getParameters(1);
                    modes = getModes(instruction, 1);

                    System.out.println(getValue(modes[0], params[0]));

                    pointer += 2;
                    break;
                case 5:
                    params = getParameters(2);
                    modes = getModes(instruction, 2);

                    if (getValue(modes[0], params[0]) != 0) {
                        pointer = (int) getValue(modes[1], params[1]);
                    } else {
                        pointer += 3;
                    }

                    break;
                case 6:
                    params = getParameters(2);
                    modes = getModes(instruction, 2);

                    if (getValue(modes[0], params[0]) == 0) {
                        pointer = (int) getValue(modes[1], params[1]);
                    } else {
                        pointer += 3;
                    }

                    break;
                case 7:
                    params = getParameters(3);
                    modes = getModes(instruction, 3);

                    if (getValue(modes[0], params[0]) < getValue(modes[1], params[1])) {
                        setValue(modes[2], params[2], 1);
                    } else {
                        setValue(modes[2], params[2], 0);
                    }

                    pointer += 4;
                    break;
                case 8:
                    params = getParameters(3);
                    modes = getModes(instruction, 3);

                    if (getValue(modes[0], params[0]) == getValue(modes[1], params[1])) {
                        setValue(modes[2], params[2], 1);
                    } else {
                        setValue(modes[2], params[2], 0);
                    }

                    pointer += 4;
                    break;
                case 9:
                    params = getParameters(1);
                    modes = getModes(instruction, 1);

                    relativeBase += getValue(modes[0], params[0]);

                    pointer += 2;
                    break;
                case 99:
                    return getValue(0, 0);
                default:
                    return error("Error: Unknown opcode " + opcode);
            }
        }
        return error("Error: Pointer (at position " + pointer
                + ") exceeds the maximum size of memory (size of " + memory.size() + ")");
    }

    private int[] getModes(long instruction, int parameters) {
        instruction = (instruction - instruction % 100) / 100;
        int[] modes = new int[parameters];
        for (int i = 0; i < parameters; i++) {
            modes[i] = (int) instruction % 10;
            instruction = (instruction - instruction % 10) / 10;
        }
        return modes;
    }

    private long getValue(long parameter) {
        return getValue(0, parameter);
    }

    private long getValue(int mode, long parameter) {
        switch (mode) {
            case 0:
                if (memory.get((int) parameter) == null) {
                    return 0;
                } else {
                    return memory.get((int) parameter);
                }
            case 1:
                return parameter;
            case 2:
                int position = (int) parameter + relativeBase;

                if (memory.get(position) == null) {
                    return 0;
                } else {
                    return memory.get(position);
                }
            default:
                return error("Error: unknown mode " + mode);
        }
    }

    private void setValue(int mode, long parameter, long value) {
        switch (mode) {
            case 0:
                memory.put((int) parameter, value);
                break;
            case 1:
                error("Error: an instruction write cannot be in mode 1");
                return;
            case 2:
                memory.put((int) parameter + relativeBase, value);
                break;
        }
    }

    private long[] getParameters(int amount) {
        long[] params = new long[amount];
        for (int i = 0; i < amount; i++) {
            params[i] = getValue(pointer + i + 1);
        }
        return params;
    }

    private int error(String text) {
        System.out.println(text);
        return -1;
    }

}
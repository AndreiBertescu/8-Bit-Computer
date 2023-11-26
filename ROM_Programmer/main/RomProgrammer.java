package main;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import main.Main.commands;

class RomProgrammer {
	// the micro commands
	static final int HLT = 0b10000000_00000000_00000000_00000000;
	static final int RST = 0b01000000_00000000_00000000_00000000;
	static final int CRST = 0b00100000_00000000_00000000_00000000;
	static final int II = 0b00010000_00000000_00000000_00000000;
	static final int FST = 0b00001000_00000000_00000000_00000000;
	static final int LST = 0b00000100_00000000_00000000_00000000;
	static final int RW = 0b00000010_00000000_00000000_00000000;
	static final int RO = 0b00000001_00000000_00000000_00000000;

	static final int RI = 0b00000000_10000000_00000000_00000000;
	static final int CO = 0b00000000_01000000_00000000_00000000;
	static final int JMP = 0b00000000_00100000_00000000_00000000;
	static final int CNT = 0b00000000_00010000_00000000_00000000;
	static final int AO = 0b00000000_00001000_00000000_00000000;
	static final int AI = 0b00000000_00000100_00000000_00000000;
	static final int BO = 0b00000000_00000010_00000000_00000000;
	static final int BI = 0b00000000_00000001_00000000_00000000;

	static final int SUB = 0b00000000_00000000_10000000_00000000;
	static final int ADD = 0b00000000_00000000_01000000_00000000;
	static final int AND = 0b00000000_00000000_00100000_00000000;
	static final int OR = 0b00000000_00000000_00010000_00000000;
	static final int XOR = 0b00000000_00000000_00001000_00000000;
	static final int NOT = 0b00000000_00000000_00000100_00000000;
	static final int SHR = 0b00000000_00000000_00000010_00000000;
	static final int SHL = 0b00000000_00000000_00000001_00000000;

	static final int FLGW = 0b00000000_00000000_00000000_10000000;
	static final int CMP = 0b00000000_00000000_00000000_01000000;
	static final int CARY = 0b00000000_00000000_00000000_00100000;
	static final int ZERO = 0b00000000_00000000_00000000_00010000;
	static final int OUT = 0b00000000_00000000_00000000_00001000;

	// the rom memory
	int[][] instructionMemory = new int[4][4096];

	// the basic commands list
	LinkedHashMap<commands, int[][]> definedCommands = new LinkedHashMap<>();

	// the way the address is made - must add up to 12
	int flagBits;
	int opCodeBits;
	int counterBits;

	// the flag positions
	int zero = 0b001;
	int overflow = 0b010;
	int compare = 0b100;

	public RomProgrammer(int flagBits, int opCodeBits, int counterBits) {
		this.flagBits = flagBits;
		this.opCodeBits = opCodeBits;
		this.counterBits = counterBits;
	}

	// main method
	public void execute(String instructionPath) {
		// define all the commands using microsteps
		initializeCommands();

		// generate all the commands
		writeToMemory();

		// write the commands to their specific bin file
		for (int i = 0; i < 4; i++)
			outputToBin(i, instructionPath);
	}

	void initializeCommands() {
		// short fetch command is: { CO, II, CNT }
		// long fetch command is: { CO, II, CNT }, { CO, IW, CNT } - this can be
		// implemented in following steps
		// the final microinstruction should always be CRST
		// no more than 2^counterBits steps

		int[][] ls = { { CO, II, CNT }, { HLT } };
		definedCommands.put(commands.HLT, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { RO, AI, CRST } };
		definedCommands.put(commands.MOVA, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { RO, BI, CRST } };
		definedCommands.put(commands.MOVB, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, AI, CNT, CRST } };
		definedCommands.put(commands.PUTA, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, BI, CNT, CRST } };
		definedCommands.put(commands.PUTB, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { AO, RW, CRST } };
		definedCommands.put(commands.STRA, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { BO, RW, CRST } };
		definedCommands.put(commands.STRB, ls);

		ls = new int[][] { { CO, II, CNT }, { AO, BI }, { AI, CRST } };
		definedCommands.put(commands.TRAB, ls);

		ls = new int[][] { { CO, II, CNT }, { AI, BO }, { BI, CRST } };
		definedCommands.put(commands.TRBA, ls);

		ls = new int[][] { { CO, II, CNT }, { FST, AI, CRST } };
		definedCommands.put(commands.FST, ls);

		ls = new int[][] { { CO, II, CNT }, { LST, AI, CRST } };
		definedCommands.put(commands.LST, ls);

		ls = new int[][] { { CO, II, CNT }, { AND, AI }, { FLGW, CRST } };
		definedCommands.put(commands.ANDI, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { AND, RW, FLGW, CRST } };
		definedCommands.put(commands.AND, ls);

		ls = new int[][] { { CO, II, CNT }, { OR, AI }, { FLGW, CRST } };
		definedCommands.put(commands.ORI, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { OR, RW, FLGW, CRST } };
		definedCommands.put(commands.OR, ls);

		ls = new int[][] { { CO, II, CNT }, { XOR, AI }, { FLGW, CRST } };
		definedCommands.put(commands.XORI, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { XOR, RW, FLGW, CRST } };
		definedCommands.put(commands.XOR, ls);

		ls = new int[][] { { CO, II, CNT }, { NOT, AI }, { FLGW, CRST } };
		definedCommands.put(commands.NOT, ls);

		ls = new int[][] { { CO, II, CNT }, { ADD, AI }, { FLGW, CRST } };
		definedCommands.put(commands.ADDI, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { FLGW, ADD, RW, CRST } };
		definedCommands.put(commands.ADD, ls);

		ls = new int[][] { { CO, II, CNT }, { SUB, ADD, AI, FLGW, CRST } };
		definedCommands.put(commands.SUBI, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { FLGW, SUB, ADD, RW, CRST } };
		definedCommands.put(commands.SUB, ls);

		ls = new int[][] { { CO, II, CNT }, { FST, BI }, { ADD, AI }, { BI, FLGW, CRST } };
		definedCommands.put(commands.INC, ls);

		ls = new int[][] { { CO, II, CNT }, { FST, BI }, { SUB, ADD, AI }, { BI, FLGW, CRST } };
		definedCommands.put(commands.DEC, ls);

		ls = new int[][] { { CO, II, CNT }, { SHR, AI }, { FLGW, CRST } };
		definedCommands.put(commands.SHR, ls);

		ls = new int[][] { { CO, II, CNT }, { SHL, AI }, { FLGW, CRST } };
		definedCommands.put(commands.SHL, ls);

		ls = new int[][] { { CO, II, CNT }, { ZERO }, { CO, JMP, CRST } };
		definedCommands.put(commands.EQ, ls);

		ls = new int[][] { { CO, II, CNT }, { ZERO }, { CO, JMP, CRST } };
		definedCommands.put(commands.NEQ, ls);

		ls = new int[][] { { CO, II, CNT }, { CARY }, { CO, JMP, CRST } };
		definedCommands.put(commands.OV, ls);

		ls = new int[][] { { CO, II, CNT }, { CARY }, { CO, JMP, CRST } };
		definedCommands.put(commands.NOV, ls);

		ls = new int[][] { { CO, II, CNT }, { CMP }, { CO, JMP, CRST } };
		definedCommands.put(commands.CMP, ls);

		ls = new int[][] { { CO, II, CNT }, { CMP }, { CO, JMP, CRST } };
		definedCommands.put(commands.NCMP, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, JMP, CRST } };
		definedCommands.put(commands.JMP, ls);

		ls = new int[][] { { CO, II, CNT }, { RST } };
		definedCommands.put(commands.RST, ls);

		ls = new int[][] { { CO, II, CNT }, { CNT, CRST } };
		definedCommands.put(commands.NOOP, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, OUT, CNT, CRST } };
		definedCommands.put(commands.OUT, ls);

		ls = new int[][] { { CO, II, CNT }, { CO, RI, CNT }, { RO, OUT, CRST } };
		definedCommands.put(commands.OUTR, ls);

		ls = new int[][] { { CO, II, CNT }, { AO, OUT, CRST } };
		definedCommands.put(commands.OUTA, ls);

		ls = new int[][] { { CO, II, CNT }, { BO, OUT, CRST } };
		definedCommands.put(commands.OUTB, ls);
	}

	void writeToMemory() {
		for (Entry<commands, int[][]> command : definedCommands.entrySet())
			for (int flag = 0; flag < Math.pow(2, flagBits); flag++) {

				// set up the conditional jumps
				int[][] values = newArray(command.getValue());

				if (((command.getKey() == commands.EQ) && ((flag & zero) == 0))
						|| ((command.getKey() == commands.NEQ) && (flag & zero) != 0)
						|| ((command.getKey() == commands.OV) && (flag & overflow) == 0)
						|| ((command.getKey() == commands.NOV) && (flag & overflow) != 0)
						|| ((command.getKey() == commands.CMP) && (flag & compare) == 0)
						|| ((command.getKey() == commands.NCMP) && (flag & compare) != 0)) {
					values[2] = definedCommands.get(commands.NOOP)[1];
				}

				for (int i = 0; i < values.length; i++) {
					int address = (flag << (opCodeBits + counterBits)) + (command.getKey().ordinal() << counterBits)
							+ i;

					// instruction
					int value = 0;
					for (int microInstruction : values[i])
						value |= microInstruction;

					// write to respective memory
					instructionMemory[0][address] = (value >> 24) & 0xff;
					instructionMemory[1][address] = (value >> 16) & 0xff;
					instructionMemory[2][address] = (value >> 8) & 0xff;
					instructionMemory[3][address] = (value >> 0) & 0xff;
				}
			}
	}

	void outputToBin(int id, String instructionPath) {
		String filePathAux = instructionPath + (id + 1) + ".bin";

		try (FileOutputStream fileOutputStream = new FileOutputStream(filePathAux);
				DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {

			for (int i = 0; i < instructionMemory[id].length; i++)
				dataOutputStream.writeByte((byte) instructionMemory[id][i]);

			System.out.println("Binary data has been written to " + filePathAux);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int[][] newArray(int[][] value) {
		int[][] ls = new int[value.length][];

		for (int i = 0; i < value.length; i++) {
			ls[i] = new int[value[i].length];

			for (int j = 0; j < value[i].length; j++)
				ls[i][j] = value[i][j];
		}

		return ls;
	}
}

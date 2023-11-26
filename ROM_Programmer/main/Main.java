package main;

public class Main {

	// the implemented commands - max 2^opCodeBits commands
	enum commands {
		HLT, MOVA, MOVB, PUTA, PUTB, STRA, STRB, TRAB, TRBA, FST, LST, ANDI, AND, ORI, OR, XORI, XOR, NOT, ADDI, ADD,
		SUBI, SUB, INC, DEC, SHR, SHL, EQ, NEQ, OV, NOV, CMP, NCMP, JMP, RST, NOOP, OUT, OUTR, OUTA, OUTB
	};

	// the way the address is made - must add up to 12
	final static int flagBits = 3;
	final static int opCodeBits = 6;
	final static int counterBits = 3;

	// the name of file to write to the rom chips
	static String instructionPath = "instructionRom";

	// the name of file to write to the hex decoder chip
	static String hexPath = "hexRom";

	// the name of file where the code to be assembled is
	static String assemblyPath = "assemblyCode.txt";
	// the name of file to write to the program chip
	static String programPath = "programRom";

	// main method
	public static void main(String[] args) {
//		RomProgrammer romProgrammer = new RomProgrammer(flagBits, opCodeBits, counterBits);
//		romProgrammer.execute(instructionPath);

//		HexProgrammer hexProgrammer = new HexProgrammer();
//		hexProgrammer.execute(hexPath);

		Assembler assembler = new Assembler();
		assembler.assemble(assemblyPath, programPath);
	}

}

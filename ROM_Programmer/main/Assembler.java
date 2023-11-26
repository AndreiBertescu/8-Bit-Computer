package main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import main.Main.commands;

public class Assembler {

	// # - decimal
	// % - binary
	// $ - hex
	ArrayList<Integer> instructions = new ArrayList<>();

	public void assemble(String assemblyPath, String programPath) {
		// read the text file and tries to interpret it
		readFile(assemblyPath);

		// show instructions
//		printContents();

		// write the instructions to the specified bin file
		outputToBin(programPath);
	}

	void readFile(String assemblyPath) {
		try (BufferedReader br = new BufferedReader(new FileReader(assemblyPath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");

				for (String token : tokens) {
					if (token.length() == 0)
						continue;

					if (token.length() >= 2 && token.substring(0, 2).equals("//"))
						break;

					for (commands command : commands.values())
						if (command.name().equals(token)) {
							instructions.add(command.ordinal());
							break;
						}

					switch (token.charAt(0)) {
					case '#':
						instructions.add(Integer.parseInt(token.substring(1, token.length())));
						break;
					case '%':
						token = token.replace("_", "");
						instructions.add(Integer.parseInt(token.substring(1, token.length()), 2));
						break;
					case '$':
						instructions.add(Integer.parseInt(token.substring(1, token.length()), 16));
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	void printContents() {
		System.out.println("\nContents:\n--------");

		for (int i : instructions)
			System.out.println(String.format("%8s", Integer.toBinaryString(i)).replace(' ', '0'));

		System.out.println("--------\n");
	}

	void outputToBin(String programPath) {
		String filePathAux = programPath + ".bin";

		try (FileOutputStream fileOutputStream = new FileOutputStream(filePathAux);
				DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {

			for (int i = 0; i < instructions.size(); i++)
				dataOutputStream.writeByte(instructions.get(i));

			System.out.println("Binary data has been written to " + filePathAux);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

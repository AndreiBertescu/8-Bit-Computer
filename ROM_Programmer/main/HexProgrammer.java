package main;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HexProgrammer {

	int[] digits = new int[10];
	int[] instructionMemory = new int[4096];
	Map<Integer, Integer> values = new HashMap<>();

	public HexProgrammer() {
		digits[0] = 0b11111100;
		digits[1] = 0b01100000;
		digits[2] = 0b11011010;
		digits[3] = 0b11110010;
		digits[4] = 0b01100110;
		digits[5] = 0b10110110;
		digits[6] = 0b10111110;
		digits[7] = 0b11100000;
		digits[8] = 0b11111110;
		digits[9] = 0b11110110;
	}

	public void execute(String romPath) {
		writeToMemory();

		outputToBin(romPath);
	}

	void writeToMemory() {
		for (int sign = 0; sign <= 1; sign++)
			for (int digit = 0; digit < 4; digit++)
				for (int nr = 0; nr <= 255; nr++) {
					int address = (sign << 10) + (digit << 8) + nr;

					int value = 0;
					switch (3 - digit) {
					case 0:
						value = (sign != 0 && (nr & 0b10000000) != 0) ? 0b00000010 : 0b00000000;
						break;
					case 1:
						if (sign == 0)
							value = digits[nr / 100 % 10];
						else if ((nr & 0b10000000) != 0)
							value = digits[Math.abs((~(nr - 1) & 0b11111111) / 100 % 10)];
						else
							value = digits[nr / 100 % 10];
						break;
					case 2:
						if (sign == 0)
							value = digits[nr / 10 % 10];
						else if ((nr & 0b10000000) != 0)
							value = digits[Math.abs((~(nr - 1) & 0b11111111) / 10 % 10)];
						else
							value = digits[nr / 10 % 10];
						break;
					case 3:
						if (sign == 0)
							value = digits[nr % 10];
						else if ((nr & 0b10000000) != 0)
							value = digits[Math.abs((~(nr - 1) & 0b11111111) % 10)];
						else
							value = digits[nr % 10];
						break;
					}

					values.put(address, value);
				}

		for (Entry<Integer, Integer> command : values.entrySet())
			instructionMemory[command.getKey()] = command.getValue();
	}

	void outputToBin(String filePathAux) {
		filePathAux += ".bin";

		try (FileOutputStream fileOutputStream = new FileOutputStream(filePathAux);
				DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {

			for (int i = 0; i < instructionMemory.length; i++)
				dataOutputStream.writeByte((byte) instructionMemory[i]);

			System.out.println("Binary data has been written to " + filePathAux);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

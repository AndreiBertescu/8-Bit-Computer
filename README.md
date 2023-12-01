# 8-Bit Computer
An 8-bit computer inspired by Ben Eater's series: [Playlist](https://www.youtube.com/playlist?list=PLowKtXNTBypGqImE405J2565dvjafglHU)  <br>
It is built following the SAP-1 architecture with a few modifications.  <br>
The computer was first created and simulated in Logisim and was remade in Multisim for a "real-world" design.  <br>
All the ROM chips were programmed using a Java project with three separate classes for the three separate functionalities.  <br>

# Features
- Two general-purpose registers (A and B)
- A 256-byte programmable RAM module
- A 6-bit program counter
- A 4-digit output module with signed/unsigned capabilities
- A dedicated ROM chip where you can write a program limited to 2^6 instructions
- An ALU with multiple operations: SUB, ADD, AND, OR, XOR, NOT, Right-Shift, and Left-Shift
- Three flags: zero, carry, and greater-than
- A clock made from a 555 timer with automatic pulse or manual pulse capabilities
- Control logic composed of 4 ROM chips and a 3-bit microinstruction counter  <br>

# Architecture
- Logic diagram
![Designed Architecture](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/ec0a706b-3eec-4712-9217-acfa8a5e957f)

- Logisim schematic
![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/2dca19bf-2529-4b3d-b3e9-e2fc61c023dd)

# Control Logic
The control logic operates by configuring a 12-bit address across 4 ROM chips, connecting the output to all control pins within the computer.

### Address Structure:
The first 3 bits: Microinstruction counter.
The next 6 bits: Op-code.
The last 3 bits: Flags (Detailed in the ALU section).
Each op-code corresponds to a specific, hardcoded instruction. The system allows a maximum of 2^3 microinstructions for an instruction, although it currently uses no more than 4.

### Microinstructions:
- HLT: Halts the computer by stopping the clock
- RST: Resets the computer by initializing the program counter
- CRST: Resets the microinstruction counter, concluding the instruction
- II: Op-code register in
- FST: Sets the Least Significant Bit (LSB) to high
- LST: Sets the Most Significant Bit (MSB) to high
- RW: Writes a value to RAM at the address stored in the instruction register* (Detailed in the RAM section)
- RO: Outputs a value from RAM at the address stored in the instruction register* (Detailed in the RAM section)
- RI: Instruction register in
- CO: Program ROM out
- JMP: Sets the program counter to the value on the bus
- CNT: Increments the program counter
- AO, BO: General-purpose register in
- AI, BI: General-purpose register out
- SUB, ADD, AND, OR, XOR, NOT, SHR, SHL: Explained in the ALU section
- FLGW: Writes the flag values to the flag register
- CMP: Enables the output of the A-greater-B flag stored in the flag register
- CARY: Enables the output of the carry flag stored in the flag register
- ZERO: Enables the output of the zero flag stored in the flag register
- OUT: Output register in

### Implemented Instructions:
- 000_000 - HLT: Halts the computer
- 000_001 - MOVA: Puts the specified value into the A register
- 000_010 - MOVB: Puts the specified value into the B register
- 000_011 - PUTA: Puts the value from the specified RAM address into the A register
- 000_100 - PUTB: Puts the value from the specified RAM address into the B register
- 000_101 - STRA: Stores the value of the A register in the RAM at the specified address
- 000_110 - STRB: Stores the value of the B register in the RAM at the specified address
- 000_111 - TRAB: Transfers the value of the A register to the B register and sets the value of the A register to 0
- 001_000 - TRBA: Transfers the value of the B register to the A register and sets the value of the B register to 0
- 001_001 - FST: Places 2^0 (1 decimal) on the bus
- 001_010 - LST: Places 2^7 (128 decimal) on the bus
- 001_011 - ANDI: Performs the AND operation on the values of the A and B registers, storing the result in the A register
- 001_100 - AND: Performs the AND operation on the values of the A and B registers, storing the result in the specified RAM address
- 001_101 - ORI: Performs the OR operation on the values of the A and B registers, storing the result in the A register
- 001_110 - OR: Performs the OR operation on the values of the A and B registers, storing the result in the specified RAM address
- 001_111 - XORI: Performs the XOR operation on the values of the A and B registers, storing the result in the A register
- 010_000 - XOR: Performs the XOR operation on the values of the A and B registers, storing the result in the specified RAM address
- 010_001 - NOT: Negates the value in the A register
- 010_010 - ADDI: Performs the addition operation on the values of the A and B registers, storing the result in the A register
- 010_011 - ADD: Performs the addition operation on the values of the A and B registers, storing the result in the specified RAM address
- 010_100 - SUBI: Performs the subtraction operation on the values of the A and B registers, storing the result in the A register
- 010_101 - SUB: Performs the subtraction operation on the values of the A and B registers, storing the result in the RAM address
- 010_110 - INC: Increments the value in the A register
- 010_111 - DEC: Decrements the value in the A register
- 011_000 - SHR: Performs the logical right shift on the value in the A register
- 011_001 - SHL: Performs the logical left shift on the value in the A register
- 011_010 - EQ: Equals conditional; if the ZERO flag is HIGH, jumps to the next specified value; otherwise, does a NOOP
- 011_011 - NEQ: Not equals conditional; if the ZERO flag is LOW, jumps to the next specified value; otherwise, does a NOOP
- 011_100 - OV: Overflow conditional; if the CARY flag is HIGH, jumps to the next specified value; otherwise, does a NOOP
- 011_101 - NOV: Not overflow conditional; if the CARY flag is LOW, jumps to the next specified value; otherwise, does a NOOP
- 011_110 - CMP: A greater than B conditional; if the CMP flag is HIGH, jumps to the next specified value; otherwise, does a NOOP
- 011_111 - NCMP: A less than B conditional; if the CMP flag is LOW, jumps to the next specified value; otherwise, does a NOOP
- 100_000 - JMP: Jumps to the next specified value
- 100_001 - RST: Resets the computer
- 100_010 - NOOP: No operation, mainly used for conditionals
- 100_011 - OUT: Outputs the next specified value
- 100_100 - OUTR: Outputs a value from RAM at the specified address
- 100_101 - OUTA: Outputs the value from the A register
- 100_110 - OUTB: Outputs the value from the B register

<strong>Note:</strong> The "specified value" is a value read from the ROM. For instructions using a "specified value," the ROM comprises of two parts: the op-code and the specified value.

# Software Side
All ROM chips in the project were programmed using binary files generated by the ROM_Programmer Java project.
The project comprises three classes, each dedicated to programming a specific type of ROM:

<strong>- The ROMProgrammer</strong> class is responsible for the control logic. It iterates through all the defined commands, calculates addresses while considering flag combinations and the microinstruction counter, and writes the appropriate set of microinstructions to the designated address.

<strong>- The HexProgrammer</strong> class is utilized for the output module, decoding an 8-bit binary value to a 7-segment display. It iterates through all 256 numbers, setting the appropriate output pins to HIGH while considering the signed/unsigned input flag.

<strong>- The Assembler</strong> class compiles a given text file containing assembly code* into a binary program file. <br><br>
*The assembly code isn't actual assembly. Instead, it utilizes the pre-defined commands along with decimal '#', binary '%', or hex values '$'. Additionally, the double-slash '//' is used as a comment initiator.

## Clock
It comprises a 555 timer with a switch that toggles it from a monostable to an astable working state. In the astable mode, the frequency can be adjusted from a 250k potentiometer.  <br>
The output of the timer is routed through a NAND gate for multiple purposes:
- To get the inverse clock signal for the control logic
- The signal is also put through an AND operation with the Halt microinstruction for stopping the computer
- A last NAND gate is used for the program counter to invert the reset signal  <br>
![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/55095d79-5b30-43c5-aaad-5dc87e20edb7)

## Program Counter
The main components are the 2 interconnected 4-bit counters that are routed straight to the program ROM that, in turn, is connected to the bus through an octal tri-state buffer. The next instruction is read only when the CO signal is high. The counter can also jump to the value of the bus when the JMP signal is high.  <br>
![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/769b020d-8333-49e0-8bbe-4a943b7d8537)  <br>

## Registers
There are a few different types of registers in the computer, some of which are more complex than others.  <br>

- <strong>The A and B registers</strong> are the most complex because they have an octal buffer connected to the output of the registers for outputting back to the bus; they are also straight-wired to the ALU and have a LED bar to see their contents.  <br>
![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/61c09441-bd1b-4ba2-aa09-901a496ccbb1)

- <strong>The instruction register</strong> is a full 8-bit register with no LED bar.
- <strong>The Op-Code register</strong> is only 6-bit and doesn't have output capabilities; it does, however, have an LED bar.
- <strong>The Flags register</strong> is a simple 3-bit register with an LED bar.  <br>
![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/d1f4d315-6a6e-4048-8a3c-fa15cdebcfbc)

## Arithmetic Logic Unit
It features 8 operations, all using their respective chips:
- Addition
- Subtraction: uses the addition chips, but when the SUB pin is high, it inverts the B register through an XOR gate and sets the carry pin high, thus creating two's complement
- And
- Or
- Xor
- Not
- Right logical shift: uses an octal buffer
- Left logical shift: uses an octal buffer

It also features three status flags that feed int the control logic ROM chips:
- Zero flag: it is the output of an OR operation using the output from the adder chips
- Carry flag: the output from the second adder chip
- Cmp flag: it is A greater than B pin of a second comparator chip

![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/40700316-b757-4099-9ff3-834e0ea2eddf)

## Random Access Memory
It features a 256-byte RAM chip with a manual programming option. The address and the actual value are run through two demultiplexers that have two separate inputs: one from the instruction register (for the address) or from the bus (for the actual value), or from two 8-bit dip switches. It also features an LED bar for the address and the value. The RAM write signal is ANDed with the clock that has been routed through a filter with the purpose of getting a pulse at the high-going edge.

![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/63640384-b309-46f5-b153-16d2f849d367)

## Output
A four-digit display that shows the value stored in the output register. It works by sending the value to a ROM chip that decodes the binary number to a decimal display. An extra clock was added to rapidly switch from outputting from a digit to the next with the purpose of minimizing the amount of ROM chips used. It also has a switch for toggling signed/unsigned display.

![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/39b35543-a934-4aa1-98af-712d6e791a04)



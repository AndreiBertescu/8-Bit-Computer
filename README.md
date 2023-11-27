# 8-Bit Computer
An 8-bit computer inspired by Ben Eater's series [Playlist](https://www.youtube.com/playlist?list=PLowKtXNTBypGqImE405J2565dvjafglHU).  <br>
It is built following the SAP-1 architecture with a few modifications.  <br>
The computer was first created and simulated in Logisim and was remade in Multisim for a "real-world" design.  <br>
All the ROM chips were programmed using a Java project with three separate classes for the three separate functionalities.  <br><br>
![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/2dca19bf-2529-4b3d-b3e9-e2fc61c023dd)

# Features
- Two general-purpose registers (A and B)
- A 256-byte programmable RAM module
- A 6-bit program counter
- A 4-digit output module with signed/unsigned capabilities
- A dedicated ROM chip where you can write a program limited to 2^6 instructions
- An ALU with multiple operations: SUB, ADD, AND, OR, XOR, NOT, Right-Shift, and Left-Shift
- Three flags: zero, carry, and greater-than
- A clock made from a 555 timer with automatic pulse or manual pulse capabilities
- Control logic composed of 4 ROM chips and a 3-bit microinstruction counter

## Clock
It comprises a 555 timer with a switch that toggles it from a monostable to an astable working state. In the astable mode, the frequency can be adjusted from a 250k potentiometer.  <br>
The output of the timer is routed through a NAND gate for multiple purposes:
- To get the inverse clock signal for the control logic
- The signal is also put through an AND operation with the Halt microinstruction for stopping the computer
- A last NAND gate is used for the program counter to invert the reset signal
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

It also features three status flags:
- Zero flag: it is the output of an OR operation using the output from the adder chips
- Carry flag: the output from the second adder chip
- Cmp flag: it is A greater than B pin of a second comparator chip

![image](https://github.com/AndreiBertescu/8-Bit-Computer/assets/126001291/40700316-b757-4099-9ff3-834e0ea2eddf)


